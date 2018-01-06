package com.paytmdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGActivity;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.Checksum;

/**
 * Created by knoxpo on 22/12/17.
 */

public class PayTm extends ReactContextBaseJavaModule {

    public PayTm(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "PayTm";
    }

    @ReactMethod
    public  void startPayment(String orderId, String checkSum, final Promise promise){
        PaytmPGService Service = PaytmPGService.getStagingService();


        //Kindly create complete Map and checksum on your server side and then put it here in paramMap.


        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("MID" , "XXXXXXXXX");
        paramMap.put("ORDER_ID" , orderId);
        paramMap.put("CUST_ID" , "CUST00001");
        paramMap.put("INDUSTRY_TYPE_ID" , "Retail");
        paramMap.put("CHANNEL_ID" , "WAP");
        paramMap.put("TXN_AMOUNT" , "1.00");
        paramMap.put("WEBSITE" , "APP_STAGING");
        paramMap.put( "EMAIL" , "abc@gmail.com");
        paramMap.put( "MOBILE_NO" , "9999999999");
        paramMap.put("CALLBACK_URL" , "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp");
        paramMap.put("CHECKSUMHASH" , checkSum);

        PaytmOrder Order = new PaytmOrder(paramMap);


        Service.initialize(Order, null);
        Log.d("LOG", "Service ini : " );
        Service.startPaymentTransaction(getCurrentActivity(), true, true,
                new PaytmPaymentTransactionCallback() {

                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                        Log.d("LOG", "UI Error : " + inErrorMessage);

                        promise.reject(Integer.valueOf(-1).toString(), inErrorMessage);
                    }

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Log.d("LOG", "Payment Transaction : " + inResponse);

                        JSONObject json = new JSONObject();
                        Set<String> keys = inResponse.keySet();
                        for (String key : keys) {
                            try {
                                // json.put(key, bundle.get(key)); see edit below
                                json.put(key, (inResponse.get(key)));
                            } catch(JSONException e) {
                                //Handle exception here
                                Log.d("LOG", e.getMessage());
                            }
                        }
                        Log.d("LOG: Json Response: ", json.toString());
                        promise.resolve(json.toString());
                       // Toast.makeText(getReactApplicationContext(), "Payment Transaction response "+inResponse.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() {
                        // If network is not
                        // available, then this
                        // method gets called..
                        promise.reject(Integer.valueOf(-1).toString(), "No Network Available");
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                        Log.d("LOG", "Auth Error : " + inErrorMessage);
                        promise.reject(Integer.valueOf(-1).toString(), inErrorMessage);
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {
                        Log.d("LOG", "ErrorLoadingWebPage " + inErrorMessage);
                        promise.reject(Integer.valueOf(iniErrorCode).toString(), inErrorMessage + " URL: " + inFailingUrl);
                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                       // Toast.makeText(getReactApplicationContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                        promise.reject(Integer.valueOf(-1).toString(), inErrorMessage);
                    }

                });
    }
}
