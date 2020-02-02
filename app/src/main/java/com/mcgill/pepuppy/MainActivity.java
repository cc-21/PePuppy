package com.mcgill.pepuppy;

import java.util.Arrays;
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
import java.io.FileNotFoundException;
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
    private static final int REQUEST_IMAGE_UPLOAD = 2;
    private TextView aText;
    private static final String[] breedInfo = {"Blue Lacy","Queensland Heeler","Rhod Ridgeback","Retriever",
            "Sharpei","Black Mouth Cur","Catahoula","Staffordshire","Affenpinscher","Afghan Hound",
            "Airedale Terrier","Akita","Australian Kelpie","Alaskan Malamute","English Bulldog","American Bulldog",
            "American English Coonhound","American Eskimo Dog","American Eskimo Dog",
            "American Eskimo Dog (Toy)","American Foxhound","American Hairless Terrier","American Staffordshire Terrier",
            "American Water Spaniel","Anatolian Shepherd Dog","Australian Cattle Dog","Australian Shepherd",
            "Australian Terrier","Basenji","Basset Hound","Beagle","Bearded Collie","Beauceron","Bedlington Terrier",
            "Belgian Malinois","Belgian Sheepdog","Belgian Tervuren","Bergamasco","Berger Picard","Bernese Mountain Dog",
            "Bichon Fris_","Black and Tan Coonhound","Black Russian Terrier","Bloodhound","Bluetick Coonhound","Boerboel",
            "Border Collie","Border Terrier","Borzoi","Boston Terrier","Bouvier des Flandres","Boxer","Boykin Spaniel",
            "Briard","Brittany","Brussels Griffon","Bull Terrier","Bull Terrier","Bulldog","Bullmastiff","Cairn Terrier",
            "Canaan Dog","Cane Corso","Cardigan Welsh Corgi","Cavalier King Charles Spaniel","Cesky Terrier","Chesapeake Bay Retriever",
            "Chihuahua","Chinese Crested Dog","Chinese Shar Pei","Chinook","Chow Chow","Cirneco dell'Etna","Clumber Spaniel","Cocker Spaniel",
            "Collie","Coton de Tulear","Curly-Coated Retriever","Dachshund","Dalmatian","Dandie Dinmont Terrier","Doberman Pinsch",
            "Doberman Pinscher","Dogue De Bordeaux","English Cocker Spaniel","English Foxhound","English Setter","English Springer Spaniel",
            "English Toy Spaniel","Entlebucher Mountain Dog","Field Spaniel","Finnish Lapphund","Finnish Spitz","Flat-Coated Retriever",
            "French Bulldog","German Pinscher","German Shepherd","German Shorthaired Pointer","German Wirehaired Pointer","Giant Schnauzer",
            "Glen of Imaal Terrier","Golden Retriever","Gordon Setter","Great Dane","Great Pyrenees","Greater Swiss Mountain Dog","Greyhound",
            "Harrier","Havanese","Ibizan Hound","Icelandic Sheepdog","Irish Red and White Setter","Irish Setter","Irish Terrier",
            "Irish Water Spaniel","Irish Wolfhound","Italian Greyhound","Japanese Chin","Keeshond","Kerry Blue Terrier","Komondor",
            "Kuvasz","Labrador Retriever","Lagotto Romagnolo","Lakeland Terrier","Leonberger","Lhasa Apso","L_wchen","Maltese",
            "Manchester Terrier","Mastiff","Miniature American Shepherd","Miniature Bull Terrier","Miniature Pinscher","Miniature Schnauzer",
            "Neapolitan Mastiff","Newfoundland","Norfolk Terrier","Norwegian Buhund","Norwegian Elkhound","Norwegian Lundehund","Norwich Terrier",
            "Nova Scotia Duck Tolling Retriever","Old English Sheepdog","Otterhound","Papillon","Parson Russell Terrier","Pekingese",
            "Pembroke Welsh Corgi","Petit Basset Griffon Vend_en","Pharaoh Hound","Plott","Pointer","Polish Lowland Sheepdog","Pomeranian",
            "Standard Poodle","Miniature Poodle","Toy Poodle","Portuguese Podengo Pequeno","Portuguese Water Dog","Pug","Puli","Pyrenean Shepherd",
            "Rat Terrier","Redbone Coonhound","Rhodesian Ridgeback","Rottweiler","Russell Terrier","St. Bernard","Saluki","Samoyed","Schipperke",
            "Scottish Deerhound","Scottish Terrier","Sealyham Terrier","Shetland Sheepdog","Shiba Inu","Shih Tzu","Siberian Husky","Silky Terrier",
            "Skye Terrier","Sloughi","Smooth Fox Terrier","Soft-Coated Wheaten Terrier","Spanish Water Dog","Spinone Italiano","Staffordshire Bull Terrier",
            "Standard Schnauzer","Sussex Spaniel","Swedish Vallhund","Tibetan Mastiff","Tibetan Spaniel","Tibetan Terrier","Toy Fox Terrier",
            "Treeing Walker Coonhound","Vizsla","Weimaraner","Welsh Springer Spaniel","Welsh Terrier","West Highland White Terrier","Whippet",
            "Wire Fox Terrier","Wirehaired Pointing Griffon","Wirehaired Vizsla","Xoloitzcuintli","Yorkshire Terrier"};
    private List<String> breedData = Arrays.asList(breedInfo);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), REQUEST_IMAGE_UPLOAD);
        //startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_IMAGE_UPLOAD);
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
            final FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                    .getCloudImageLabeler();
            try
            {
                if (requestCode == REQUEST_IMAGE_CAPTURE)
                {
                    Bitmap bmp = BitmapFactory.decodeFile(aCurrentPhotoPath);
                    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bmp);
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
                else if(requestCode == REQUEST_IMAGE_UPLOAD)
                {
                    Uri selectedImage = data.getData();

                    Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bmp);
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
            catch (FileNotFoundException e)
            {
                Log.d("", "dispatchUploadImageIntent: " + e.toString());
            } catch (IOException e)
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
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public boolean breadContains(String pName)
    { return breedData.stream().anyMatch(breed -> breed.trim().toLowerCase().equals(pName.trim().toLowerCase())); }

    private void classify(List<FirebaseVisionImageLabel> labels)
    {
        String labelResult = "";
        int judger=0;
        for (int i = 0; i < labels.size(); i++) {
            String dogname = labels.get(i).getText();
            if (breadContains(dogname)){
                if (judger==0){
                    labelResult += "Found Label:\n"; }
                labelResult += dogname;
                labelResult += ": " + String.format("%03.2f", labels.get(i).getConfidence()*100) + "%\n";
                labelResult += ": " + "https://en.wilipedia.org/wiki/" + dogname + "\n";
                Log.d("Label", labelResult);
                judger++;
            }
        }
        if (judger<1){
            labelResult += "Sorry, but we recommend this site for you!";
            labelResult += "https://www.dogspot.in/adoption/";
        }
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
