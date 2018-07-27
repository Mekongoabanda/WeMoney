package ws.wolfsoft.shoeshappiness;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class ActivitySignup extends AppCompatActivity implements View.OnClickListener {

  ImageView back;
    //ici on déclare nos variables pour l'inscription de l'utilisateur
    private MyEditText username;
    private MyEditText mail;
    private MyEditText password;
    private MyTextView signupbutt;

    private ProgressDialog progressdialog;
    private FirebaseAuth firebaseAuth;

    //déclaration pour utiliser la base de données
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        //on initilaise notre firebaseAuth dévlrée là haut, en vue de l'authentification à travers firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //pour notre progressBar déclarée là haut
        progressdialog = new ProgressDialog(this);

 //attribution des valeurs pour nos variables déclrées pour l'inscription
        username = (MyEditText) findViewById(R.id.username);
        mail = (MyEditText) findViewById(R.id.mail);
        password= (MyEditText) findViewById(R.id.password);
        signupbutt = (MyTextView)findViewById(R.id.signupbutton);

        back = (ImageView)findViewById(R.id.back);

        signupbutt.setOnClickListener(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ActivitySignup.this, MainActivity.class);
                startActivity(it);
            }
        });
    }

    //procédure privée pour les champs à remplir afin d'authentifier l'utilisateur
    private void registerUser(){
    final String Nom_d_utilisateur = username.getText().toString().trim();
    String Email = mail.getText().toString().trim();
    final String Mot_de_passe = password.getText().toString().trim();

    if (TextUtils.isEmpty(Email)){
        //si le champ de l'Email est vide
        Toast.makeText(this, "Veuillez entrer votre email", Toast.LENGTH_SHORT).show();
        //arreter l'execution de la fonction
        return;
    }
    if (TextUtils.isEmpty(Mot_de_passe)){
        //si le champ du mot de passe est vide
        Toast.makeText(this, "Veuillez entrer votre Mot de passe", Toast.LENGTH_SHORT).show();
        //arreter l'execution de la fonction
        return;
    }
    if (TextUtils.isEmpty(Nom_d_utilisateur)){
        // si le champs du nom d'user est vide
        Toast.makeText(this, "Veuillez entrer votre nom d'utilisateur", Toast.LENGTH_SHORT).show();
        //arreter l'execution de la fonction
        return;
    }
    //si la validation est ok
    //nous aurons d'abord une apparition d'une barre de progression
    progressdialog.setMessage("Patientez, enregistrement de l'utilisateur...");
    //SetCanceledonTouchOutside(false): ne pas enlever la barre de progression au touché à l'exterieure de celle ci
    progressdialog.setCanceledOnTouchOutside(false);
    progressdialog.show();
    firebaseAuth.createUserWithEmailAndPassword(Email,Mot_de_passe)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        //Lorsque l'utilisateur s'isncrit, les données suivantes lui sont assignées...genre statut par defaut, nom d'utilisateur qu'il a inscrit, photo (par défaut) etc
                        FirebaseUser current_user = firebaseAuth.getInstance().getCurrentUser();
                        String uid = current_user.getUid();

                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                        HashMap<String, String> userMap = new HashMap<>();
                        userMap.put("nom", Nom_d_utilisateur);
                        userMap.put("status", "Hi there, I'm using WeMoney.");
                        userMap.put("image", "default");
                        userMap.put("profil_image", "default");
                        userMap.put("password", Mot_de_passe);
                        userMap.put("phone_number", "default");

                        mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    // l'utilisateur est bien enregistré et vous êtes connecté
                                    //nous allons commencer l'activité de profil ici
                                    //maintenant, nous allons afficher un toast uniquement
                                    Toast.makeText(ActivitySignup.this, "Enregistrement réussi!", Toast.LENGTH_SHORT).show();
                                    progressdialog.hide();
                                    Intent mainIntent = new Intent(ActivitySignup.this, activity_bienvenue.class);
                                    //quand on appuie sur retour ça quitte l'app si on est coonnecté
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();
                                }
                            }
                        });

                    }
                    else{
                        Toast.makeText(ActivitySignup.this, "Impossible de vous enregistrer. Veuillez recommencer", Toast.LENGTH_SHORT).show();
                        progressdialog.hide();
                    }

                }
            });
}

    @Override
    public void onClick(View view) {
        //si view est égale au signupbutt alors on enregistre l'utilisateur (bref vous comprennez lol), genre si on observe un click sur signtubuut
if (view == signupbutt){
    registerUser();

}
    }
}

