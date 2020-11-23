package com.just.justbikev2;


        import androidx.appcompat.app.AppCompatActivity;

        import android.app.AlertDialog;
        import android.content.Intent;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.Window;
        import android.view.WindowManager;
        import android.view.animation.Animation;
        import android.view.animation.AnimationUtils;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.RatingBar;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.just.justbikev2.Common.Common;
        import com.just.justbikev2.Model.RateUs;
        import com.rengwuxian.materialedittext.MaterialEditText;

public class RateUsActivity extends AppCompatActivity {

    TextView ratetitle,rateanswer;
    Button ratesubmit;
    ImageView imageView;
    RatingBar ratingstars;
    String result;

    Animation image_anim;

    String noOfStars = "5";

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
        setContentView(R.layout.activity_rate_us);

        ratetitle = findViewById(R.id.rate_title);
        rateanswer = findViewById(R.id.rate_answer);
        imageView = findViewById(R.id.one);
        ratesubmit = findViewById(R.id.submit_rating);
        ratingstars = findViewById(R.id.rating);

        image_anim = AnimationUtils.loadAnimation(this,R.anim.image_anim);
        imageView.startAnimation(image_anim);
      ratingstars.setNumStars(5);
      ratesubmit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          AlertDialog.Builder alertdialog = new AlertDialog.Builder(RateUsActivity.this);
          alertdialog.setTitle("It's valuable");

          LayoutInflater inflater = LayoutInflater.from(RateUsActivity.this);
          View layout_home = inflater.inflate(R.layout.send_complaint_card,null);
          EditText etHomeAddress = layout_home.findViewById(R.id.etHomeAddress);
          etHomeAddress.setHint("Type here");
          TextView btn_tV = layout_home.findViewById(R.id.btn_tV);
          btn_tV.setText("Send");
          alertdialog.setView(layout_home);
          AlertDialog alertDialog= alertdialog.create();
          layout_home.findViewById(R.id.complaint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(!etHomeAddress.getText().toString().equals("") && Common.currentUser!=null) {
                DatabaseReference referenceRateUs = FirebaseDatabase.getInstance().getReference("RateUs");
                RateUs rateUsModel = new RateUs(Common.currentUser.getName(),Common.currentUser.getPhone(),etHomeAddress.getText().toString(),noOfStars);
                referenceRateUs.push().setValue(rateUsModel);
                Toast.makeText(RateUsActivity.this, "Thank you!", Toast.LENGTH_SHORT).show();
                etHomeAddress.setText("");
                alertDialog.dismiss();
              }
              else
                Toast.makeText(RateUsActivity.this, "Please Enter Your feedback!", Toast.LENGTH_SHORT).show();
            }
          });
          alertDialog.show();
        }
      });

        ratingstars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                result = String.valueOf((int) (ratingstars.getRating()));

                if (result.equals("1")){
                    imageView.setImageResource(R.drawable.one);
                    imageView.startAnimation(image_anim);
                    rateanswer.setText("Sorry for inconvenient Service");
                  noOfStars="1";
                }
                else if(result.equals("2")){
                    imageView.setImageResource(R.drawable.two);
                    imageView.startAnimation(image_anim);
                    rateanswer.setText("Good");
                  noOfStars="2";
                }
                else if(result.equals("3")){
                    imageView.setImageResource(R.drawable.three);
                    imageView.startAnimation(image_anim);
                    rateanswer.setText("Great");
                  noOfStars="3";
                }
                else if(result.equals("4")){
                    imageView.setImageResource(R.drawable.four);
                    imageView.startAnimation(image_anim);
                    rateanswer.setText("Excellent");
                  noOfStars="4";
                }
                else if(result.equals("5")){
                    imageView.setImageResource(R.drawable.five);
                    imageView.startAnimation(image_anim);
                    rateanswer.setText("Thank you, We just love it");
                  noOfStars="5";
                }
                else {
                    Toast.makeText(getApplicationContext(),"Give Ratings",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
