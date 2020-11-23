package com.just.justbikev2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.just.justbikev2.Adapter.DocumentsAdapter;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Model.DocumentsModel;
import com.just.justbikev2.Model.UserModel;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UploadDocuments extends AppCompatActivity implements View.OnClickListener  {

    public static final int CODE = 111;
    private static int IMAGE_PICKER =21;

    private static int CAMERA_SELECT = 22;
    ViewPager2 myviewpager;
    DocumentsAdapter adapter;
    LinearLayout indicatorlay;
    ArrayList<DocumentsModel> docu;
    ImageView upload;
    Button actionButton;

    String profilePic;
    Uri imageUri;

    String currentImagePath = null;

    ProgressDialog progressBar;

    StorageTask storageTask;

    Bitmap imageBitmap;

    StorageReference storageReference;
    DatabaseReference databaseReference;

     BottomSheetDialog bt;

    UserModel userModel;

    int noOfDocs = 4;
     String[] docUrls = new String[noOfDocs];
     String newName ,newEmail, newPass, newSecureCode ,newPhone,address;

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
      setContentView(R.layout.upload_documents_slider);
      initDocUrls();

        Intent i= getIntent();
        if(i!=null){
            newName = i.getStringExtra("newName");
            newEmail = i.getStringExtra("newEmail");
            newPass =  i.getStringExtra("newPass");
            newSecureCode = i.getStringExtra("newSecureCode");
            newPhone = i.getStringExtra("newPhone");
            address = i.getStringExtra("address");
            profilePic = i.getStringExtra("profilePic");
        }

        myviewpager=findViewById(R.id.myviewpager);
        indicatorlay=findViewById(R.id.lay_onbord);


        upload = findViewById(R.id.upload_doc_btn);
        actionButton=findViewById(R.id.onbordingbtn);

        storageReference = FirebaseStorage.getInstance().getReference("documents");
        databaseReference = FirebaseDatabase.getInstance().getReference("User");

        adapter=new DocumentsAdapter(getAccessoryList(),UploadDocuments.this);
        myviewpager.setAdapter(adapter);
        setupIndicator();
        setupCurrentIndicator(0);

        myviewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setupCurrentIndicator(position);
            }
        });

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myviewpager.getCurrentItem()<noOfDocs-1){
                    myviewpager.setCurrentItem(myviewpager.getCurrentItem()+1);
                }else {
                    Toast.makeText(UploadDocuments.this, "Profile Created Successfully! :D", Toast.LENGTH_LONG).show();
                  Date date = new Date(System.currentTimeMillis());
                  userModel = new UserModel(newName,newEmail,newPass,newPhone,"false",newSecureCode,address,
                                                      docUrls[0],docUrls[1],docUrls[2],docUrls[3],profilePic,Common.getDeviceName()
                                                        , String.valueOf(date));
                  //      String uploadId = databaseReference.push().getKey();      This generates a random key to store the object in
                  databaseReference.child(newPhone).setValue(userModel);
                    Common.currentUser = userModel;
                    Intent i = new Intent(UploadDocuments.this,Home.class);
                    startActivity(i);
                    finish();
                }
            }


        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(storageTask!=null && storageTask.isInProgress())
                    Toast.makeText(UploadDocuments.this, "Uploading in Process!", Toast.LENGTH_SHORT).show();
                else {
                     bt = new BottomSheetDialog(UploadDocuments.this, R.style.BottomSheetDialogTheme);
                    View view = LayoutInflater.from(UploadDocuments.this).inflate(R.layout.choose_upload_options, null);
                    view.findViewById(R.id.cameraBtn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkCameraPermissions();
                        }
                    });
                    view.findViewById(R.id.fileBtn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            choosePhotoFile();
                        }
                    });
                    bt.setContentView(view);
                    bt.show();
                }
            }
        });



    }

  private void initDocUrls() {
      for(int i=0;i<noOfDocs;i++)
        docUrls[i]="";
  }

  private void checkCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, CODE);
        }else{  //Permission is granted
          captureImage();
        }
    }



    public void detectTextFromImage(){
        imageBitmap = BitmapFactory.decodeFile(currentImagePath);
        FirebaseVisionImage vision=FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        textRecognizer.processImage(vision).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displayTextFromImage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadDocuments.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void displayTextFromImage(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.TextBlock> blocks = firebaseVisionText.getTextBlocks();
        if(blocks.size()==0)
            Toast.makeText(this, "No Text Detected!", Toast.LENGTH_SHORT).show();
        else
        {
            for(FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks()){
                String text = block.getText();
                //Set text to textView  ; Remove below line if you don't want the ML Application in this app
                docu.set(myviewpager.getCurrentItem(),new DocumentsModel(text,imageUri.toString()));
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_PICKER && resultCode == RESULT_OK && data!=null && data.getData()!=null ){
            imageUri = data.getData();
            uploadPhoto();

            switch (myviewpager.getCurrentItem()){
                case 0:
                    docu.set(myviewpager.getCurrentItem(),new DocumentsModel("3 more Steps!",imageUri.toString()));
                    break;
                case 1:
                    docu.set(myviewpager.getCurrentItem(),new DocumentsModel("2 last Step!",imageUri.toString()));
                    break;
                case 2:
                    docu.set(myviewpager.getCurrentItem(),new DocumentsModel("Last step!",imageUri.toString()));
                    break;
              case 3:
                docu.set(myviewpager.getCurrentItem(),new DocumentsModel("You're good to go!",imageUri.toString()));
                break;

            }
            adapter.notifyDataSetChanged();
            bt.dismiss();
        }

        if(requestCode == CAMERA_SELECT && resultCode == RESULT_OK ){
            File capImage  = new File(currentImagePath);
            imageUri = Uri.fromFile(capImage);
          //  detectTextFromImage();        Uncomment to use Machine learning Application
            uploadPhoto();
            switch (myviewpager.getCurrentItem()){
              case 0:
                docu.set(myviewpager.getCurrentItem(),new DocumentsModel("3 more Steps!",imageUri.toString()));
                break;
              case 1:
                docu.set(myviewpager.getCurrentItem(),new DocumentsModel("2 last Step!",imageUri.toString()));
                break;
              case 2:
                docu.set(myviewpager.getCurrentItem(),new DocumentsModel("Last step!",imageUri.toString()));
                break;
              case 3:
                docu.set(myviewpager.getCurrentItem(),new DocumentsModel("You're good to go!",imageUri.toString()));
                break;

            }
            adapter.notifyDataSetChanged();
            bt.dismiss();
        }
    }
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if(requestCode == CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
      captureImage();
    }
  }
    private void choosePhotoFile() {
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,IMAGE_PICKER);

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
                startActivityForResult(camerIntent,CAMERA_SELECT);
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

    private void uploadPhoto() {

        if(imageUri!=null){
            progressBar = new ProgressDialog(this);
            progressBar.show();
          progressBar.setCanceledOnTouchOutside(false);

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
                                    progressBar.dismiss();

                                }
                            }, 500);
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            docUrls[myviewpager.getCurrentItem()] = downloadUrl.toString();
                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.dismiss();
                    Toast.makeText(UploadDocuments.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                  //  progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress((int)progress);
                    progressBar.setMessage("Uploading "+(int)progress+"% . . .");
                }
            });
        }else
            Toast.makeText(this, "No file selected! ", Toast.LENGTH_SHORT).show();
    }

    private void setupIndicator() {
        ImageView[] indicator=new ImageView[adapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8,0,8,0);
        for (int i=0; i<indicator.length; i++){
            indicator[i]=new ImageView(getApplicationContext());
            indicator[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.indicator_inactive));
            indicator[i].setLayoutParams(layoutParams);
            indicatorlay.addView(indicator[i]);
        }

    }

    private ArrayList<DocumentsModel> getAccessoryList() {
         docu = new ArrayList<>();
        DocumentsModel model = new DocumentsModel();
         model.setImage("https://firebasestorage.googleapis.com/v0/b/just-bike-6a869.appspot.com/o/aadhar_card_vector.png?alt=media&token=e6ebac1a-adfd-4bc4-8ff5-918220dd04e0");
        model.setName("Aadhar Card Front\n( Aadhar Number/Address should be visible )");
        docu.add(model);

        model = new DocumentsModel();
        model.setImage("https://firebasestorage.googleapis.com/v0/b/just-bike-6a869.appspot.com/o/aadhar_back.png?alt=media&token=ef517229-ae2e-415d-8880-db338e67119e");
        model.setName("Aadhar Card Back\n( Address [if present] should be visible )");
        docu.add(model);

        model = new DocumentsModel();
      model.setImage("https://firebasestorage.googleapis.com/v0/b/just-bike-6a869.appspot.com/o/license.jpg?alt=media&token=d258836e-eb46-42f2-aaef-170bb82e6027");
        model.setName("Driving License Front\n( License Number should be visible )");
        docu.add(model);

      model = new DocumentsModel();
      model.setImage("https://firebasestorage.googleapis.com/v0/b/just-bike-6a869.appspot.com/o/license_back.jpg?alt=media&token=8f07babe-085e-4717-8f71-5c2acf2ed472");
      model.setName("Driving License Back\n");
      docu.add(model);
      //      model = new DocumentsModel();
//
//        model.setBikeImageSmall("https://image.freepik.com/free-vector/businessman-profile-cartoon_18591-58479.jpg");
//        model.setName("Passport Size photo\n( Can take a selfie )");
//
//        docu.add(model);

        return docu;
    }

    private void setupCurrentIndicator(int index) {

        int itemcildcount=indicatorlay.getChildCount();
        for (int i=0; i<itemcildcount; i++){
            ImageView imageView=(ImageView)indicatorlay.getChildAt(i);
            if (i==index){
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.indicator_active));
            }else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.indicator_inactive));
            }
        }

        if (index==adapter.getItemCount()-1){
            actionButton.setText("Start");

        }else {
            actionButton.setText("Next");

        }


    }

    @Override
    public void onClick(View v) {

    }
    int c=0;
  @Override
  public void onBackPressed() {
    Toast.makeText(this, "Hit back again to quit!", Toast.LENGTH_SHORT).show();
    c++;
    if(c>1)
      super.onBackPressed();
  }

}

