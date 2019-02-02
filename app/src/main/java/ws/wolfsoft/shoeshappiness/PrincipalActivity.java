package ws.wolfsoft.shoeshappiness;

/*
created by MEKONGO ABANDA 03/07/2018
 */

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.emmanuelkehinde.shutdown.Shutdown;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;


import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.Date;

import spencerstudios.com.bungeelib.Bungee;

public class PrincipalActivity extends AppCompatActivity {

    //ceci est en rapport avec notre procédure publique OnBackPressed
    final String TAG = this.getClass().getName();

    private Toolbar mToolbar;
    private ProgressDialog progressdialog;

    private DatabaseReference mUsersRef;
    private FirebaseAuth mAuth;

    //L'on déclare tous les fragments qui pourront être afficher lors du switch de nos items de la bottomBar
    private ChatsFragment chatsFragment;
    private FriendsFragment friendsFragment;
    private RequestsFragment requestsFragment;
    private MoiFragment moiFragment;
    private ExtrasFragment extrasFragment;


    //déclaration de notre Frame layout qui va contenir nos fragments
    private FrameLayout mMainFrame;

    //déclaration de notre bottom navigation view
    private BottomNavigationView mMainNav;

    //pour nos viewpager, genre pour la communication de nos différents fragments avec le notre activité de messagerie
    private ViewPager mViewPager;
    //regardez la description du SectionPgerAdapter dans son code java SectionPagerAdapter.Java
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_principal );


        mToolbar = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("WeMoney");


       //****** on initialise notre frame layout et notre bottom view navigation******//
        mMainFrame = (FrameLayout) findViewById( R.id.main_frame );
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);
       //*****************************************************************************//
       //pour notre progressDialog déclarée là haut
        progressdialog = new ProgressDialog(this);
        //initialisation de l'authentification
        mAuth = FirebaseAuth.getInstance();
        //on initialise notre animateur de toolbar
        //on cherche l'ID de l'utilisateur courant
        mUsersRef = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( mAuth.getCurrentUser().getUid() );
        //action sur notre toolbar du app_bar_layout.xml, on met un nom à notre toolbar

        //******l'on initialise nos fragments déclarés plus haut******//
        chatsFragment = new ChatsFragment();
        friendsFragment = new FriendsFragment();
        requestsFragment = new RequestsFragment();
        extrasFragment = new ExtrasFragment();
        moiFragment = new MoiFragment();
        //***********************************************************//

        //On indique le fragment par défaut
        setFragment( chatsFragment );
        //***************************************************************************************************************************

        mMainNav.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

               //action lorsque clic sur un item de notre bottom bar
                switch (item.getItemId()) {

                    case R.id.nav_chat:
                        //change la couleur de l'item en vert (thème de l'app)
                        mMainNav.setItemBackgroundResource( R.color.green );
                        //attribution d'un fragment
                        setFragment(chatsFragment);
                        getSupportActionBar().setTitle("WeMoney (Chat)");
                        Bungee.fade( PrincipalActivity.this );
                        return true;

                    case R.id.nav_friends:
                        //change la couleur de l'item
                        mMainNav.setItemBackgroundResource( R.color.deuxiemecouleur );
                        //attribution d'un fragment
                        setFragment( friendsFragment );
                        getSupportActionBar().setTitle("WeFriends");
                        Bungee.fade( PrincipalActivity.this );
                        return true;

                    case R.id.nav_extras:
                        //change la couleur de l'item
                        mMainNav.setItemBackgroundResource( R.color.deuxiemecouleur );
                        //attribution d'un fragment
                        setFragment( extrasFragment );
                        Bungee.fade( PrincipalActivity.this );
                        getSupportActionBar().setTitle("WeMoney (Extras)");
                        return true;

                    case R.id.nav_moi:
                        //change la couleur de l'item
                        mMainNav.setItemBackgroundResource( R.color.deuxiemecouleur );
                        //attribution d'un fragment
                        setFragment( moiFragment );
                        Bungee.fade( PrincipalActivity.this );
                        getSupportActionBar().setTitle("WeMoney");
                        return true;

                }
                return false;
            }
        } );


    }


    @Override
    public void onStart(){
        super.onStart();
        //vérifier si l'utilisateur est signé (non null) et mettre à jour l'interface utilisateur en conséquence
        FirebaseUser currentUser  = mAuth.getCurrentUser();

        if (currentUser != null){

            mUsersRef.child( "online" ).setValue("true");

        } else {

            mUsersRef.child( "online" ).setValue("true");
        }

    }


    @Override
    public void onStop(){
        super.onStop();

        //Capture la date actuelle
       // final String currentDate = DateFormat.getDateTimeInstance().format( new Date());
            mUsersRef.child( "online" ).setValue( ServerValue.TIMESTAMP);

    }


 //---------------------------------------------------------------DEBUT APPUYER DEUX FOIS POUR QUITTER--------------------------------------------------------------------------------------
    //twice c'est le booléen qui nous permettra de savoir que l'utilisateur a appuyé deux fois déja sur le bouton
    boolean twice = false;
    @Override
    public void onBackPressed() {

        Log.d(TAG, "click");

        // Si l'utilisateur appuit deux fois alors il retourne à la page précédente
        if (twice == true){
            // CE CODE EST POUR QUITTER L'APPLICTION lors du click
            Intent intent = new Intent (Intent.ACTION_MAIN);
            intent.addCategory( Intent.CATEGORY_HOME );
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            startActivity( intent );
            finish();
            System.exit( 0 );

            //le système rentre à la page précédente
            //super.onBackPressed();
            //avec une animation fondue
            Bungee.fade( PrincipalActivity.this );
        }
        twice = true;
        Log.d(TAG, "twice: " + twice);

        Toast.makeText( PrincipalActivity.this, "Hey, vous êtes en train de quitter WeMoney!", Toast.LENGTH_LONG ).show();
        new Handler(  ).postDelayed( new Runnable() {
            @Override
            public void run() {
                twice = false;
                Log.d(TAG, "twice: " + twice);
            }
            //delayMillis c'est le temps que ça fait avant de retourner à false (false c'est ce qui se passe lorsqu'on clique la 1ere fois)
        }, 10000);


    }
    //--------------------------------------------------------FIN APPUYER DEUX FOIS POUR QUITTER-------------------------------------------------------------------------------------------------------------



    //--------------------------------------------------------DEBUT  METHODES PRIVEES pour assigner les fragments------------------------------------------------------------------------------------------

    //Méthode privée pour assigner le fragment dans le FrameLayout
    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace( R.id.main_frame, fragment);
        fragmentTransaction.commit();

    }

    //--------------------------------------------------------FIN METHODES PRIVEES pour assigner les fragments------------------------------------------------------------------------------------------





