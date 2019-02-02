/*   A INCLURE DANS LE MANIFEST, POUR L'action DU CLICK

<intent-filter>
      <action android:name="ws.wolfsoft.shoeshappiness_TARGET_NOTIFICATION" ></action>
       <category android:name="android.intent.category.DEFAULT"></category>
 </intent-filter>

            */

package ws.wolfsoft.shoeshappiness;

//Created by MEKONGO ABANDA On 07/10/2018, DIJON, France

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

@Override
    public void onMessageReceived (RemoteMessage remoteMessage){
    super.onMessageReceived( remoteMessage );

    //déclaration des variables utilisés pour le titre et les messages de notification
    String notification_title = remoteMessage.getNotification().getTitle();
    String notification_message = remoteMessage.getNotification().getBody();
    String click_action = remoteMessage.getNotification().getClickAction();
    //On prend notre from_user_id présent dans le code Node.js
    String from_user_id = remoteMessage.getData().get( "from_user_id" );

// ici nous allons indiquer le forme de la notification à envoyer telles que ecrites dans Node.js
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.logo1n)
            .setContentTitle(notification_title)
            .setContentText(notification_message);

    //Action éffectuer lorsque le récepteur de la notification clique sur celle ci
    Intent resultIntent = new Intent(click_action);
    resultIntent.putExtra( "user_id", from_user_id );

    PendingIntent resultPendingIntent =
            PendingIntent.getActivity(this,
                    0,
                    resultIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

    mBuilder.setContentIntent( resultPendingIntent );



    int mNotificationId = (int) System.currentTimeMillis();

    NotificationManager mNotifyMgr = (NotificationManager) getSystemService( NOTIFICATION_SERVICE );

    mNotifyMgr.notify(mNotificationId, mBuilder.build());

}

}
