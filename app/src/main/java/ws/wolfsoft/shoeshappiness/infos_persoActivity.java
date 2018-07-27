package ws.wolfsoft.shoeshappiness;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class infos_persoActivity extends AppCompatActivity {

    //Objets
    private CardView mRec_infos_btn;
    private TextInputLayout mpassword;
    private TextInputLayout mNom;
    private TextInputLayout mNum_tel;
    private EditText mEdit_nom;
    private EditText mEdit_password;
    private EditText mEdit_tel;


    //déclaration de notre Toolbar
    private Toolbar mToolbar;

    //firebase
    private DatabaseReference mPasswordDatabase;
    private DatabaseReference mNomDatabase;
    private DatabaseReference mNumeroTelDatabase;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth firebaseAuth;

    //progress
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos_perso);

        //pour notre progressBar déclarée là haut
        mProgress = new ProgressDialog(this);


        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        //cette fonction c'est pour l'utilisateur courant (ne surtout pas l'oublier), elle permet de pointer vers l'utilisateur du compte et pas chez une autre personne
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        //on va chercher à ecrire dans les données de l'user, genre cette méthode va pointer vers la base de donnée "Users"
        mNomDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mNumeroTelDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mPasswordDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        //Donnons un titre à notre toolbar
        mToolbar = (Toolbar)findViewById(R.id.status_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Changer Mes Informations");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // On crée des variables qui vont contenir les informations mis à jour afin de les attribuer
        String Nom_value = getIntent().getStringExtra("Nom_value");
        String phone_number_value = getIntent().getStringExtra("phone_number_value");
        String password_value = getIntent().getStringExtra("password_value");

        //On donne des valeurs à nos variables privées déclarées là haut
        mNom = (TextInputLayout) findViewById(R.id.username_input);
        mNum_tel = (TextInputLayout) findViewById(R.id.num_input);
        mpassword = (TextInputLayout) findViewById(R.id.password_input);
        mRec_infos_btn = (CardView) findViewById(R.id.rec_infos_btn);
        mEdit_nom = (EditText) findViewById(R.id.edit_username);
        mEdit_tel = (EditText) findViewById(R.id.edit_num);
        mEdit_password = (EditText) findViewById(R.id.edit_password);

        //ICI nous assignons les nouvelles données entrées et mettons ) jour le nom, le number_phone et le password
        mNom.getEditText().setText(Nom_value);
        mNum_tel.getEditText().setText(phone_number_value);
        mpassword.getEditText().setText(password_value);
        
        //vous connaissez cette methode qui dit "lorsqu'on clique sur le boutton sauvegarder dans l'activité du changement d'informations..."
        mRec_infos_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //Progress
                mProgress = new ProgressDialog(infos_persoActivity.this);
                mProgress.setTitle("Mise à jour des informations...");
                mProgress.setMessage("Veuillez patienter pendant que nous enregistrons les modifications");
                //SetCanceledonTouchOutside(false): ne pas enlever la barre de progression au touché à l'exterieure de celle ci
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();

                //on crée des string qu'on affecte à nos objets
                String Nom = mNom.getEditText().getText().toString();
                final String password = mpassword.getEditText().getText().toString();
                String number_phone = mNum_tel.getEditText().getText().toString();

                //ICI on gère les affectations en relation avec la base de données pour le mot de passe
                mPasswordDatabase.child("password").setValue(password).addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            firebaseAuth.sendPasswordResetEmail(password);

                            mProgress.dismiss();


                        }else {

                            Toast.makeText(getApplicationContext(), "Erreur au niveau du mot de passe, veuillez vérifier votre connexion internet", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //ICI on gère les affectations en relation avec la base de données pour le Nom d'utilisateur

                mNomDatabase.child("nom").setValue(Nom).addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            mProgress.dismiss();
                            Intent status_intent = new Intent(infos_persoActivity.this, Main_messagerie.class);
                            startActivity(status_intent);
                            Toast.makeText(getApplicationContext(), "Nom mis à jour", Toast.LENGTH_SHORT).show();


                        }else {

                            Toast.makeText(getApplicationContext(), "Erreur au niveau du Nom, veuillez vérifier votre connexion internet", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                ////ICI on gère les affectations en relation avec la base de données pour le Numéro de telephone
                mNumeroTelDatabase.child("phone_number").setValue(number_phone).addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            mProgress.dismiss();
                            Toast.makeText(getApplicationContext(), "Numéro mis à jour", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "Pour plus de sécurité, allez modifier votre mot de passe depuis votre boite mail", Toast.LENGTH_SHORT).show();

                        }else {

                            Toast.makeText(getApplicationContext(), "Erreur au niveau du Numéro de téléphone, veuillez vérifier votre connexion internet", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
