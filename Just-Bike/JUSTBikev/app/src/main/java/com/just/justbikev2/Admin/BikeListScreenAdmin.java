package com.just.justbikev2.Admin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.just.justbikev2.BikeDetailsActivity;
import com.just.justbikev2.Adapter.BikeListAdapter;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Interface.ItemClickListener;
import com.just.justbikev2.Model.Vehicle;
import com.just.justbikev2.R;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class BikeListScreenAdmin extends AppCompatActivity implements View.OnClickListener,BikeListAdapter.OnClickListener{
    RecyclerView bikeListRecyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference vehicleList;
    FirebaseRecyclerAdapter<Vehicle, VehicleViewHolderAdmin> adapter;

    String categoryId;

    Vehicle vehicleCLicked;

    EditText name ,colour, cc , cost , mileage, description , year , weight ;
    Button select , upload;
    Vehicle newVehicle;

    //Search functionality
    FirebaseRecyclerAdapter<Vehicle, VehicleViewHolderAdmin> searchAdapter;
    List<String> suggestedSearch = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    RelativeLayout relativeLayout ;

    Uri uri;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bike_list_recycler);
        swipeRefreshLayout = findViewById(R.id.RVBikeList);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.orange_btn,
                R.color.com_facebook_blue,
                R.color.green);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    loadVehicles();
                    adapter.startListening();
                }
                else{
                    Toast.makeText(getBaseContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        if(getIntent()!=null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if(categoryId!=null && !categoryId.isEmpty()) {

            firebaseDatabase = FirebaseDatabase.getInstance();
            vehicleList = firebaseDatabase.getReference("Vehicles");
            bikeListRecyclerView = findViewById(R.id.bikeRecycler);

            layoutManager = new LinearLayoutManager(this);
            bikeListRecyclerView.setLayoutManager(layoutManager);
            bikeListRecyclerView.setHasFixedSize(true);

            relativeLayout = findViewById(R.id.RelativeLayoutBikeList);

            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference("vehicles/");

            loadVehicles();

            //Searching
            materialSearchBar = findViewById(R.id.serachBar);
            materialSearchBar.setHint("Search...");
            loadSuggestList();
            materialSearchBar.setLastSuggestions(suggestedSearch);
            materialSearchBar.setCardViewElevation(10);
            materialSearchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    List<String> suggest = new ArrayList<>();
                    for(String search : suggestedSearch){
                            if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                                suggest.add(search);
                    }
                    materialSearchBar.setLastSuggestions(suggest);
                    if(!materialSearchBar.getText().isEmpty() || !materialSearchBar.getText().equals("")) {
                        showSearchItems(s.toString());
                        searchAdapter.startListening();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {
                    //When searchBar is closed , restore to original data
                    if(!enabled)
                        bikeListRecyclerView.setAdapter(adapter);
                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    //When search finished , show resultant searched items
                    showSearchItems(text);
                    searchAdapter.startListening();
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });



        }
    }

    private void showSearchItems(CharSequence text) {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Vehicles")
                .orderByChild("name").equalTo(text.toString())
                .limitToLast(50);

        FirebaseRecyclerOptions<Vehicle> options =
                new FirebaseRecyclerOptions.Builder<Vehicle>()
                        .setQuery(query, Vehicle.class)
                        .build();
        searchAdapter = new FirebaseRecyclerAdapter<Vehicle, VehicleViewHolderAdmin>(options) {
            @Override
            protected void onBindViewHolder(@NonNull VehicleViewHolderAdmin holder, int position, @NonNull Vehicle model) {
                holder.bikeName.setText(model.getName());
                Picasso.get().load(model.getImage1()).into(holder.bikeImage);
                BlurTransformation transformation1 = new BlurTransformation(getBaseContext(),25,1);
                Picasso.get().load(model.getImage1()).transform(transformation1).into(holder.bikeImageBlur);
                holder.ccValue.setText(model.getCc()+"");
                holder.mileageValue.setText(model.getMileage()+"");
                holder.colour.setText(model.getColour());
                holder.costValue.setText(model.getCost()+"");
                holder.weightValue.setText(model.getWeight()+"");
                holder.yearValue.setText(model.getYear()+"");
                final Vehicle clickItem = model;
                vehicleCLicked = clickItem;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(BikeListScreenAdmin.this,BikeDetailsActivity.class);
                        intent.putExtra("BikeId", searchAdapter.getRef(position).getKey() );
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public VehicleViewHolderAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.bike_card_view, parent, false);
                return new VehicleViewHolderAdmin(view);
            }
        };
        searchAdapter.notifyDataSetChanged();
        bikeListRecyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggestList() {
        vehicleList.orderByChild("parId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    Vehicle lastSearchVehicle = data.getValue(Vehicle.class);
                    suggestedSearch.add(lastSearchVehicle.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadVehicles() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Vehicles")
                .orderByChild("parId").equalTo(categoryId)
                .limitToLast(50);

        FirebaseRecyclerOptions<Vehicle> options =
                new FirebaseRecyclerOptions.Builder<Vehicle>()
                        .setQuery(query, Vehicle.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Vehicle, VehicleViewHolderAdmin>(options) {
            @Override
            protected void onBindViewHolder(@NonNull VehicleViewHolderAdmin holder, int position, @NonNull Vehicle model) {
                holder.bikeName.setText(model.getName());
                Picasso.get().load(model.getImage1()).transform(new RoundedCornersTransformation(50,0)).into(holder.bikeImage);
                BlurTransformation transformation1 = new BlurTransformation(getBaseContext(),25,3);
                Picasso.get().load(model.getImage1()).transform(new RoundedCornersTransformation(50,0)).transform(transformation1).into(holder.bikeImageBlur);
                int opacity = 153;
                holder.bikeImageBlur.setBackgroundColor(opacity * 0x1000000);
                holder.ccValue.setText(model.getCc()+"");
                holder.mileageValue.setText(model.getMileage()+"");
                holder.colour.setText(model.getColour());
                holder.costValue.setText(model.getCost()+"");
                holder.weightValue.setText(model.getWeight()+"");
                holder.yearValue.setText(model.getYear()+"");
                final Vehicle clickItem = model;
                vehicleCLicked = clickItem;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(BikeListScreenAdmin.this,BikeDetailsActivity.class);
                        intent.putExtra("BikeId", adapter.getRef(position).getKey() );
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public VehicleViewHolderAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.bike_card_view, parent, false);
                return new VehicleViewHolderAdmin(view);
            }
        };
        adapter.notifyDataSetChanged();
        bikeListRecyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

  /*  private ArrayList<BikeDetailsModel> getBikeList() {

        BikeDetailsModel model = new BikeDetailsModel();
        model.setBikeName("CBR 250R");
        model.setColour("Red-White");
        model.setCc(350);
        model.setCost(1200);
        model.setMileage(30);
        model.setWeight(800);
        model.setYear(2020);
        model.setBikeImage(R.drawable.cbr250r);
        bikeDetailsModels.add(model);

        model = new BikeDetailsModel();


        return bikeDetailsModels;
    }
*/
  @Override
  protected void onStart() {
      super.onStart();
     if(adapter!=null) adapter.startListening();
  }
    @Override
    public void onClick(View v) {

        Toast.makeText(this,"Clicked",Toast.LENGTH_LONG);
    }

    @Override
    public void onSelect(int position) {

        Intent intent = new Intent(this,BikeDetailsActivity.class);
        intent.putExtra("bikeModel", vehicleCLicked);
        startActivity(intent);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
            if(item.getTitle().equals("Add"))
                addBikeDialog();
             else if(item.getTitle().equals("Update"))
                 showDialogUpdate(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
            else if(item.getTitle().equals("Delete"))
                 deleteBike(adapter.getRef(item.getOrder()).getKey());
            return super.onContextItemSelected(item);
    }
    private void deleteBike(final String key) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("DELETE BIKE ?");
        alert.setMessage("Are you sure you want to remove it?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                vehicleList.child(key).removeValue();
                Snackbar.make(relativeLayout,"Category Deleted!",Snackbar.LENGTH_SHORT).show();
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

    private void showDialogUpdate(final String key, final Vehicle item) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(BikeListScreenAdmin.this);
        alert.setTitle("Update Bike");
        alert.setMessage("Please fill all details!");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_bike_layout = inflater.inflate(R.layout.add_bike_card_admin , null);

        alert.setView(add_bike_layout);
        alert.setIcon(R.drawable.package_image);


        name = add_bike_layout.findViewById(R.id.bikeNameET);
        colour = add_bike_layout.findViewById(R.id.bikeColorET);
        cc = add_bike_layout.findViewById(R.id.bikeCCET);
        cost = add_bike_layout.findViewById(R.id.bikeCostET);
        mileage= add_bike_layout.findViewById(R.id.bikeMileageET);
        description = add_bike_layout.findViewById(R.id.bikeDescription);
        year = add_bike_layout.findViewById(R.id.bikeYear);
        weight = add_bike_layout.findViewById(R.id.bikeWeightET);
        select = add_bike_layout.findViewById(R.id.selectBtn);
        upload = add_bike_layout.findViewById(R.id.uploadBtn);

        name.setText(item.getName());
        colour.setText(item.getColour());
        cc.setText(item.getCc());
        cost.setText(item.getCost());
        mileage.setText(item.getMileage());
        description.setText(item.getDetails());
        year.setText(item.getYear());
        weight.setText(item.getWeight());


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
               if(uri!=null) item.setImage1(uri.toString());
                item.setName(name.getText().toString());
                item.setCc(cc.getText().toString());
                item.setColour(colour.getText().toString());
                item.setCost(cost.getText().toString());
                item.setDetails(description.getText().toString());
                item.setWeight(weight.getText().toString());
                item.setYear(year.getText().toString());
                item.setMileage(mileage.getText().toString());
                vehicleList.child(key).setValue(item);
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
    private void updateImage(final Vehicle item) {
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
                    Toast.makeText(BikeListScreenAdmin.this, "Uploaded Sucessfully!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            item.setImage1(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mdialog.dismiss();
                    Toast.makeText(BikeListScreenAdmin.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void addBikeDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(BikeListScreenAdmin.this);
        alert.setTitle("Add New Bike");
        alert.setMessage("Please fill all details!");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_bike_layout = inflater.inflate(R.layout.add_bike_card_admin , null);

        alert.setView(add_bike_layout);
        alert.setIcon(R.drawable.package_image);


        name = add_bike_layout.findViewById(R.id.bikeNameET);
        colour = add_bike_layout.findViewById(R.id.bikeColorET);
        cc = add_bike_layout.findViewById(R.id.bikeCCET);
        cost = add_bike_layout.findViewById(R.id.bikeCostET);
        mileage= add_bike_layout.findViewById(R.id.bikeMileageET);
        description = add_bike_layout.findViewById(R.id.bikeDescription);
        year = add_bike_layout.findViewById(R.id.bikeYear);
        weight = add_bike_layout.findViewById(R.id.bikeWeightET);
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
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(newVehicle!=null){
                    vehicleList.push().setValue(newVehicle);
                    Snackbar.make(relativeLayout,"Bike "+newVehicle.getName()+" added!",Snackbar.LENGTH_SHORT).show();
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
    private void chooseImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i.createChooser(i,"Select Picture"), Common.PICK_IMAGE_REQ);
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
                    Toast.makeText(BikeListScreenAdmin.this, "Uploaded Sucessfully!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newVehicle = new Vehicle();
                            newVehicle.setImage1(uri.toString());
                            newVehicle.setName(name.getText().toString());
                            newVehicle.setCc(cc.getText().toString());
                            newVehicle.setColour(colour.getText().toString());
                            newVehicle.setCost(cost.getText().toString());
                            newVehicle.setDetails(description.getText().toString());
                            newVehicle.setWeight(weight.getText().toString());
                            newVehicle.setYear(year.getText().toString());
                            newVehicle.setParId(categoryId+"_0");
                            newVehicle.setMileage(mileage.getText().toString());

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mdialog.dismiss();
                    Toast.makeText(BikeListScreenAdmin.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.PICK_IMAGE_REQ && resultCode == RESULT_OK && data!=null && data.getData()!=null)
        {
            uri = data.getData();
            select.setText("Selected");

        }
    }
}
