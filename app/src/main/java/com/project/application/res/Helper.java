package com.project.application.res;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Helper {

    private static Helper myHelper;

    public Helper() {

    }

    public static synchronized Helper getInstance() {
        if (myHelper == null) {
            myHelper = new Helper();
        }
        return myHelper;
    }


    public void nuke() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                            return myTrustedAnchors;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception e) {
        }
    }

    public boolean checkInternetAvailability(Context ctx) {
        ConnectivityManager conMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        boolean flag = false;

        if (netInfo != null) {
            flag = true;
        }

        return flag;
    }

    public ProgressDialog showProgressDialog(Context cxt, String title, String msg) {
        final ProgressDialog progressDialog = new ProgressDialog(cxt);
        progressDialog.setTitle(title);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(true);
        return progressDialog;
    }
    final private Calendar calendar = Calendar.getInstance();
    public String getCurrentDate(String type) {

        SimpleDateFormat mdformat = null;
        if(type.equalsIgnoreCase("normal")){
            mdformat = new SimpleDateFormat("yyyy-MM-dd");
        }
        else{
            mdformat = new SimpleDateFormat("dd");
        }


        return mdformat.format(calendar.getTime());
    }
    public String getCurrentTime(){
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        return mdformat.format(calendar.getTime());
    }


    public void getTimeAndDate(final Context ctx){










    }



}
