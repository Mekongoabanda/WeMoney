package ws.wolfsoft.shoeshappiness;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import ws.wolfsoft.shoeshappiness.R;

/*
 * Created by Mekongo ABANDA on 04/10/17.
 */

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;
    private DatabaseReference mFriendsDatabase, mUsersDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View mMainView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // On gonfle le layout ce fragment et la retourne à la fin de la fonction principale
        mMainView = inflater.inflate( R.layout.fragment_friends, container, false );

        //référence de notre recyclerView et de notre variable
        mFriendsList = (RecyclerView) mMainView.findViewById( R.id.friends_list );
        //instance d'authentification
        mAuth = FirebaseAuth.getInstance();
        //notre utilisateur courant
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        //On récupère l'ID de l'utilisateur courant dans la Table "WeFriends de notre base de donnée
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child( "WeFriends" ).child( mCurrent_user_id );
        mFriendsDatabase.keepSynced( true );
        //référance vers les utilisateurs et leur données
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child( "Users");
        mUsersDatabase.keepSynced( true );

        //On adapte notre recyclerView au layout
        mFriendsList.setHasFixedSize( true );
        mFriendsList.setLayoutManager( new LinearLayoutManager( getContext() ) );

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

                Friends.class,
                R.layout.user_single_friends,
                FriendsViewHolder.class,
                mFriendsDatabase

        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, Friends friends, int position) {
                        friendsViewHolder.setDate(friends.getDate());
                        final String list_user_id = getRef( position ).getKey();

                        //ICI on essaie de trouver dans "Users" les ID des utilisateurs présents dans "WeFriends" afin de pouvoir avoir afficher leur données dans notre recyclerView
                        mUsersDatabase.child( list_user_id ).addValueEventListener( new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                final String userName = dataSnapshot.child( "nom" ).getValue().toString();
                                String userImage = dataSnapshot.child( "image" ).getValue().toString();


                                if (dataSnapshot.hasChild( "online" )){
                                    String userOnline = dataSnapshot.child( "online" ).getValue().toString();

                                    //Pour afficher la boule verte telle que décrite dans la procédure "setUsersOnline"
                                    friendsViewHolder.setUsersOnline( userOnline );
                                }

                                //On affecte le nom de la base de donnée dans notre procédure "setNom"
                                friendsViewHolder.setNom( userName );

                                //Pour charger l'image, nous allons utiliser Picasso dans la procédure "setUserimage"
                                friendsViewHolder.setUserImage( userImage, getContext() );


                                //------------------------------------------Action du click sur un utilisateur----------------------------------------------------------------------------------
                                friendsViewHolder.mView.setOnClickListener( new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {


                                        //ICI nous allons afficher les options que l'on souhaite afficher lorsqu'on click sur un Ami
                                        CharSequence options[] = new CharSequence[]{"Envoyer un message", "Ouvrir le profile", "Demande d'argent", "Envoyer de l'argent"};
                                        //définition de notre "Alert dialog"
                                        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsFragment.this.getContext());
                                        //Titre de notre builder
                                        builder.setTitle( "Que voulez vous faire" );
                                        //on affecte nos options crées là haut et on déclenche les actions possibles lors du clic de ces options
                                        builder.setItems( options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                //Evenements de click pour chaque items (options)
                                                if (i == 0){
                                                    //On se dirige vers la conversation de l'ami
                                                    Intent chatItent = new Intent(getContext(), ChatActivity.class);
                                                    chatItent.putExtra("user_id", list_user_id);
                                                    chatItent.putExtra( "user_name", userName );
                                                    startActivity(chatItent);

                                                }

                                                //Si on clique sur l'option 1 (2e option)
                                                if (i == 1){
                                                    //On se dirige vers le profil de l'ami
                                                    Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                                    profileIntent.putExtra("user_id", list_user_id);
                                                    startActivity(profileIntent);


                                                }


                                            }

                                        } );
                                        builder.show();
                                    }
                                } );

                                //--------------------------------------------------------------------------------------------------------------------------------------------------------------

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        } );
            }
        };

        mFriendsList.setAdapter( friendsRecyclerViewAdapter );

    }

    //une class Publique pour notre UserViewHolder
    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super( itemView );
            mView = itemView;

        }

        //Affectation locale de la date d'amitié avec notre objet statut du layout
        public void setDate (String date){

            TextView userStatusView = (TextView) mView.findViewById( R.id.User_friend_status );
            userStatusView.setText( date );

        }

        //Affectation locale du nom et de notre objet nom dans le layout
        public void setNom (String nom){

            TextView userNameView = (TextView) mView.findViewById( R.id.user_friend_name );
            userNameView.setText( nom );

        }

        public void setUserImage(final String image, final Context ctx){
            final CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_friend_image);
            ProgressBar mProgressBar = (ProgressBar) mView.findViewById( R.id.progressBarAllUsers );

            mProgressBar.setVisibility(View.VISIBLE);

            Picasso.with( ctx ).load( image ).networkPolicy( NetworkPolicy.OFFLINE )
                    .placeholder( R.drawable.default_avatar ).into( userImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    Picasso.with( ctx ).load( image ).placeholder( R.drawable.default_avatar ).into( userImageView);

                }
            });

            if(!image.equals(userImageView)) {

                mProgressBar.setVisibility( View.INVISIBLE );
            }
        }


        public void setUsersOnline (String Online_status){

             ImageView UsersOnlineView = (ImageView) mView.findViewById( R.id.user_single_online );

             if (Online_status.equals( "true" ) ){

                 UsersOnlineView.setVisibility( View.VISIBLE );

             }else {

                 UsersOnlineView.setVisibility( View.INVISIBLE );
             }
        }
    }
}
