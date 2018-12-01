package com.gotenacious.app.mobile_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gotenna.sdk.GoTenna;
import com.gotenna.sdk.commands.GTCommandCenter;
import com.gotenna.sdk.exceptions.GTInvalidAppTokenException;
import com.gotenna.sdk.messages.GTBaseMessageData;
import com.gotenna.sdk.messages.GTGroupCreationMessageData;
import com.gotenna.sdk.messages.GTMessageData;
import com.gotenna.sdk.messages.GTTextOnlyMessageData;

public class MessageActivity extends AppCompatActivity implements GTCommandCenter.GTMessageListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            GoTenna.setApplicationToken(getApplicationContext(), "djfhbkskdfbkwdj");
        } catch (GTInvalidAppTokenException e) {
            e.printStackTrace();
        }
        GTCommandCenter.getInstance().setMessageListener(this);
        setContentView(R.layout.activity_message);
    }

    @Override
    public void onIncomingMessage(GTMessageData gtMessageData) {

    }

    @Override
    public void onIncomingMessage(GTBaseMessageData gtBaseMessageData) {
        GTTextOnlyMessageData gtTextOnlyMessageData = (GTTextOnlyMessageData) gtBaseMessageData;
        System.out.print(gtTextOnlyMessageData.getText());
    }
}
