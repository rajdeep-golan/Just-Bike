package com.just.justbikev2.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Interface.ItemClickListener;
import com.just.justbikev2.Model.Banner;
import com.just.justbikev2.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BannerAdmin extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference category;

    RecyclerView bannerRV;
    RecyclerView.LayoutManager layoutManager;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    EditText name, id;
    Button select, upload;

    Uri uri;

    Banner item;

    FirebaseRecyclerAdapter<Banner, BannerAdminVH> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_admin);

        bannerRV = findViewById(R.id.bannerRecycler);
        layoutManager = new LinearLayoutManager(this);

        bannerRV.setLayoutManager(layoutManager);

        firebaseDatabase = FirebaseDatabase.getInstance();
        category = firebaseDatabase.getReference("Banner");

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("bannerImages/");

        loadListBanner();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }

    private void loadListBanner() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Banner")
                .limitToLast(50);

        FirebaseRecyclerOptions<Banner> options =
                new FirebaseRecyclerOptions.Builder<Banner>()
                        .setQuery(query, Banner.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Banner, BannerAdminVH>(options) {

            @Override
            protected void onBindViewHolder(@NonNull BannerAdminVH menuViewHolder, final int i, @NonNull Banner category) {

                menuViewHolder.name.setText(category.getName());
                Picasso.get().load(category.getImage()).into(menuViewHolder.image);
//                final Banner clickItem = category;
//                menuViewHolder.setItemClickListener(new ItemClickListener() {
//                    @Override
//                    public void onClick(View view, int position, boolean isLongClick) {
//                        Intent intent = new Intent(BannerAdmin.this,BikeListScreenAdmin.class);
//                        intent.putExtra("CategoryId",adapter.getRef(i).getKey());
//                        startActivity(intent);
//                    }
//                });
            }

            @NonNull
            @Override
            public BannerAdminVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.banner_card_admin, parent, false);

                return new BannerAdminVH(view);
            }
        };

        adapter.notifyDataSetChanged();
        bannerRV.setAdapter(adapter);
      //   swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals("Add"))
            showDialogAdd();
        else if (item.getTitle().equals("Update"))
            showDialogUpdate(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        else if (item.getTitle().equals("Delete"))
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
        return super.onContextItemSelected(item);
    }

    private void showDialogAdd() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(BannerAdmin.this);
        alert.setTitle("Add New Banner");
        alert.setMessage("Please fill all details!");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_bike_layout = inflater.inflate(R.layout.add_banner_card_dialog_admin, null);

        alert.setView(add_bike_layout);
        alert.setIcon(R.drawable.ic_laptop_mac_black_24dp);


        name = add_bike_layout.findViewById(R.id.bannerNameET);
        id = add_bike_layout.findViewById(R.id.bannerId);
        select = add_bike_layout.findViewById(R.id.selectBtn);
        upload = add_bike_layout.findViewById(R.id.uploadBtn);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        alert.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (item != null) {
                    category.push().setValue(item);
                }
              //  loadListBanner();
            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item = null;
            //    loadListBanner();
            }
        });
        alert.show();
    }

    private void deleteCategory(String key) {
        category.child(key).removeValue();
    }

    private void chooseImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i.createChooser(i, "Select Picture"), Common.PICK_IMAGE_REQ);
    }

    private void showDialogUpdate(final String key, final Banner item) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(BannerAdmin.this);
        alert.setTitle("Update Banner");
        alert.setMessage("Please fill all details!");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_banner_card_dialog_admin, null);

        alert.setView(add_menu_layout);
        alert.setIcon(R.drawable.ic_laptop_mac_black_24dp);


        name = add_menu_layout.findViewById(R.id.bannerNameET);
        id = add_menu_layout.findViewById(R.id.bannerId);
        select = add_menu_layout.findViewById(R.id.selectBtn);
        upload = add_menu_layout.findViewById(R.id.uploadBtn);

        name.setText(item.getName());
        id.setText(item.getId());

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateImage(item);
            }
        });
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setName(name.getText().toString());
                item.setId(id.getText().toString());

                Map<String, Object> update = new HashMap<>();
                update.put("id", item.getId());
                update.put("image", item.getImage());
                update.put("name", item.getName());

                category.child(key)
                        .updateChildren(update)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(BannerAdmin.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
             //   loadListBanner();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }
    private void updateImage(Banner banner) {
        if(uri!=null){
            final ProgressDialog mdialog = new ProgressDialog(this);
            mdialog.setMessage("Uploading ...");
            mdialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child(imageName);
            imageFolder.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mdialog.dismiss();
                    Toast.makeText(BannerAdmin.this, "Uploaded Sucessfully!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            banner.setImage(uri.toString());

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mdialog.dismiss();
                    Toast.makeText(BannerAdmin.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progess =(100.0 *taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    mdialog.setMessage("Uploaded "+(int)progess+"%");
                }
            });
        }
    }
    private void uploadImage() {
        if (uri != null) {
            final ProgressDialog mdialog = new ProgressDialog(this);
            mdialog.setMessage("Uploading ...");
            mdialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child(imageName);
            imageFolder.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mdialog.dismiss();
                    Toast.makeText(BannerAdmin.this, "Uploaded Sucessfully!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            item = new Banner();
                            item.setName(name.getText().toString());
                            item.setId(id.getText().toString());
                            item.setImage(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mdialog.dismiss();
                    Toast.makeText(BannerAdmin.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progess = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mdialog.setMessage("Uploaded " + (int) progess + "%");
                }
            });
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            select.setText("Selected");

        }
    }
}
