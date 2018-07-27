package ws.wolfsoft.shoeshappiness;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import customfonts.MyEditText;
import customfonts.MyTextView;

/**
 * Created by Mekongo ABANDA on 04/10/17.
 */

public class ActivitySignin extends AppCompatActivity implements View.OnClickListener {

    ImageView back;

    //on déclare nos variables pour la connexion
    private MyEditText username;
    private MyEditText password;
    private  MyTextView buttonsignin;

    private ProgressDialog progressdialog;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        //on initialise nos variables déclarées pour la connexion
        password = (MyEditText) findViewById(R.id.password);
        username = (MyEditText) findViewById(R.id.username);
        buttonsignin = (MyTextView) findViewById(R.id.signin);

        //on initilaise notre firebaseAuth déclarée là haut, en vue de la connexion à travers firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // si la méthode getcurrentuser des objets n'est pas nulle
        // signifie que l'utilisateur est déjà connecté
        if(firebaseAuth.getCurrentUser() != null){
           //Si l'utilisateur est déja connecté voici ce qui se passe
            Intent mainIntent = new Intent(ActivitySignin.this, Main_messagerie.class);
            //quand on appuie sur retour ça quitte l'app si on est coonnecté
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        }

        //pour notre progressDialog déclarée là haut
        progressdialog = new ProgressDialog(this);


        back = (ImageView) findViewById(R.id.back);
        //gérer le click
        buttonsignin.setOnClickListener(this);

        //le bouton back (flêche noire en haut à gauche de l'écran) va nous dirigez vers l'acceuil en cliquant dessus
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ActivitySignin.this, MainActivity.class);
                startActivity(it);
            }
        });
    }

    //procédure privée pour les champs à remplir afin de connecter l'utilisateur
    private void userLogin(){
        String E_mail = username.getText().toString().trim();
        String Mot_de_passe = password.getText().toString().trim();

        if (TextUtils.isEmpty(Mot_de_passe)){
            //si le champ du mot de passe est vide
            Toast.makeText(this, "Veuillez entrer votre Mot de passe", Toast.LENGTH_SHORT).show();
            //arreter l'execution de la fonction
            return;
        }
        if (TextUtils.isEmpty(E_mail)){
            // si le champs du nom d'user est vide
            Toast.makeText(this, "Veuillez entrer votre Email", Toast.LENGTH_SHORT).show();
            //arreter l'execution de la fonction
            return;
        }
        //si la validation est ok
        //nous aurons d'abord une apparition d'une barre de progression
        progressdialog.setMessage("Patientez, connexion  en cours...");
        //SetCanceledonTouchOutside(false): ne pas enlever la barre de progression au touché à l'exterieure de celle ci
        progressdialog.setCanceledOnTouchOutside(false);
        progressdialog.show();

        firebaseAuth.signInWithEmailAndPassword(E_mail,Mot_de_passe)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressdialog.dismiss();
                        if(task.isSuccessful()){

                        //démarrer l'activité de profil
                           Intent mainIntent = new Intent(ActivitySignin.this, Main_messagerie.class);

                            //quand on appuie sur retour ça quitte l'app si on est coonnecté
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();

                        }else{
                            Toast.makeText(ActivitySignin.this, "Impossible de vous connecter. Veuillez recommencer", Toast.LENGTH_SHORT).show();
                            progressdialog.hide();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        //si view est égale au buttonsignin alors on connecte l'utilisateur (bref vous comprennez lol), genre si on observe un click sur buttonsignin
        if(view == buttonsignin){
            userLogin(); //ici on appelle la procédure privée userLogin crée là haut

        }

    }
}
