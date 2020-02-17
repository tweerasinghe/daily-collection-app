package com.project.application.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.project.application.res.Helper;
import com.project.application.myapplication.R;
import com.project.application.volley.CustomRequest;
import com.project.com1.volley.VolleySingleton;

public class HomeActivity extends AppCompatActivity {

    CardView scanCardView, manualCardView, paymentCardView;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ctx = this;

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);


        FloatingActionButton fab = findViewById(R.id.logoutbtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

                builder.setMessage("Logout Now ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences preferences = getSharedPreferences("appInfo", MODE_PRIVATE);
                                SharedPreferences.Editor edit = preferences.edit();
                                edit.putBoolean("isLogin", false).commit();

                                Intent loginscreen = new Intent(ctx, LoginActivity.class);
                                loginscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(loginscreen);
                                HomeActivity.this.finish();


                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });


        scanCardView = findViewById(R.id.qrscan);
        manualCardView = findViewById(R.id.manualscan);
        paymentCardView = findViewById(R.id.paymenthostory);

        scanCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.getInstance().checkInternetAvailability(ctx) == true) {
                    startActivity(new Intent(getApplicationContext(), ScanActivity.class));
                } else {
                    Toast.makeText(ctx, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        manualCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Helper.getInstance().checkInternetAvailability(ctx) == true) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setTitle("Enter Vehicle No");

                    final EditText input = new EditText(ctx);
                    builder.setView(input);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (input.getText().toString().isEmpty()) {
                                Toast.makeText(ctx, "Please Enter Vehicle No", Toast.LENGTH_SHORT).show();
                            } else {
                                final ProgressDialog progressDialog = Helper.getInstance().showProgressDialog(ctx, "Please Wait", "Data Receiving ...");
                                progressDialog.show();

                                 String vehicleNumber = input.getText().toString().replaceAll(" ", "");


                                Helper.getInstance().nuke();

                                final Map<String, String> map = new HashMap<>();
                                map.put("vehNo", vehicleNumber);
                                map.put("date", Helper.getInstance().getCurrentDate("normal"));


                                final String URL = getResources().getString(R.string.url) + "SearchServletThroughVehNo";

                                final RequestQueue requestQueue = VolleySingleton.getInstance(ctx).getRequestQueue();
                                CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, URL, map, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        progressDialog.dismiss();


                                        if (response.length() == 8) {

                                            String vehicleID = null;
                                            String qrID = null;
                                            String vehNo = null;
                                            String ownerName = null;
                                            String mobile1 = null;
                                            String mobile2 = null;
                                            String address = null;
                                            String balance = null;

                                            try {
                                                vehicleID = response.getString("idvehicle");
                                                qrID = response.getString("QRID");
                                                vehNo = response.getString("vehNo");
                                                ownerName = response.getString("ownerName");
                                                mobile1 = response.getString("mobile1");
                                                mobile2 = response.getString("mobile2");
                                                address = response.getString("address");
                                                balance = response.getString("balance");

                                                Log.e("TTTT", balance);

                                                onBackPressed();

                                                Intent intent = new Intent(HomeActivity.this, PrintActivity.class);
                                                intent.putExtra("vehID", vehicleID);
                                                intent.putExtra("qr", qrID);
                                                intent.putExtra("vehNo", vehNo);
                                                intent.putExtra("ownerName", ownerName);
                                                intent.putExtra("mobile1", mobile1);
                                                intent.putExtra("mobile2", mobile2);
                                                intent.putExtra("address", address);
                                                intent.putExtra("balance", balance);

                                                startActivity(intent);


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }


                                        } else if (response.length() == 1) {
                                            try {
                                                int result = response.getInt("status");
                                                switch (result) {
                                                    case 0:
                                                        Toast.makeText(ctx, "Invalid QR!", Toast.LENGTH_SHORT).show();
                                                        onBackPressed();
                                                        break;
                                                    case 1:
                                                        Toast.makeText(ctx, "Already Paid!", Toast.LENGTH_SHORT).show();
                                                        onBackPressed();
                                                        break;
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Toast.makeText(ctx, "Invalid Data!", Toast.LENGTH_SHORT).show();
                                            onBackPressed();
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
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                } else {

                    Toast.makeText(ctx, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }


            }
        });

        paymentCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Helper.getInstance().checkInternetAvailability(ctx) == true) {


                    final int USER_ID = getSharedPreferences("appInfo", MODE_PRIVATE).getInt("userid", 000);


                    final ProgressDialog progressDialog = Helper.getInstance().showProgressDialog(ctx, "Please Wait", "Data Receiving ...");
                    progressDialog.show();


                    Helper.getInstance().nuke();

                    final Map<String, String> map = new HashMap<>();

                    String date = Helper.getInstance().getCurrentDate("normal");
                    map.put("date", date);
                    map.put("userID", USER_ID + "");

                    ;


                    final String URL = getResources().getString(R.string.url) + "GetHistoryCount";

                    final RequestQueue requestQueue = VolleySingleton.getInstance(ctx).getRequestQueue();
                    CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, URL, map, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();


                            try {
                                final String result = response.getString("status");


                                switch (result) {
                                    case "0":
                                        Toast.makeText(ctx, "No Records!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case "1":
                                        startActivity(new Intent(getApplicationContext(), HistoryActivity.class));

                                        break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VolleyError", error.toString());
                        }
                    });

                    requestQueue.add(jsObjRequest);


                } else {
                    Toast.makeText(ctx, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    @Override
    public void onBackPressed() {

    }
}
