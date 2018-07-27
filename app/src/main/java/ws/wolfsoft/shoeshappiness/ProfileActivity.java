package ws.wolfsoft.shoeshappiness;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.text.DateFormat;
import java.util.Date;

import static ws.wolfsoft.shoeshappiness.R.drawable.rectangleaccept;


        /* Created by Mekongo ABANDA on 15/05/2018.
        */

public class ProfileActivity extends AppCompatActivity {

    //Déclaration de nos variables
    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn,mCorrectIcon,mprofileDeclineBtn, mAlertebtn;

    private DatabaseReference mUsersDatabase, mFriendReqDatabase, mFriendDatabase;

    private ProgressDialog mProgressDialog;

    private FirebaseUser mCurrent_user;

    private String mCurrent_state;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_profile );

        final String user_id = getIntent().getStringExtra( "user_id" );

        //Ici on cherche les Reférences dans la base de donnée
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( user_id );
        mUsersDatabase.keepSynced( true );

        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child( "Demandes_d'ami" );
        mFriendReqDatabase.keepSynced( true );

        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child( "WeFriends" );
        mFriendDatabase.keepSynced( true );
        //Ici on prend l'utilisateur courant dans la base de donnée, ici l'utilisateur courant sera désigné par mCurrent_user
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        /////On connecte nos variables à nos objet de l'interface graphique "activity_profile"/////
        mProfileImage = (ImageView) findViewById( R.id.profile_image );
        mProfileName = (TextView) findViewById( R.id.profile_displayName );
        mProfileStatus = (TextView) findViewById( R.id.profile_status );
        mProfileFriendsCount = (TextView) findViewById( R.id.profile_totalFriends );
        mProfileSendReqBtn = (Button) findViewById( R.id.profile_send_req_btn );
        mCorrectIcon = (Button) findViewById( R.id.correct_icon );
        mprofileDeclineBtn = (Button) findViewById( R.id.profile_decline_btn );
        mAlertebtn = (Button) findViewById( R.id.alertebtn );

        //on initialise notre Etat courant par le terme "pas_ami"
        //Donc notre mCurrent_state sera vu comme le fait de ne pas être ami
        mCurrent_state = "pas_ami";

        //On initiale notre Progress dialog
        mProgressDialog = new ProgressDialog( this );

        //On dit à notre progresse bar d'afficher comme titre "chargement des données de l'utilisateur"
        mProgressDialog.setTitle( R.string.chargement_des_données_utilisateur );

        //On dit à notre progresse bar d'afficher comme message "chargement des données de l'utilisateur"
        mProgressDialog.setMessage( "Veuillez patienter pendant que nous chargeons les données utilisateur." );

        //Notre progress dialog ne quitte pas tant qu'elle charge, même si nous touchons l'écran, hors de la progress dialog
        mProgressDialog.setCanceledOnTouchOutside( false );
        mProgressDialog.show();


/////ICI nous allons affficher le nom, statut et image de profile de l'utilisateur non courant//////
        mUsersDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //On cherche les informartions de l'utilisateur non courant dans la base de donnée
                String display_name = dataSnapshot.child( "nom" ).getValue().toString();
                String status = dataSnapshot.child( "status" ).getValue().toString();
                final String image = dataSnapshot.child( "image" ).getValue().toString();
                //// afficher le nom de l'utilisateur non courant////
                mProfileName.setText( display_name );
                //// afficher le statut de l'utilisateur non courant////
                mProfileStatus.setText( status );

                //// afficher la photo de profil de l'utilisateur non courant////
                //sans sauvegarder l'image de profil dans la mémoire cache on aura utilisé ce code
                //Picasso.with( ProfileActivity.this ).load( image ).placeholder( R.drawable.default_avatar ).into( mProfileImage );

                //Mais si nous voulons voir la photo même en étant hors ligne nous utilisons ceci pour la mettre dans la mémoire cache
                Picasso.with( ProfileActivity.this ).load( image ).networkPolicy( NetworkPolicy.OFFLINE ).placeholder( R.drawable.default_avatar )
                        .into( mProfileImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                                Picasso.with( ProfileActivity.this ).load( image ).placeholder( R.drawable.default_avatar ).into( mProfileImage );

                            }
                        });


