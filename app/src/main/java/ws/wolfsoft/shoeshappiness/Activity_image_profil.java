package ws.wolfsoft.shoeshappiness;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import spencerstudios.com.bungeelib.Bungee;

//import uk.co.senab.photoview.PhotoViewAttacher;

public class Activity_image_profil extends AppCompatActivity {

    //ceci est en rapport avec notre procédure publique OnBackPressed
    final String TAG = this.getClass().getName();

    private ImageView profile_image;
    private ImageView like;
    private TextView number_like;

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

   // private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_image_profil );

        profile_image = (ImageView) findViewById( R.id.image_de_profile );
        like = (ImageView) findViewById( R.id.like );
        number_like = (TextView) findViewById( R.id.number_like );


        //notre progress bar
        //mProgressBar = (ProgressBar) findViewById(R.id.progressBar_pp);

        //Utilisateur courant
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        //La référence "Users" de notre database firebase
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        //Afficher les données de cette Database lorsqu'il ny'a pas de connexion (mémoire cache). ( voir WeMoney.Class)
        mUserDatabase.keepSynced( true );


        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //synchroniser les champs statuts, nom d'utilisateur, image de profile entre ceux du projet et de la BD firebase
                String nom = dataSnapshot.child("nom").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();

                //désormais les noms et les status sont attribués à l'utilisateur grace à cette fonction
               // display_name.setText(nom);
                //profile_status.setText(status);
                //charger l'image téléchargée qui se trouve dans notre storage firebase
                //mProgressBar.setVisibility( View.VISIBLE);

                if(!image.equals("default")) {

                    //charger l'image téléchargée qui se trouve dans notre storage firebase
                    Picasso.with(Activity_image_profil.this).load(image).placeholder(R.drawable.default_avatar).into(profile_image);

                }
                if (!image.equals( "image" )){
                    //mProgressBar.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        like.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"NewApi", "ResourceAsColor"})
            @Override
            public void onClick(View v) {
                like.setBackground( getDrawable( R.drawable.like_red ) );
                number_like.setTextColor( R.color.rose_like );
            }
        });
    }
//------------------------------------------------------------------ DEBUT :CLIQUER DEUX FOIS POUR QUITTTER ----------------------------------------------------------------------------------------------------------------------------------------
    //twice c'est le booléen qui nous permettra de savoir que l'utilisateur a appuyé deux fois déja sur le bouton
    boolean twice = false;
    @Override
    public void onBackPressed() {

        Log.d(TAG, "click");

        // Si l'utilisateur appuit deux fois alors il retourne à la page précédente
        if (twice == true){
            /* CE CODE EST POUR QUITTER L'APPLICTION lors du click
            Intent intent = new Intent (Intent.ACTION_MAIN);
            intent.addCategory( Intent.CATEGORY_HOME );
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            startActivity( intent );
            finish();
            System.exit( 0 ); */

            //le système rentre à la page précédente
            super.onBackPressed();
            //avec une animation fondue
            Bungee.fade( Activity_image_profil.this );
        }
        twice = true;
        Log.d(TAG, "twice: " + twice);

        Toast.makeText( Activity_image_profil.this, "Appuyez une deuxième fois pour quitter", Toast.LENGTH_SHORT ).show();
        new Handler(  ).postDelayed( new Runnable() {
            @Override
            public void run() {
                twice = false;
                Log.d(TAG, "twice: " + twice);
            }
            //delayMillis c'est le temps que ça fait avant de retourner à false (false c'est ce qui se passe lorsqu'on clique la 1ere fois)
        }, 3000);


    }

    //-------------------------------------------------------------- FIN :CLIQUER DEUX FOIS POUR QUITTTER -------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
