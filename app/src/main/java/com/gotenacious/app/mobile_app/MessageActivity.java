package com.gotenacious.app.mobile_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.Toast;

import com.gotenna.sdk.GoTenna;
import com.gotenna.sdk.bluetooth.GTConnectionManager;
import com.gotenna.sdk.commands.GTCommand;
import com.gotenna.sdk.commands.GTCommandCenter;
import com.gotenna.sdk.commands.GTError;
import com.gotenna.sdk.commands.Place;
import com.gotenna.sdk.exceptions.GTDataMissingException;
import com.gotenna.sdk.exceptions.GTInvalidAppTokenException;
import com.gotenna.sdk.gids.GIDManager;
import com.gotenna.sdk.interfaces.GTErrorListener;
import com.gotenna.sdk.interfaces.GTMessageDataListener;
import com.gotenna.sdk.messages.GTBaseMessageData;
import com.gotenna.sdk.messages.GTLocationMessageData;
import com.gotenna.sdk.messages.GTMessageData;
import com.gotenna.sdk.messages.GTTextOnlyMessageData;
import com.gotenna.sdk.responses.GTResponse;
import com.gotenna.sdk.user.User;
import com.gotenna.sdk.user.UserDataStore;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.gotenna.sdk.commands.GTCommand.*;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener, GTCommandCenter.GTMessageListener, GTConnectionManager.GTConnectionListener, GTCommandResponseListener, GTErrorListener, GTMessageDataListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            GoTenna.setApplicationToken(getApplicationContext(), "AAYRSBUHDEIRVB9EBBkGAwlVBF5HUlgDB0EMUgsVEQddGBoDA0NcW18ERQUSWUVX");
            GTConnectionManager.getInstance().clearConnectedGotennaAddress();
            GTConnectionManager.getInstance().scanAndConnect(GTConnectionManager.GTDeviceType.MESH);
            GTConnectionManager.getInstance().addGtConnectionListener(this);
//            GTCommandCenter.getInstance().setMessageListener(this);

        } catch (GTInvalidAppTokenException e) {
            e.printStackTrace();
        }

//            Button button = (Button) findViewById(R.id.sendButton);
//        button.setOnClickListener(this);
        setContentView(R.layout.activity_message);
    }

    @Override
    public void onIncomingMessage(GTMessageData gtMessageData) {
        Toast.makeText(this, "first", Toast.LENGTH_SHORT).show();
        // Get Text
//        String text = gtMessageData.getText();
//
//        // Parse Text
//
//        if(text.startsWith("SOS")) {
//            String[] messageData = text.split("]");
//            String type = messageData[1].split("[")[1];
//            String message = messageData[1];
//
//            // Get Location
////            GTLocationMessageData locationData = gtMessageData.
////            Double lat = locationData.getLatitude();
////            Double lon = locationData.getLongitude();
//
//            // Send Post
//            sendPost(message, type, lat, lon);
//        }
//
//        // Print
//        Toast.makeText(this,  gtMessageData., Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onIncomingMessage(GTBaseMessageData gtBaseMessageData) {
        Toast.makeText(this, gtBaseMessageData.getText(), Toast.LENGTH_SHORT).show();
//
//        // Get Text
          String text = gtBaseMessageData.getText();
          Toast.makeText(this, text, Toast.LENGTH_LONG);

          String type = text.split("\\]")[0].split("\\[")[1];
          String message = text.split("\\]")[1];
         if(text.startsWith("SOS")) {
                // Get Location
//                GTLocationMessageData locationData = gtBaseMessageData.getLocationMessageData();
//                Double lat = locationData.getLatitude();
//                Double lon = locationData.getLongitude();
                // Send Post
                sendPost(type, message, 18.3953717, -66.1527784);
            }
        // Print
        Toast.makeText(this,  gtBaseMessageData.getText(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionStateUpdated(GTConnectionManager.GTConnectionState gtConnectionState) {
        Toast.makeText(this,  gtConnectionState.name(), Toast.LENGTH_LONG).show();
        if (gtConnectionState == GTConnectionManager.GTConnectionState.CONNECTED) {
//            Toast.makeText(this, "Set Message Listener", Toast.LENGTH_SHORT).show();
//            Object user = User.createUser("Yoyo", GIDManager.generateRandomizedPersonalGID());
            UserDataStore.getInstance().registerUser("Yoyo",  GIDManager.generateRandomizedPersonalGID());
            String message = "SOS[Other] Hello Engine4";
            try {
//                GTCommandCenter.getInstance().sendSetGeoRegion(Place.NORTH_AMERICA, this, this);
                GTCommandCenter.getInstance().setMessageListener(this);
                GTTextOnlyMessageData data = new GTTextOnlyMessageData(message);
                GTCommandCenter.getInstance().sendBroadcastMessage(data.serializeToBytes(), this, this);
                Toast.makeText(this, "Sent!", Toast.LENGTH_SHORT).show();
            } catch (GTDataMissingException e) {
                Log.e("send error", e.getMessage());
                e.printStackTrace();
            }
            //byte[] data = message.getBytes();
            //Toast.makeText(this, String.valueOf(data.length), Toast.LENGTH_LONG).show();
            //GTCommandCenter.getInstance().sendMessage(gtnToBytes((message)), 1111111111, this, this, false);
            //Toast.makeText(this, "Sent!", Toast.LENGTH_SHORT).show();
        }
    };

    public void sendPost(final String type, final String message, final Double lat, final Double lon) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Long tsLong = System.currentTimeMillis()/1000;
                    String timestamp = tsLong.toString();

                    URL url = new URL("http://gt-staging-api.us-west-2.elasticbeanstalk.com/message");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("sent", new Date().toString());
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

    @Override
    public void onResponse(GTResponse gtResponse) {
        //Toast.makeText(this, gtResponse.toString().concat("succ"), Toast.LENGTH_LONG).show();
        // gtResponse.getMessageData();
        Toast.makeText(this, String.valueOf(gtResponse.getMessageData()), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onError(GTError gtError) {
        //Toast.makeText(this, gtError.toString(), Toast.LENGTH_LONG).show();
    }

    public byte[] gtnToBytes(String text)
    {
        // Use the goTenna SDK's helper classes to format the text data
        // in a way that is easily parsable
        GTTextOnlyMessageData gtTextOnlyMessageData = null;

        try
        {
            gtTextOnlyMessageData = new GTTextOnlyMessageData(text);
        }
        catch (GTDataMissingException e)
        {
            Log.w("LOL", e);
        }

        if (gtTextOnlyMessageData == null)
        {
            return null;
        }

        return gtTextOnlyMessageData.serializeToBytes();
    }

    @Override
    public void onIncomingMessageData(GTMessageData gtMessageData) {
        Toast.makeText(this, "toasty", Toast.LENGTH_LONG);
    }

    @Override
    public void onClick(View v) {
        String message = "SOS[Other] Hello Engine4";
        GTTextOnlyMessageData data = null;

        try {
            data = new GTTextOnlyMessageData(message);
            GTCommandCenter.getInstance().sendBroadcastMessage(data.serializeToBytes(), this, this);
        } catch (GTDataMissingException e) {
            Log.e("Troll", e.getMessage());
            e.printStackTrace();
        }
    }
}
