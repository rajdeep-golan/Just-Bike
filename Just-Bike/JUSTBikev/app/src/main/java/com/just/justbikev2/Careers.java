package com.just.justbikev2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.pdf417.encoder.PDF417;
import com.just.justbikev2.Adapter.CitySpinnerAdapter;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Model.Career;

public class Careers extends AppCompatActivity  implements AdapterView.OnItemSelectedListener{
  Spinner citySpinner;
  ImageView uploadCV,submit;
  EditText aboutYou;
  TextView pdfName;
  String cVUrl;
  String[] jobNames = {"Delivery Ranger", "Accountant", "Sales & Marketing", "Software Developer",
                          "Mechanical Engineer","Electronics Engineer"};
  int flags[] = {R.drawable.delivery_boy_vector, R.drawable.calculator_vector, R.drawable.sales, R.drawable.software
    , R.drawable.mechanical, R.drawable.electronic};
  int citySpinnerPosition;

  int PDF_CODE = 12;
  int flagPdfSelected=0;
  DatabaseReference cv;
  StorageReference cvStorage;
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
    setContentView(R.layout.activity_careers);

    cv = FirebaseDatabase.getInstance().getReference("Career");
    cvStorage = FirebaseStorage.getInstance().getReference("CV");

    uploadCV = findViewById(R.id.uploadCV);
    aboutYou = findViewById(R.id.aboutYou);
    submit = findViewById(R.id.submit);
    pdfName = findViewById(R.id.pdfName);
    citySpinner = findViewById(R.id.roleSpinner);
    CitySpinnerAdapter customAdapter = new CitySpinnerAdapter(getApplicationContext(), flags, jobNames);
    citySpinner.setAdapter(customAdapter);
    citySpinnerPosition = 0;

    citySpinner.setOnItemSelectedListener(this);
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
          Career career = new Career(cVUrl , Common.currentUser.getPhone(),jobNames[citySpinnerPosition] , aboutYou.getText().toString());
         cv.child(Common.currentUser.getPhone()+"+"+System.currentTimeMillis()).setValue(career); // Key is applicant's phone no. + current time , so that the applicant can apply for multiple positions
          Toast.makeText(Careers.this, "Thank you. Our team will get to you shortly.", Toast.LENGTH_LONG).show();
          Intent i = new Intent(Careers.this , Home.class);
          startActivity(i);
          finish();
        }
        else
          Toast.makeText(Careers.this, "Please provide all information", Toast.LENGTH_SHORT).show();
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

   StorageReference storageRef = cvStorage.child("upload"+System.currentTimeMillis()+".pdf");
   storageRef.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
     @Override
     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
       Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
       while(!uriTask.isComplete());
       Uri uri = uriTask.getResult();
       cVUrl = uri.toString();
       flagPdfSelected = 1;
       Toast.makeText(Careers.this, "CV uploaded successfully!", Toast.LENGTH_SHORT).show();
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

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    citySpinnerPosition = position;
  }


  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    citySpinner.setSelection(0);
    citySpinnerPosition = 0;
  }
}
