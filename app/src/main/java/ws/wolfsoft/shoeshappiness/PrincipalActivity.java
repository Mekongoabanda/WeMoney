package ws.wolfsoft.shoeshappiness;

/*
created by MEKONGO ABANDA 03/07/2018
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;

public class PrincipalActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ProgressDialog progressdialog;

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

        mAuth = FirebaseAuth.getInstance();
        //action sur notre toolbar du app_bar_layout.xml, on met un nom à notre toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("WeMoney");


       //****** on initialise notre frame layout et notre bottom view navigation******//
        mMainFrame = (FrameLayout) findViewById( R.id.main_frame );
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);
       //*****************************************************************************//

        //******l'on initialise nos fragments déclarés plus haut******//
        chatsFragment = new ChatsFragment();
        friendsFragment = new FriendsFragment();
        requestsFragment = new RequestsFragment();
        extrasFragment = new ExtrasFragment();
        moiFragment = new MoiFragment();
        //***********************************************************//

        //On indique le fragment par défaut
        setFragment( chatsFragment );

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
                        return true;
                        
                    case R.id.nav_friends:
                        //change la couleur de l'item
                        mMainNav.setItemBackgroundResource( R.color.deuxiemecouleur );
                        //attribution d'un fragment
                        setFragment( friendsFragment );
                        return true;
                        
                    case R.id.nav_extras:
                        //change la couleur de l'item
                        mMainNav.setItemBackgroundResource( R.color.deuxiemecouleur );
                        //attribution d'un fragment
                        setFragment( extrasFragment );
                        return true;
                        
                    case R.id.nav_moi:
                        //change la couleur de l'item
                        mMainNav.setItemBackgroundResource( R.color.deuxiemecouleur );
                        //attribution d'un fragment
                        setFragment( moiFragment );
                        return true;
                    
                }
                return false;
            }
        } );


    }

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
            FirebaseAuth.getInstance().signOut();
            progressdialog.hide();
            startActivity(new Intent(this, MainActivity.class));
            progressdialog.setMessage("Vous êtes déconnectés");
            progressdialog.show();
            finish();
        }

        if (item.getItemId() == R.id.settings_btn){
            Intent settingsIntent = new Intent(PrincipalActivity.this, Settings_activity.class);
            startActivity(settingsIntent);
        }

        if (item.getItemId() == R.id.all_user_btn){
            Intent settingsIntent = new Intent(PrincipalActivity.this, UsersActivity.class);
            startActivity(settingsIntent);
        }

        if (item.getItemId() == R.id.Mes_informations_item){
            Intent settingsItent = new Intent (PrincipalActivity.this, infos_persoActivity.class);
            startActivity(settingsItent);
        }

        return true;
    }
    /*------------------------------------------------------------------------  FIN TOOLBAR MENU ITEM   ----------------------------------------------------------------------*/
}
