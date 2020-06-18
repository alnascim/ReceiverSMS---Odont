package com.example.receiversms;

import android.Manifest;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final long START_TIME_IN_MILLIS = 5000;
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 10;
    private static final int PERMISSION_SEND_SMS = 1;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private CountDownTimer mCountDownTimer;
    RequestQueue requestQueue;
    TextView txtAviso;
    Button btnSend;
    int iIDTabela;
    String txtMessage;

    private String select_return_URL = "http://prajaoficial1.hospedagemdesites.ws/dropshopping/php_select_return.php";
    private String update_status_return_URL = "http://prajaoficial1.hospedagemdesites.ws/dropshopping/php_update_status_return.php";

    Timer timer = new Timer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtAviso = (TextView) findViewById(R.id.txtAviso);
        btnSend= (Button) findViewById(R.id.btnSend);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        //startTimer();
        ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_REQUEST_READ_SMS);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("11984197482",null,"Teste",null,null);


            }
        });
    }
    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                sendSMSMessage();
                //mCountDownTimer.cancel();
                mTimeLeftInMillis = START_TIME_IN_MILLIS;
                updateCountDownText();
                startTimer();
            }
        }.start();

    }
    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        txtAviso.setText(timeLeftFormatted);
    }
    protected <MultipleMsg> void sendSMSMessage() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, select_return_URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int i = 0;
                try {
                    JSONArray marcacoes = response.getJSONArray("marcacoes");
                    JSONObject marcacao = marcacoes.getJSONObject(i);

                    String status_retorno = marcacao.getString("status_retorno");
                    String phoneNo= marcacao.getString("telefone");

                    iIDTabela=marcacao.getInt("ID");
                    if (status_retorno.equals("1")) {
                        txtMessage = "Obrigado ! Caso haja qualquer alteração na sua confirmação, favor entrar em contato!";
                    }

                    if (status_retorno.equals("2")) {
                        txtMessage = "OK ! \n Entraremos em contato para reagendar a sua consulta.!";
                    }

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo,null,txtMessage,null,null);

                    //vUpdateTable(iIDTabela);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("ERRO",e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
        }
    public void vUpdateTable(final int iID) {
        StringRequest request = new StringRequest(Request.Method.POST,
                update_status_return_URL, new Response.Listener<String>() {
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
                parameters.put("id",Integer.toString(iID));
                return parameters;
            }
        };
        requestQueue.add(request);
    }
}
