package ws.wolfsoft.shoeshappiness;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity {

   private String mUserChat;
   private Toolbar mToolbarChat;
   private DatabaseReference mRootRef, mUsersRef;
   private CircleImageView mProfileImage;
   private TextView mTitleView;
   private TextView mLastSeenView;
   private FirebaseAuth mAuth;
   private String  mCurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chat );

        //Initialisations
        mToolbarChat = (Toolbar) findViewById( R.id.chat_app_bar );
        setSupportActionBar( mToolbarChat );

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled( true );
        actionBar.setDisplayShowCustomEnabled( true );

        //On récupère l'intent de l'user non courant depuis notre "UsersActivity"
        mUserChat = getIntent().getStringExtra( "user_id" );
        //On avait assigné le nom sur "username" dans notre Friends Activity, il va vous aider à afficher ce nom sans passer par
        //une procédure addOnSingleValueEventListener
        String Username = getIntent().getStringExtra( "user_name" );

        //on asigne le nom à notre Toolbar
        //getSupportActionBar().setTitle( Username );

        //Ici nous allons créer un gonfleur afin d'ajouter l'image d el'utilisateur à la toolbar
        LayoutInflater inflater = (LayoutInflater) this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View action_bar_view = inflater.inflate( R.layout.chat_custom_bar,null );

        actionBar.setCustomView( action_bar_view );

//----------------REFERENCES DATABASE-----------
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mRootRef.keepSynced( true );
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        //on cherche l'ID de l'utilisateur courant
        mUsersRef = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( mAuth.getCurrentUser().getUid() );
   //-------------------------------------------

//-----------------------Elements du "chat_custom_bar.xml"------------------------------------------------------------------------------------
        mProfileImage = (CircleImageView) findViewById( R.id.Custom_app_image );
        mTitleView = (TextView) findViewById( R.id.custom_bar_title );
        mLastSeenView = (TextView) findViewById( R.id.custom_bar_seen );

        //On assigne le nom par "user_name" de l'activité précédente "WeFriends"
        mTitleView.setText(Username);


        mRootRef.child( "Users" ).child( mUserChat ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child( "online" ).getValue().toString();
                final String image_profile = dataSnapshot.child("image").getValue().toString();

                if(online.equals( "true" )){

                    mLastSeenView.setText( "En ligne" );

                }else {
                    //si il n'est plus en ligne
                    ///on déclare notre class GetTimeAgo
                    GetTimeAgo gettimeago = new GetTimeAgo();
                    //on crée une variable last time qui va contenir la situation selon le calcul fait dans GetTimeAgo.class
                    long lastTime = Long.parseLong( online );
                    //On stock dans "lastSeenTime" notre long lasttime
                    String lastSeenTime = gettimeago.getTimeAgo( lastTime, getApplicationContext() );
                    //On affecte donc notre lastSeenTime dans notre interface graphique pour afficher depuis quand il et déconnecté
                    mLastSeenView.setText( lastSeenTime );
                }

                    //-------------------------------------Chargement de l'image----------------------------------------

                    //Mais si nous voulons voir la photo même en étant hors ligne nous utilisons ceci pour la mettre dans la mémoire cache
                    Picasso.with( ChatActivity.this ).load( image_profile ).networkPolicy( NetworkPolicy.OFFLINE ).placeholder( R.drawable.default_avatar )
                            .into( mProfileImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {

                                    Picasso.with( ChatActivity.this ).load( image_profile ).placeholder( R.drawable.default_avatar ).into( mProfileImage );

                                }
                            } );
                    //-----------------------------------------------------------------------------------------

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        //****************************************Création de notre table pour la CHAT INSTANTANEE*********************************************************************
        mRootRef.child( "Chat" ).child( mCurrentUserId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild( mUserChat )){

                    //Nous définissons les attibuts et leur valeur qui seront dans la table tchat ("Vue" et "le temps capturé par le server"
                    Map chatAddMap = new HashMap();
                    chatAddMap.put( "Vue", false );
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP );

                    //Nous construisons la table de Tchat de la manière suivante:
                    Map chatUserMap =new HashMap();
                    chatUserMap.put( "Chat/" + mCurrentUserId + "/" + mUserChat, chatAddMap );
                    chatUserMap.put( "Chat/" + mUserChat + "/" + mCurrentUserId, chatAddMap );

                    mRootRef.updateChildren( chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            //Si il n'ya pas eu d'erreur dans la base de données lors de la création de notre table
                            if (databaseError != null){

                                Log.d( "CHAT_LOG", databaseError.getMessage().toString());
                            }

                        }
                    } );
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );

    }

}