/*------------------------------------------------------------------------  FIN TOOLBAR MENU ITEM    ----------------------------------------------------------------------*/
    //ici on applique les options du menu sur notre toolbar(options crées dans menu_messagerie
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_messagerie, menu);
        return true;
    }

    //fonctions pour les conséquences du clic sur un item du menu de la toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        //si l'utilisateur clique sur l'item actualité
        if(item.getItemId() == R.id.actu_item){
            //nous aurons d'abord un message d'apparition( si l'action est rapide c'est normale que le message n'apparaisse pas
            progressdialog.setMessage("Chargement du fil d'actualité WeMoney");
            progressdialog.show();
            Intent actuIntent = new Intent(PrincipalActivity.this, ActivityActuMain.class);
            startActivity(actuIntent);
            progressdialog.hide();

        }

//ici on applique le fonction déconnexion sur notre item "deconnexion" du menu toolbar si l'utilisateur appuie le boutton de déconnexion
        if (item.getItemId() == R.id.main_logOut) {

            //si la déconnexion est ok
            //nous aurons d'abord une apparition d'une barre de progression
            progressdialog.setMessage("Déconnexion  en cours...");
            progressdialog.show();
            mAuth.signOut();
            //progressdialog.hide();
            Intent mainIntent = new Intent(PrincipalActivity.this, ActivitySignin.class);
            //quand on appuie sur retour ça quitte l'app si on est coonnecté car la tâche Principale activity a été supprimé
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
            Toast.makeText( PrincipalActivity.this, "Déconnecté", Toast.LENGTH_SHORT ).show();
            //progressdialog.show();
            progressdialog.dismiss();
        }

        if (item.getItemId() == R.id.settings_btn){
            Intent settingsIntent = new Intent(PrincipalActivity.this, Settings_activity.class);
            startActivity(settingsIntent);
        }

        if (item.getItemId() == R.id.all_user_btn){
            Intent settingsIntent = new Intent(PrincipalActivity.this, UsersActivity.class);
            startActivity(settingsIntent);
        }

        if (item.getItemId() == R.id.QR_code_item){
            Intent qrcode = new Intent(PrincipalActivity.this, myQrCodeActivity.class);
            startActivity(qrcode);

        }
        if (item.getItemId() == R.id.Mes_informations_item){
            Intent settingsItent = new Intent (PrincipalActivity.this, infos_persoActivity.class);
            startActivity(settingsItent);
        }

        if (item.getItemId() == R.id.actu_item){
            Intent actuItent = new Intent (PrincipalActivity.this, ActivityActuMain.class);
            startActivity(actuItent);
        }

        if (item.getItemId() == R.id.envoyer_argent_item){
            Intent actuItent = new Intent (PrincipalActivity.this, PutCardActivity.class);
            startActivity(actuItent);
        }

        return true;
    }
    /*------------------------------------------------------------------------  FIN TOOLBAR MENU ITEM   ----------------------------------------------------------------------*/
}
