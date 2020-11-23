package com.just.justbikev2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.just.justbikev2.Adapter.CitySpinnerAdapter;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Model.Career;
import com.just.justbikev2.Model.FranchiseModel;

public class Franchise extends AppCompatActivity {

  ImageView uploadCV,submit;
  EditText aboutYou;
  TextView pdfName,locationTV;
  String cVUrl;


  int PDF_CODE = 12;
  int flagPdfSelected=0;
  DatabaseReference cv;
  StorageReference cvStorage;
  String location;
  FranchiseModel franchiseModel;
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
    setContentView(R.layout.activity_franchise);

    cv = FirebaseDatabase.getInstance().getReference("Franchise");
    cvStorage = FirebaseStorage.getInstance().getReference("CvFranchise");

    uploadCV = findViewById(R.id.uploadCV);
    aboutYou = findViewById(R.id.aboutYou);
    submit = findViewById(R.id.submit);
    pdfName = findViewById(R.id.pdfName);
    locationTV = findViewById(R.id.locationTV);
    uploadCV.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        selectPDF();
      }
    });
    submit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(flagPdfSelected==1 && Common.currentUser!=null &&
                           cVUrl!=null
                      && aboutYou.getText()!=null && !aboutYou.getText().toString().equals(""))
        {
          location = locationTV.getText().toString();
          franchiseModel = new FranchiseModel(location ,  Common.currentUser.getPhone(),cVUrl, aboutYou.getText().toString());
          cv.child(Common.currentUser.getPhone()+"+"+System.currentTimeMillis()).setValue(franchiseModel); // Key is applicant's phone no. + current time , so that the applicant can apply for multiple franchise requests
          Toast.makeText(Franchise.this, "Thank you. Our team will get to you shortly.", Toast.LENGTH_LONG).show();
          Intent i = new Intent(Franchise.this , Home.class);
          startActivity(i);
          finish();
        }
        else
          Toast.makeText(Franchise.this, "Please provide all information", Toast.LENGTH_SHORT).show();
      }
    });
  }

  private void selectPDF() {
    Intent i = new Intent();
    i.setType("application/pdf");
    i.setAction(i.ACTION_GET_CONTENT);
    startActivityForResult(Intent.createChooser(i,"Select PDF"),PDF_CODE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(requestCode == PDF_CODE && resultCode ==RESULT_OK && data!=null && data.getData()!=null){
      pdfName.setVisibility(View.VISIBLE);
      pdfName.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/")+1)+".pdf");

      uploadPDF(data.getData());
    }
  }

  private void uploadPDF(Uri data) {
   final ProgressDialog dialog = new ProgressDialog(this);
   dialog.setMessage("Uploading . . .");
   dialog.show();
    dialog.setCanceledOnTouchOutside(false);

   StorageReference storageRef = cvStorage.child("upload"+System.currentTimeMillis()+".pdf");
   storageRef.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
     @Override
     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
       Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
       while(!uriTask.isComplete());
       Uri uri = uriTask.getResult();
       cVUrl = uri.toString();
       flagPdfSelected = 1;
       Toast.makeText(Franchise.this, "CV uploaded successfully!", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
       }
   }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
     @Override
     public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
        double progress = (100* taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
       dialog.setMessage("Uploading "+(int)progress+"% . . .");
     }
   });
  }

}
