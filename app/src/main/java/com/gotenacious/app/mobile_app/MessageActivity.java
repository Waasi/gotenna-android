package com.gotenacious.app.mobile_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gotenna.sdk.GoTenna;
import com.gotenna.sdk.bluetooth.GTConnectionManager;
import com.gotenna.sdk.commands.GTCommandCenter;
import com.gotenna.sdk.exceptions.GTInvalidAppTokenException;
import com.gotenna.sdk.messages.GTBaseMessageData;
import com.gotenna.sdk.messages.GTLocationMessageData;
import com.gotenna.sdk.messages.GTMessageData;
import com.gotenna.sdk.messages.GTTextOnlyMessageData;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MessageActivity extends AppCompatActivity implements GTCommandCenter.GTMessageListener, GTConnectionManager.GTConnectionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            GoTenna.setApplicationToken(getApplicationContext(), "SDK_KEY_HERE");
            GTConnectionManager.getInstance().scanAndConnect(GTConnectionManager.GTDeviceType.MESH);
            GTConnectionManager.getInstance().addGtConnectionListener(this);
            GTCommandCenter.getInstance().setMessageListener(this);
        } catch (GTInvalidAppTokenException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_message);
    }

    @Override
    public void onIncomingMessage(GTMessageData gtMessageData) {
    }

    @Override
    public void onIncomingMessage(GTBaseMessageData gtBaseMessageData) {
        GTTextOnlyMessageData gtTextOnlyMessageData = (GTTextOnlyMessageData) gtBaseMessageData;

        // Get Text
        String text = gtTextOnlyMessageData.getText();

        // Parse Text

        if(text.startsWith("SOS")) {
            String[] messageData = text.split("]");
            String type = messageData[1].split("[")[1];
            String message = messageData[1];


            // Get Location
            GTLocationMessageData locationData = gtTextOnlyMessageData.getLocationMessageData();
            Double lat = locationData.getLatitude();
            Double lon = locationData.getLongitude();

            // Send Post
            sendPost(message, type, lat, lon);
        }

        // Print
        Toast.makeText(this,  gtTextOnlyMessageData.getText(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionStateUpdated(GTConnectionManager.GTConnectionState gtConnectionState) {
        Toast.makeText(this,  gtConnectionState.name(), Toast.LENGTH_LONG).show();
    };

    public void sendPost(final String type, final String message, final Double lat, final Double lon) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Long tsLong = System.currentTimeMillis()/1000;
                    String timestamp = tsLong.toString();

                    URL url = new URL("GO_TENACIOUS_API_HOSTNAME");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("send", timestamp);
                    jsonParam.put("message", message);
                    jsonParam.put("type", type);
                    jsonParam.put("lat", lat);
                    jsonParam.put("lon", lon);

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG", conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    Log.e("Erororororro", e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    };
}
