package com.just.justbikev2.Admin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.andremion.counterfab.CounterFab;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Interface.ItemClickListener;
import com.just.justbikev2.Model.Category;
import com.just.justbikev2.Model.Token;
import com.just.justbikev2.R;
import com.just.justbikev2.SignInAcitivity;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class HomeAdmin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference category;
    TextView fullName;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, MenuViewHolderAdmin> adapter;
    DrawerLayout drawer;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    CardView chooseTiming;
    Button viewArticles;

    EditText name ;
    Button select , upload;

    Category newCategory;

    SwipeRefreshLayout swipeRefreshLayout;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Menu Management");

        setSupportActionBar(toolbar);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.orange_btn,
                R.color.com_facebook_blue,
                R.color.green);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    loadMenu();
                    adapter.startListening();

                }
                else{
                    Toast.makeText(getBaseContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("uploadImages/");
        CounterFab fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               enterDetails();
               // Intent i = new Intent(HomeAdmin.this, CreateCoupon.class);
               // startActivity(i);
            }
        });

        viewArticles = findViewById(R.id.viewArticles);
        chooseTiming = findViewById(R.id.chooseTiming);
        chooseTiming.setVisibility(View.GONE);
        viewArticles.setVisibility(View.GONE);
        drawer = findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(new Integer(0));

        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Setting Name for user
        View headerView = navigationView.getHeaderView(0);
        fullName = headerView.findViewById(R.id.phone);
        if(Common.currentUser!=null && !Common.currentUser.getName().isEmpty())
          fullName.setText(Common.currentUser.getName());

        //Loading menu
        recyclerView = findViewById(R.id.recyclerViewMenuHome);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
       // recyclerView.setHasFixedSize(true);

        loadMenu();
        //Loading intent for notification of order : Admin side

        updateToken(FirebaseInstanceId.getInstance().getToken());
//        Intent serivce = new Intent(HomeAdmin.this, ListenOrderAdmin.class);
//        startService(serivce);
    }

    private void updateToken(String tokenRefresh) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference tokens = firebaseDatabase.getReference("Tokens");
        Token token = new Token(tokenRefresh,true);
        if(Common.currentUser!=null)
            tokens.child(Common.currentUser.getPhone()).setValue(token);
    }

    private void enterDetails() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(HomeAdmin.this);
        alert.setTitle("Add New Bike");
        alert.setMessage("Please fill all details!");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_item_admin_cart , null);

        alert.setView(add_menu_layout);
        alert.setIcon(R.drawable.package_image);


        name = add_menu_layout.findViewById(R.id.enterNameET);
        select = add_menu_layout.findViewById(R.id.selectBtn);
        upload = add_menu_layout.findViewById(R.id.uploadBtn);

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
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(newCategory!=null){
                    category.push().setValue(newCategory);
                    Snackbar.make(drawer , "New Category "+newCategory.getName()+" was added",Snackbar.LENGTH_SHORT).show();
                }
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

    private void uploadImage() {
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
                    Toast.makeText(HomeAdmin.this, "Uploaded Sucessfully!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newCategory = new Category(uri.toString(),name.getText().toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mdialog.dismiss();
                    Toast.makeText(HomeAdmin.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void chooseImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i.createChooser(i,"Select Picture"),Common.PICK_IMAGE_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.PICK_IMAGE_REQ && resultCode == RESULT_OK && data!=null && data.getData()!=null)
        {
            uri = data.getData();
            select.setText("Selected");

        }
    }

    private void loadMenu() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Category")
                .limitToLast(50);

        FirebaseRecyclerOptions<Category> options =
                new FirebaseRecyclerOptions.Builder<Category>()
                        .setQuery(query, Category.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolderAdmin>(options) {

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolderAdmin menuViewHolder, final int i, @NonNull Category category) {

                menuViewHolder.name.setText(category.getName());
                Picasso.get().load(category.getImage()).into(menuViewHolder.image);
                final Category clickItem = category;
                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(HomeAdmin.this,BikeListScreenAdmin.class);
                        intent.putExtra("CategoryId",adapter.getRef(i).getKey());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public MenuViewHolderAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);

                return new MenuViewHolderAdmin(view);
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Clicked on Settings", Toast.LENGTH_SHORT).show();
            return true;
        } else if(id == R.id.action_refresh){
            loadMenu();
           adapter.startListening();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals("Update"))
            showDialogUpdate(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        else if(item.getTitle().equals("Delete"))
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
        return super.onContextItemSelected(item);
    }

    private void deleteCategory(String key) {
        DatabaseReference bikes = database.getReference("Vehicles");
        Query bikeInCategory = bikes.orderByChild("parId").equalTo(key);
        bikeInCategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postdata : dataSnapshot.getChildren())
                    postdata.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        category.child(key).removeValue();
        Snackbar.make(drawer,"Category Deleted!",Snackbar.LENGTH_SHORT).show();
    }

    private void showDialogUpdate(final String key, final Category item) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(HomeAdmin.this);
        alert.setTitle("Update Category");
        alert.setMessage("Please fill all details!");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_item_admin_cart , null);

        alert.setView(add_menu_layout);
        alert.setIcon(R.drawable.package_image);


        name = add_menu_layout.findViewById(R.id.enterNameET);
        select = add_menu_layout.findViewById(R.id.selectBtn);
        upload = add_menu_layout.findViewById(R.id.uploadBtn);

        name.setText(item.getName());

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
                category.child(key).setValue(item);
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
    private void updateImage(final Category item) {
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
                    Toast.makeText(HomeAdmin.this, "Uploaded Sucessfully!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            item.setImage(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mdialog.dismiss();
                    Toast.makeText(HomeAdmin.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_banner) {
            Intent i=new Intent(HomeAdmin.this,BannerAdmin.class);
            startActivity(i);
        } else if (id == R.id.nav_gallery) {
            Intent i=new Intent(HomeAdmin.this,Delivery.class);
            startActivity(i);

        } else if (id == R.id.orders) {
          Intent orderList = new Intent(HomeAdmin.this, ViewOrdersAdmin.class);
          startActivity(orderList);
        }
//         else if (id == R.id.nav_cart) {
//            Intent cartView = new Intent(HomeAdmin.this , PickUp.class);
//            startActivity(cartView);
//
//        }
        else if (id == R.id.logout) {
            Intent sinIn = new Intent(HomeAdmin.this , SignInAcitivity.class);
            sinIn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(sinIn);

        } else if (id == R.id.nav_send) {
            Intent messagesIntent = new Intent(this ,ContactUsAdmin.class);
            startActivity(messagesIntent);


        }
        else if (id == R.id.nav_franchise) {
          Intent messagesIntent = new Intent(this ,UsersList.class);
          startActivity(messagesIntent);


        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();
       if(adapter!=null) adapter.startListening();
    }
}
