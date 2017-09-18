package danie.gmhserver;

/**
 * Created by danie on 3/12/2017.
 */

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ty on 3/10/2017.
 */

public class SmsSender {
    private static final int MY_PERMISSIONS_SEND_SMS = 123;
    private static final int MY_PERMISSIONS_RECEIVE_SMS = 321;;
    private static final int MAX_SMS_LENGTH = 130; //Made shorter to comply to added headers on some phones
    Context mContext;
    String phone;
    String message;

    public SmsSender(Context context) {
        mContext = context;
        phone = "";
        message = "";
    }

    public SmsSender(Context context, String aPhone){
        mContext = context;
        phone = aPhone;
        message = "";
    }

    public SmsSender(Context context, String aPhone, String aMessage) {
        mContext = context;
        phone = aPhone;
        message = aMessage;
    }

    public void setPhone(String aPhone) {
        phone = aPhone;
    }

    public void setMessage(String aMessage) {
        message = aMessage;
    }

    public void sendSMS(String message) {
        String SENT = "SMS_SENT"; String DELIVERED = "SMS_DELIVERED";
        PendingIntent sentPI = PendingIntent.getBroadcast(mContext, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(mContext, 0, new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        mContext.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(mContext, "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(mContext, "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(mContext, "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(mContext, "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(mContext, "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        mContext.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(mContext, "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(mContext, "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phone, null, message, sentPI, deliveredPI);
    }

    private void requestSMSPermission() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_SEND_SMS);
            }
        } else {
            sendSMS(message);
        }
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,
                    Manifest.permission.RECEIVE_SMS)) {
            } else {
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_RECEIVE_SMS);
            }
        }
    }

    //@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_SEND_SMS: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    sendSMS(message);
                } else {
                    // permission denied
                }
                break;
            }
        }
    }

    public void sendDirections(String request){
        Log.d("Status", "SENDING");

        ConnectivityManager cm =
                (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        boolean isConnected = isWiFi && (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting());

        Log.d("Connection", Boolean.toString(isConnected));

        if(!isConnected){
            message = "NO SERVICE";
            Log.d("Message", message);
            requestSMSPermission();
            return;
        }

        DirectionSet ds = new DirectionSet(request);

        Log.d("URL", ds.getUrl());

        try {
            ds.fetchDirections();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        if(!ds.isValid()) {
            message = "INVALID";
            Log.d("Message", message);
            requestSMSPermission();
            return;
        }


        ArrayList<Direction> directions = ds.getDirections();

        //Construct giant string that contains directions
        //Each direction is divided by ||
        //Each component of the direction is divided by ^^
        String all = "";
        for(Direction direction : directions){
            Direction.Coordinate start = direction.getStart();
            Direction.Coordinate end = direction.getEnd();
            String instruction = direction.getInstruction();
            all += (start.lat + "^^" + start.lng + "^^" + end.lat + "^^" + end.lng + "^^" + instruction + "||");
        }

        int queryLength = (int) Math.ceil(all.length() / ((double) MAX_SMS_LENGTH));
        ArrayList<String> allSMS = new ArrayList<String>();
        int index = 0;
        int count = 1;
        while (index < all.length()) {
            allSMS.add("<" + count + "/" + queryLength + ">" + all.substring(index, Math.min(index + MAX_SMS_LENGTH,all.length())));
            index += MAX_SMS_LENGTH;
            count++;
        }

        for(String SMS : allSMS) {
            message = SMS;
            Log.d("Message", message);
            requestSMSPermission();
        }
    }
}
