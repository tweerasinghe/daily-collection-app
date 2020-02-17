package com.project.application.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.project.application.printer.BluetoothPrinter;
import com.project.application.res.Helper;
import com.project.application.myapplication.R;
import com.project.application.volley.CustomRequest;
import com.project.com1.volley.VolleySingleton;

public class PrintActivity extends AppCompatActivity {
    Context ctx;
    TextView qr, vehNo, ownerName, mobile1, mobile2, address, balance;
    String vehID;
    Button print;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        ctx = this;

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);


        final SharedPreferences preferences = getSharedPreferences("appInfo", MODE_PRIVATE);
        final String COMPANY_NAME = preferences.getString("companyName", "N/A");
        final String COMPANY_ADDRESS = preferences.getString("companyAddress", "N/A");
        final String COMPANY_TELEPHONE = preferences.getString("companyTelephone", "N/A");
        final int USER_ID = preferences.getInt("userid", 000);


        qr = findViewById(R.id.printQRCode);
        vehNo = findViewById(R.id.printVehNo2);
        ownerName = findViewById(R.id.printOwnerName2);
        mobile1 = findViewById(R.id.printMobile);
        mobile2 = findViewById(R.id.printMobile3);
        address = findViewById(R.id.printAddress2);
        print = findViewById(R.id.printTicket);
        balance = findViewById(R.id.balance);

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (qr.getText().toString().isEmpty()) {
                    Toast.makeText(ctx, "Empty Data!", Toast.LENGTH_SHORT).show();
                } else {
                    if (Helper.getInstance().checkInternetAvailability(ctx) == true) {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);
                        alertDialog.setTitle("Amount");
                        alertDialog.setMessage("Please enter amount");

                        final EditText input = new EditText(ctx);
                        input.setInputType(InputType.TYPE_CLASS_NUMBER);
                        alertDialog.setView(input);

                        alertDialog.setPositiveButton("Next",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        final String VALUE = input.getText().toString();

                                        if (VALUE.isEmpty()) {
                                            Toast.makeText(ctx, "Please Enter Amount", Toast.LENGTH_SHORT).show();
                                        } else {


                                            final ProgressDialog progressDialog = Helper.getInstance().showProgressDialog(ctx, "Please Wait", "Data Receiving ...");
                                            progressDialog.show();


                                            Helper.getInstance().nuke();

                                            final Map<String, String> map = new HashMap<>();
                                            map.put("vehID", vehID);
                                            map.put("userID", USER_ID + "");
                                            map.put("amount", VALUE + "");


                                            final String URL = getResources().getString(R.string.url) + "SaveServlet";

                                            final RequestQueue requestQueue = VolleySingleton.getInstance(ctx).getRequestQueue();
                                            CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, URL, map, new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    progressDialog.dismiss();


                                                    if (response.length() > 0) {
                                                        try {
                                                            final String status = response.getString("status");

                                                            if (status.equalsIgnoreCase("1")) {
                                                                Toast.makeText(ctx, "Payment Complete!", Toast.LENGTH_SHORT).show();
                                                            } else {


                                                                 String vehicleNumber = response.getString("vehicleNumber");
                                                                 String date = response.getString("date");
                                                                 String time = response.getString("time");
                                                                 String Cash = response.getString("cash");
                                                                 String remainBalance = response.getString("remainAndDays").split("-")[0];
                                                                 String remainDays = response.getString("remainAndDays").split("-")[1];
                                                                 String insertID = "1027";

                                                                if (status.equalsIgnoreCase("9")) {


                                                                    final String paymentdate = response.getString("paymentdate");
                                                                    printTicket(COMPANY_NAME, COMPANY_ADDRESS, COMPANY_TELEPHONE, insertID, vehicleNumber, date, time, Cash, remainBalance, remainDays, "2", paymentdate);

                                                                } else {
                                                                    printTicket(COMPANY_NAME, COMPANY_ADDRESS, COMPANY_TELEPHONE, insertID, vehicleNumber, date, time, Cash, remainBalance, remainDays, "1", "");

                                                                }


                                                                onBackPressed();

                                                            }


                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else {
                                                        Toast.makeText(ctx, "Server Error", Toast.LENGTH_SHORT).show();
                                                    }


                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Log.e("VolleyError", error.toString());
                                                }
                                            });

                                            requestQueue.add(jsObjRequest);


                                        }
                                    }
                                });

                        alertDialog.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                        alertDialog.show();


                    } else {
                        Toast.makeText(ctx, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });


        loadDataFromIntent(getIntent().getExtras());

    }

    private void loadDataFromIntent(Bundle extras) {
        final ProgressDialog progressDialog = Helper.getInstance().showProgressDialog(ctx, "Please Wait", "Data Receiving ...");
        progressDialog.show();

        vehID = extras.getString("vehID");
        qr.setText(extras.getString("qr"));
        vehNo.setText(extras.getString("vehNo"));
        ownerName.setText(extras.getString("ownerName"));
        mobile1.setText(extras.getString("mobile1"));
        mobile2.setText(extras.getString("mobile2"));
        address.setText(extras.getString("address"));
        balance.setText(extras.getString("balance"));


        progressDialog.dismiss();
    }

    private void printTicket(final String companyName, final String address, final String telephone, final String ticketID, final String vehicleNumber, final String date, final String time, final String cash, final String remainBalance, final String remainDays, final String type, final String paymentdate) {


        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice mBtDevice = btAdapter.getBondedDevices().iterator().next();   // Get first paired device

        final BluetoothPrinter mPrinter = new BluetoothPrinter(mBtDevice);


        mPrinter.connectPrinter(new BluetoothPrinter.PrinterConnectListener() {

            @Override
            public void onConnected() {
                try {

                    String[] ad = address.split(",");


                    mPrinter.setAlign(BluetoothPrinter.ALIGN_CENTER);
                    mPrinter.printTextAsImage("මහනුවර මහ නගර සභාවේ", 20f, 10, ctx);
                    mPrinter.printTextAsImage("අනුමත කුලී/ත්\u200Dරීරෝද රථ දෛනික", 20f, 10, ctx);
                    mPrinter.printTextAsImage("නැවතුම් ගාස්තු අයකිරීම", 20f, 10, ctx);
                    mPrinter.addNewLine();
                    mPrinter.printText("HELP DESK " + telephone);
                    mPrinter.addNewLine();
                    mPrinter.printLine();
                    mPrinter.addNewLine();

                    mPrinter.setAlign(BluetoothPrinter.ALIGN_LEFT);


                    mPrinter.printTextAsImage("ටිකට් අංකය : " + ticketID, 20f, 10, ctx);
                    mPrinter.printTextAsImage("වාහන අංකය : " + vehicleNumber, 20f, 10, ctx);

                    if (type.equalsIgnoreCase("1")) {
                        mPrinter.printTextAsImage("දිනය හා වේලාව : " + date + " | " + time, 20f, 10, ctx);
                    } else {
                        mPrinter.printTextAsImage("දිනය හා වේලාව : " + Helper.getInstance().getCurrentDate("normal") + " | " + time, 20f, 10, ctx);
                    }

                    mPrinter.printTextAsImage("වලංගු වන දින : ", 20f, 10, ctx);

                    if (type.equalsIgnoreCase("1")) {
                        mPrinter.printText(date);
                        mPrinter.addNewLine();
                    } else {


                        String[] ar = date.split(",");
                        for (String s : ar) {

                            String date = s.split("/")[0];
                            double price = Double.parseDouble(s.split("/")[1]);

                            mPrinter.printText(date + " | " + price + " LKR");
                            mPrinter.addNewLine();
                        }
                    }


                    mPrinter.printTextAsImage("දෛනික ගාස්තුව : " + Double.parseDouble(cash) + " LKR", 20f, 10, ctx);
                    mPrinter.printTextAsImage("හිඟ මුදල් එකතුව : " + Double.parseDouble(remainBalance) + " LKR", 20f, 10, ctx);
                    mPrinter.printTextAsImage("හිඟ දින ගණන : " + remainDays, 20f, 10, ctx);


                    mPrinter.addNewLines(3);


                    mPrinter.finish();

                } catch (Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setTitle("Exception");
                    builder.setMessage(e.getMessage())
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }


            }

            @Override
            public void onFailed() {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Printer Error");
                builder.setMessage("Initialization Failed!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }


        });


    }


}
