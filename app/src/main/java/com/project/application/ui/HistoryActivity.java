package com.project.application.ui;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import com.project.application.printer.BluetoothPrinter;
import com.project.application.res.CustomAdapter;
import com.project.application.res.Helper;
import com.project.application.res.PaymentHolder;
import com.project.application.myapplication.R;
import com.project.com1.volley.VolleySingleton;

public class HistoryActivity extends AppCompatActivity {

    ArrayList<PaymentHolder> dataModels;
    ListView listView;
    Context ctx;
    private static CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ctx = this;

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        final ArrayList<PaymentHolder> result = new ArrayList<>();
        listView = (ListView) findViewById(R.id.historyListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Already Paid");
                builder.setMessage("Reprint Ticket ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final SharedPreferences preferences = getSharedPreferences("appInfo", MODE_PRIVATE);
                        final String COMPANY_NAME = preferences.getString("companyName", "N/A");
                        final String COMPANY_ADDRESS = preferences.getString("companyAddress", "N/A");
                        final String COMPANY_TELEPHONE = preferences.getString("companyTelephone", "N/A");
                        final int USER_ID = preferences.getInt("userid", 000);
                        final PaymentHolder HOLDER = result.get(position);


                        final String insertID = HOLDER.getTicketID();
                        final String date = HOLDER.getDate();
                        final String time = HOLDER.getTime();
                        final String vehNo = HOLDER.getVehicleNo();

                        String month = getMonth(date.split("-")[1].replaceAll(" ", ""));


                        printTicket(COMPANY_NAME, COMPANY_ADDRESS, COMPANY_TELEPHONE, insertID, vehNo, null, month, date, time);

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        final Helper HELPER = new Helper();

        if (HELPER.checkInternetAvailability(ctx) == true) {

            final ProgressDialog progressDialog = Helper.getInstance().showProgressDialog(ctx, "Please Wait", "Data Receiving ...");
            progressDialog.show();

            final SharedPreferences preferences = getSharedPreferences("appInfo", MODE_PRIVATE);

            final int USER_ID = preferences.getInt("userid", 0);
            final String DATE = new Helper().getCurrentDate("normal");


            Log.e("VolleyDate", DATE);

            HELPER.nuke();


            final String URL = getResources().getString(R.string.url) + "HistoryServlet?userid=" + String.valueOf(USER_ID + "&date=" + String.valueOf(DATE));

            final RequestQueue queue = VolleySingleton.getInstance(ctx).getRequestQueue();


            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    progressDialog.dismiss();


                    if (response.length() > 0) {


                        result.clear();

                        for (int i = 0; i < response.length(); i++) {
                            try {


                                JSONObject jsonObject = response.getJSONObject(i);
                                result.add(new PaymentHolder(jsonObject.getString("ticketID"), jsonObject.getString("vehNo"), jsonObject.getString("date"), jsonObject.getString("time"), jsonObject.getString("balance")));


                            } catch (JSONException e) {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctx);
                                builder.setTitle("Exception");
                                builder.setMessage(e.getMessage())
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //do things
                                            }
                                        });
                                android.app.AlertDialog alert = builder.create();
                                alert.show();
                            }

                        }

                        Collections.reverse(result);

                        CustomAdapter adapter = new CustomAdapter(result, ctx);

                        listView.setAdapter(adapter);
                    } else {
                        Toast.makeText(ctx, "No Data!", Toast.LENGTH_SHORT).show();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctx);
                    builder.setTitle("Exception");
                    builder.setMessage(error.getMessage())
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                }
            });
            queue.add(jsonArrayRequest);


        } else {
            Toast.makeText(ctx, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }


    }

    private void printTicket(final String companyName, final String address, final String telephone, final String ticketID, final String vehicleNo, final String oName, final String month, final String date, final String time) {


        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice mBtDevice = btAdapter.getBondedDevices().iterator().next();   // Get first paired device

        final BluetoothPrinter mPrinter = new BluetoothPrinter(mBtDevice);


        mPrinter.connectPrinter(new BluetoothPrinter.PrinterConnectListener() {

            @Override
            public void onConnected() {
                try {

                    String[] ad = address.split(",");
                    final String adno = ad[0];
                    final String adstreet = ad[1];
                    final String adcity = ad[2];

                    mPrinter.setAlign(BluetoothPrinter.ALIGN_CENTER);
                    mPrinter.printText("ELECTRONIC FEE COLLECTION SYSTEM");
                    mPrinter.addNewLine();
                    mPrinter.printText("MUNICIPAL COUNCIL KANDY ");
                    mPrinter.addNewLine();
                    mPrinter.printText(companyName);
                    mPrinter.addNewLine();
                    mPrinter.printText(adno + adstreet);
                    mPrinter.addNewLine();
                    mPrinter.printText(adcity);
                    mPrinter.addNewLine();
                    mPrinter.printText("HELP DESK " + telephone);
                    mPrinter.addNewLine();
                    mPrinter.printLine();
                    mPrinter.addNewLine();
                    mPrinter.setAlign(BluetoothPrinter.ALIGN_LEFT);

                    mPrinter.printText("Ticket ID   - " + ticketID);
                    mPrinter.addNewLine();

                    mPrinter.printText("Vehicle No  - " + vehicleNo);
                    mPrinter.addNewLine();

                    mPrinter.printText("Month       - " + month);
                    mPrinter.addNewLine();

                    mPrinter.printText("Date        - " + date);
                    mPrinter.addNewLine();

                    mPrinter.printText("Time        - " + time);
                    mPrinter.addNewLine();


                    mPrinter.printText("Total       - 50 LKR");
                    mPrinter.addNewLine();
                    mPrinter.printLine();
                    mPrinter.setAlign(BluetoothPrinter.ALIGN_CENTER);
                    mPrinter.printText("Thank You!");
                    mPrinter.addNewLine();
                    mPrinter.addNewLines(3);


                    mPrinter.finish();

                } catch (Exception e) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctx);
                    builder.setTitle("Exception");
                    builder.setMessage(e.getMessage())
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                }


            }

            @Override
            public void onFailed() {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctx);
                builder.setTitle("Printer Error");
                builder.setMessage("Initialization Failed!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                android.app.AlertDialog alert = builder.create();
                alert.show();
            }


        });


    }

    private String getMonth(String month) {

        String monthString = null;

        switch (month) {
            case "01":
                monthString = "January";
                break;
            case "02":
                monthString = "February";
                break;
            case "03":
                monthString = "March";
                break;
            case "04":
                monthString = "April";
                break;
            case "05":
                monthString = "May";
                break;
            case "06":
                monthString = "June";
                break;
            case "07":
                monthString = "July";
                break;
            case "08":
                monthString = "August";
                break;
            case "09":
                monthString = "September";
                break;
            case "10":
                monthString = "October";
                break;
            case "11":
                monthString = "November";
                break;
            case "12":
                monthString = "December ";
                break;
        }
        return monthString;
    }
}
