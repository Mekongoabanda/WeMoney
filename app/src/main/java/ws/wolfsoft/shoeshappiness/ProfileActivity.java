package ws.wolfsoft.shoeshappiness;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.squareup.picasso.Transformation;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import spencerstudios.com.bungeelib.Bungee;


/* Created by Mekongo ABANDA on 15/05/2018.
        */

public class ProfileActivity extends AppCompatActivity {

    //Déclaration de nos variables
    private CircleImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount, mNumberPhone, mEmail;
    private Button mProfileSendReqBtn,mCorrectIcon,mprofileDeclineBtn, mAlertebtn;

    private DatabaseReference mUsersDatabase, mFriendReqDatabase, mFriendDatabase, mNotificationDatabase;
    private DatabaseReference mRootRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser mCurrent_user;

    private ProgressDialog mProgressDialog;
    private ProgressBar mProgressBar;

    private String mCurrent_state ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.profile_user_activity );

        final String user_id = getIntent().getStringExtra( "user_id" );

        /*-------------------------------Chemin d'accès de nos références dans la base de donnée---------------------------------------------------------------*/
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( user_id );
        mUsersDatabase.keepSynced( true );

        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child( "Demandes_d'ami" );
        mFriendReqDatabase.keepSynced( true );

        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child( "WeFriends" );
        mFriendDatabase.keepSynced( true );

        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child( "notifications" );
        mNotificationDatabase.keepSynced( true );

        /*-------------------------------------------------------------------------------------------------------------------------------------------------------*/


        //Ici on prend l'utilisateur courant dans la base de donnée, ici l'utilisateur courant sera désigné par mCurrent_user
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();


        //-------------------------------------Connexion de nos variables à nos objets de "activity_profile"-------------------------------------------------------------------------------------
        mProfileImage = (CircleImageView) findViewById( R.id.profile_image );
        mProfileName = (TextView) findViewById( R.id.profile_displayName );
        mProfileStatus = (TextView) findViewById( R.id.profile_status );
        mProfileFriendsCount = (TextView) findViewById( R.id.profile_totalFriends );
        mProfileSendReqBtn = (Button) findViewById( R.id.profile_send_btn );
        //mCorrectIcon = (Button) findViewById( R.id.correct_icon );
        mprofileDeclineBtn = (Button) findViewById( R.id.profile_decline_btn );
        mAlertebtn = (Button) findViewById( R.id.alertebtn );
        mNumberPhone = (TextView) findViewById( R.id.number_phone );
        mEmail = (TextView) findViewById( R.id.email );
        mProgressBar = (ProgressBar) findViewById( R.id.progressbarprofile );
        //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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
                String phone_number = dataSnapshot.child( "phone_number" ).getValue().toString();
                //// afficher le nom de l'utilisateur non courant////
                mProfileName.setText( display_name );
                //// afficher le statut de l'utilisateur non courant////
                mProfileStatus.setText( status );
                mNumberPhone.setText( phone_number );

                //// afficher la photo de profil de l'utilisateur non courant////
                //sans sauvegarder l'image de profil dans la mémoire cache on aura utilisé ce code
                //Picasso.with( ProfileActivity.this ).load( image ).placeholder( R.drawable.default_avatar ).into( mProfileImage );


                if(!image.equals("default")) {
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
                            } );
                }

                if (!image.equals( "image" )){
                    mProgressBar.setVisibility(View.INVISIBLE);
                }




