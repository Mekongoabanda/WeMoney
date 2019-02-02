package ws.wolfsoft.shoeshappiness;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by HP on 25/07/2018.
 */

public class WeMoney extends Application {

    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();
 //-------------------------------------------------Memoire cache String et Picasso-----------------------------------------------------------------------------------------------------------------------
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
 //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        //initialisation de l'authentification
        mAuth = FirebaseAuth.getInstance();
        //on cherche l'ID de l'utilisateur courant
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( mAuth.getCurrentUser().getUid() );

        //On va travailleur avec notre écouteur addValueEventListener
        mUsersDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //
                if (dataSnapshot != null ) {
                    //Capture la date actuelle
                    //final String currentDate = DateFormat.getDateTimeInstance().format( new Date());
                     //capturer la date lorsque l'user est en horsligne
                    mUsersDatabase.child( "online" ).onDisconnect().setValue( ServerValue.TIMESTAMP ); // VOIR MAIN ACTIVITY POUR LA VALEUR "True"


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        } );



    }
}
