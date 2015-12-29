package com.example.chicharo.call_blocker.listener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.example.chicharo.call_blocker.ContactModel;
import com.example.chicharo.call_blocker.R;
import com.example.chicharo.call_blocker.activities.MainActivity;

import java.lang.reflect.Method;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class CallBlocker extends BroadcastReceiver {

    private static final String SETTINGS_SHARED_PREFERENCES_NAME = "Settings";
    private static final String BLOCK_HIDDEN_NUMBERS = "allowHiddenNumbers";
    private static final String BLOCK_UNKNOWN_NUMBERS = "allowUnknownNumbers";
    private static final String BLOCK_BLACKLIST_NUMBERS = "allowBlacklistNumbers";
    NotificationManager mNM;
    TelephonyManager telephonyManager;
    ITelephony telephonyService;

    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                boolean blockUnknownNumbers = isAContact(context, incomingNumber);
                boolean blockHiddenNumbers = blockCallFromHiddenNumbers(context);
                boolean blockBlacklistNumbers = isInOwnBlackList(context, incomingNumber);
                Bundle bb = intent.getExtras();
                if (!blockHiddenNumbers) {
                    if (blockBlacklistNumbers) {
                        blockCall(context, incomingNumber);
                    } else {
                        if (blockUnknownNumbers) { //is a contact?
                            blockCall(context, incomingNumber);

                        }
                    }
                } else {
                    if (incomingNumber == null) {
                        blockCall(context, null);
                    } else {
                        if (blockBlacklistNumbers) {
                            blockCall(context, incomingNumber);
                        } else {
                            if (blockUnknownNumbers) { //is a contact?
                                blockCall(context, incomingNumber);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean blockCallFromHiddenNumbers(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SETTINGS_SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        return settings.getBoolean(BLOCK_HIDDEN_NUMBERS, false);
    }

    private boolean isInOwnBlackList(Context context, String incomingNumber) {
        SharedPreferences settings = context.getSharedPreferences(SETTINGS_SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        if (!(settings.getBoolean(BLOCK_BLACKLIST_NUMBERS, false))) {
            return false;
        }
        Realm realm = Realm.getInstance(context);
        RealmQuery query = realm.where(ContactModel.class);
        RealmResults<ContactModel> results = query.equalTo("phoneNumber", incomingNumber).findAll();
        return !results.isEmpty();
    }

    private boolean isAContact(Context context, String incomingNumber) {
        SharedPreferences settings = context.getSharedPreferences(SETTINGS_SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        if (!(settings.getBoolean(BLOCK_UNKNOWN_NUMBERS, false))) {
            return false;
        }
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(incomingNumber));

        Cursor cursor = context.getContentResolver().query(contactUri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return false;
        } else {
            if (cursor != null) {
                cursor.close();
            }
            return true;
        }
    }

    public void blockCall(Context ctx, String incomingNumber) {
        TelephonyManager telephony = (TelephonyManager)
                ctx.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class cls = Class.forName(telephony.getClass().getName());
            Method m = cls.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);
            //telephonyService.silenceRinger();
            telephonyService.endCall();
            Long current_time = getCurrentTime();
            showNotification(ctx, incomingNumber, current_time);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Long getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime().getTime();
    }

    private void showNotification(Context context, String number, Long curent_time) {
        String content_title = context.getResources().getString(R.string.notification_block_call_content_title);
        String content_text = context.getResources().getString(R.string.notification_block_call_content_text);
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        Cursor cursor = context.getContentResolver().query(contactUri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        String name = null;
        if (cursor != null && cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null) {
            cursor.close();
        }
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        if (number == null) {
            content_title = context.getResources().getString(R.string.notification_block_call_private_content_title);
            content_text = context.getResources().getString(R.string.notification_block_call_private_content_text);
            number = "";
        }

        Notification notification;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(context)
                    .setContentTitle(content_title)
                    .setContentText(content_text + " " + (name != null ? name : number) + "!")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pIntent)
                    .setWhen(curent_time)
                    .build();
        } else {
            notification = new Notification.Builder(context)
                    .setContentTitle(content_title)
                    .setContentText(content_text + " " + (name != null ? name : number) + "!")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pIntent)
                    .setWhen(curent_time)
                    .getNotification();
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification);
    }

}
