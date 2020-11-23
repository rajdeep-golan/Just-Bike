package com.just.justbikev2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Model.Career;
import com.just.justbikev2.Model.Order;
import com.just.justbikev2.Model.Request;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class OrderConfirmation extends AppCompatActivity {
    ImageButton trackBtn,downloadPDF;
    String orderNo;
    ImageView tick;

  int pageWidth = 1200,pageHeight= 2010 ;
  Bitmap bmp ,scaledBmp;
  Bitmap cbr;


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
        setContentView(R.layout.activity_order_confirmation);

        trackBtn = findViewById(R.id.trackBtn);
      tick = findViewById(R.id.tick);
      Glide.with(this).asGif().load(R.drawable.tick).into(tick);

        Intent i = getIntent();
        if(i!=null){
            orderNo=i.getStringExtra("orderNo");
        }
        trackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OrderConfirmation.this, TrackingOrderList.class);
                i.putExtra("orderStatus","0");  //0 means Order is placed. When employee sees order , status becomes 0 =>  Still Time to reach you
                i.putExtra("orderNo",orderNo);
                startActivity(i);
                finish();
            }
        });

      downloadPDF= findViewById(R.id.downloadPDF);
      bmp = BitmapFactory.decodeResource(getResources(),R.drawable.logo_chotu);
      // scaledBmp = Bitmap.createScaledBitmap(bmp,pageWidth,518,false);
    if(Common.currentRequest!=null) {
      downloadPDF.setVisibility(View.VISIBLE);
         cbr = BitmapFactory.decodeResource(getResources(), R.drawable.cbr250r);
      downloadPDF.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Dexter.withContext(OrderConfirmation.this).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {


                              PdfDocument pdf = new PdfDocument();
                              Paint paint = new Paint();
                              Paint titlePaint = new Paint();
                              titlePaint.setTextAlign(Paint.Align.LEFT);
                              titlePaint.setTextSize(20);
                              titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                              PdfDocument.PageInfo info = new PdfDocument.PageInfo.Builder(pageWidth, 2010, 1).create();
                              PdfDocument.Page page = pdf.startPage(info);

                              Canvas canvas = page.getCanvas();
                              canvas.drawBitmap(bmp, 40, 20, paint);

                              scaledBmp = Bitmap.createScaledBitmap(cbr, 400, 300, false);
                              canvas.drawBitmap(scaledBmp, pageWidth - 400, 150, paint);

                              Bitmap cityMap = BitmapFactory.decodeResource(getResources(), R.drawable.kolkata_map);
                              scaledBmp = Bitmap.createScaledBitmap(cityMap, 300, 200, false);
                              canvas.drawBitmap(scaledBmp, pageWidth - 350, pageHeight-250, paint);

                              canvas.drawText("Just Bike Services Pvt. Ltd.", 20, 150, titlePaint);
                              canvas.drawText("1100 University St, Seattle ", 20, 180, titlePaint);
                              canvas.drawText("WA, United States - 98101", 20, 210, titlePaint);

                              paint.setColor(getResources().getColor(R.color.black));
                              paint.setTextSize(20);
                              paint.setTextAlign(Paint.Align.RIGHT);
                              titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
                              canvas.drawText("Call: 8585858586", pageWidth - 20, 40, paint);
                              canvas.drawText("Visit: www.justbike.in", pageWidth - 20, 80, paint);

                              titlePaint.setTextAlign(Paint.Align.CENTER);
                              titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                              titlePaint.setTextSize(70);
                              canvas.drawText("Invoice", pageWidth / 2, 100, titlePaint);

                              titlePaint.setTextAlign(Paint.Align.LEFT);
                              titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                              titlePaint.setTextSize(35);

                              titlePaint.setColor(Color.GRAY);
                              canvas.drawText("Start Date" , 20, 500, titlePaint);
                              canvas.drawText("End Date" , 20, 550, titlePaint);
                              canvas.drawText("Customer Details" , 20, 600, titlePaint);

                              titlePaint.setColor(getResources().getColor(R.color.black));
                              // Chnage the below 2 to Request . get start time and end time , don;t change x,y
                              if(Common.currentRequest.getStartDate()!=null)
                                canvas.drawText(Common.currentRequest.getStartDate(), 200, 500, titlePaint);
                              else
                                 canvas.drawText("22/11/2020 , 19:30", 200, 500, titlePaint);
                              if(Common.currentRequest.getEndDate()!=null)
                                canvas.drawText(Common.currentRequest.getEndDate(), 200, 550, titlePaint);
                              else
                                canvas.drawText("24/11/2020 , 12:00", 200, 550, titlePaint);

                              canvas.drawText(Common.currentUser.getName(), 20, 650, titlePaint);
                              canvas.drawText(Common.currentUser.getPhone(), 20, 700, titlePaint);

                              //Put Request . get adress
                              canvas.drawText(Common.currentUser.getHomeAddress(), 20, 750, titlePaint);

                              titlePaint.setTextAlign(Paint.Align.LEFT);
                              canvas.drawText("Order No.: " + SystemClock.currentThreadTimeMillis(), 20, 300, titlePaint);

                              Date date = new Date();
                              SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                              canvas.drawText("Date: " + sdf.format(date), 20, 350, titlePaint);

                              sdf = new SimpleDateFormat("HH:mm:ss");
                              canvas.drawText("Time: " + sdf.format(date), 20, 400, titlePaint);

                              canvas.drawText("Payment Method: Electronic" , pageWidth-480, 600, titlePaint);

                              paint.setStyle(Paint.Style.FILL);
                           //   paint.setStrokeWidth(2);
                              canvas.drawRect(20, 780, pageWidth - 20, 860, paint);

                              paint.setColor(Color.WHITE);
                              paint.setTextAlign(Paint.Align.LEFT);
                              paint.setStyle(Paint.Style.FILL);
                              paint.setTextSize(30);
                              canvas.drawText("Item", 40, 830, paint);

                              paint.setColor(Color.GRAY);
                              canvas.drawText("Authorised Signatory", 40, pageHeight-300, paint);
                              canvas.drawText("Selected City", pageWidth-300, pageHeight-300, paint);

                              Bitmap sign = BitmapFactory.decodeResource(getResources(), R.drawable.signature);
                              scaledBmp = Bitmap.createScaledBitmap(sign, 300, 150, false);
                              canvas.drawBitmap(scaledBmp,40, pageHeight-250,paint);

                              paint.setTextAlign(Paint.Align.RIGHT);
                              paint.setColor(Color.WHITE);
                              canvas.drawText("Amount (INR)", pageWidth - 80, 830, paint);

                              canvas.drawLine(pageWidth - 280, 800, pageWidth - 280, 850, paint);

                              paint.setColor(Color.BLACK);

                              if(Common.currentRequest.getOrderList()!=null && Common.currentRequest.getOrderList().size()>0) {
                                int i=0;
                                for(Order order :Common.currentRequest.getOrderList()) {
                                  paint.setTextAlign(Paint.Align.LEFT);
                                  canvas.drawText(order.getProductName(), 40, 950+ (i*50), paint);

                                  paint.setTextAlign(Paint.Align.RIGHT);
                                  canvas.drawText(order.getPrice(), pageWidth - 80, 950+ (i*50) , paint);
                                  i++;
                                }

                                paint.setTextAlign(Paint.Align.RIGHT);
                                if(Common.currentRequest.getDiscount()!=null)
                                 canvas.drawText(Common.currentRequest.getDiscount(), pageWidth - 40, 1400, paint);
                                else
                                  canvas.drawText("0.00", pageWidth - 40, 1400, paint);

                                paint.setTextSize(50);
                                canvas.drawText(Common.currentRequest.getTotal(), pageWidth - 40, 1500, paint);
                              }

                              paint.setTextSize(50);
                              canvas.drawText("Discount ₹", 850, 1400, paint);

                              canvas.drawText("Total ₹", 450, 1480, paint);
                              paint.setTextSize(35);
                              paint.setColor(Color.GRAY);
                              canvas.drawText(" ( Inclusive of all taxes )", 850, 1500, paint);




                              pdf.finishPage(page);
                              String timestamp = new SimpleDateFormat("yyyyMMdd_hhss").format(new Date());
                              File file = new File(Environment.getExternalStorageDirectory(), "/receipt_just_bike_" + timestamp + ".pdf");

                              try {
                                pdf.writeTo(new FileOutputStream(file));
                                Toast.makeText(OrderConfirmation.this, "Downloaded! Please wait and later check your files.", Toast.LENGTH_SHORT).show();
                                waitingDialog = new SpotsDialog.Builder()
                                  .setContext(OrderConfirmation.this)
                                  .setTheme(R.style.Custom)
                                  .build();
                                waitingDialog.show();
                                previewPdf(file);

                              } catch (IOException e) {
                                e.printStackTrace();
                              }
                              pdf.close();
                            }

              @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                            }
                          }
            ).check();
        }
      });
    }
    else
    {
      downloadPDF.setVisibility(View.GONE);
    }
    }

