package com.project.application.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.project.application.res.Helper;
import com.project.application.myapplication.R;
import com.project.application.volley.CustomRequest;
import com.project.com1.volley.VolleySingleton;

public class LoginActivity extends AppCompatActivity {

    private Context ctx;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ctx = this;

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);


        preferences = getSharedPreferences("appInfo", MODE_PRIVATE);

        if (isFirst() == true) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }

        ctx = LoginActivity.this;


        Button b = findViewById(R.id.loginButton);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Helper.getInstance().checkInternetAvailability(ctx) == true) {
                     String username = ((EditText) findViewById(R.id.un)).getText().toString();
                    final String password = ((EditText) findViewById(R.id.pw)).getText().toString();

                    if (username.isEmpty() || password.isEmpty()) {
                        Toast.makeText(ctx, "Fill All Fields", Toast.LENGTH_SHORT).show();
                    } else {
                        final ProgressDialog progressDialog = Helper.getInstance().showProgressDialog(LoginActivity.this, "Please Wait", "Data Receiving ...");
                        progressDialog.show();


                        Helper.getInstance().nuke();

                        final Map<String, String> map = new HashMap<>();
                        map.put("username", username);
                        map.put("password", password);


                        final String URL = getResources().getString(R.string.url) + "LoginServlet";

                        final RequestQueue requestQueue = VolleySingleton.getInstance(ctx).getRequestQueue();
                        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, URL, map, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressDialog.dismiss();
                                if (response.length() == 5) {

                                    try {
                                         int userID = response.getInt("id");
                                         String userName = response.getString("name");


                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putInt("userid", userID);
                                        editor.putString("name", userName);

                                        editor.putBoolean("isLogin", true);

                                        editor.putString("companyName", response.getString("companyName"));
                                        editor.putString("companyAddress", response.getString("companyAddress"));
                                        editor.putString("companyTelephone", response.getString("companyTelephone"));


                                        editor.commit();

                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    Toast.makeText(ctx, "Authentication Failed!", Toast.LENGTH_SHORT).show();
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
                } else {
                    Toast.makeText(ctx, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    private Boolean isFirst() {
        return preferences.getBoolean("isLogin", false);
    }
}