//---------------------------------------------- DEBUT [FRIEND LIST// REQUEST FEATURE] -----------------------------------------------------------------------------------------------------------------------------
                        ///************Ici c'est le côté utilisateur courant, ce qui s'affiche lorsqu'on lui a transmi une demande*************/////

                        //Modifier le profil de l'émétteur et du récepteur d'un utilisateur selon le type de l'Etat courant (Demande reçue ou demande envoyée)
                        mFriendReqDatabase.child( mCurrent_user.getUid() ).addListenerForSingleValueEvent( new ValueEventListener() {
                            /* @RequiredApi sert à changer le background ci bas, besoin de l'API 21 LOLIPOP*/
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.hasChild( user_id )) {
                                    String Type_de_requête = dataSnapshot.child( user_id ).child( "Type_de_requête" ).getValue().toString();

                                    if (Type_de_requête.equals( "Reçu" )) {

                                        mCurrent_state = "Demande Reçue";
                                        mProfileSendReqBtn.setText( "Refuser la demande" );
                                        mProfileSendReqBtn.setBackground( getDrawable( R.drawable.rectangle46 ) );
                                        mAlertebtn.setVisibility( View.VISIBLE );
                                        mprofileDeclineBtn.setVisibility( View.VISIBLE );
                                        mprofileDeclineBtn.setText( "Accepter la demande" );
                                        mprofileDeclineBtn.setBackground( getDrawable( R.drawable.rectangleannulerdemande ) );

                                    } else if (Type_de_requête.equals( "envoyé" )) {

                                        mCurrent_state = "Demande_envoyée";
                                        mProfileSendReqBtn.setText( "Demande envoyée" );
                                        mCorrectIcon.setVisibility( View.VISIBLE );
                                        mprofileDeclineBtn.setVisibility( View.VISIBLE );
                                        mprofileDeclineBtn.setText( "Annuler la demande" );
                                        mProfileSendReqBtn.setVisibility( View.INVISIBLE );
                                        mProfileSendReqBtn.setEnabled( false );

                                    }

                                    mProgressDialog.dismiss();

                                } else {

                                    mFriendReqDatabase.child( mCurrent_user.getUid() ).addListenerForSingleValueEvent( new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.hasChild( user_id )) {

                                                mCurrent_state = "ami";
                                                mProfileSendReqBtn.setEnabled( true );
                                                // le bouton "DEMANDE ENVOYEE" se change en "DEMANDE D'AMITIE"
                                                mProfileSendReqBtn.setText( "Dissoudre l'amitié" );
                                                // le bouton "ANNULER LA DEMANDE" disparaît
                                                mprofileDeclineBtn.setVisibility( View.INVISIBLE );
                                                // l'icône validé en vert disparaît
                                                mCorrectIcon.setVisibility( View.INVISIBLE );
                                                //L'icone alerte devient invisible
                                                mAlertebtn.setVisibility( View.INVISIBLE );
                                            }

                                            mProgressDialog.dismiss();

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                            mProgressDialog.dismiss();
                                        }
                                    } );

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {


                            }   // la progress dialog s'enlève lorsque toutes les infos sont chargées
                        } );

        }
                @Override
                public void onCancelled(DatabaseError databaseError){

                }

    });
        //---------------------------------------------- FIN [FRIEND LIST// REQUEST FEATURE] ------------------------------------------------------------------





//------------------------------------------ACTION LORSQU'ON CLIQUE SUR LE BOUTON "DEMANDE D'AMITIE"---------------------------------------------------------------------------------------

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //l'action sur le bouton est activée, passée donc en mode "true"
                mProfileSendReqBtn.setEnabled( false );
                // Si le statut courant de deux utilisateurs = pas_ami, c'est à dire si deux utilisateurs ne sont pas encore amis
                if(mCurrent_state.equals("pas_ami")){

                    //Une Table Demande_d'ami (mFriendReqDatabase) et deux relations dans la table se crée entre les deux utilisateurs. Comment se bout de code marche?
                    //La 1ere relation "envoyé", l'émetteur et le récepteur ont un type_de_requête = "envoyé".
                    //l'id de l'émetteur contient lid du récepteur qui contient le type_de_requête = "envoyé"
                    mFriendReqDatabase.child( mCurrent_user.getUid() ).child( user_id ).child( "Type_de_requête" )
                            .setValue( "envoyé" ).addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //si la 1ere relation est un succès
                            if(task.isSuccessful()){
                                //La 2e relation "Recu", l'émetteur et le récepteur ont un type_de_requête = "Reçu"
                                //l'id du récepteur contient l'id de l'émetteur qui contient le type_de_requête = "Reçu"
                                mFriendReqDatabase.child( user_id ).child( mCurrent_user.getUid() ).child( "Type_de_requête" )
                                        .setValue( "Reçu" ).addOnSuccessListener( new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //Le statut courant change en "requête_envoyée"
                                        mCurrent_state = "Demande_envoyée";

                                        //Un message apparaît pour signifier que la demande a bien été envoyée
                                        Toast.makeText( ProfileActivity.this, "Demande envoyée avec succès", Toast.LENGTH_SHORT ).show();
                                        //une icone validé apparaît en vert
                                        mCorrectIcon.setVisibility( View.VISIBLE );

                                        // le bouton "ANNULER LA DEMANDE" apparaît
                                        mprofileDeclineBtn.setVisibility( View.VISIBLE );

                                        // le bouton "DEMANDE D'AMITIE" se change en "DEMANDE ENVOYEE"
                                        mProfileSendReqBtn.setText("Demande Envoyée");

                                    }
                                } );

                            }

                            else{

                                Toast.makeText( ProfileActivity.this, "Impossible de traiter votre demande. Désolé.", Toast.LENGTH_SHORT ).show();
                            }

                            //l'action sur le clic du bouton s'annule
                            mProfileSendReqBtn.setEnabled( true );

                        }
                    } );

                }
            }
        });