//---------------------------------------------- DEBUT [FRIEND LIST// REQUEST FEATURE] -----------------------------------------------------------------------------------------------------------------------------
                ///************Ici c'est le côté utilisateur courant, ce qui s'affiche lorsqu'on lui a transmi ou qu'il a reçu une demande*************/////

                //ICI nous recherchons dans la base de donnée WeFriends --> utilisateur courant --> Id de l'user
                mFriendDatabase.child( mCurrent_user.getUid()).child( user_id ).addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //SI dans WeFriends --> Id de l'user il y'a une donnée appelé "date" alors cela signifie qu'il sont amis
                        if (dataSnapshot.hasChild( "date" )){
                                //Donc on fait tout ceci :
                                mCurrent_state = "ami";
                                mprofileDeclineBtn.setEnabled( true );
                                mProfileSendReqBtn.setEnabled( false );
                                mProfileSendReqBtn.setVisibility( View.INVISIBLE );
                                // le bouton "DEMANDE ENVOYEE" se change en "DEMANDE D'AMITIE"
                                mprofileDeclineBtn.setText( "Dissoudre l'amitié" );
                                // le bouton "ANNULER LA DEMANDE" disparaît
                                mprofileDeclineBtn.setVisibility( View.VISIBLE );
                                //L'icone alerte devient invisible
                                mAlertebtn.setVisibility( View.INVISIBLE );

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                } );


                //Modifier le profil de l'émétteur et du récepteur d'un utilisateur selon le type de l'Etat courant (Demande reçue ou demande envoyée)
                mFriendReqDatabase.child( mCurrent_user.getUid() ).addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild( user_id )) {
                            String Type_de_requête = dataSnapshot.child( user_id ).child( "Type_de_requête" ).getValue().toString();

                            if (Type_de_requête.equals( "Reçu" )) {

                                mprofileDeclineBtn.setVisibility( View.VISIBLE );
                                mCurrent_state = "Demande Reçue";
                                mprofileDeclineBtn.setText( "Refuser la demande" );
                                mAlertebtn.setVisibility( View.VISIBLE );
                                mProfileSendReqBtn.setVisibility( View.VISIBLE );
                                mProfileSendReqBtn.setText( "Accepter la demande" );


                            } else if (Type_de_requête.equals( "envoyé" )) {

                                mCurrent_state = "Demande_envoyée";
                                mprofileDeclineBtn.setVisibility( View.VISIBLE );
                                mprofileDeclineBtn.setText( "Annuler la demande" );
                                mProfileSendReqBtn.setVisibility( View.INVISIBLE );
                                mprofileDeclineBtn.setEnabled( true );
                                mProfileSendReqBtn.setEnabled( false );

                            }

                            mProgressDialog.dismiss();

                        } else {

                            mFriendReqDatabase.child( mCurrent_user.getUid() ).addListenerForSingleValueEvent( new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild( user_id )) {

                                        mCurrent_state = "ami";
                                        mprofileDeclineBtn.setEnabled( true );
                                        mProfileSendReqBtn.setEnabled( false );
                                        mProfileSendReqBtn.setVisibility( View.INVISIBLE );
                                        // le bouton "DEMANDE ENVOYEE" se change en "DEMANDE D'AMITIE"
                                        mprofileDeclineBtn.setText( "Dissoudre l'amitié" );
                                        // le bouton "ANNULER LA DEMANDE" disparaît
                                        mprofileDeclineBtn.setVisibility( View.VISIBLE );
                                        //L'icone alerte devient invisible
                                        mAlertebtn.setVisibility( View.INVISIBLE );

                                    }



                                    mProgressDialog.hide();

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





//------------------------------------------ (1) ACTION LORSQU'ON CLIQUE SUR LE BOUTON "DEMANDE D'AMITIE"---------------------------------------------------------------------------------------

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //l'action sur le bouton est activée, passée donc en mode "true"
                mProfileSendReqBtn.setEnabled( false );

                // Si le statut courant de deux utilisateurs = pas_ami, c'est à dire si deux utilisateurs ne sont pas encore amis
                if(mCurrent_state.equals("pas_ami")){

                    mProgressDialog.setTitle( "Demande d'ami"  );
                    mProgressDialog.setCanceledOnTouchOutside( false );
                    mProgressDialog.setMessage( "Votre demande est en cours de traitement, veuillez patienter...");
                    mProgressDialog.show();

                    // si après le clique sur le bouton d'envoi de demande l'état courant est à "Pas ami" alors une table notification se crée
                    DatabaseReference newNotificationRef = mRootRef.child( "notifications" ).child( user_id ).push();
                    //création du nom crypté d'une notification
                    String newNotificationID = newNotificationRef.getKey();

                    /*Si la demande est envoyée une notification sera envoyée. On crée donc les données de notre table notification
                        qu'on apelle notificationData*/
                    HashMap<String, String> notificationData = new HashMap<>( );
                    //notification data vient de ("from") l'utilisateur courant (mCurrent_user) et est de type "demande d'ami"
                    notificationData.put("from", mCurrent_user.getUid());
                    notificationData.put( "type", "demandes_d'ami" );


                    /* Methode très simple pour créer et indiquer l'architecturen de la table demande_d'ami et notificztions*/
                    Map requestMap = new HashMap(  );
                    /* Par exemple ici on dit: crée une table "Demandes_d'amis, le "/" c'est pour tabuler. Ensuite on lui dit d'ajouter (+)
                     une donnée ayant comme nom l'ID de l'émetteur, on tabule ensuite pour ajouter une donnée avec comme nom l'ID du recepteur
                       qui contiendra à son tour "type de donnée = envoyé"
                       NB: Arborescence à consulter dans la base de donnée (Realtime Database)*/
                    requestMap.put("Demandes_d'ami/" + mCurrent_user.getUid() + "/" + user_id + "/Type_de_requête", "envoyé" );
                    requestMap.put( "Demandes_d'ami/" + user_id + "/" + mCurrent_user.getUid() + "/Type_de_requête", "Reçu");
                    requestMap.put ("notifications/" + user_id + "/" + newNotificationID, notificationData);


                    mRootRef.updateChildren( requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        //ICI nous avons les actions à effectuer lorsque l'écriture dans la base de donnée s'est bien passée
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            //Si il y'a eu un problème lors de l'écriture dans la base donnée
                            if(databaseError != null){

                                Toast.makeText( ProfileActivity.this, "Une erreur s'est produite lors de l'envoi de la demande", Toast.LENGTH_SHORT).show();
                            }

                            mprofileDeclineBtn.setVisibility( View.VISIBLE );
                            mProfileSendReqBtn.setVisibility( View.INVISIBLE );
                            mProfileSendReqBtn.setEnabled( true );
                            //l'action sur le clic du bouton s'active
                            mprofileDeclineBtn.setEnabled( true );
                            mCurrent_state = "Demande_envoyée";
                            // le bouton "DEMANDE ENVOYEE" se change en "Annuler la demande"
                            mprofileDeclineBtn.setText( "Annuler la demande" );
                            Toast.makeText( ProfileActivity.this, "Demande envoyée avec succès, attendez sa réponse", Toast.LENGTH_LONG ).show();
                            mProgressDialog.dismiss();
                        }
                    });

                }

                /******************** ACTION LORSQUE LE RECEPTEUR reçoit la demande" *****************************/
                if (mCurrent_state.equals( "Demande Reçue" )) {

                    mProgressDialog.setTitle( "Validation de la demande"  );
                    mProgressDialog.setCanceledOnTouchOutside( false );
                    mProgressDialog.setMessage( "Votre réponse est en cours de traitement, veuillez patienter...");
                    mProgressDialog.show();

                    //Capture la date actuelle
                    final String currentDate = DateFormat.getDateTimeInstance().format( new Date());

                    Map friendsMap = new HashMap(  );

                    friendsMap.put("WeFriends/" + mCurrent_user.getUid() + "/" + user_id + "/date", currentDate );
                    friendsMap.put( "WeFriends/" + user_id + "/" + mCurrent_user.getUid() + "/date", currentDate );

                    // lorsque la demande d'amie est acceptée la table demande d'amitié est supprimée
                    friendsMap.put("Demandes_d'ami/" + mCurrent_user.getUid() + "/" + user_id , null );
                    friendsMap.put( "Demandes_d'ami/" + user_id + "/" + mCurrent_user.getUid() , null);

                    mRootRef.updateChildren( friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError == null){

                                mCurrent_state = "ami";

                                mProfileSendReqBtn.setEnabled( false );
                                mProfileSendReqBtn.setVisibility( View.INVISIBLE );
                                // le bouton "ANNULER LA DEMANDE" disparaît
                                mprofileDeclineBtn.setVisibility( View.VISIBLE );
                                mprofileDeclineBtn.setEnabled( true );
                                // le bouton "DEMANDE ENVOYEE" se change en "DEMANDE D'AMITIE"
                                mprofileDeclineBtn.setText( "Dissoudre l'amitié" );
                                //L'icone alerte devient invisible
                                mAlertebtn.setVisibility( View.INVISIBLE );
                                ////Un message "Demande annulée" apparaît
                                Toast.makeText( ProfileActivity.this, "Demande acceptée, rendez vous sur Wefriends", Toast.LENGTH_LONG ).show();
                                mProgressDialog.dismiss();

                            }else{

                                String error = databaseError.getMessage();
                                Toast.makeText( ProfileActivity.this, "how désolé " + error, Toast.LENGTH_SHORT ).show();


                            }


                        }
                    } );
                }
            }
        });
