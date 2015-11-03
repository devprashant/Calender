package com.example.mau.calender.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.example.mau.calender.R;
import com.example.mau.calender.activity.MainActivity;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by sony on 10/26/2015.
 */
public class GCMMessageSource {

    private Context context;
    private SharedPreferences prefs;

    public GCMMessageSource(Context context) {
        this.context = context;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void getFirebaseMessage() {
        //Firebase code
        final String firebaseURL = "https://luminous-inferno-9046.firebaseio.com/";
        Firebase.setAndroidContext(context);
        Firebase ref = new Firebase(firebaseURL);
        Firebase gcmObj = ref.child("gcm");
        /*Firebase gcmTitle = gcmObj.child("Title");
        Firebase gcmMessage = gcmObj.child("Message");
        Firebase gcmMessageId = gcmObj.child("MessageId");

        //setting initial value
        gcmMessageId.setValue(0);
        gcmTitle.setValue("initial title");
        gcmMessage.setValue("message First one");*/
        //setting value change listener
        gcmObj.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getValue());
                System.out.println("Value type: " + (dataSnapshot.child("MessageId").getValue()).getClass().getName());
                //check for correct messageId type input
                /* Application crashes if type is different from long, example  String */
                if(((dataSnapshot.child("MessageId").getValue()).getClass().getSimpleName()).equals("Long")){
                    int messageId = ((Long) dataSnapshot.child("MessageId").getValue()).intValue();
                    //save received messageId to sharedpreferences
                    Editor edit = prefs.edit();
                    //create notification by first checking for new message
                    if (messageId != prefs.getInt("MessageId", 3)) {
                        createNotification((dataSnapshot.child("Title").getValue()).toString()
                                , (dataSnapshot.child("Message").getValue()).toString());
                        edit.putInt("MessageId", messageId);
                        edit.apply();
                    } else {
                        //Its not coming here
                    }
                } else {
                    System.out.println("Wrong Type for MessageId");
                    System.out.println("Type of MessageId" + (dataSnapshot.child("MessageId").getValue()).getClass().getSimpleName());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void createNotification(String title, String message){
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(),intent,0);

        Notification noti = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(message).setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                        //.addAction(R.mipmap.ic_launcher, "Call", pIntent)
                        //.addAction(R.mipmap.ic_launcher, "More", pIntent)
                        //.addAction(R.mipmap.ic_launcher, "And more", pIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);
    }

}