//------------------------------------------------------------------------------------------------------------------------------------------------------------------//


//----------------------------------------- DEBUT  ANNULER/REFUSER LA DEMANDE Côté ENVOYEUR ET RECEPTEUR --------------------------------------------------------------------------------------

/********************ACTION LORSQU'ON CLIQUE SUR LE BOUTON "ANNULER LA DEMANDE (Côté envoyeur)"*****************************/
        mprofileDeclineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Si l'état courant est de type "demande envoyée"
                if (mCurrent_state.equals( "Demande_envoyée" )) {
                      // on annule la demande envoyé, de ce fait les valeurs adéquates seront supprimées de la base de données
                      mFriendReqDatabase.child( mCurrent_user.getUid()).child( user_id ).removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                          @Override
                          // si l'opération de suppression est un succès
                          public void onSuccess(Void aVoid) {

                              mFriendReqDatabase.child( user_id ).child( mCurrent_user.getUid() ).removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                                  @Override
                                  public void onSuccess(Void aVoid) {

                                      mCurrent_state = "pas_ami";

                                      mProfileSendReqBtn.setEnabled( true );
                                      // le bouton "DEMANDE ENVOYEE" se change en "DEMANDE D'AMITIE"
                                      mProfileSendReqBtn.setText( R.string.Demande_d_ami );
                                      // le bouton "ANNULER LA DEMANDE" disparaît
                                      mprofileDeclineBtn.setVisibility( View.INVISIBLE );
                                      // l'icône validé en vert disparaît
                                      mCorrectIcon.setVisibility( View.INVISIBLE );
                                      ////Un message "Demande annulée" apparaît
                                      Toast.makeText( ProfileActivity.this, "Demande annulée", Toast.LENGTH_SHORT ).show();

                                  }
                              } );

                          }
                      } );

                }
                /******************** ACTION LORSQU'ON LE RECEPTEUR reçoit la demande" *****************************/
                if (mCurrent_state.equals( "Demande Reçue" )) {

                    final String currentDate = DateFormat.getDateInstance().format( new Date());

                    mFriendDatabase.child( mCurrent_user.getUid() ).child( user_id ).setValue( currentDate )
                            .addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendDatabase.child( user_id ).child( mCurrent_user.getUid() ).setValue( currentDate ).addOnSuccessListener( new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    // on annule la demande envoyé, de ce fait les valeurs adéquates seront supprimées de la base de données
                                    mFriendReqDatabase.child( mCurrent_user.getUid()).child( user_id ).removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                                        @Override
                                        // si l'opération de suppression est un succès
                                        public void onSuccess(Void aVoid) {

                                            mFriendReqDatabase.child( user_id ).child( mCurrent_user.getUid() ).removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mCurrent_state = "ami";

                                                    mProfileSendReqBtn.setEnabled( true );
                                                    // le bouton "DEMANDE ENVOYEE" se change en "DEMANDE D'AMITIE"
                                                    mProfileSendReqBtn.setText( "Dissoudre l'amitié" );
                                                    // le bouton "ANNULER LA DEMANDE" disparaît
                                                    mprofileDeclineBtn.setVisibility( View.INVISIBLE );
                                                    // l'icône validé en vert disparaît
                                                    mCorrectIcon.setVisibility( View.INVISIBLE );
                                                    //L'icone alerte devient invisible
                                                    mAlertebtn.setVisibility( View.INVISIBLE );
                                                    ////Un message "Demande annulée" apparaît
                                                    Toast.makeText( ProfileActivity.this, "Demande acceptée, rendez vous sur Wefriends", Toast.LENGTH_SHORT ).show();

                                                }
                                            } );

                                        }
                                    } );
                                }
                            } );
                        }
                    } );

                }

            }

        });
//----------------------------------------- FIN  ANNULER/REFUSER LA DEMANDE Côté ENVOYEUR ET RECEPTEUR --------------------------------------------------------------------------------------
    }
}