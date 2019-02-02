package ws.wolfsoft.shoeshappiness;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ProgressDialog;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Main_messagerie extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ProgressDialog progressdialog;

    //pour nos viewpager, genre pour la communication de nos différents fragments avec le notre activité de messagerie
    private ViewPager mViewPager;
    //regardez la description du SectionPgerAdapter dans son code java SectionPagerAdapter.Java
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_messagerie);

        mAuth = FirebaseAuth.getInstance();

        //pour notre progressDialog déclarée là haut
        progressdialog = new ProgressDialog(this);

        //action sur notre toolbar du app_bar_layout.xml, on met un nom à notre toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("WeMoney (Chat)");

        //ici on gères nos tabs et les adapter en fonction de notre code dans le SectionsPagerAdapter.java
        mViewPager = (ViewPager) findViewById(R.id.messagerie_tabpager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

       mViewPager.setAdapter(mSectionsPagerAdapter);

       mTabLayout = (TabLayout) findViewById(R.id.messagerie_tabs);
        mTabLayout.setupWithViewPager(mViewPager);


    }


    //ici on applique les options du menu sur notre toolbar(options crées dans menu_messagerie
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_messagerie, menu);
        return true;
    }

//fonctions pour les conséquences du clic sur un item du menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);


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
            Intent settingsIntent = new Intent(Main_messagerie.this, Settings_activity.class);
            startActivity(settingsIntent);
        }

        if (item.getItemId() == R.id.all_user_btn){
            Intent settingsIntent = new Intent(Main_messagerie.this, UsersActivity.class);
            startActivity(settingsIntent);
        }

        if (item.getItemId() == R.id.Mes_informations_item){
            Intent settingsItent = new Intent (Main_messagerie.this, infos_persoActivity.class);
            startActivity(settingsItent);
        }

        if (item.getItemId() == R.id.envoyer_argent_item){
            Intent settingsIntent = new Intent (Main_messagerie.this, PrincipalActivity.class);
            startActivity( settingsIntent );
        }

        return true;
    }
}