//  public static Bitmap getBitmapFromURL(String src) {
//    try {
//      URL url = new URL(src);
//      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//      connection.setDoInput(true);
//      connection.connect();
//      InputStream input = connection.getInputStream();
//      Bitmap myBitmap = BitmapFactory.decodeStream(input);
//      return myBitmap;
//    } catch (IOException e) {
//      // Log exception
//      return null;
//    }
//  }


  android.app.AlertDialog waitingDialog;
  private void previewPdf(File file) {
    PackageManager packageManager = getPackageManager();
    Intent testIntent = new Intent(Intent.ACTION_VIEW);
    testIntent.setType("application/pdf");
    List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
    if (list.size() > 0) {
      Intent intent = new Intent();
      intent.setAction(Intent.ACTION_VIEW);
      Uri uri;
      if (Build.VERSION.SDK_INT < 24)
        uri = Uri.fromFile(file);
      else
        uri = Uri.parse(file.getPath()); // My work-around for new SDKs, doesn't work in Android 10.
      intent.setDataAndType(uri, "application/pdf");
      startActivity(intent);
      waitingDialog.dismiss();

    } else {
      waitingDialog.dismiss();

      Toast.makeText(this, "Download a PDF Viewer to see the generated PDF", Toast.LENGTH_SHORT).show();
    }
  }
}
