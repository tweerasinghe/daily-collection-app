package com.project.application.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import com.project.application.res.Helper;
import com.project.application.myapplication.R;
import com.project.application.volley.CustomRequest;
import com.project.com1.volley.VolleySingleton;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView scannerView;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        ctx = this;

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
    }

    @Override
    public void handleResult(Result result) {

        if (Helper.getInstance().checkInternetAvailability(ctx) == true) {


            final ProgressDialog progressDialog = Helper.getInstance().showProgressDialog(ctx, "Please Wait", "Data Receiving ...");
            progressDialog.show();

            String[] lines = result.getText().split("\\n");

            final String VEHICLE_NO = lines[2].split("-")[1].replaceAll(" ", "");


            Helper.getInstance().nuke();

            final Map<String, String> map = new HashMap<>();


            map.put("vehNo", VEHICLE_NO);


            final String URL = getResources().getString(R.string.url) + "VehicleSearchServlet";

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


                            onBackPressed();

                            final Intent intent = new Intent(ScanActivity.this, PrintActivity.class);
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
                                    Toast.makeText(ctx, "Payment Complete!", Toast.LENGTH_SHORT).show();
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


        } else {
            Toast.makeText(ctx, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}
