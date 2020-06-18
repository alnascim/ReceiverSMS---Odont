package com.example.receiversms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class InterceptSMS extends BroadcastReceiver {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private String insert_URL_Return = "http://prajaoficial1.hospedagemdesites.ws/dropshopping/php_insert_return.php";
    private String TAG = InterceptSMS.class.getSimpleName();
    private String NUMBER_FILTER="Teste";
    String txtMessage,txtnome;
    TextView texto;

    RequestQueue requestQueue;
    @Override
    public void onReceive(Context context, Intent intent) {
        requestQueue = Volley.newRequestQueue(context);
        Bundle extras= intent.getExtras();
        String phone="";
        String bodyMessage="";
        String message="";
        if(extras != null) {
            Object[] smsExtra = (Object[]) extras.get("pdus");
            for (int i = 0; i < smsExtra.length; i++) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
                phone = sms.getDisplayOriginatingAddress();
                bodyMessage=sms.getMessageBody();
            }

            if (bodyMessage.equals("1")) {
                final String finalPhone = phone;
                StringRequest request = new StringRequest(Request.Method.POST,
                        insert_URL_Return, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String,String> getParams() throws AuthFailureError {

                        Map<String,String> parameters = new HashMap<String,String>();
                        parameters.put("telefone", finalPhone);
                        parameters.put("status_retorno", "1");

                        return parameters;
                    }
                };
                requestQueue.add(request);

            }
            if (bodyMessage.equals("2")) {
               final String finalPhone = phone;
                StringRequest request = new StringRequest(Request.Method.POST,
                        insert_URL_Return, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String,String> getParams() throws AuthFailureError {

                        Map<String,String> parameters = new HashMap<String,String>();
                        parameters.put("telefone", finalPhone);
                        parameters.put("status_retorno", "2");
                        return parameters;
                    }
                };
                requestQueue.add(request);
            }


        }

    }

}
