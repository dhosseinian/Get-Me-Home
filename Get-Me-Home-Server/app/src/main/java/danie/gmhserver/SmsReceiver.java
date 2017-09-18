package danie.gmhserver;

/**
 * Created by danie on 3/12/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import static android.support.v4.content.ContextCompat.startActivity;

public class SmsReceiver extends BroadcastReceiver {
    public static String MESSAGE1 = "destination"; //Value will change
    public static String MESSAGE2 = "phoneNumber"; //Value will change
    Context context;
    String data;
    String source;

    @Override
    public void onReceive(Context cont, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //---get the SMS message passed in---
        context = cont;
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String[] str = {""};
        data = "";
        source = "";
        if (bundle != null) {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String format = bundle.getString("format");
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                Log.d("source", msgs[i].getOriginatingAddress());
                source = msgs[i].getOriginatingAddress();
                str[i] += msgs[i].getMessageBody();
                str[i] += "\n";
            }
        }

        for (int i = 0; i < str.length; i++) {
            data += str[i];
        }

        //Check if text starts with tag
        if(data.startsWith("#GMH")) {
            /*
            Intent getDir = new Intent(context, MainActivity.class);
            getDir.putExtra(MESSAGE1, data);
            getDir.putExtra(MESSAGE2, source);
            context.startActivity(getDir);
            */
            Thread thread = new Thread(new Runnable() {
                SmsSender sender = new SmsSender(context, source);
                @Override
                public void run() {
                    try  {
                        sender.sendDirections(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        }

        //throw new UnsupportedOperationException("Not yet implemented");
    }
}