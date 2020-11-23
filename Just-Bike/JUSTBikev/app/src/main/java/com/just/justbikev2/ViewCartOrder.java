package com.just.justbikev2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.just.justbikev2.Adapter.CartAdapter;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Database.Database;
import com.just.justbikev2.Model.Order;
import com.razorpay.Checkout;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ViewCartOrder extends AppCompatActivity implements View.OnClickListener, com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener,
  com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {
  RecyclerView recyclerView;
  RecyclerView.LayoutManager layoutManager;

  FirebaseDatabase firebaseDatabase;
  DatabaseReference databaseReference;

  TextView amount;
  ImageButton confirmBtn;


  List<Order> orderList = new ArrayList<>();
  CartAdapter adapter;
  int totalAmount = 0;


  //
//    static PayPalConfiguration payPalConfiguration = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) //for testing sandbox , change it later when in production
//                        .clientId(Config.PAY_PAL_ID);
  TextView noOfChoices, amountReducedValue, discountPer;

  HashMap<String, Integer> discountMap;
  RadioButton seven, fifteen, thirty;
  int noOfDaysChosen = 30;
  int countOfBikes = 0;
  double reducedAmount = 0;
  double discountPercentage = 100;
  Locale locale = new Locale("en", "in");
  NumberFormat format = NumberFormat.getCurrencyInstance(locale);
  int sTime;
  String endTimeString;
  int endHour, endMin;
  int monthCal, dayCal;
  String startDateString, startTimeString, endDateString; //start date selected by user from
  int startYear, startMonth, startDay;
  int startHour, startMin;

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
    setContentView(R.layout.activity_view_cart_order);
    Checkout.preload(getApplicationContext());

    bindViews();
//        Intent i = new Intent(this,PayPalService.class);
//        i.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,payPalConfiguration);
//        startService(i);

    layoutManager = new LinearLayoutManager(this);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(layoutManager);


    firebaseDatabase = FirebaseDatabase.getInstance();
    databaseReference = firebaseDatabase.getReference("Requests");

    amount = findViewById(R.id.amountValue);
    confirmBtn = findViewById(R.id.confirm_button);

    confirmBtn.setOnClickListener(this);
    if (Common.isConnectedToInternet(this))
      loadCart();
    else {
      Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
      return;
    }

  }

  private void bindViews() {
    noOfChoices = findViewById(R.id.noOfChoices);
    seven = findViewById(R.id.seven);
    fifteen = findViewById(R.id.fifteen);
    thirty = findViewById(R.id.thirty);
    amountReducedValue = findViewById(R.id.amountReducedValue);
    discountPer = findViewById(R.id.discountPer);
    recyclerView = findViewById(R.id.CartRV);
    setDiscountMap();
  }

  private void setDiscountMap() {
    discountMap = new HashMap<>();
    discountMap.put("7", 10);
    discountMap.put("15", 20);
    discountMap.put("30", 40);

    thirty.setChecked(true);
    discountPer.setText(String.valueOf(discountMap.get("30")));
    if (discountMap != null && discountMap.get("30") != null)
      discountPercentage = 100 - discountMap.get("30"); //Since if discount is 20% , value is 80% (100-20) of total value
    seven.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
          discountPer.setText(String.valueOf(discountMap.get("7")));
          if (discountMap != null && discountMap.get("7") != null)
            discountPercentage = 100 - discountMap.get("7");
          noOfDaysChosen = 7;
          loadCart();
        }
      }
    });
    fifteen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
          discountPer.setText(String.valueOf(discountMap.get("15")));
          if (discountMap != null && discountMap.get("15") != null)
            discountPercentage = 100 - discountMap.get("15");
          noOfDaysChosen = 15;
          loadCart();
        }
      }
    });
    thirty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
          discountPer.setText(String.valueOf(discountMap.get("30")));
          if (discountMap != null && discountMap.get("30") != null)
            discountPercentage = 100 - discountMap.get("30");
          noOfDaysChosen = 30;
          loadCart();
        }
      }
    });
  }

  private void loadCart() {
    orderList = new Database(this).getCart();
    adapter = new CartAdapter(orderList, this);
    adapter.notifyDataSetChanged();
    recyclerView.setAdapter(adapter);
    int total = 0;
    countOfBikes = 0;
    for (Order order : orderList) {
      countOfBikes++;
      total += Integer.parseInt(order.getPrice());
    }
    String amt = format.format(costAsPerDays(total));
    if (amt.indexOf('.') > 0)    //for not putting decimal point
      amount.setText(amt.substring(0, amt.indexOf('.')));
    else
      amount.setText(amt);
    noOfChoices.setText(String.valueOf(countOfBikes));
    reducedAmount = (double) total * (discountPercentage / 100);
    if (format.format(reducedAmount).indexOf('.') > 0)    //for not putting decimal point
      amountReducedValue.setText(format.format(costAsPerDays(reducedAmount)).substring(0, format.format(costAsPerDays(reducedAmount)).indexOf('.')));
    else
      amountReducedValue.setText(format.format(costAsPerDays(reducedAmount)));
    totalAmount = total;
    if (countOfBikes == 0)
      amountReducedValue.setVisibility(View.GONE);
    else
      amountReducedValue.setVisibility(View.VISIBLE);
  }

  public double costAsPerDays(double amtForDays) {
    if (countOfBikes != 0) {
      return amtForDays * (noOfDaysChosen / countOfBikes);    //To get amount as per days
    }
    return amtForDays;
  }
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    if(item.getTitle().equals("Delete"))
      new Database(this).removeFromCart(orderList.get(item.getOrder()).getProductId());
    return super.onContextItemSelected(item);
  }
  @Override
  public void onClick(View v) {

    switch (v.getId()) {
      case R.id.confirm_button:
        if (orderList.size() > 0) {
          if (Common.currentUser == null) {
            Toast.makeText(ViewCartOrder.this, "Login to continue", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(ViewCartOrder.this, SignInAcitivity.class);
            startActivity(i);
          } else if (Common.currentUser.getVerified() != null && !Common.currentUser.getVerified().equals("1")) {
            Toast.makeText(ViewCartOrder.this, "Profile not verified! Please contact 8585858586 for instant help", Toast.LENGTH_LONG).show();
            Intent i = new Intent(ViewCartOrder.this, Articles.class);
            i.putExtra("updateProfile", "1");
            startActivity(i);
          } else
            startDateChose();

        } else
          Toast.makeText(this, "Package list is empty! Please add your wishes", Toast.LENGTH_LONG).show();
        break;
    }
  }

  private void openAccessories() {
    Intent i = new Intent(ViewCartOrder.this, AccessoryListScreen.class);
    i.putExtra("bikeId", "10");
    i.putExtra("bikeCost", String.valueOf((int) costAsPerDays(reducedAmount)));
    i.putExtra("bikeName", "Package");
    i.putExtra("bikeImage", "");
    i.putExtra("startDate", startDateString + " , " + startTimeString);
    i.putExtra("endDate", endDateString + " , " + endTimeString);
    i.putExtra("startEndDate", "");
    i.putExtra("startEndTime", "");
    startActivity(i);
  }

  private void startDateChose() {
    Calendar now = Calendar.getInstance();
    com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
      ViewCartOrder.this,
      now.get(Calendar.YEAR), // Initial year selection
      now.get(Calendar.MONTH), // Initial month selection
      now.get(Calendar.DAY_OF_MONTH) // Inital day selection
    );
    dpd.setAccentColor(getResources().getColor(R.color.btn_red_bg));
    dpd.setTitle("Your Journey Start Date");
    //Past dates are not allowed
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis() - 1000);
    dpd.setMinDate(calendar);

    dpd.show(getSupportFragmentManager(), "Datepickerdialog");

  }

  public void displayTime() {
    final BottomSheetDialog bt = new BottomSheetDialog(ViewCartOrder.this, R.style.BottomSheetDialogTheme);
    View view = LayoutInflater.from(ViewCartOrder.this).inflate(R.layout.time_confirm_lay, null);
    view.findViewById(R.id.btn_book).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openAccessories();
        bt.dismiss();
      }
    });
    ImageView imageView = view.findViewById(R.id.my_image);
    Picasso.get().load(R.drawable.package_image).into(imageView);
    TextView name = view.findViewById(R.id.txt_prname);
    name.setText("Package");

    TextView price = view.findViewById(R.id.txt_prprice);
    Locale locale = new Locale("en", "in");
    NumberFormat format = NumberFormat.getCurrencyInstance(locale);
    price.setText(format.format(costAsPerDays(reducedAmount)));
    view.findViewById(R.id.startJourney).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startDateChose();
        bt.dismiss();

      }
    });

    view.findViewById(R.id.endJourney).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        endDateChose();
        bt.dismiss();


      }
    });
    TextView endTimeTV = view.findViewById(R.id.endTime);
    endTimeTV.setText(endTimeTV.getText().toString() + endDateString + " , " + endTimeString);
    TextView startTimeTV = view.findViewById(R.id.startTime);
    startTimeTV.setText(startTimeTV.getText().toString() + startDateString + " , " + startTimeString);
    bt.setContentView(view);
    bt.show();


  }

  private void startTimeChose() {
    Calendar rightNow = Calendar.getInstance();
    int hours = rightNow.get(Calendar.HOUR_OF_DAY);
    int minsCur = rightNow.get(Calendar.MINUTE);
    com.wdullaer.materialdatetimepicker.time.TimePickerDialog timePickerDialog = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(this, startHour, startMin, 0, false);
    timePickerDialog.setAccentColor(getResources().getColor(R.color.btn_red_bg));

    if (monthCal == rightNow.get(Calendar.MONTH) && dayCal == rightNow.get(Calendar.DAY_OF_MONTH))
      timePickerDialog.setMinTime((hours + 2) % 24, minsCur, 0);

    timePickerDialog.setThemeDark(true);
    timePickerDialog.setTitle("Your Journey Start Time");
    timePickerDialog.dismissOnPause(true);
    timePickerDialog.show(getSupportFragmentManager(), "StartTimeickerdialog");
  }

  private void endTimeChose() {
    com.wdullaer.materialdatetimepicker.time.TimePickerDialog timePickerDialog = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(this, startHour, startMin, 0, false);
    timePickerDialog.setAccentColor(getResources().getColor(R.color.btn_red_bg));

    String[] startDateNo = startDateString.split("/");
    String[] endDateNo = endDateString.split("/");
    timePickerDialog.setThemeDark(true);
    timePickerDialog.setTitle("Your Journey End Time");
    timePickerDialog.dismissOnPause(true);

    if (startDateNo[0].equals(endDateNo[0]) && startDateNo[1].equals(endDateNo[1]))   // Same month and same date
    {
      timePickerDialog.setMinTime(startHour, startMin, 0);
      timePickerDialog.setMaxTime(23, 30, 0);
    } else if (startDateNo[0].equals(endDateNo[0]))   // exact end date
      timePickerDialog.setMaxTime(startHour, startMin, 0);     //Since day has already changed
    timePickerDialog.show(getSupportFragmentManager(), "EndTimeickerdialog");

  }

  private void endDateChose() {
    Calendar now = Calendar.getInstance();
    com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
      ViewCartOrder.this,
      now.get(Calendar.YEAR), // Initial year selection
      now.get(Calendar.MONTH), // Initial month selection
      now.get(Calendar.DAY_OF_MONTH) // Inital day selection
    );
    dpd.setAccentColor(getResources().getColor(R.color.btn_red_bg));
    dpd.setTitle("Your Journey Start Date");

    Calendar minCal = Calendar.getInstance();
    minCal.set(startYear, monthCal, dayCal);
    dpd.setMinDate(minCal);

    Calendar maxCal = Calendar.getInstance();
    //Depending of choice of spinner
    if (noOfDaysChosen == 30)
      maxCal.set(startYear, monthCal, dayCal + 30);
    else if (noOfDaysChosen == 15)
      maxCal.set(startYear, monthCal, dayCal + 15);
    else if (noOfDaysChosen == 7)
      maxCal.set(startYear, monthCal, dayCal + 7);
    dpd.setMaxDate(maxCal);
    dpd.show(getSupportFragmentManager(), "EndDatepickerdialog");
  }

  @Override
  public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
    if (view.getTag().equals("EndTimeickerdialog")) {
      endHour = hourOfDay;
      if (minute % 30 != 0) {
        int minuteFloor = minute - (minute % 30);
        minute = minuteFloor + (minute == minuteFloor + 1 ? 30 : 0);
        if (minute == 60)
          minute = 0;
      }
      endMin = minute;
      endTimeString = (endHour < 10 ? "0" + endHour : endHour) + ":" + (endMin < 10 ? "0" + endMin : endMin);
      displayTime();
    } else if (view.getTag().equals("StartTimeickerdialog")) {
      startHour = hourOfDay;
      if (minute % 30 != 0) {
        int minuteFloor = minute - (minute % 30);
        minute = minuteFloor + (minute == minuteFloor + 1 ? 30 : 0);
        if (minute == 60)
          minute = 0;
      }
      startMin = minute;
      startTimeString = (startHour < 10 ? "0" + startHour : startHour) + ":" + (startMin < 10 ? "0" + startMin : startMin);
      sTime = Integer.parseInt(startTimeString.substring(0, 2) + startTimeString.substring(2 + 1));
      endDateChose();
    }
  }

  @Override
  public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
    //   String date = "You picked the following date: "+dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
    if (view.getTag().equals("Datepickerdialog")) {
      monthCal = monthOfYear;
      dayCal = dayOfMonth;
      startYear = year;
      startMonth = monthOfYear;
      startDay = dayOfMonth;
      startDateString = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
      startTimeChose();
    } else if (view.getTag().equals("EndDatepickerdialog")) {
      endDateString = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
      endTimeChose();
    }
  }

}
