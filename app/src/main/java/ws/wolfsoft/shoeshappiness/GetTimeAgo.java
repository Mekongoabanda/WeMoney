package ws.wolfsoft.shoeshappiness;

/*
*  Créer le 30/01/2019 par MEKONGO ABANDA
* Ce code permet d'améliorer les précisions du temps
* exemple : "il y'a deux jours"; "hier"; "il y'a 10 minutes"
*
* */

import android.app.Application;
import android.content.Context;


public class GetTimeAgo extends Application {

    //secondes
    private static final int SECOND_MILLIS = 1000;
    //minutes
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    //heures
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    //jours
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "il y'a quelques secondes";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "il y'a une minute";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return "Il y'a " + diff / MINUTE_MILLIS + " minutes";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "il y'a une heure";
        } else if (diff < 24 * HOUR_MILLIS) {
            return "Il y'a " + diff / HOUR_MILLIS + " heures";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "En ligne hier";
        } else {
            return "Il y'a " + diff / DAY_MILLIS +  " quelques jours";
        }
    }
}
