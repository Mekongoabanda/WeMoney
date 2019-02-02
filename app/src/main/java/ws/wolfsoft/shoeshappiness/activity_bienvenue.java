package ws.wolfsoft.shoeshappiness;

/**
 * Created by HP on 07/10/2017.
 */
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import customfonts.MyTextView;
public class activity_bienvenue extends AppCompatActivity {

    //objets
   private MyTextView valideznum;
    private TextView  Nom_utilisateur;
    private EditText mEntrerTelephone;

    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mEntrerTelephoneReference;

    //progress
    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bienvenue);

        firebaseAuth = FirebaseAuth.getInstance();

        //Firebase
        //cette fonction c'est pour l'utilisateur courant (ne surtout pas l'oublier), elle permet de pointer vers l'utilisateur du compte et pas chez une autre personne
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        //on va chercher à ecrire dans les données de l'user, genre cette méthode va pointer vers la base de donnée "Users"
        mEntrerTelephoneReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        FirebaseUser user = firebaseAuth.getCurrentUser();

        // On crée une variable qui va contenir les informations mis à jour afin de les attribuer
        String Num_tel = getIntent().getStringExtra("phone_number_value");

        Nom_utilisateur = (TextView) findViewById(R.id.Nom_utilisateur);
        valideznum = (MyTextView) findViewById(R.id.ValiderNum);
        mEntrerTelephone =(EditText) findViewById(R.id.entrerTelephone);


        //ICI nous assignons la nouvelle donnée entrée et attribuons le numéro de téléphone au nouvel inscrit
         mEntrerTelephone.setText(Num_tel);

//mettre l'email de l'utilisateur à l'emplacement du TexView "nom utilisateur"
        Nom_utilisateur.setText(user.getEmail());


        valideznum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Progress
                mProgress = new ProgressDialog(activity_bienvenue.this);
                mProgress.setTitle("Enregistrement de votre numéro");
                mProgress.setMessage("Veuillez patienter pendant que nous enregistrons votre numéro");
                mProgress.show();

                //on crée des string qu'on affecte à nos objets
                String Num = mEntrerTelephone.getText().toString();

                //ICI on gère les affectations en relation avec la base de données pour le mot de passe
                mEntrerTelephoneReference.child("phone_number").setValue(Num).addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            mProgress.dismiss();
                            Intent status_intent = new Intent(activity_bienvenue.this, PrincipalActivity.class);
                            startActivity(status_intent);
                            Toast.makeText(getApplicationContext(), "Numéro enregistré", Toast.LENGTH_SHORT).show();

                        }else {

                            Toast.makeText(getApplicationContext(), "Erreur au niveau du numéro ou sinon veuillez vérifier votre connexion internet", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
