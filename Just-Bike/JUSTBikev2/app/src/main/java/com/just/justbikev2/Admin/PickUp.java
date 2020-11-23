package com.just.justbikev2.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Home;
import com.just.justbikev2.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class PickUp extends AppCompatActivity {
    ImageButton finish , generateQRbtn, front , back , left , right ;
    ImageView frontIV , backIV , leftIV , rightIV ,QRCodeImage;
    CardView QRCodeImageCard;
    EditText collectingAmtValue , estimatedCostValue,issueET;
    private static int CAMERA_SELECT1 = 1;
    private static int CAMERA_SELECT2 = 2;
    private static int CAMERA_SELECT3 = 3;
    private static int CAMERA_SELECT4 = 4;
    private static int CAMERA_SELECT5 = 5;
    private static int CAMERA_SELECT6 = 6;
    private final static int CAMERA_CODE = 42;
    public static final int CODE = 111;
    String currentImagePath = null;

    StorageTask storageTask;
    Uri imageUri;
    ProgressDialog progressBar;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    int flag;

    ArrayList<String> docUrls = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        setContentView(R.layout.activity_pick_up);

        bindViews();
        storageReference = FirebaseStorage.getInstance().getReference("pickup");
        databaseReference = FirebaseDatabase.getInstance().getReference("Pickup");

        setOnClicks();

    }
    private void setOnClicks() {
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDelivery();
            }
        });
        generateQRbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data_in_code;
                if (Common.currentUser != null && Common.currentUser.getName() != null)
                    data_in_code = Common.currentUser.getName() + " is authorised to take the vehicle ";
                else
                    data_in_code = "Invalid Person. Do not give the vehicle";
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(data_in_code, BarcodeFormat.QR_CODE, 300, 300);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    QRCodeImage.setImageBitmap(bitmap);
                    QRCodeImageCard.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag =1;
                startCamera();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag =2;
                startCamera();
            }
        });
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag =3;
                startCamera();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag =4;
                startCamera();
            }
        });

    }

    private void startCamera() {
        if(storageTask!=null && storageTask.isInProgress())
            Toast.makeText(PickUp.this, "Uploading in Process!", Toast.LENGTH_SHORT).show();
        checkCameraPermissions();
        captureImage();
    }

    private void checkCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, CODE);
        }
    }
    public void captureImage(){
        Intent camerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(camerIntent.resolveActivity(getPackageManager())!=null){
            File imageFile = null;
            try {
                imageFile = getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(imageFile!=null){
                Uri imageuri = FileProvider.getUriForFile(this,"com.just.justbikev2.fileprovider",imageFile);
                camerIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageuri);
                switch(flag){
                    case 1:
                        startActivityForResult(camerIntent,CAMERA_SELECT1);
                        break;
                    case 2:
                        startActivityForResult(camerIntent,CAMERA_SELECT2);
                        break;
                    case 3:
                        startActivityForResult(camerIntent,CAMERA_SELECT3);
                        break;
                    case 4:
                        startActivityForResult(camerIntent,CAMERA_SELECT4);
                        break;

                }
            }
        }
    }
    private File getImageFile() throws IOException  {
        String timestamp = new SimpleDateFormat("yyyyMMdd_hhss").format(new Date());
        String imageName = "jpg_"+timestamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageName,".jpg",storageDir);

        currentImagePath = imageFile.getAbsolutePath();
        return  imageFile;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openScanner();
        }
    }
    private void openScanner() {
        new IntentIntegrator(PickUp.this).initiateScan();
    }
    private void uploadPhoto() {

        if(imageUri!=null){
            progressBar = new ProgressDialog(this);
            progressBar.setMessage("Uploading ...");
            progressBar.show();
            // Uri.fromFile(new File("path/to/images/rivers.jpg"));
            StorageReference fileStorage = storageReference.child(imageUri.getLastPathSegment());
            storageTask = fileStorage.putFile(imageUri).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //      progressBar.setVisibility(View.VISIBLE);
                                    progressBar.dismiss();

                                }
                            }, 500);
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            //Common.currentRequest.getPhone()
                            sendMessage(Common.currentUser.getPhone(),"7477561951",downloadUrl.toString());
                            docUrls.add(downloadUrl.toString());
                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.dismiss();
                    Toast.makeText(PickUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.dismiss();
//                    double progress = (100 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
//                    //  progressBar.setVisibility(View.VISIBLE);
//                    progressBar.setProgress((int)progress);
//                    progressBar.setMessage("Uploading "+(int)progress+"% . . .");
                }
            });
        }else
            Toast.makeText(this, "No file selected! ", Toast.LENGTH_SHORT).show();
    }
    private void sendMessage(String sender , String receiver , String media){

        DatabaseReference chat = FirebaseDatabase.getInstance().getReference("Chat");
        HashMap<String,String> userMap = new HashMap<>();

        userMap.put("sender",sender);
        userMap.put("receiver",receiver);
        userMap.put("media",media);
        userMap.put("time",String.valueOf(System.currentTimeMillis()));

        chat.push().setValue(userMap);
    }
    private void finishDelivery(){
        if(issueET.getText()!=null)
            docUrls.add(issueET.getText().toString());
        if(estimatedCostValue.getText()!=null)
            docUrls.add(estimatedCostValue.getText().toString());
        if(collectingAmtValue.getText()!=null)
            docUrls.add(collectingAmtValue.getText().toString());
        if(storageTask!=null && storageTask.isInProgress())
            Toast.makeText(PickUp.this, "Uploading in Process... Try again!", Toast.LENGTH_SHORT).show();
        else {
            if (Common.currentRequest != null && Common.currentRequest.getPhone() != null)
                databaseReference.child(Common.currentRequest.getPhone()).setValue(docUrls);
            else
                databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(docUrls);
            Toast.makeText(this, "Verification Successful! Please start riding.", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(PickUp.this, HomeAdmin.class);
            startActivity(i);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_SELECT1 && resultCode == RESULT_OK) {
            File capImage = new File(currentImagePath);
            imageUri = Uri.fromFile(capImage);
            frontIV.setVisibility(View.VISIBLE);
            frontIV.setImageURI(imageUri);
            uploadPhoto();
        }
        if (requestCode == CAMERA_SELECT2 && resultCode == RESULT_OK) {
            File capImage = new File(currentImagePath);
            imageUri = Uri.fromFile(capImage);
            backIV.setVisibility(View.VISIBLE);
            backIV.setImageURI(imageUri);
            uploadPhoto();
        }
        if (requestCode == CAMERA_SELECT3 && resultCode == RESULT_OK) {
            File capImage = new File(currentImagePath);
            imageUri = Uri.fromFile(capImage);
            leftIV.setVisibility(View.VISIBLE);
            leftIV.setImageURI(imageUri);
            uploadPhoto();
        }
        if (requestCode == CAMERA_SELECT4 && resultCode == RESULT_OK) {
            File capImage = new File(currentImagePath);
            imageUri = Uri.fromFile(capImage);
            rightIV.setVisibility(View.VISIBLE);
            rightIV.setImageURI(imageUri);
            uploadPhoto();
        }


    }
    private void bindViews() {
        finish  = findViewById(R.id.finish);
        generateQRbtn = findViewById(R.id.generateAadhar);
        front = findViewById(R.id.front);
        back  = findViewById(R.id.back);
        left  = findViewById(R.id.left);
        right  = findViewById(R.id.right);
         frontIV  = findViewById(R.id.frontIV);
         backIV  = findViewById(R.id.backIV);
         leftIV  = findViewById(R.id.leftIV);
         rightIV  = findViewById(R.id.rightIV);
         collectingAmtValue =findViewById(R.id.collectingAmtValue);
         estimatedCostValue = findViewById(R.id.estimatedCostValue);
        issueET = findViewById(R.id.issueET);
        QRCodeImage = findViewById(R.id.QRCodeImage);
        QRCodeImageCard = findViewById(R.id.QRCodeImageCard);
    }
}
