package com.mcgill.pepuppy;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static androidx.core.content.FileProvider.*;

public class MainActivity extends AppCompatActivity
{
    private static final int PERMISSION_CODE = 1000;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_UPLOAD = 2;
    private Button aCaptureBtn;
    private Button aUploadBtn;
    private String aCurrentPhotoPath;
    private TextView aText;
    private TextView aBreedLink;
    private TextView aRecommendLink;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aRecommendLink = findViewById(R.id.recommend_link);
        aBreedLink = findViewById(R.id.breed_link);
        aText = findViewById(R.id.textView2);
        aCaptureBtn = findViewById(R.id.capture_image_btn1);
        aUploadBtn = findViewById(R.id.capture_image_btn2);
        aCaptureBtn.setOnClickListener( view ->
        {
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                dispatchTakePictureIntent();
            }
            else {
                // Permission not enabled
                String[] permission ={Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                // Show popup to request permissions
                requestPermissions(permission, PERMISSION_CODE);
            }
        });
        aUploadBtn.setOnClickListener( view -> dispatchUploadImageIntent());
    }

    private void dispatchUploadImageIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), REQUEST_IMAGE_UPLOAD);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode == PERMISSION_CODE)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                dispatchTakePictureIntent();
            }
            else
            {
                Toast.makeText(this, "Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            // Create the File where the photo should go
            File photoFile = null;
            try
            {
                photoFile = createImageFile();
            }
            catch (IOException ex)
            {
                Log.d("", "dispatchTakePictureIntent: " + ex.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null)
            {
                Uri photoURI = getUriForFile(
                        this,
                        "com.mcgill.pepuppy.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            final FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                    .getCloudImageLabeler();
            try
            {
                if (requestCode == REQUEST_IMAGE_CAPTURE)
                {
                    Bitmap bmp = BitmapFactory.decodeFile(aCurrentPhotoPath);
                    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bmp);
                    labeler.processImage(image)
                            .addOnSuccessListener( labels -> classify(labels) )
                            .addOnFailureListener( exception -> alert("Image cannot be recognized.") );
                }
                else if(requestCode == REQUEST_IMAGE_UPLOAD)
                {
                    Uri selectedImage = data.getData();
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bmp);
                    labeler.processImage(image)
                            .addOnSuccessListener( labels -> classify(labels) )
                            .addOnFailureListener( exception -> alert("Image cannot be recognized.") );
                }
            }
            catch (IOException e)
            {
                Log.d("", "dispatchUploadImageIntent: " + e.toString());
            }
        }
    }

    public void alert(String message)
    {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle("Woops!");
        dlgAlert.setPositiveButton("OK", (dialog, which)-> {});
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void classify(List<FirebaseVisionImageLabel> labels)
    {
        String labelResult = "";
        String breedLink = "PUPPY 404";
        for (int i = 0; i < labels.size(); i++)
        {
            String breedLabel = labels.get(i).getText();
            if (BreedData.contains(breedLabel))
            {
                if (labelResult.equals("PUPPY 404"))
                {
                    labelResult += "Found breed:\n";
                }
                labelResult += breedLabel + " - " + String.format("%03.2f", labels.get(i).getConfidence()*100) + "%\n";
                if(breedLink.equals("PUPPY 404"))
                {
                    if (breedLabel.contains(" "))
                    {
                        breedLabel=breedLabel.replace(" ","_");
                    }
                    breedLink = "Look at: https://en.wikipedia.org/wiki/" + breedLabel;
                }
                Log.d("Label", labelResult);
            }
        }
        if (labelResult.equals(""))
        {
            labelResult += "Woops, PUPPY NOT FOUND!";
            aText.setGravity(Gravity.CENTER);
        }
        aRecommendLink.setText("Useful Link: https://www.petfinder.com/search/dogs-for-adoption/?sort%5B0%5D=recently_added");
        aBreedLink.setText(breedLink, TextView.BufferType.NORMAL);
        aRecommendLink.setVisibility(View.VISIBLE);
        aBreedLink.setVisibility(View.VISIBLE);
        aText.setText(labelResult, TextView.BufferType.NORMAL);
        aText.setBackgroundColor(Color.WHITE);
    }

    private File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,   /* prefix */
                ".jpg",   /* suffix */
                storageDir       /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        aCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
