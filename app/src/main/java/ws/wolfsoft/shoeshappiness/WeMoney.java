package ws.wolfsoft.shoeshappiness;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by HP on 25/07/2018.
 */

public class WeMoney extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Stocker les données de la database dans la mémoire cache (donnée de type String uniquement (chaine de caractère)).
        FirebaseDatabase.getInstance().setPersistenceEnabled( true );

        //pour les données de type image par example on utilise
        //notamment pour la bibliothèque Picasso
        Picasso.Builder builder = new Picasso.Builder( this );
        builder.downloader( new OkHttpDownloader( this, Integer.MAX_VALUE ) );
        Picasso built = builder.build();
        built.setIndicatorsEnabled( true );
        built.setIndicatorsEnabled( true );
        Picasso.setSingletonInstance( built );

    }
}
