package com.just.justbikev2.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Helper.AadhaarXMLParser;
import com.just.justbikev2.Model.AadharCard;
import com.just.justbikev2.Model.Request;
import com.just.justbikev2.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Delivery extends AppCompatActivity implements   AdapterView.OnItemSelectedListener {

    ImageButton finish ,scanAdharbtn , custonbikeBtn , aLFront , aLBack , front , back , left , right , letter , QR ,SpeedoMeterBtn;
    ImageView   custonbikeIV , aLFrontIV , aLBackIV , frontIV , backIV , leftIV , rightIV , letterIV ,SpeedoMeter ;
    TextView scanAdharTV, QRTV;
    Spinner paymentSpinner;
    TextView outstandingTV;

    private final static int CAMERA_CODE = 42;
    public static final int CODE = 111;
    private static int CAMERA_SELECT1 = 1;
    private static int CAMERA_SELECT2 = 2;
    private static int CAMERA_SELECT3 = 3;
    private static int CAMERA_SELECT4 = 4;
    private static int CAMERA_SELECT5 = 5;
    private static int CAMERA_SELECT6 = 6;
    private static int CAMERA_SELECT7 = 7;
    private static int CAMERA_SELECT8 = 8;
  private static int CAMERA_SELECT10 = 10;

    String currentImagePath = null;

    StorageTask storageTask;
    Uri imageUri;
    ProgressDialog progressBar;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    DatabaseReference requestReference;

    IntentResult intentResult;

    int flag;

    ArrayList<String> docUrls = new ArrayList<>();

    Intent intent;

    String outstandingAmt;
    String orderId;

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
        setContentView(R.layout.activity_delivery);
        bindViews();

        intent = getIntent();
        if(intent!=null){
            outstandingAmt = intent.getStringExtra("outstandingAmt");
            orderId = intent.getStringExtra("orderId");
        }


        storageReference = FirebaseStorage.getInstance().getReference("delivery");
        databaseReference = FirebaseDatabase.getInstance().getReference("Delivery");
        requestReference = FirebaseDatabase.getInstance().getReference("Requests");

        setOnClicks();

        //Get Outstanding Amount from intent and set in
       //  outstandingTV.setText(Common.currentRequest.getOutstandingAmt());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.payment_method, android.R.layout.simple_spinner_item);


        paymentSpinner.setOnItemSelectedListener(this);
        paymentSpinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Set Current Request payment to selected
        String selected = parent.getItemAtPosition(position).toString();
        Request request = Common.currentRequest;
        if(request!=null) {
            request.setPaymentMethod(selected);
            request.setOutstandingAmt("0");
                requestReference.child(orderId).setValue(request);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setOnClicks() {
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDelivery();
            }
        });
        scanAdharbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag =0;
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!hasPermissions())
                        requestPermissions();
                    else
                        openScanner();
                } else
                    openScanner();
            }
        });
        SpeedoMeterBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            flag =10;
            startCamera();
          }
        });
        custonbikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag =1;
                startCamera();
            }
        });
        aLFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag =2;
                startCamera();
            }
        });
        aLBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag =3;
                startCamera();
            }
        });
        front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag =4;
                startCamera();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag =5;
                startCamera();
            }
        });
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag =6;
                startCamera();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag =7;
                startCamera();
            }
        });
        letter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag =8;
                startCamera();
            }
        });
        QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag =9;
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!hasPermissions())
                        requestPermissions();
                    else
                        openScanner();
                } else
                    openScanner();
            }
        });
    }

    private void startCamera() {
        if(storageTask!=null && storageTask.isInProgress())
            Toast.makeText(Delivery.this, "Uploading in Process!", Toast.LENGTH_SHORT).show();
        else
          checkCameraPermissions();
    }

    private void checkCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, CODE);
        }else{  //Permission is granted
          captureImage();
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
                    case 5:
                        startActivityForResult(camerIntent,CAMERA_SELECT5);
                        break;
                    case 6:
                        startActivityForResult(camerIntent,CAMERA_SELECT6);
                        break;
                    case 7:
                        startActivityForResult(camerIntent,CAMERA_SELECT7);
                        break;
                    case 8:
                        startActivityForResult(camerIntent,CAMERA_SELECT8);
                        break;
                  case 10:
                    startActivityForResult(camerIntent,CAMERA_SELECT10);
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
    private boolean hasPermissions() {
        int result = ContextCompat.checkSelfPermission(Delivery.this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;

    }

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Delivery.this, Manifest.permission.CAMERA)) {

        } else
            ActivityCompat.requestPermissions(Delivery.this, new String[]{Manifest.permission.CAMERA}, CAMERA_CODE);

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
        new IntentIntegrator(Delivery.this).initiateScan();
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
                            if(Common.currentUser!=null && Common.currentRequest!=null)
                              sendMessage(Common.currentUser.getPhone(),Common.currentRequest.getPhone(),downloadUrl.toString());
                            docUrls.add(downloadUrl.toString());
                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.dismiss();
                    Toast.makeText(Delivery.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        if(storageTask!=null && storageTask.isInProgress())
            Toast.makeText(Delivery.this, "Uploading in Process... Try again!", Toast.LENGTH_SHORT).show();
        else {
            if (Common.currentRequest != null && Common.currentRequest.getPhone() != null)
                databaseReference.child(Common.currentRequest.getPhone()).setValue(docUrls);
            else
                databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(docUrls);
            Toast.makeText(this, "Successfully Completed!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Delivery.this,HomeAdmin.class);
            startActivity(i);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_SELECT1 && resultCode == RESULT_OK) {
            File capImage = new File(currentImagePath);
            imageUri = Uri.fromFile(capImage);
            custonbikeIV.setVisibility(View.VISIBLE);
            custonbikeIV.setImageURI(imageUri);
            uploadPhoto();
        }
        if (requestCode == CAMERA_SELECT2 && resultCode == RESULT_OK) {
            File capImage = new File(currentImagePath);
            imageUri = Uri.fromFile(capImage);
            aLFrontIV.setVisibility(View.VISIBLE);
            aLFrontIV.setImageURI(imageUri);
            uploadPhoto();
        }
        if (requestCode == CAMERA_SELECT3 && resultCode == RESULT_OK) {
            File capImage = new File(currentImagePath);
            imageUri = Uri.fromFile(capImage);
            aLBackIV.setVisibility(View.VISIBLE);
            aLBackIV.setImageURI(imageUri);
            uploadPhoto();
        }
        if (requestCode == CAMERA_SELECT4 && resultCode == RESULT_OK) {
            File capImage = new File(currentImagePath);
            imageUri = Uri.fromFile(capImage);
            frontIV.setVisibility(View.VISIBLE);
            frontIV.setImageURI(imageUri);
            uploadPhoto();
        }
        if (requestCode == CAMERA_SELECT5 && resultCode == RESULT_OK) {
            File capImage = new File(currentImagePath);
            imageUri = Uri.fromFile(capImage);
            backIV.setVisibility(View.VISIBLE);
            backIV.setImageURI(imageUri);
            uploadPhoto();
        }
        if (requestCode == CAMERA_SELECT6 && resultCode == RESULT_OK) {
            File capImage = new File(currentImagePath);
            imageUri = Uri.fromFile(capImage);
            leftIV.setVisibility(View.VISIBLE);
            leftIV.setImageURI(imageUri);
            uploadPhoto();
        }
        if (requestCode == CAMERA_SELECT7 && resultCode == RESULT_OK) {
            File capImage = new File(currentImagePath);
            imageUri = Uri.fromFile(capImage);
            rightIV.setVisibility(View.VISIBLE);
            rightIV.setImageURI(imageUri);
            uploadPhoto();
        }
        if (requestCode == CAMERA_SELECT8 && resultCode == RESULT_OK) {
            File capImage = new File(currentImagePath);
            imageUri = Uri.fromFile(capImage);
            letterIV.setVisibility(View.VISIBLE);
            letterIV.setImageURI(imageUri);
            uploadPhoto();
        }if (requestCode == CAMERA_SELECT10 && resultCode == RESULT_OK) {
        File capImage = new File(currentImagePath);
        imageUri = Uri.fromFile(capImage);
        SpeedoMeter.setVisibility(View.VISIBLE);
        SpeedoMeter.setImageURI(imageUri);
        uploadPhoto();
      }
        //For Aadhar (flag = 0 ) and  QR (flag = 9 )Scanner
        intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null)
                Toast.makeText(this, "---Blank---", Toast.LENGTH_SHORT).show();
            else {
                if(flag==0) {
                    getAadharDetails();
                    scanAdharTV.setVisibility(View.VISIBLE);
                }else if(flag==9){
                    QRTV.setVisibility(View.VISIBLE);
                    QRTV.setText(intentResult.getContents());
                }
            }
        }
    }
    public void getAadharDetails() {

        AadharCard newCard = null;
        try {
            newCard = new AadhaarXMLParser().parse(intentResult.getContents());

            String adharCardDetails = "Address : \n"+newCard.getAddress()+
                                    "\nName : \n" +newCard.name+
                                     "\nDOB : \n" +newCard.dob  ;
            scanAdharTV.setText(adharCardDetails);


        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
}
    private void bindViews() {

        finish = findViewById(R.id.finish);

         scanAdharbtn = findViewById(R.id.scanAadhar);
         custonbikeBtn  = findViewById(R.id.custOnBike);
         aLFront = findViewById(R.id.uploadAdhar);
         aLBack  = findViewById(R.id.uploadLicense);
         front  = findViewById(R.id.front);
         back  = findViewById(R.id.back);
         left  = findViewById(R.id.left);
         right  = findViewById(R.id.right);
         letter  = findViewById(R.id.letterSign);
         QR  = findViewById(R.id.scanQr);
           custonbikeIV  = findViewById(R.id.custOnBikeImage);
           aLFrontIV  = findViewById(R.id.ALFront);
           aLBackIV  = findViewById(R.id.ALBack);
           frontIV  = findViewById(R.id.frontIV);
           backIV  = findViewById(R.id.backIV);
           leftIV  = findViewById(R.id.leftIV);
           rightIV  = findViewById(R.id.rightIV);
           letterIV   = findViewById(R.id.letterSignIV);
         scanAdharTV = findViewById(R.id.aadaharDetailsTV);
         QRTV = findViewById(R.id.QRCodeDetails);
         paymentSpinner = findViewById(R.id.paymentSpinner);
         outstandingTV = findViewById(R.id.outstandingCostValue);
      SpeedoMeterBtn = findViewById(R.id.SpeedoMeterBtn);
      SpeedoMeter = findViewById(R.id.SpeedoMeter);
    }
}
