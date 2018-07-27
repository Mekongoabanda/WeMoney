package ws.wolfsoft.shoeshappiness;  //CODE DESTINEE A LA PAGE DE CHANGEMENT DE STATUT

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.Reference;

public class StatusActivity extends AppCompatActivity {

    //déclaration de notre Toolbar
    private Toolbar mToolbar;

    private TextInputLayout mStatus;
    private CardView mSavebtn;

    //firebase
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;

    //progress
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //Firebase
        //cette fonction c'est pour l'utilisateur courant (ne surtout pas l'oublier), elle permet de pointer vers l'utilisateur du compte et pas chez une autre personne
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        //on va chercher à ecrire dans les données de l'user, genre cette méthode va pointer vers la base de donnée "Users"
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);


        //Donnons un titre à notre toolbar
        mToolbar = (Toolbar)findViewById(R.id.status_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String status_value = getIntent().getStringExtra("status_value");

        mStatus = (TextInputLayout) findViewById(R.id.status_input);
        mSavebtn = (CardView) findViewById(R.id.status_input_btn);

        mStatus.getEditText().setText(status_value);

        //vous connaissez cette methode qui dit "lorsqu'on clique sur le boutton sauvegarder dans l'activité du statut..."
        mSavebtn.setOnClickListener(new View.OnClickListener(){
           @Override
            public void onClick(View view){

               //Progress
               mProgress = new ProgressDialog(StatusActivity.this);
               mProgress.setTitle("Saving Changes");
               mProgress.setMessage("Veuillez patienter pendant que nous enregistrons les modifications");
               mProgress.show();

               String status = mStatus.getEditText().getText().toString();
               mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()){

                           mProgress.dismiss();
                           Intent status_intent = new Intent(StatusActivity.this, Settings_activity.class);
                           startActivity(status_intent);

                       }else {

                           Toast.makeText(getApplicationContext(), "Erreur, veuillez vérifier votre connexion internet", Toast.LENGTH_SHORT).show();
                       }
                   }
               });
           }
        });
    }
}
