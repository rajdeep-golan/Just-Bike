package com.just.justbikev2.Admin;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.just.justbikev2.BikeDetailsActivity;
import com.just.justbikev2.BikeListScreen;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Home;
import com.just.justbikev2.Interface.ItemClickListener;
import com.just.justbikev2.Model.ContactModel;
import com.just.justbikev2.Model.UserModel;
import com.just.justbikev2.Model.Vehicle;
import com.just.justbikev2.R;
import com.just.justbikev2.ViewHolder.ShowCommentsVH;
import com.just.justbikev2.ViewHolder.UserVH;
import com.just.justbikev2.ViewHolder.ViewHolder;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import jp.wasabeef.picasso.transformations.BlurTransformation;

public class UsersList extends AppCompatActivity {

  FirebaseRecyclerAdapter<UserModel, UserVH> adapter;
  RecyclerView userRecycler ;
  DatabaseReference user;
  FirebaseRecyclerAdapter<UserModel, UserVH> searchAdapter;

  FirebaseRecyclerAdapter<ContactModel,ShowCommentsVH> adapterContacts;
  List<String> suggestedSearch = new ArrayList<>();
  MaterialSearchBar materialSearchBar;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
      WindowManager.LayoutParams.FLAG_FULLSCREEN);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
    }
    setContentView(R.layout.activity_users_list);

    user = FirebaseDatabase.getInstance().getReference("User");
    userRecycler = findViewById(R.id.userRecycler);
    userRecycler.setLayoutManager(new LinearLayoutManager(this));


    materialSearchBar = findViewById(R.id.serachBar);
    loadVehicles();

    loadSuggestList();

    materialSearchBar.setHint("Search...");

    materialSearchBar.setCardViewElevation(10);
    materialSearchBar.addTextChangeListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        int c= 0;
        List<String> suggest = new ArrayList<>();
        for (String search : suggestedSearch) {
          if(c<1)
            if (search!=null && search.toLowerCase().contains(materialSearchBar.getText().toLowerCase())) {
              suggest.add(search);
              c++;
            }
        }
        materialSearchBar.setLastSuggestions(suggest);
        if (!materialSearchBar.getText().isEmpty() || !materialSearchBar.getText().equals("")) {
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
        if (!enabled)
          userRecycler.setAdapter(adapter);

      }

      @Override
      public void onSearchConfirmed(CharSequence text) {
        //When search finished , show resultant searched items
        showSearchItems(text);
        searchAdapter.startListening();
        materialSearchBar.clearSuggestions();

      }

      @Override
      public void onButtonClicked(int buttonCode) {

      }
    });

  }
  private void showSearchItems(CharSequence text) {
    Query query = FirebaseDatabase.getInstance()
      .getReference()
      .child("User")
      .orderByKey().equalTo(text.toString())
      .limitToLast(100);

    FirebaseRecyclerOptions<UserModel> options =
      new FirebaseRecyclerOptions.Builder<UserModel>()
        .setQuery(query, UserModel.class)
        .build();

    searchAdapter = new FirebaseRecyclerAdapter<UserModel, UserVH>(options) {
      @Override
      protected void onBindViewHolder(@NonNull UserVH holder, int position, @NonNull UserModel model) {
        if(model.getVerified().equals("0"))
        {
          holder.verified.setText("Not Verified");
          holder.verified.setTextColor(getResources().getColor(R.color.btn_red_bg));
        }
        holder.phoneNo.setText(model.getPhone());
        holder.wallet.setText("₹"+model.getWallet());

        holder.wallet.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            AlertDialog.Builder alertdialog = new AlertDialog.Builder(UsersList.this);
            alertdialog.setTitle("Enter new amount");
            alertdialog.setMessage("This will be new Total");

            LayoutInflater inflater = LayoutInflater.from(UsersList.this);
            View view = inflater.inflate(R.layout.send_complaint_card,null);

            TextView btn_text = view.findViewById(R.id.btn_tV);
            btn_text.setText("Change");

            EditText newAmount = view.findViewById(R.id.etHomeAddress);
            newAmount.setHint("Only Digits , without comma");
            newAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
            alertdialog.setView(view);
            final AlertDialog dialog = alertdialog.create();

            view.findViewById(R.id.complaint).setOnClickListener(new View.OnClickListener() {
                                                                   @Override
                                                                   public void onClick(View v) {

                                                                     dialog.dismiss();
                Map<String,Object> walletMap = new HashMap<>();
                if(model.getVerified().equals("1"))
                  walletMap.put("wallet", newAmount.getText().toString());

                DatabaseReference this_user = user.child(model.getPhone());
                this_user.updateChildren(walletMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(UsersList.this, "Wallet Amount changed successfully", Toast.LENGTH_SHORT).show();
                    holder.wallet.setText("₹"+newAmount.getText().toString());
                  }
                });
              }
            });

            dialog.show();

          }
        });

        holder.name.setText(model.getName());
        Picasso.get().load(model.getProfilePic()).placeholder(R.drawable.biker).into(holder.profilePic);
        holder.viewDocs.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent i = new Intent(UsersList.this , ViewDocs.class);
            i.putExtra("user",model);
            startActivity(i);
          }
        });
        holder.locationIcon.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Toast.makeText(UsersList.this, "location", Toast.LENGTH_SHORT).show();
          }
        });
        holder.contactsIcon.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v)
            {
              Intent i = new Intent(UsersList.this,ViewContacts.class);
              i.putExtra("phoneNo",model.getPhone());
              startActivity(i);
            }

        });
        holder.verified.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            //ask confirmation to change the verification state
            AlertDialog.Builder alertdialog = new AlertDialog.Builder(UsersList.this);
            alertdialog.setTitle("Are you sure?");
            alertdialog.setMessage("Change Verification State?");

            LayoutInflater inflater = LayoutInflater.from(UsersList.this);
            View view = inflater.inflate(R.layout.update_order_admin,null);

            view.findViewById(R.id.orderStatusSpinner).setVisibility(View.GONE);
            view.findViewById(R.id.reachedVerify).setVisibility(View.GONE);
            alertdialog.setView(view);
            alertdialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                int newState;
                Map<String,Object> verificationMap = new HashMap<>();
                if(model.getVerified().equals("1")) {
                  newState = 1;
                  verificationMap.put("verified", "0");
                }
                else {
                  newState = 0;
                  verificationMap.put("verified", "1");
                }

                DatabaseReference this_user = user.child(model.getPhone());
                this_user.updateChildren(verificationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(UsersList.this, "Verification State changed successfully", Toast.LENGTH_SHORT).show();
                    if(newState == 1) {
                      holder.verified.setText("Verified");
                      holder.verified.setTextColor(getResources().getColor(R.color.green));
                    }
                    else{
                      holder.verified.setText("Not Verified");
                      holder.verified.setTextColor(getResources().getColor(R.color.btn_red_bg));
                    }
                  }
                });
              }
            });
            alertdialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              }
            });
            alertdialog.show();
          }
        });
      }
      @NonNull
      @Override
      public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.user_view_card, parent, false);
        return new UserVH(view);
      }
    };
    userRecycler.setAdapter(searchAdapter);
  }

  private void loadSuggestList() {
    user.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        for(DataSnapshot data : dataSnapshot.getChildren()){

          UserModel lastSearchVehicle = data.getValue(UserModel.class);
          suggestedSearch.add(lastSearchVehicle.getPhone());
          suggestedSearch.add(lastSearchVehicle.getName());

        }
        List<String> oneElementSearch = new ArrayList<>();
        if(suggestedSearch!=null && suggestedSearch.size()>0)
          oneElementSearch.add(suggestedSearch.get(suggestedSearch.size()-1));
        materialSearchBar.setLastSuggestions(oneElementSearch);

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  private void loadVehicles() {
    Query query = FirebaseDatabase.getInstance()
      .getReference()
      .child("User")
      .limitToLast(100000);
    Map<String, String> namePhoneMap = new HashMap<String, String>();

    FirebaseRecyclerOptions<UserModel> options =
      new FirebaseRecyclerOptions.Builder<UserModel>()
        .setQuery(query, UserModel.class)
        .build();
    adapter = new FirebaseRecyclerAdapter<UserModel, UserVH>(options) {
      @Override
      protected void onBindViewHolder(@NonNull UserVH holder, int position, @NonNull UserModel model) {
        if(model.getVerified().equals("0"))
        {
          holder.verified.setText("Not Verified");
          holder.verified.setTextColor(getResources().getColor(R.color.btn_red_bg));
        }
        holder.phoneNo.setText(model.getPhone());
        holder.name.setText(model.getName());
        holder.wallet.setText("₹"+model.getWallet());
        holder.wallet.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            AlertDialog.Builder alertdialog = new AlertDialog.Builder(UsersList.this);
            alertdialog.setTitle("Enter new amount");
            alertdialog.setMessage("This will be new Total");

            LayoutInflater inflater = LayoutInflater.from(UsersList.this);
            View view = inflater.inflate(R.layout.send_complaint_card,null);

            TextView btn_text = view.findViewById(R.id.btn_tV);
            btn_text.setText("Change");

            EditText newAmount = view.findViewById(R.id.etHomeAddress);
            newAmount.setHint("Only Digits , without comma");
            newAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
            alertdialog.setView(view);
            final AlertDialog dialog = alertdialog.create();
            view.findViewById(R.id.complaint).setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                dialog.dismiss();
                Map<String,Object> walletMap = new HashMap<>();
                if(model.getVerified().equals("1"))
                  walletMap.put("wallet", newAmount.getText().toString());


                DatabaseReference this_user = user.child(model.getPhone());
                this_user.updateChildren(walletMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(UsersList.this, "Wallet Amount changed successfully", Toast.LENGTH_SHORT).show();
                    holder.wallet.setText("₹"+newAmount.getText().toString());
                  }
                });
              }
            });

            dialog.show();

          }
        });
        Picasso.get().load(model.getProfilePic()).placeholder(R.drawable.biker).into(holder.profilePic);
        holder.viewDocs.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent i = new Intent(UsersList.this , ViewDocs.class);
            i.putExtra("user",model);
            startActivity(i);
          }
        });
        holder.locationIcon.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Toast.makeText(UsersList.this, "location", Toast.LENGTH_SHORT).show();
          }
        });
        holder.contactsIcon.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent i = new Intent(UsersList.this,ViewContacts.class);
            i.putExtra("phoneNo",model.getPhone());
            startActivity(i);
          }
        });
        holder.verified.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            //ask confirmation to change the verification state
            AlertDialog.Builder alertdialog = new AlertDialog.Builder(UsersList.this);
            alertdialog.setTitle("Are you sure?");
            alertdialog.setMessage("Change Verification State?");

            LayoutInflater inflater = LayoutInflater.from(UsersList.this);
            View view = inflater.inflate(R.layout.update_order_admin,null);

            view.findViewById(R.id.orderStatusSpinner).setVisibility(View.GONE);
            view.findViewById(R.id.reachedVerify).setVisibility(View.GONE);
            alertdialog.setView(view);
            alertdialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                int newState;
                Map<String,Object> verificationMap = new HashMap<>();
                if(model.getVerified().equals("1")) {
                  newState = 1;
                  verificationMap.put("verified", "0");
                }
                else {
                  newState = 0;
                  verificationMap.put("verified", "1");
                }

                DatabaseReference this_user = user.child(model.getPhone());
                this_user.updateChildren(verificationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(UsersList.this, "Verification State changed successfully", Toast.LENGTH_SHORT).show();
                    if(newState == 1) {
                      holder.verified.setText("Verified");
                      holder.verified.setTextColor(getResources().getColor(R.color.green));
                    }
                    else{
                        holder.verified.setText("Not Verified");
                        holder.verified.setTextColor(getResources().getColor(R.color.btn_red_bg));
                    }
                  }
                });
              }
            });
            alertdialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              }
            });
            alertdialog.show();
          }
        });
      }

      @NonNull
      @Override
      public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.user_view_card, parent, false);
        return new UserVH(v);
      }
    };
    userRecycler.setAdapter(adapter);

  }

  @Override
  protected void onStart() {
    super.onStart();
    if(adapter!=null)
      adapter.startListening();
  }
}
