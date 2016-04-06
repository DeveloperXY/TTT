package com.spartech.ttt.gameutils;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.github.nkzawa.socketio.client.Socket;
import com.spartech.ttt.R;
import com.spartech.ttt.socketio.TTTApplication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mohammed Aouf ZOUAG on 06/04/2016.
 */
public class NotificationUtils {
    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_ACCEPT = "Accept";
    private static final String ACTION_DENY = "Deny";

    private Context mContext;

    public NotificationUtils(Context context) {
        mContext = context;
    }

    /**
     * Displays a system notification informing the user of an incoming rematch request.
     */
    public void displayRequestNotification() {
        Intent acceptIntent = new Intent(mContext, NotificationActionService.class);
        acceptIntent.setAction(ACTION_ACCEPT);
        PendingIntent piAccept = PendingIntent.getService(mContext, 0, acceptIntent, 0);

        Intent denyIntent = new Intent(mContext, NotificationActionService.class);
        denyIntent.setAction(ACTION_DENY);
        PendingIntent piDeny = PendingIntent.getService(mContext, 0, denyIntent, 0);

        Notification notification = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Rematch request")
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Your opponent is requesting a rematch. What is it going to be ?"))
                .addAction(R.drawable.ic_action_tick, ACTION_ACCEPT, piAccept)
                .addAction(R.drawable.ic_action_cancel, ACTION_DENY, piDeny)
                .build();

        NotificationManager nm = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        nm.notify(NOTIFICATION_ID, notification);
    }

    public static class NotificationActionService extends IntentService {
        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            // Dismiss the notification
            NotificationManager nm = (NotificationManager) getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(NOTIFICATION_ID);

            String action = intent.getAction();
            acceptRematchRequest(ACTION_ACCEPT.equals(action));
        }

        /**
         * Responds to the incoming rematch request.
         *
         * @param action to take (true: accept / false: deny)
         */
        private void acceptRematchRequest(boolean action) {
            Socket socket = ((TTTApplication) getApplication()).getSocket();

            try {
                JSONObject params = new JSONObject();
                params.put("response", action);
                socket.emit("rematchResponse", params);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
