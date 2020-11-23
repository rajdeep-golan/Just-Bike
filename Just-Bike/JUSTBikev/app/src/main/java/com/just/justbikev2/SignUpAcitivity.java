package com.just.justbikev2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.chaos.view.PinView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Model.UserModel;
import com.just.justbikev2.Remote.IGoogleServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpAcitivity extends AppCompatActivity implements View.OnClickListener {

  ImageButton signIn, getlocation;
  EditText name, email, password, secureCode, addressET;
  DatabaseReference user_table;
  ProgressDialog progressDialog;
  PinView pinView;
  TextView signInBtn;
  CircleImageView profilePic;

  ImageButton uploadPhoto;
  String phoneNumber;

  String profilePicString;
  ProgressDialog progressBar;
  Uri imageUri;

  BottomSheetDialog bt;

  StorageTask storageTask;

  StorageReference storageReference;
  String currentImagePath = null;
  public static final int UPDATE_INTERVAL = 5000;
  public static final int FASTEST_INTERVAL = 3000;
  public static final int DISPLACEMENT = 10;
  public static final int PLAY_SERVICES_REQUEST = 93;


  private LocationRequest locationRequest;
  private LocationCallback locationCallback;
  private FusedLocationProviderClient fusedLocationProviderClient;
  private Location currentlocation;


  public static final int CODE = 111;
  private static int IMAGE_PICKER = 21;
  private final static int LOCATION_REQUEST_CODE = 23;

  private static int CAMERA_SELECT = 22;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
      WindowManager.LayoutParams.FLAG_FULLSCREEN);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
    }
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sign_up_layout);

    bindVIews();

    signInBtn.setOnClickListener(this);

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    user_table = database.getReference("User");

    storageReference = FirebaseStorage.getInstance().getReference("documents");


    uploadPhoto.setOnClickListener(this);
    signIn.setOnClickListener(this);
    getlocation.setOnClickListener(this);

    Dexter.withContext(this)
      .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
      .withListener(new PermissionListener() {
        @Override
        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
          initLocation();

        }

        @Override
        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
          Toast.makeText(SignUpAcitivity.this, "You must enable this permission to use App", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

        }
      }).check();


    //Runtime permission
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
      ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
      }, LOCATION_REQUEST_CODE);
    } else {
//            if(checkPlayServices()) {
//                buildGoogleAPIClient();
//                createLocationRequest();
//            }
    }
    Intent intent = getIntent();
    if (intent != null) {
      phoneNumber = intent.getStringExtra("phone");
    }
  }

  private void initLocation() {
    buildLocationRequest();
    buildLocationCallback();
    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
  }

  private void buildLocationCallback() {
    locationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        currentlocation = locationResult.getLastLocation();
      }
    };
  }

  private void buildLocationRequest() {
    locationRequest = new LocationRequest();
    locationRequest.setInterval(UPDATE_INTERVAL);
    locationRequest.setFastestInterval(FASTEST_INTERVAL);
    locationRequest.setSmallestDisplacement(DISPLACEMENT);
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }

  private void bindVIews() {
    name = findViewById(R.id.name);
    email = findViewById(R.id.email);
    signIn = findViewById(R.id.SignUp);
    password = findViewById(R.id.password);
    secureCode = findViewById(R.id.secureCode);
    pinView = findViewById(R.id.pinView);
    signInBtn = findViewById(R.id.signInBtn);
    uploadPhoto = findViewById(R.id.uploadPhoto);
    addressET = findViewById(R.id.addressET);
    getlocation = findViewById(R.id.getlocation);
    profilePic = findViewById(R.id.profilePic);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.SignUp:
        if ((name != null && !name.getText().toString().equals("")) &&
          (email != null && !email.getText().toString().equals("")) &&
          (password != null && !password.getText().toString().equals("")) &&
          (secureCode != null && !secureCode.getText().toString().equals("")) &&
          (addressET != null && !addressET.getText().toString().equals(""))) {
          if (Common.isConnectedToInternet(getBaseContext())) {
            progressDialog = new ProgressDialog(SignUpAcitivity.this);
            progressDialog.setMessage("Please Wait . . \n creating Account");
            progressDialog.show();
            user_table.addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                //  UserModel userModel = new UserModel(name.getText().toString(), password.getText().toString(),secureCode.getText().toString());
                //        user_table.child(phoneNumber.getText().toString()).setValue(userModel);
                //       Toast.makeText(SignUpAcitivity.this, "Account Created!", Toast.LENGTH_LONG).show();
                Intent i = new Intent(SignUpAcitivity.this, UploadDocuments.class);
                i.putExtra("newName", name.getText().toString());
                i.putExtra("newEmail", email.getText().toString());
                i.putExtra("newPass", password.getText().toString());
                i.putExtra("newSecureCode", secureCode.getText().toString());
                i.putExtra("newPhone", phoneNumber);
                i.putExtra("profilePic", profilePicString);
                i.putExtra("address", addressET.getText().toString());

                startActivity(i);
                finish();
              }


              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
            });
          } else {
            Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            return;
          }
        } else
          Toast.makeText(this, "Please provide all inforamtion", Toast.LENGTH_SHORT).show();
        break;
      case R.id.signInBtn:
        Intent i = new Intent(SignUpAcitivity.this, SignInAcitivity.class);
        startActivity(i);
        finish();
        break;
      case R.id.uploadPhoto:
        if (storageTask != null && storageTask.isInProgress())
          Toast.makeText(SignUpAcitivity.this, "Uploading in Process!", Toast.LENGTH_SHORT).show();
        else {
          bt = new BottomSheetDialog(SignUpAcitivity.this, R.style.BottomSheetDialogTheme);
          View view = LayoutInflater.from(SignUpAcitivity.this).inflate(R.layout.choose_upload_options, null);
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
        break;
      case R.id.getlocation:
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
          PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
          Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          Toast.makeText(this, "Turn on Location", Toast.LENGTH_SHORT).show();
          return;
        }
        if (fusedLocationProviderClient != null && fusedLocationProviderClient.getLastLocation() != null) {
          fusedLocationProviderClient.getLastLocation()
            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
              @Override
              public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                  // Logic to handle location object
                  //String addressPay = location.getProvider();
                  currentlocation = location;
                  // addressET.setText(addressPay);
                }
              }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              Toast.makeText(SignUpAcitivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }).addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
              if (task != null && task.getResult() != null) {
                String coordiantes = new StringBuilder().append(task.getResult().getLatitude())
                  .append("/")
                  .append(task.getResult().getLongitude()).toString();
                Single<String> singleAddress = Single.just(getAddressFromLatLng(task.getResult().getLatitude(), task.getResult().getLongitude()));
                Disposable disposable = singleAddress.subscribeWith(new DisposableSingleObserver<String>() {
                  @Override
                  public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull String s) {
                    addressET.setText(s);
                  }

                  @Override
                  public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                    Toast.makeText(SignUpAcitivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                  }
                });
              }
            }
          });
        }