//------------------------------------------------------------------------------------------------------------------------------------------------------------------//


//-----------------------------------------DEBUT  ANNULER/REFUSER LA DEMANDE Côté ENVOYEUR ET RECEPTEUR --------------------------------------------------------------------------------------

/******************** (2) ACTION LORSQU'ON CLIQUE SUR LE BOUTON "ANNULER LA DEMANDE" (Côté envoyeur)"*****************************/
        mprofileDeclineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Si l'état courant est de type "demande envoyée"
                if (mCurrent_state.equals( "Demande_envoyée" )) {

                    mProgressDialog.setTitle( "Annulation de la demande"  );
                    mProgressDialog.setCanceledOnTouchOutside( false );
                    mProgressDialog.setMessage( " Veuillez patienter pendant que nous annulons la demande...");
                    mProgressDialog.show();

                    // on annule la demande envoyé, de ce fait les valeurs adéquates seront supprimées de la base de données
                    mFriendReqDatabase.child( mCurrent_user.getUid()).child( user_id ).removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        // si l'opération de suppression est un succès
                        public void onSuccess(Void aVoid) {

                            mFriendReqDatabase.child( user_id ).child( mCurrent_user.getUid() ).removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mCurrent_state = "pas_ami";

                                    mProfileSendReqBtn.setVisibility( View.VISIBLE );
                                    mProfileSendReqBtn.setEnabled( true );
                                    // le bouton "DEMANDE ENVOYEE" se change en "DEMANDE D'AMITIE"
                                    mProfileSendReqBtn.setText( R.string.Demande_d_ami );
                                    // le bouton "ANNULER LA DEMANDE" disparaît
                                    mprofileDeclineBtn.setVisibility( View.INVISIBLE );
                                    ////Un message "Demande annulée" apparaît
                                    Toast.makeText( ProfileActivity.this, "Demande annulée", Toast.LENGTH_SHORT ).show();
                                    mProgressDialog.dismiss();

                                }
                            } );

                        }
                    } );

                }

                //Si l'état courant est de type "demande reçue"
                if (mCurrent_state.equals( "Demande Reçue" )) {

                    mProgressDialog.setTitle( "Refus de la demande"  );
                    mProgressDialog.setCanceledOnTouchOutside( false );
                    mProgressDialog.setMessage( " Veuillez patienter pendant que nous traitons votre refus...");
                    mProgressDialog.show();

                    // on annule la demande envoyé, de ce fait les valeurs adéquates seront supprimées de la base de données
                    mFriendReqDatabase.child( mCurrent_user.getUid()).child( user_id ).removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        // si l'opération de suppression est un succès
                        public void onSuccess(Void aVoid) {

                            mFriendReqDatabase.child( user_id ).child( mCurrent_user.getUid() ).removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mCurrent_state = "pas_ami";

                                    mProfileSendReqBtn.setVisibility( View.VISIBLE );
                                    mProfileSendReqBtn.setEnabled( true );
                                    // le bouton "DEMANDE ENVOYEE" se change en "DEMANDE D'AMITIE"
                                    mProfileSendReqBtn.setText( R.string.Demande_d_ami );
                                    // le bouton "ANNULER LA DEMANDE" disparaît
                                    mprofileDeclineBtn.setVisibility( View.INVISIBLE );
                                    ////Un message "Demande annulée" apparaît
                                    Toast.makeText( ProfileActivity.this, "Demande refusée", Toast.LENGTH_SHORT ).show();
                                    mProgressDialog.dismiss();

                                }
                            } );

                        }
                    } );

                }


                //-----------------------------------------DISSOUDRE L'AMITIE ------------------------------------------------------------------------------

                if (mCurrent_state.equals( "ami" )){

                    mProgressDialog.setTitle( "Suppression de l'amitié"  );
                    mProgressDialog.setCanceledOnTouchOutside( false );
                    mProgressDialog.setMessage( " Veuillez patienter pendant que nous supprimons vos liens...");
                    mProgressDialog.show();

                    Map unfriendsMap = new HashMap(  );

                    //Supprimer la relation qu'il y'a entre les deux utilisateurs dans la table Wefriends vu que l'amitié a été dissous
                    unfriendsMap.put("WeFriends/" + mCurrent_user.getUid() + "/" + user_id , null );
                    unfriendsMap.put( "WeFriends/" + user_id + "/" + mCurrent_user.getUid() , null );


                    mRootRef.updateChildren( unfriendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError == null){

                                //il ne sont plus amis
                                mCurrent_state = "pas_ami";

                                // le bouton "DEMANDE ENVOYEE" se change en "DEMANDE D'AMITIE"
                                mProfileSendReqBtn.setText( R.string.Demande_d_ami );
                                // le bouton "ANNULER LA DEMANDE" disparaît
                                mProfileSendReqBtn.setVisibility( View.VISIBLE );
                                mprofileDeclineBtn.setVisibility( View.INVISIBLE );
                                mprofileDeclineBtn.setEnabled( false );
                                //L'icone alerte devient invisible
                                mAlertebtn.setVisibility( View.INVISIBLE );


                                ////Un message "Demande annulée" apparaît
                                Toast.makeText( ProfileActivity.this,"L'amitié est finie entre vous deux ", Toast.LENGTH_SHORT ).show();
                                mProgressDialog.dismiss();

                            }else{

                                String error = databaseError.getMessage();
                                Toast.makeText( ProfileActivity.this, error, Toast.LENGTH_SHORT ).show();


                            }

                            mProfileSendReqBtn.setEnabled( true );


                        }
                    } );

                }

            }

        });
//----------------------------------------- FIN  ANNULER/REFUSER LA DEMANDE Côté ENVOYEUR ET RECEPTEUR --------------------------------------------------------------------------------------


    }


}