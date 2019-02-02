package ws.wolfsoft.shoeshappiness;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.sgaikar1.autoscrollinglayout.AutoScrollingLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;
import spencerstudios.com.bungeelib.Bungee;

import static android.content.Intent.getIntent;


public class MoiFragment extends Fragment  {

    private TextView display_name, profile_status, texte_wallet, gallerie, services_wemoney;
    private CircleImageView profile_image;
    private ImageView  qrcode, image_wallet;
    private Button mAlerteButton;


    private DatabaseReference mUserDatabase, mUserDatabase2, mFriendReqDatabase, mFriendDatabase, mNotificationDatabase, mRootRef;
    private FirebaseUser mCurrent_user;
    private FirebaseAuth firebaseAuth;

    private ProgressBar mProgressBar;

    private String mCurrent_state;

    public MoiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_moi, container, false );

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );

        //notre progress bar
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar_pp);

        display_name = (TextView) view.findViewById( R.id.display_name );
        profile_status = (TextView) view.findViewById( R.id.status );
        texte_wallet = (TextView) view.findViewById( R.id.mon_porte_monnaie );
        profile_image = (CircleImageView) view.findViewById( R.id.profile_image );
        qrcode = (ImageView) view.findViewById( R.id.qrcode );
        image_wallet = (ImageView) view.findViewById( R.id.image_wallet );
        mAlerteButton = (Button) view.findViewById( R.id.alerte_btn );
        services_wemoney = (TextView) view.findViewById( R.id.services_wemoney );

        //Utilisateur courant
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        final String current_uid = mCurrent_user.getUid();


        //route vers notre base de donnée afin de raccourcir tout appel
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child( "WeFriends" );
        mFriendDatabase.keepSynced( true );



        mUserDatabase = mRootRef.child("Users").child(current_uid);
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
                display_name.setText(nom);
                profile_status.setText(status);
                //charger l'image téléchargée qui se trouve dans notre storage firebase
                mProgressBar.setVisibility(View.VISIBLE);

                if(!image.equals("default")) {

                    //charger l'image téléchargée qui se trouve dans notre storage firebase
                    Picasso.with(getActivity()).load(image).networkPolicy( NetworkPolicy.OFFLINE ).placeholder(R.drawable.default_avatar).into(profile_image);

                }
                if (!image.equals( "image" )){
                    mProgressBar.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });



//---------------------------------------   DEBUT     FAIRE SCROLLER NOTRE BACKGROUND SUR LE PAGE PERSO DE L'USER-------------------------------------------------------------------------------------------------------------
        AutoScrollingLayout autoScrollingLayout =(AutoScrollingLayout) view.findViewById(R.id.scrolling_layout);
        autoScrollingLayout.setBackgroundSrc(ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.wemoney_num));
        autoScrollingLayout.setSpeed(1800f);
        autoScrollingLayout.setBackgroundAlpha(0.5f);
        autoScrollingLayout.setTintColor(Color.parseColor("#228B22"));
//---------------------------------------    FIN       FAIRE SCROLLER NOTRE BACKGROUND SUR LE PAGE PERSO DE L'USER-------------------------------------------------------------------------------------------------------------



        //---------------------------------------- DEBUT ACTION SUR LES CLICS------------------------------------------------------------------------------------------------------------------------------------
        texte_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(getActivity(), WalletActivity.class));
                Bungee.fade( getActivity() );
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(getActivity(), Activity_image_profil.class));
                Bungee.zoom( getActivity() );
            }
        });

        profile_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(getActivity(), StatusActivity.class));
                Bungee.fade( getActivity() );
            }
        });

        display_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(getActivity(), infos_persoActivity.class));
                Bungee.fade( getActivity() );
            }
        });

        qrcode.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(getActivity(),myQrCodeActivity.class ) );
                Bungee.fade( getActivity() );
            }
        } );

        //---------------------------------------- FIN ACTION SUR LES CLICS-----------------------------------------------------------------------------------
    }

}

