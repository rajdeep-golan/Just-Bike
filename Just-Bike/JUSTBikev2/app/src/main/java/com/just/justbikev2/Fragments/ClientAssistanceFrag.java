package com.just.justbikev2.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.just.justbikev2.ClientMessageActivity;
import com.just.justbikev2.Home;
import com.just.justbikev2.R;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ClientAssistanceFrag extends Fragment {
    private CardView chat , call,complaintCard,emailCard;
  private String contactNo = "+918585858586";
    private static int CALL_CODE = 34;

  private String to = "info@justbike.in";
    public ClientAssistanceFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =   inflater.inflate(R.layout.fragment_client_assistance, container, false);
        chat = view.findViewById(R.id.chatCard);
        call = view.findViewById(R.id.callCard);
        complaintCard= view.findViewById(R.id.complaintCard);
      emailCard=view.findViewById(R.id.emailCard);
      emailCard.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          openEmailDialog();

        }
      });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getContext(), ClientMessageActivity.class);
                startActivity(i);
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUs();
            }
        });
        complaintCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openComplaintDialog();
            }
        });
        return view;
    }

  private void openEmailDialog() {
    AlertDialog.Builder alertdialog = new AlertDialog.Builder(getContext());
    alertdialog.setTitle("Enter both fields");

    LayoutInflater inflater = LayoutInflater.from(getContext());
    View layout_home = inflater.inflate(R.layout.email_body_dialog,null);
    MaterialEditText etHomeAddress = layout_home.findViewById(R.id.etSubject);
    EditText body =  layout_home.findViewById(R.id.body);
    alertdialog.setView(layout_home);
    layout_home.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(etHomeAddress!=null && !etHomeAddress.getText().toString().equals("") && body!=null && !body.getText().toString().equals("")) {
          Intent email = new Intent(Intent.ACTION_SENDTO);
          email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});  //can add more recipients separated by comma
          email.putExtra(Intent.EXTRA_SUBJECT, etHomeAddress.getText().toString());
          email.putExtra(Intent.EXTRA_TEXT, body.getText().toString());

          //   email.setType("message/rfc822");
          email.setData(Uri.parse("mailto:"));
          if(email.resolveActivity(getContext().getPackageManager())!=null){
            startActivity(Intent.createChooser(email, "Choose an Email client :"));
          }else
            Toast.makeText(getContext(), "There is no application to send mails.", Toast.LENGTH_SHORT).show();
        }
        else
          Toast.makeText(getContext(), "Please Enter Your message!", Toast.LENGTH_SHORT).show();
      }
    });
    alertdialog.show();
  }

  private void openComplaintDialog() {
            AlertDialog.Builder alertdialog = new AlertDialog.Builder(getContext());
            alertdialog.setTitle("Let's find a solution!");

            LayoutInflater inflater = LayoutInflater.from(getContext());
            View layout_home = inflater.inflate(R.layout.send_complaint_card,null);
    EditText etHomeAddress = layout_home.findViewById(R.id.etHomeAddress);
            etHomeAddress.setHint("Enter your message");
            alertdialog.setView(layout_home);
            layout_home.findViewById(R.id.complaint).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!etHomeAddress.getText().toString().equals("")) {
                        Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=17139982613&text="+etHomeAddress.getText().toString()));
                        if (sendIntent.resolveActivity(getContext().getPackageManager()) != null) {
                            startActivity(sendIntent);
                        }else {
                            Toast.makeText(getContext(),"Whatsapp not installed! Please install Whatsapp",Toast.LENGTH_LONG).show();
                        }

                    }
                    else
                        Toast.makeText(getContext(), "Please Enter Your message!", Toast.LENGTH_SHORT).show();
                }
            });
        alertdialog.show();

        }


    private void askForCallPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},CALL_CODE);
    }
    public void callUs(){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+contactNo));

        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED)
            askForCallPermission();
        else
            startActivity(callIntent);

    }
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CALL_CODE ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + contactNo));
                startActivity(callIntent);
            } else
                Toast.makeText(getContext(), "Please accept calling permission before making a Call", Toast.LENGTH_SHORT).show();
        }

    }

}
