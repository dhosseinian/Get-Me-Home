package cs117.getmehome;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Resending extends AppCompatActivity {
    double lat;
    double lng;
    String message;
    BroadcastReceiver smsReceiver;
    private IntentFilter myFilter;
    SendSms sendSms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resending);

        sendSms = new SendSms(this);
        Intent i = getIntent();
        message = i.getStringExtra(Direction.CONTENT);
        lat = i.getDoubleExtra(Direction.LAT, 0);
        lng = i.getDoubleExtra(Direction.LONG, 0);

        Log.d("received msg", message);
        smsReceiver = new SmsReceiver(message);
        myFilter = new IntentFilter();
        myFilter.addAction("android.provider.Telephony.SMS_RECEIVED");

        sendSms.setPhone(MainActivity.PHONENO);
        sendSms.setMessage(message);
        // toSend = #GMH\ndestination\nlatitude\nlongitude
        String toSend = "#GMH\n" + message + "\n" + Double.toString(lat) + "\n" + Double.toString(lng);
        sendSms.sendSMS(toSend);
        finish();
    }
}