//                if(mlocation!=null) {
//                    mGoogleServices.getAddress(String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=false&key=AIzaSyD6qCL_yTdaA7SIS-ptw8bEXYbcslixF7U",
//                            mlocation.getLatitude(), mlocation.getLongitude())).enqueue(new Callback<JsonObject>() {
//                        @Override
//                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//
//                            try {
//                                JSONObject jsonObject = new JSONObject(response.body().toString());
//                                JSONArray resultArray = jsonObject.getJSONArray("results");
//                                JSONObject firstObj = resultArray.getJSONObject(0);
//                                String addressPay = firstObj.getString("formatted_address");
//                                addressET.setText(addressPay);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<JsonObject> call, Throwable t) {
//                            Toast.makeText(SignUpAcitivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
        break;
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if(requestCode == CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
      captureImage();
    }
  }

  private String getAddressFromLatLng(double latitude, double longitude) {

    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
    String result = "";
    try {
      List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
      if (addressList != null && addressList.size() > 0) {
        Address address = addressList.get(0);
        StringBuilder stringBuilder = new StringBuilder(address.getAddressLine(0));
        result = stringBuilder.toString();
      } else
        Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();

    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (fusedLocationProviderClient != null)
      fusedLocationProviderClient.removeLocationUpdates(locationCallback);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (fusedLocationProviderClient != null) {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return;
      }
      fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
  }

  private void checkCameraPermissions() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CODE);
    }else   //It means Permission is granted.
    {
      captureImage();
    }
  }

  private void choosePhotoFile() {
    Intent i = new Intent();
    i.setType("image/*");
    i.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(i, IMAGE_PICKER);

  }

  public void captureImage() {
    Intent camerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (camerIntent.resolveActivity(getPackageManager()) != null) {
      File imageFile = null;
      try {
        imageFile = getImageFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
      if (imageFile != null) {
        Uri imageuri = FileProvider.getUriForFile(this, "com.just.justbikev2.fileprovider", imageFile);
        camerIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
        startActivityForResult(camerIntent, CAMERA_SELECT);
      }
    }
  }

  private File getImageFile() throws IOException {
    String timestamp = new SimpleDateFormat("yyyyMMdd_hhss").format(new Date());
    String imageName = "jpg_" + timestamp + "_";
    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    File imageFile = File.createTempFile(imageName, ".jpg", storageDir);

    currentImagePath = imageFile.getAbsolutePath();
    return imageFile;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == IMAGE_PICKER && resultCode == RESULT_OK && data != null && data.getData() != null) {
      imageUri = data.getData();
      uploadPhoto();
      profilePic.setImageURI(imageUri);
  //    uploadPhoto.setVisibility(View.INVISIBLE);
      bt.dismiss();
    }

    if (requestCode == CAMERA_SELECT && resultCode == RESULT_OK) {
      File capImage = new File(currentImagePath);
      imageUri = Uri.fromFile(capImage);
      //  detectTextFromImage();        Uncomment to use Machine learning Application
      uploadPhoto();
      if(imageUri!=null)
      profilePic.setImageURI(imageUri);
    //  uploadPhoto.setVisibility(View.INVISIBLE);
      bt.dismiss();
    }
  }
int c=0;
  @Override
  public void onBackPressed() {
    Toast.makeText(this, "Hit back again to quit!", Toast.LENGTH_SHORT).show();
    c++;
    if(c>1)
    super.onBackPressed();
  }

  private void uploadPhoto() {

    if (imageUri != null && imageUri.getLastPathSegment()!=null) {
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
                //      progressBar.setVisibility(View.VISIBLE);
                progressBar.dismiss();

              }
            }, 500);
            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!urlTask.isSuccessful()) ;
            Uri downloadUrl = urlTask.getResult();
            profilePicString = downloadUrl.toString();
          }
        }
      ).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          progressBar.dismiss();
          Toast.makeText(SignUpAcitivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
      }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
          if(taskSnapshot.getTotalByteCount()!=0) {
            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            //  progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress((int) progress);
            progressBar.setMessage("Uploading " + (int) progress + "% . . .");
          }
        }
      });
    } else
      Toast.makeText(this, "No file selected! ", Toast.LENGTH_SHORT).show();
  }

}
