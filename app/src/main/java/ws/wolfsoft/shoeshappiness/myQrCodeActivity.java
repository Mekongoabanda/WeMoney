package ws.wolfsoft.shoeshappiness;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import spencerstudios.com.bungeelib.Bungee;

public class myQrCodeActivity extends AppCompatActivity {


   private CircleImageView profile_image;
   private ImageView mCoder_btn, qrcodep;
   private TextView display_name;
   private Toolbar mToolbar;
   private DatabaseReference muserDatabase;
   private FirebaseUser mCurrentUser;
   private Menu menu;
   private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_my_qr_code );



        //-------------------------------------------------Réfenrences LOCALES------------------------------------------------------------------------------------------------------
        profile_image = (CircleImageView) findViewById( R.id.profile_image );
        mCoder_btn = (ImageView) findViewById( R.id.coder_btn );
        qrcodep = (ImageView) findViewById( R.id.qrcodep );
        display_name = (TextView) findViewById( R.id.display_name );
        mToolbar = (Toolbar) findViewById(R.id.users_appBar);
//-----------------------------------------------Réfenrences LOCALES FIN ------------------------------------------------------------------------------------------------------------------------

        //--------------------------------------TOOLBAR---------------------------------------------------------------------------------------------------------
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Mon code QR");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //-----------------------------------------------TOOLBAR FIN----------------------------------------------------------------------------------------------------------



        mProgress = new ProgressDialog( this );

        mCoder_btn.setVisibility( View.INVISIBLE );
        mProgress = new ProgressDialog( myQrCodeActivity.this );
        mProgress.setMessage( "Veuillez patienter pendant que nous construisons le QR code de votre profil... " );
        mProgress.setCanceledOnTouchOutside( true );
        mProgress.setTitle( "Création du Weprofile QR Code" );
        mProgress.show();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        muserDatabase = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( current_uid );
        muserDatabase.keepSynced( true );

        muserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //synchroniser les champs statuts, nom d'utilisateur, image de profile entre ceux du projet et de la BD firebase
                String nom = dataSnapshot.child("nom").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();

                //désormais les noms et les status sont attribués à l'utilisateur grace à cette fonction
                display_name.setText(nom);


                if(!image.equals("default")) {

                    //sans sauvegarder l'image de profil dans la mémoire cache on aura utilisé ce code
                    Picasso.with(myQrCodeActivity.this).load(image).networkPolicy( NetworkPolicy.OFFLINE ).placeholder(R.drawable.default_avatar).into(profile_image);


                }
                if (!image.equals( "image" )){

                }


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });





        //---------------------------------------------ACTIONS SUR LES CLICK--------------------------------------------------------------------
      profile_image.setOnClickListener( new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent image_profil = new Intent( myQrCodeActivity.this, Activity_image_profil.class );
              Bungee.zoom( myQrCodeActivity.this );
              startActivity( image_profil );
          }
      } );



//-----------------------------------------------------FIN CLICK -----------------------------------------------------------------------------------

    }

    //ici on applique les options du menu sur notre toolbar(options crées dans menu_messagerie
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_qrcode, menu);
        return true;
    }
}
