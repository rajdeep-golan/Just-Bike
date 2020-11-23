package com.just.justbikev2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Model.Article;
import com.just.justbikev2.Model.PhotoUpload;
import com.squareup.picasso.Picasso;

public class UserUploads extends AppCompatActivity implements View.OnClickListener {
    private static final int IMAGE_PICKER = 1;
     ImageView chooseFile,upload;
     EditText aboutPhoto,editTextTitleUpload;
     ImageView imageView;
     ProgressBar progressBar;

     Uri imageUri;

     StorageTask storageTask;

     StorageReference storageReference;
     DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_uploads);
        chooseFile = findViewById(R.id.chooseFileBtn);
        editTextTitleUpload = findViewById(R.id.editTextTitleUpload);
        upload = findViewById(R.id.uploadBtn);
        aboutPhoto = findViewById(R.id.editTextUpload);
        imageView = findViewById(R.id.imageUpload);
        progressBar = findViewById(R.id.progress_bar);

        storageReference = FirebaseStorage.getInstance().getReference("articles");
        databaseReference = FirebaseDatabase.getInstance().getReference("Article");
        chooseFile.setOnClickListener(this);
        upload.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_PICKER && resultCode == RESULT_OK && data!=null && data.getData()!=null ){
                imageUri = data.getData();
            Picasso.get().load(imageUri).placeholder(R.drawable.biker).into(imageView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chooseFileBtn:
                choosePhotoFile();
                break;
            case R.id.uploadBtn:
                if(storageTask!=null && storageTask.isInProgress())
                    Toast.makeText(this, "Uploading in Process!", Toast.LENGTH_SHORT).show();
                uploadPhoto();

                break;
//            case R.id.showFeedText:
//
//                Intent i = new Intent(UserUploads.this , PhotoUploadList.class);
//                startActivity(i);
//                break;
        }

    }

    public String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadPhoto() {

        if(imageUri!=null){
            StorageReference fileStorage = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            storageTask = fileStorage.putFile(imageUri).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 500);
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();

                            Article upload = new Article(Common.currentUser.getPhone(),editTextTitleUpload.getText().toString().trim(),aboutPhoto.getText().toString().trim(),downloadUrl.toString());

                            String uploadId = databaseReference.push().getKey();
                            databaseReference.child(uploadId).setValue(upload);
                            Intent i = new Intent(UserUploads.this,Articles.class);
                            i.putExtra("updateProfile",1);
                            Toast.makeText(UserUploads.this, "Uploaded successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(i);
                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(UserUploads.this, "Unable to upload! Please try again.", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progressBar.setProgress((int)progress);
                }
            });
        }else
            Toast.makeText(this, "No file selected! ", Toast.LENGTH_SHORT).show();
    }

    private void choosePhotoFile() {
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,IMAGE_PICKER);

    }
}
