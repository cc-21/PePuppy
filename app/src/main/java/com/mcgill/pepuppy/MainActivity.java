package com.mcgill.pepuppy;

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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static androidx.core.content.FileProvider.*;

public class MainActivity extends AppCompatActivity
{
    private Button aCaptureBtn;
    private Button aVisitedBtn;
    private static final int PERMISSION_CODE = 1000;
    private String aCurrentPhotoPath;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private TextView aText;
    private TextView aBreedLink;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aBreedLink = findViewById(R.id.breed_link);
        aText = findViewById(R.id.textView2);
        aCaptureBtn = findViewById(R.id.capture_image_btn1);
        aVisitedBtn = findViewById(R.id.capture_image_btn2);
        aCaptureBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    {
                        dispatchTakePictureIntent();
                    }else{
                        // Permission not enabled
                        String[] permission ={Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        // Show popup to request permissions
                        requestPermissions(permission, PERMISSION_CODE);
                    }
                }
                else { }
            }
        });
        aVisitedBtn.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dispatchUploadImageIntent();
                    }
        });
    }

    // Todo
    private void dispatchUploadImageIntent()
    {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_CODE:
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
            } catch (IOException ex)
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
            if (requestCode == REQUEST_IMAGE_CAPTURE)
            {
                Bitmap bmp = BitmapFactory.decodeFile(aCurrentPhotoPath);
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bmp);
                final FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                        .getCloudImageLabeler();
                labeler.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>()
                        {
                            @Override
                            public void onSuccess(List<FirebaseVisionImageLabel> labels)
                            {
                                classify(labels);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener()
                        {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                alert("Image cannot be recognized.");
                            }
                        });
            }
        }
    }

    public void alert(String message)
    {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle("Woops!");
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void classify(List<FirebaseVisionImageLabel> labels)
    {
        String labelResult = "Found Lable is: \n";
        for (int i = 0; i < labels.size(); i++) {
            labelResult += labels.get(i).getText();
            labelResult += ": " + String.format("%03.2f", labels.get(i).getConfidence()*100) + "%\n";
            Log.d("Label", labelResult);
        }

        aBreedLink.setText("https://www.dogspot.in/adoption/");
        aText.setText(labelResult, TextView.BufferType.NORMAL);
        aText.setBackgroundColor(Color.WHITE);
    }

    private File createImageFile() throws IOException
    {// Create an image file name
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
