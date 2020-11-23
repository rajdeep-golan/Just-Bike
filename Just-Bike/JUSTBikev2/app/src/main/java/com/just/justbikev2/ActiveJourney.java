package com.just.justbikev2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Model.MyResponse;
import com.just.justbikev2.Model.Notification;
import com.just.justbikev2.Model.Request;
import com.just.justbikev2.Model.Sender;
import com.just.justbikev2.Model.Token;
import com.just.justbikev2.Model.Upi;
import com.just.justbikev2.Model.UserModel;
import com.just.justbikev2.Model.Vehicle;
import com.just.justbikev2.Remote.APIService;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveJourney extends AppCompatActivity implements  PaymentResultListener,
  com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener{
    Intent intent;
    String totalAmt , orderId,orderStatus,bikeName, bikeCostPerDay;
    TextView readyBikeName,startTimeReady,endTimeReady,totalCostValue,activeOrEnd;
    TextView outstandingValue;
    ImageButton extendRide, trackBike,payOutstanding;

    FrameLayout trackLayout,extendLayout;
  String endDateStringOfBooking = "";

  String bikeId="01";
  String bikeCost = "1000";

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
        setContentView(R.layout.activity_active_journey);

        Checkout.preload(getApplicationContext());

        bindViews();

    apiService = Common.getFCMService();

    upiId="rajput.dbs@dbs";

    DatabaseReference upi = FirebaseDatabase.getInstance().getReference().child("Upi");
    upi.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for(DataSnapshot postData : dataSnapshot.getChildren()){
          Upi upiModel = postData.getValue(Upi.class);
          if(Common.cityId.equals(upiModel.getCityId()))
            upiId = upiModel.getUpiId();
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
        intent = getIntent();
        if (intent != null) {
            totalAmt = intent.getStringExtra("totalAmt");
            orderId = intent.getStringExtra("orderId");
            orderStatus = intent.getStringExtra("orderStatus");
            bikeName =  intent.getStringExtra("bikeName");
            //Get Order Status , if 3 , i.e. delivered , set visibility of Track Btn GONE , otherwise set it as orderStatus
        }
      if(bikeName!=null)
        readyBikeName.setText(bikeName);
      if(Common.currentRequest!=null) {
        startTimeReady.setText(Common.currentRequest.getStartDate());
        endTimeReady.setText(Common.currentRequest.getEndDate());
        endDateStringOfBooking = Common.currentRequest.getEndDate();
        convertDateStringToInt(endDateStringOfBooking);
        if(Common.currentRequest.getOutstandingAmt()!=null)
          outstandingValue.setText((int)(Double.parseDouble(Common.currentRequest.getOutstandingAmt()))+"");
      }
        if(orderStatus!=null && ( orderStatus.equals("1") || orderStatus.equals("2") ||orderStatus.equals("0")))  // 0,1,2
          {
          activeOrEnd.setText("Your journey is yet to start");
          trackLayout.setVisibility(View.VISIBLE);
        }
        else if(orderStatus!=null && orderStatus.equals("5")) {
          extendLayout.setVisibility(View.GONE);
          activeOrEnd.setText("Your booking was cancelled");
        }
        else if(orderStatus!=null && orderStatus.equals("4")) {
          extendLayout.setVisibility(View.GONE);
          activeOrEnd.setText("Your journey is has ended");
        }else if(orderStatus!=null && orderStatus.equals("3"))
        {
          trackLayout.setVisibility(View.GONE);
          extendLayout.setVisibility(View.VISIBLE);
          activeOrEnd.setText("Your journey is Active");
        }
    FrameLayout payingLayout = findViewById(R.id.payingLayout);
    if(outstandingValue.getText()==null || outstandingValue.getText().toString().equals("0"))
      payingLayout.setVisibility(View.GONE);
        else{
          payOutstanding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              payBalanceBottomSheet();
            }
          });
        }



        totalCostValue.setText(totalAmt);
        trackBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActiveJourney.this,TrackingOrderList.class);
                i.putExtra("orderNo",orderId);
                i.putExtra("orderStatus",orderStatus);

                startActivity(i);
            }
        });
        extendRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
              openBottomLayForExtendRide();
            }
            });
    }

    int amtOutstandingInt = 0;
  int isOutstandingPay = 0;   // 0 : From Extend ride , 1: from Outstanding Pay
  private void payBalanceBottomSheet() {
    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActiveJourney.this,R.style.RedDialogTheme);
    LayoutInflater inflater = getLayoutInflater();
    View add_money_dialog = inflater.inflate(R.layout.add_money_dialog, null);
    alertDialogBuilder.setView(add_money_dialog);
    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();
    EditText addMoneyET =  add_money_dialog.findViewById(R.id.amtTransfer);
    addMoneyET.setEnabled(false);
    addMoneyET.setText(outstandingValue.getText().toString());
    TextView enterAmount = add_money_dialog.findViewById(R.id.enterAmount);
    enterAmount.setText("Outstanding Amount");
    add_money_dialog.findViewById(R.id.transferBtn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        amtOutstandingInt =(int) Double.parseDouble(Common.currentRequest.getOutstandingAmt());
        amount = amtOutstandingInt;
        isOutstandingPay=1;
        startPayment();
          alertDialog.dismiss();
      }
    });
    add_money_dialog.findViewById(R.id.upiBtn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        amtOutstandingInt =(int) Double.parseDouble(Common.currentRequest.getOutstandingAmt());
        amount = amtOutstandingInt;
        isOutstandingPay=1;
        payUsingUPI();
          alertDialog.dismiss();
      }
    });


  }

  //Format should be DD/MM/YYY , HH:mm
    private int convertDateStringToInt(String date){

    //Date
      String[] monthDay = date.split("/");
      String dayString,monthString;
      int month =Integer.parseInt( monthDay[1]);
      int day = Integer.parseInt( monthDay[0]);
      month+=10;    //As per my algo , month is added 10 , Sept : 09 -> 19 , Dec :12-> 22
      monthCal = month;

      monthString = String.valueOf(month);
      if(day<10)
        dayString = "0"+day;
      else
        dayString = String.valueOf(day);

      dayCal =  day;

      //Time
      String minute,hour;
      minute = date.substring(date.length()-2);
      hour = date.substring(date.length()-5,date.length()-3);

      String dateStr = monthString + dayString + hour + minute;
      try {
        return Integer.parseInt(dateStr);
      }catch (Exception e){
        return 0;
      }
    }

  Locale locale = new Locale("en", "in");
  NumberFormat format = NumberFormat.getCurrencyInstance(locale);
  TextView extensionCostTV;
  private void openBottomLayForExtendRide()
    {
      DatabaseReference vehicles = FirebaseDatabase.getInstance().getReference("Vehicles");
      vehicles.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          for(DataSnapshot postData :dataSnapshot.getChildren()){
            Vehicle vehicle = postData.getValue(Vehicle.class);
            if(vehicle.getName().equals(bikeName)) {
              bikeCostPerDay = vehicle.getCost();
              bikeId = vehicle.getKeyId();
              bikeCost = vehicle.getCost();
              extensionCostTV.setText(format.format(getAmountAsPerTime(bikeCost)));
            }
          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
      });
      final BottomSheetDialog bt = new BottomSheetDialog(ActiveJourney.this, R.style.BottomSheetDialogTheme);
      View view = LayoutInflater.from(ActiveJourney.this).inflate(R.layout.extend_ride_bottom, null);
      view.findViewById(R.id.upiBtn).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

          bt.dismiss();
        }
      });
      view.findViewById(R.id.upiBtn).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          payUsingUPI();
        }
      });
      view.findViewById(R.id.pay_extend).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          startPayment();
          bt.dismiss();
        }
      });
      extensionCostTV = view.findViewById(R.id.extensionCostTV);

      TextView endDate = view.findViewById(R.id.endDate);

      view.findViewById(R.id.endDate).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          endDateChose();
          bt.dismiss();

        }
      });
      TextView endTime = view.findViewById(R.id.endTime);
