package com.just.justbikev2.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.just.justbikev2.AccessoryListScreen;
import com.just.justbikev2.Adapter.MyArticlesViewHolder;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Model.Article;
import com.just.justbikev2.Model.UserModel;
import com.just.justbikev2.R;
import com.just.justbikev2.UserUploads;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class AccountFragment extends Fragment {

    private RecyclerView rvMyArticle;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference vehicleList;
    FirebaseRecyclerAdapter<Article, MyArticlesViewHolder> adapter;
    private boolean isViewShown = false;

    CircleImageView imgProfile;
    TextView tvName,tvDesc1,tvEmail,description,tvMyArticle;
    ImageView isVerified;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            isViewShown = true;
            // call your function
        } else {
            isViewShown = false;
        }
    }
    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        imgProfile =v.findViewById(R.id.imgProfile);
        tvName = v.findViewById(R.id.tvName);
        tvDesc1 =v.findViewById(R.id.tvDesc1);
        tvEmail = v.findViewById(R.id.tvEmail);
        isVerified = v.findViewById(R.id.isVerified);
        description = v.findViewById(R.id.tvDesc);
        tvMyArticle = v.findViewById(R.id.tvMyArticle);

        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openComplaintDialog();
            }
        });
        tvMyArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), UserUploads.class);
                startActivity(i);
            }
        });
        if(Common.currentUser!=null && Common.currentUser.getVerified()!=null && Common.currentUser.getVerified().equals("1"))
            Picasso.get().load(R.drawable.verified).into(isVerified);
        isVerified.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  openEditDialog();
                }
        });

        rvMyArticle = v.findViewById(R.id.rvMyArticle);
        rvMyArticle.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        rvMyArticle.setLayoutManager(layoutManager);

        firebaseDatabase = FirebaseDatabase.getInstance();
        vehicleList = firebaseDatabase.getReference("Article");
        if(Common.currentUser!=null) {
            Picasso.get().load(Common.currentUser.getProfilePic()).placeholder(R.drawable.biker).into(imgProfile);
            //Later Set it to About the usr
            tvDesc1.setText(Common.currentUser.getDescription());
            //Later set it to User Email
            tvEmail.setText(Common.currentUser.getEmail());
            tvName.setText(Common.currentUser.getName());
            loadArticles();
        }
        return v;
    }

    private void openComplaintDialog() {
        android.app.AlertDialog.Builder alertdialog = new android.app.AlertDialog.Builder(getContext());
        alertdialog.setTitle("Tell us about you!");

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View layout_home = inflater.inflate(R.layout.send_complaint_card,null);
        MaterialEditText etHomeAddress = layout_home.findViewById(R.id.etHomeAddress);
        etHomeAddress.setHint("Enter your description");
        TextView btn_tV = layout_home.findViewById(R.id.btn_tV);
        btn_tV.setText("Update");
        alertdialog.setView(layout_home);
        layout_home.findViewById(R.id.complaint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etHomeAddress.getText().toString().equals("")) {
                    Map<String,Object> passMap = new HashMap<>();
                    passMap.put("description",etHomeAddress.getText().toString());
                    if(Common.currentUser!=null){ DatabaseReference users = FirebaseDatabase.getInstance().getReference("User");
                        users.child(Common.currentUser.getPhone()).updateChildren(passMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //    users.orderByChild("phone").equalTo().addListenerForSingleValueEvent(new ValueEventListener() {
                                users.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.child(Common.currentUser.getPhone()).exists()) {
                                            Common.currentUser = dataSnapshot.child(Common.currentUser.getPhone()).getValue(UserModel.class);
                                        }
                                        Toast.makeText(getContext(), "Description updated successfully", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(getContext(), "Updating Failed! Try again.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else
                    Toast.makeText(getContext(), "Please enter your details!", Toast.LENGTH_SHORT).show();
            }
        });
        alertdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                tvDesc1.setText(Common.currentUser.getDescription());
                dialog.dismiss();
            }
        });
        alertdialog.show();

    }

    ImageView pic,aadharFront,adharBack,liense ,lienseBack,additionalDoc;

    private void openEditDialog() {
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(getContext(),R.style.RedDialogTheme);
        alertdialog.setTitle("Instantly Verify: 8585858586");
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View layout_home = inflater.inflate(R.layout.upload_photos_card,null);
        ImageButton uploadBtn,uploadAadhar,uploadAadharBack,uploadLicense,uploadLicenseBack,uploadAdditionalDoc;
        pic = layout_home.findViewById(R.id.pic);
        aadharFront = layout_home.findViewById(R.id.aadharFront);
        adharBack = layout_home.findViewById(R.id.adharBack);
        liense = layout_home.findViewById(R.id.liense);
        lienseBack = layout_home.findViewById(R.id.lienseBack);
        uploadBtn = layout_home.findViewById(R.id.uploadBtn);
        uploadAadhar = layout_home.findViewById(R.id.uploadAadhar);
        uploadAadharBack = layout_home.findViewById(R.id.uploadAadharBack);
        uploadLicense = layout_home.findViewById(R.id.uploadLicense);
        uploadLicenseBack = layout_home.findViewById(R.id.uploadLicenseBack);
      uploadAdditionalDoc = layout_home.findViewById(R.id.uploadAdditionalDoc);
        storageReference = FirebaseStorage.getInstance().getReference("documents");
        databaseReference = FirebaseDatabase.getInstance().getReference("User");


        if(Common.currentUser!=null) {
          if(Common.currentUser.getProfilePic()!=null && !Common.currentUser.getProfilePic().equals(""))
            Picasso.get().load(Common.currentUser.getProfilePic()).placeholder(R.drawable.biker).into(pic);
          if(Common.currentUser.getAadhar()!=null && !Common.currentUser.getAadhar().equals(""))
            Picasso.get().load(Common.currentUser.getAadhar()).placeholder(R.drawable.aadhar_card_vector).into(aadharFront);
          if(Common.currentUser.getAadharBack()!=null && !Common.currentUser.getAadharBack().equals(""))
            Picasso.get().load(Common.currentUser.getAadharBack()).placeholder(R.drawable.aadhar_back).into(adharBack);
          if(Common.currentUser.getLicense()!=null && !Common.currentUser.getLicense().equals(""))
            Picasso.get().load(Common.currentUser.getLicense()).placeholder(R.drawable.license).into(liense);
          if(Common.currentUser.getLicesneBack()!=null && !Common.currentUser.getLicesneBack().equals(""))
            Picasso.get().load(Common.currentUser.getLicesneBack()).placeholder(R.drawable.license_back).into(lienseBack);
          if(Common.currentUser.getAdditionalDoc()!=null && !Common.currentUser.getAdditionalDoc().equals(""))
            Picasso.get().load(Common.currentUser.getAdditionalDoc()).placeholder(R.drawable.bike_assasin).into(additionalDoc);

        }
      if(Common.currentUser!=null && Common.currentUser.getVerified()!=null && Common.currentUser.getVerified().equals("0"))
      {
        uploadBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            flag = 1;
            initUpload();
          }
        });
        uploadAadhar.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            flag = 2;
            initUpload();
          }
        });
        uploadAadharBack.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            flag = 3;
            initUpload();
          }
        });
        uploadLicense.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            flag = 4;
            initUpload();
          }
        });
        uploadLicenseBack.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            flag = 5;
            initUpload();
          }
        });
        uploadAdditionalDoc.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            flag = 6;
            initUpload();
          }
        });
      }
        alertdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
          @Override
          public void onDismiss(DialogInterface dialog) {
            Picasso.get().load(Common.currentUser.getProfilePic()).placeholder(R.drawable.biker).into(imgProfile);
            dialog.dismiss();
          }
        });

        alertdialog.setView(layout_home);
        alertdialog.show();
    }
    int flag = 0;
    BottomSheetDialog bt;
    private static int IMAGE_PICKER =21;
    private static int CAMERA_SELECT = 22;
    String currentImagePath = null;

    private void initUpload() {
        bt = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.choose_upload_options, null);
        view.findViewById(R.id.cameraBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(getContext()).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        captureImage();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(getContext(), "Please allow Camera!", Toast.LENGTH_SHORT).show();
                        checkCameraPermissions();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
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
    public static final int CODE = 111;

    private void checkCameraPermissions() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CODE);
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
        if(camerIntent.resolveActivity(getContext().getPackageManager())!=null){
            File imageFile = null;
            try {
                imageFile = getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(imageFile!=null){
                Uri imageuri = FileProvider.getUriForFile(getContext(),"com.just.justbikev2.fileprovider",imageFile);
                camerIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageuri);
                startActivityForResult(camerIntent,CAMERA_SELECT);
            }
        }
    }
    private File getImageFile() throws IOException  {
        String timestamp = new SimpleDateFormat("yyyyMMdd_hhss").format(new Date());
        String imageName = "jpg_"+timestamp+"_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageName,".jpg",storageDir);

        currentImagePath = imageFile.getAbsolutePath();
        return  imageFile;
    }
    Uri imageUri;


    ProgressDialog progressBar;

    StorageReference storageReference;
    DatabaseReference databaseReference;
    StorageTask storageTask;
    private void uploadPhoto() {

        if(imageUri!=null){
            progressBar = new ProgressDialog(getContext());
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
                                    progressBar.dismiss();
                                }
                            }, 500);
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            Map<String,Object> passMap = new HashMap<>();

                            if(flag == 1 && downloadUrl!=null)
                                passMap.put("profilePic",downloadUrl.toString());
                            if(flag == 2 && downloadUrl!=null)
                                passMap.put("aadhar",downloadUrl.toString());
                            if(flag == 3 && downloadUrl!=null)
                                passMap.put("aadharBack",downloadUrl.toString());
                            if(flag == 4 && downloadUrl!=null)
                                passMap.put("license",downloadUrl.toString());
                            if(flag == 5 && downloadUrl!=null)
                                passMap.put("licesneBack",downloadUrl.toString());
                          if(flag == 6 && downloadUrl!=null)
                            passMap.put("additionalDoc",downloadUrl.toString());

                          //      userModel = new UserModel(newName,newPass,newPhone,"false",newSecureCode,address,docUrls[0],docUrls[1],docUrls[2],profilePic);
                               if(Common.currentUser!=null){ DatabaseReference users = FirebaseDatabase.getInstance().getReference("User");
                                users.child(Common.currentUser.getPhone()).updateChildren(passMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    //    users.orderByChild("phone").equalTo().addListenerForSingleValueEvent(new ValueEventListener() {
                                          users.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.child(Common.currentUser.getPhone()).exists()) {
                                                        Common.currentUser = dataSnapshot.child(Common.currentUser.getPhone()).getValue(UserModel.class);
                                                    }
                                                Toast.makeText(getContext(), "Image uploaded Successfully", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Toast.makeText(getContext(), "Updating Failed! Try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.dismiss();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progressBar.setProgress((int)progress);
                    progressBar.setMessage("Uploading "+(int)progress+"% . . .");
                }
            });
        }else
            Toast.makeText(getContext(), "No file selected! ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_PICKER && resultCode == Activity.RESULT_OK && data!=null && data.getData()!=null ){
            imageUri = data.getData();
            if(flag == 1)
                Picasso.get().load(imageUri).placeholder(R.drawable.biker).into(pic);
            if(flag == 2)
                Picasso.get().load(imageUri).placeholder(R.drawable.aadhar_card_vector).into(aadharFront);
            if(flag == 3)
                Picasso.get().load(imageUri).placeholder(R.drawable.aadhar_back).into(adharBack);
            if(flag == 4)
                Picasso.get().load(imageUri).placeholder(R.drawable.license).into(liense);
            if(flag == 5)
                Picasso.get().load(imageUri).placeholder(R.drawable.license_back).into(lienseBack);
          if(flag == 6)
            Picasso.get().load(imageUri).placeholder(R.drawable.license_back).into(additionalDoc);
            uploadPhoto();
            bt.dismiss();
        }

        if(requestCode == CAMERA_SELECT && resultCode == Activity.RESULT_OK ){
            File capImage  = new File(currentImagePath);
            imageUri = Uri.fromFile(capImage);
            if(flag == 1)
               Picasso.get().load(imageUri).placeholder(R.drawable.biker).into(pic);
            if(flag == 2)
                Picasso.get().load(imageUri).placeholder(R.drawable.aadhar_card_vector).into(aadharFront);
            if(flag == 3)
                Picasso.get().load(imageUri).placeholder(R.drawable.aadhar_back).into(adharBack);
            if(flag == 4)
                Picasso.get().load(imageUri).placeholder(R.drawable.license).into(liense);
            if(flag == 5)
                Picasso.get().load(imageUri).placeholder(R.drawable.license_back).into(lienseBack);
          if(flag == 6)
            Picasso.get().load(imageUri).placeholder(R.drawable.license_back).into(additionalDoc);
          uploadPhoto();
            bt.dismiss();
        }
    }

    private void loadArticles() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Article")
                .orderByChild("userId").equalTo(Common.currentUser.getPhone())
                .limitToLast(50);

        FirebaseRecyclerOptions<Article> options =
                new FirebaseRecyclerOptions.Builder<Article>()
                        .setQuery(query, Article.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Article, MyArticlesViewHolder>(options) {

            @NonNull
            @Override
            public MyArticlesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_my_articles, parent, false);
                return new MyArticlesViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MyArticlesViewHolder holder, int position, @NonNull Article model) {
                holder.tvTitle.setText(model.getTitle());
                Picasso.get().load(model.getImage()).fit().into(holder.imgArticle);
            }
        };

        adapter.notifyDataSetChanged();
        rvMyArticle.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

}
