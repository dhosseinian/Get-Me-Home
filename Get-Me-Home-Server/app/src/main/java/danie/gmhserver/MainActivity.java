package danie.gmhserver;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText console;
    BroadcastReceiver smsReceiver;
    private IntentFilter myFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        console = (EditText) findViewById(R.id.editText5);

        smsReceiver = new SmsReceiver();
        myFilter = new IntentFilter();
        myFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, myFilter);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // Unregister the SMS receiver
        unregisterReceiver(smsReceiver);
    }
}