//     if(endDate.getText().toString().equals(endDateString))
//       endTime.setVisibility(View.GONE);
//      view.findViewById(R.id.endTime).setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//          endTimeChose();
//          bt.dismiss();
//        }
//      });

      Date date1 ;
      Date date2 ;
      //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
      SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy , HH:mm");

if(endDateString!=null && endTimeString!=null) {
  if(!endTimeString.equals("") && !endDateString.equals(""))
    endDate.setText(endDateString);
  endTime.setText(endTimeString);
  try {
    date1 = sdf2.parse(endDateString+" , "+endTimeString);
    date2 = sdf2.parse(endDateStringOfBooking);
    DateTime dateTime1 = new DateTime(), dateTime2 = new DateTime();
    if (date1 != null && date2 != null) {
      dateTime1 = new DateTime(date1);
      dateTime2 = new DateTime(date2);
    }

    noOfDays = Days.daysBetween(dateTime2, dateTime1).getDays();
    int noOfHours = Hours.hoursBetween(dateTime2, dateTime1).getHours();
    hours = noOfHours % 24;
  } catch (ParseException e) {
    e.printStackTrace();
  }

//      price.setText(format.format(getAmountAsPerTime(bikeCost)));
//      TextView endTimeTV = view.findViewById(R.id.endTime);
//      if()
//        endTimeTV.setText(endTimeTV.getText().toString()+endDateString+" , "+endTimeString);
//      TextView startTimeTV = view.findViewById(R.id.startTime);
//      if()
//        startTimeTV.setText(startTimeTV.getText().toString()+startDateString+" , "+startTimeString);

}
      bt.setContentView(view);
      bt.show();
    }


  int hours , noOfDays;
    private int getAmountAsPerTime(String cost) {
        int amt = Integer.parseInt(cost);
        int hourPrice =0, dayPrice=0;
        if(0<hours && hours<=6)
            hourPrice = amt - 200;
        else if(6<hours && hours<=12)
            hourPrice = amt -100;
        else if(12<hours && hours<=24)
            hourPrice = amt;
        if(1<noOfDays && noOfDays<=7)
            dayPrice =amt*noOfDays;
        else if(7<noOfDays && noOfDays<=14)
            dayPrice = (amt-100)*noOfDays;
        else if(15<noOfDays && noOfDays<=30)
            dayPrice = (amt-200)*noOfDays;
        else if(30<noOfDays)
            dayPrice = (amt-300)*noOfDays;
      amount = hourPrice+dayPrice;
        return amount;
    }

    int amount;
    @Override
    public void onPaymentSuccess(String s) {
      Request request;
      if(isOutstandingPay==1){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Requests");
        Map<String,Object> outstandingMap = new HashMap<>();
        outstandingMap.put("outstandingAmt","0");
        databaseReference.child(orderId).updateChildren(outstandingMap);
        Toast.makeText(ActiveJourney.this, "Thank You! , Balance cleared.", Toast.LENGTH_SHORT).show();
        databaseReference.child(orderId).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Common.currentRequest = dataSnapshot.getValue(Request.class);
            outstandingValue.setText("0");
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(ActiveJourney.this, "Error updating amount", Toast.LENGTH_SHORT).show();
          }
        });
      }
      else {
        DatabaseReference vehiclesRef = FirebaseDatabase.getInstance().getReference("Vehicles");
        List<Integer> listDate = new ArrayList<>();
        listDate.add(convertDateStringToInt(Common.currentRequest.getStartDate().substring(0,8)));    // till 8 , as dd/mm/yy length = 8
        listDate.add(convertDateStringToInt(endDateString.substring(0,8)));
        vehiclesRef.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot postData : dataSnapshot.getChildren()) {
              Vehicle v = postData.getValue(Vehicle.class);
              List<List<Integer>> bookingSlot = new ArrayList<>();
              if (v.getBookingSlot() != null) {
                bookingSlot = v.getBookingSlot();
                int pushCount = 0;
                if (v.getName().equals(bikeName)) {
                  if (bookingSlot != null)
                    pushCount = bookingSlot.size();
                  vehiclesRef.child(postData.getKey()).child("bookingSlot").child(pushCount + "").setValue(listDate);
                }
              } else {
                int pushCount = 0;
                if (v.getName().equals(bikeName)) {
                  if (bookingSlot != null)
                    pushCount = bookingSlot.size();
                  vehiclesRef.child(postData.getKey()).child("bookingSlot").child(pushCount + "").setValue(listDate);
                }
              }
            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
        });
        request = new Request(Common.currentUser.getPhone(), Common.currentRequest.getAddress(), Common.currentRequest.getPurpose(),
          Common.currentUser.getName(),
          Common.currentRequest.getOrderList(), String.valueOf(amount),
          Common.currentRequest.getComment(), "0", "1",
          "latlng", Common.currentRequest.getOutstandingAmt(), String.valueOf(discountAmt), "UPI/PAYTM",
          endDateStringOfBooking, endDateString, Common.currentRequest.getNoOfHelmets(), 0, 0);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Requests");

        String orderNo = String.valueOf(System.currentTimeMillis());
        databaseReference.child(orderNo).setValue(request);
        Toast.makeText(ActiveJourney.this, "Thank You! Enjoy your extension Trip.", Toast.LENGTH_SHORT).show();
        sendNotification(orderNo);
        Common.currentRequest = request;
        Intent i = new Intent(this,ActiveJourney.class);
        startActivity(i);
        finish();
        }


    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Error during payment , Try again!", Toast.LENGTH_SHORT).show();

    }
    public void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_live_vyPSpJi7gF28TF");

        checkout.setImage(R.drawable.just_black_logo);

        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();

            options.put("name", "Just Bike");

            options.put("description", "Secured Payment Gateway");
            options.put("image", R.drawable.biker);
            // options.put("order_id", Common.getOrderId());
            options.put("currency", "INR");

            String razorpayAmt = String.valueOf(amount*100);  // 18% tax (*100 for paise)
            options.put("amount", "100");

            checkout.open(activity, options);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    String endTimeString ,endDateString;
    int endHour, endMin;
    private void endTimeChose() {
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog timePickerDialog =
          com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(this,0,0,0,false);
        timePickerDialog.setThemeDark(true);
        timePickerDialog.setTitle("Your Journey End Time");
        timePickerDialog.dismissOnPause(true);
        timePickerDialog.show(getSupportFragmentManager(), "Timeickerdialog");
    }

    int monthCal,dayCal;
    private void endDateChose() {
      final Calendar cldr = Calendar.getInstance();
//      int day = cldr.get(Calendar.DAY_OF_MONTH);
//      int month = cldr.get(Calendar.MONTH);
      int startYear = cldr.get(Calendar.YEAR);
      DatePickerDialog picker;


      // date picker dialog
      picker = new DatePickerDialog(ActiveJourney.this,
        new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            endDateString = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
            endTimeChose();
          }
        }, startYear, monthCal - 11, dayCal);
      picker.setMessage("Your Journey End Date");
      Calendar minCal = Calendar.getInstance();
      minCal.set(startYear, monthCal - 11, dayCal);
      picker.getDatePicker().setMinDate(minCal.getTimeInMillis());

      Calendar maxCal = Calendar.getInstance();

      DatabaseReference vehicleList = FirebaseDatabase.getInstance().getReference("Vehicles");
      if(bikeId!=null){
      vehicleList.child(bikeId).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          if (dataSnapshot.exists()) {
            Vehicle chosenVeh = dataSnapshot.getValue(Vehicle.class);
            if(chosenVeh!=null){
            List<List<Integer>> bookingSlots = chosenVeh.getBookingSlot();
            List<Pair<Integer, Integer>> datesGreaterThanSDate = new ArrayList<>();    // Pairs of Day , Month
            if (bookingSlots != null) {
              for (int i = 0; i < bookingSlots.size(); i++) {
                if (bookingSlots.get(i) != null) {
                  int startDateTime = bookingSlots.get(i).get(0);
                  int endDateTime = bookingSlots.get(i).get(1);
                  startDateTime -= 10000000;
                  endDateTime -= 10000000;
                  int sMon = 3, sDay = 21, eMon = 10, eDay = 28;
                  String startDate = String.valueOf(startDateTime);
                  String endDate = String.valueOf(endDateTime);

                  if (startDate.length() == 8) {
                    sDay = Integer.parseInt(startDate.substring(2, 4));
                    sMon = Integer.parseInt(startDate.substring(0, 2));
                  } else if (startDate.length() == 7) {
                    sDay = Integer.parseInt(startDate.substring(1, 3));
                    sMon = Integer.parseInt("0" + startDate.substring(0, 1));
                  }
                  Date date1 = new GregorianCalendar(startYear, monthCal - 10, dayCal).getTime();    // Initial end date
                  Date date2 = new GregorianCalendar(startYear, sMon - 1, sDay).getTime();
                  long diff = date2.getTime() - date1.getTime();
                  if (diff > 0)
                    datesGreaterThanSDate.add(new Pair<>(sDay, sMon - 1));
                  if (endDate.length() == 8) {
                    eDay = Integer.parseInt(endDate.substring(2, 4));
                    eMon = Integer.parseInt(endDate.substring(0, 2));
                  } else if (endDate.length() == 7) {
                    eDay = Integer.parseInt(endDate.substring(1, 3));
                    eMon = Integer.parseInt("0" + endDate.substring(0, 1));
                  }


                  date1 = new GregorianCalendar(startYear, monthCal - 10, dayCal).getTime();    // Chosen start date
                  date2 = new GregorianCalendar(startYear, eMon - 1, eDay).getTime();
                  diff = date1.getTime() - date2.getTime();
                  if (diff > 0)// Date is greater , should be added
                    datesGreaterThanSDate.add(new Pair<>(eDay, eMon - 1));
                }
              }
              if (datesGreaterThanSDate.size() > 0) {
                Pair<Integer, Integer> smallestPairDate = datesGreaterThanSDate.get(0);  //Initially taking it to be the first in list
                for (int i = 1; i < datesGreaterThanSDate.size(); i++) {
                  Pair<Integer, Integer> p = datesGreaterThanSDate.get(i);
                  Date date1 = new GregorianCalendar(startYear, smallestPairDate.second, smallestPairDate.first).getTime();    // Chosen start date
                  Date date2 = new GregorianCalendar(startYear, p.second, p.first).getTime();    //Pair of DD , MM
                  long diff = date2.getTime() - date1.getTime();
                  if (diff < 0)    // We need the smallest out of all dates greater than Selected start date
                  {
                    //date2 is smaller
                    smallestPairDate = new Pair<>(p.first, p.second);
                  }
                }
                maxCal.set(startYear, smallestPairDate.second, smallestPairDate.first);
                picker.getDatePicker().setMaxDate(maxCal.getTimeInMillis());

              }
            }
          }
            if(minCal.getTimeInMillis()<maxCal.getTimeInMillis())
              picker.getDatePicker().setMaxDate(maxCal.getTimeInMillis());
          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
          Toast.makeText(ActiveJourney.this, "Unable to find pre-booking dates", Toast.LENGTH_SHORT).show();
        }
      });
    }
     picker.show();
    }
    @Override
    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
        endHour = hourOfDay;
        if (minute%30!=0) {
            int minuteFloor = minute - (minute % 30);
            minute = minuteFloor + (minute == minuteFloor + 1 ? 30 : 0);
            if (minute == 60)
                minute = 0;
        }
        endMin = minute;
        endTimeString=(endHour<10?"0"+endHour:endHour)+":"+(endMin<10?"0"+endMin:endMin);
      openBottomLayForExtendRide();

    }

    private void bindViews() {
         readyBikeName=findViewById(R.id.readyBikeName);
         startTimeReady=findViewById(R.id.startTimeReady);
         endTimeReady=findViewById(R.id.endTimeReady);
         totalCostValue=findViewById(R.id.totalCostValue);
         activeOrEnd=findViewById(R.id.activeOrEnd);
         extendRide=findViewById(R.id.extendRide);
        trackBike = findViewById(R.id.trackBike);
        trackLayout = findViewById(R.id.trackLayout);
        extendLayout = findViewById(R.id.extendLayout);
      outstandingValue = findViewById(R.id.outstandingValue);
      payOutstanding = findViewById(R.id.payOutstanding);
    }

  private final int UPI_PAYMENT = 342;
  private String upiId;
  private void payUsingUPI() {

    Uri uri = Uri.parse("upi://pay").buildUpon()
      .appendQueryParameter("pa", upiId)
      .appendQueryParameter("pn", "Just Bike")
      .appendQueryParameter("tn", bikeName)
      .appendQueryParameter("am", String.valueOf(amount))
      .appendQueryParameter("cu", "INR")
      .build();
    Intent upiPaymentIntent = new Intent(Intent.ACTION_VIEW);
    upiPaymentIntent.setData(uri);

    Intent chooser = Intent.createChooser(upiPaymentIntent, "Pay with");

    if (chooser.resolveActivity(getPackageManager()) != null)
      startActivityForResult(chooser, UPI_PAYMENT);
    else {
      Toast.makeText(this, "No UPI app found! Please download any OR choose other payment options", Toast.LENGTH_LONG).show();
    }

  }

  private void upiPaymentDataOperation(ArrayList<String> dataList) {
    if (Common.isConnectedToInternet(this)) {
      String str = dataList.get(0);
      String paymentCancel = "";
      if (str == null)
        str = "discard";
      String status = "";
      String approvalRefNo = "";
      String response[] = str.split("&");
      for (int i = 0; i < response.length; i++) {
        String equalStr[] = response[i].split("=");
        if (equalStr.length >= 2) {
          if (equalStr[0].toLowerCase().equals("Status".toLowerCase()))
            status = equalStr[1].toLowerCase();
          else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) ||
            equalStr[0].toLowerCase().equals("txnRef".toLowerCase()))
            approvalRefNo = equalStr[1];
          else
            paymentCancel = "Payment Cancelled by You";
        }
      }
      if (status.equals("success")) {
        upiPaymentSuccessful();
        Toast.makeText(this, "Transaction Successful", Toast.LENGTH_SHORT).show();
      } else if (paymentCancel.equals("Payment Cancelled by You")) {
        Toast.makeText(this, "Payment Cancelled by You", Toast.LENGTH_SHORT).show();
      }
    }
  }
  int discountAmt=0;
  private void upiPaymentSuccessful() {
    if (Common.currentUser != null) {
      Request request;

      if(isOutstandingPay==1){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Requests");
        Map<String,Object> outstandingMap = new HashMap<>();
        outstandingMap.put("outstandingAmt","0");
        databaseReference.child(orderId).updateChildren(outstandingMap);
        Toast.makeText(ActiveJourney.this, "Thank You! , Balance cleared.", Toast.LENGTH_SHORT).show();
         databaseReference.child(orderId).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Common.currentRequest = dataSnapshot.getValue(Request.class);
            outstandingValue.setText("0");
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(ActiveJourney.this, "Error updating amount", Toast.LENGTH_SHORT).show();
          }
        });
      }
      else {
        DatabaseReference vehiclesRef = FirebaseDatabase.getInstance().getReference("Vehicles");
        List<Integer> listDate = new ArrayList<>();
        listDate.add(Integer.parseInt(endDateStringOfBooking));
        listDate.add(Integer.parseInt(endDateString));
        vehiclesRef.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot postData : dataSnapshot.getChildren()) {
              Vehicle v = postData.getValue(Vehicle.class);
              List<List<Integer>> bookingSlot = new ArrayList<>();
              if (v.getBookingSlot() != null) {
                bookingSlot = v.getBookingSlot();
                int pushCount = 0;
                if (v.getName().equals(bikeName)) {
                  if (bookingSlot != null)
                    pushCount = bookingSlot.size();
                  vehiclesRef.child(postData.getKey()).child("bookingSlot").child(pushCount + "").setValue(listDate);
                }
              } else {
                int pushCount = 0;
                if (v.getName().equals(bikeName)) {
                  if (bookingSlot != null)
                    pushCount = bookingSlot.size();
                  vehiclesRef.child(postData.getKey()).child("bookingSlot").child(pushCount + "").setValue(listDate);
                }
              }
            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
        });
        request = new Request(Common.currentUser.getPhone(), Common.currentRequest.getAddress(), Common.currentRequest.getPurpose(),
          Common.currentUser.getName(),
          Common.currentRequest.getOrderList(), String.valueOf(amount),
          Common.currentRequest.getComment(), "0", "1",
          "latlng", Common.currentRequest.getOutstandingAmt(), String.valueOf(discountAmt), "UPI/PAYTM",
          endDateStringOfBooking, endDateString, Common.currentRequest.getNoOfHelmets(), 0, 0);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Requests");

        String orderNo = String.valueOf(System.currentTimeMillis());
        databaseReference.child(orderNo).setValue(request);
        Toast.makeText(ActiveJourney.this, "Thank You! , Order Received", Toast.LENGTH_SHORT).show();
        sendNotification(orderNo);
        Common.currentRequest = request;
        Intent i = new Intent(this, OrderConfirmation.class);
        i.putExtra("orderNo", orderNo);
        startActivity(i);
        finish();
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case UPI_PAYMENT:
        if (resultCode == RESULT_OK || resultCode == 11) {
          if (data != null) {
            String trxt = data.getStringExtra("response");
            ArrayList<String> dataList = new ArrayList<>();
            dataList.add(trxt);
            upiPaymentDataOperation(dataList);
          } else {
            ArrayList<String> dataList = new ArrayList<>();
            dataList.add("nothing");
            upiPaymentDataOperation(dataList);
          }
        } else {
          ArrayList<String> dataList = new ArrayList<>();
          dataList.add("nothing");
          upiPaymentDataOperation(dataList);
        }
        break;
    }
  }
  APIService apiService;

  private void sendNotification(String orderNo) {
    DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
    Query data = tokens.orderByChild("isServerToken").equalTo(true);
    data.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot postDat : dataSnapshot.getChildren()) {
          Token serverToken = postDat.getValue(Token.class);

          //Creating RAW payload to send
          Notification notification = new Notification("You have new order" + orderNo, "Just Bike");
          Sender content = new Sender(serverToken.getToken(), notification);
          apiService.sendNotification(content).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
              if (response.code() == 200) {
                if (response.body().success == 1) {
                  Toast.makeText(ActiveJourney.this, "Vehicle Booked Successfully!", Toast.LENGTH_SHORT).show();
                  finish();
                } else
                  Toast.makeText(ActiveJourney.this, "Failed to send Notication!!", Toast.LENGTH_SHORT).show();
              }

            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
              Log.e("Error!", t.getMessage());
            }
          });
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });

    //Sending it via users
    DatabaseReference staff = FirebaseDatabase.getInstance().getReference("Tokens");
    Query dataStaff = data.orderByChild("isServerToken").equalTo(true);
    dataStaff.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot postDat : dataSnapshot.getChildren()) {
          UserModel serverToken = postDat.getValue(UserModel.class);

          //Creating RAW payload to send
          Notification notification = new Notification("You have new order" + orderNo, "Just Bike");
          Sender content = new Sender(serverToken.getPhone(), notification);
          apiService.sendNotification(content).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
              if (response.code() == 200) {
                if (response.body().success == 1) {
                  Toast.makeText(ActiveJourney.this, "Vehicle Booked Successfully!", Toast.LENGTH_SHORT).show();
                  finish();
                } else
                  Toast.makeText(ActiveJourney.this, "Failed to send Notication!!", Toast.LENGTH_SHORT).show();
              }

            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
              Log.e("Error!", t.getMessage());
            }
          });
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

}
