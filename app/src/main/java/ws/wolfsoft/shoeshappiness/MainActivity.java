package ws.wolfsoft.shoeshappiness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.DateFormat;
import java.util.Date;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Mekongo ABANDA on 04/10/17.
 */

public class MainActivity extends AppCompatActivity {

    TextView signin;
    TextView signup;

    FirebaseAuth firebaseAuth;
    DatabaseReference mUsersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

       firebaseAuth = FirebaseAuth.getInstance();

       FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseAuth.getCurrentUser() != null){
            //Si l'utilisateur est déja connecté voici ce qui se passe
            //on cherche l'ID de l'utilisateur courant
            mUsersRef = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( firebaseAuth.getCurrentUser().getUid());
            Toast.makeText( MainActivity.this, "Content de vous savoir avec nous", Toast.LENGTH_SHORT ).show();
            Intent mainIntent = new Intent(MainActivity.this, PrincipalActivity.class);
            //quand on appuie sur retour ça quitte l'app si on est coonnecté
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        }


        signin = (TextView)findViewById(R.id.signin);
        signup = (TextView)findViewById(R.id.signup);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(MainActivity.this,ActivitySignin.class);
                startActivity(it);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(MainActivity.this,ActivitySignup.class);
                startActivity(it);

            }
        });
    }

   @Override
    public void onStart(){
        super.onStart();
        //vérifier si l'utilisateur est signé (non null) et mettre à jour l'interface utilisateur en conséquence
        FirebaseUser currentUser  = firebaseAuth.getCurrentUser();
        if (currentUser != null){

            sendToStart();

        } else {

            mUsersRef.child( "online" ).setValue("true");
        }

    }


    //lorsque l'activité s'arrête l'utilisateur est signé déconnecté
    @Override
    public void onStop(){
        super.onStop();

        FirebaseUser currentUser  = firebaseAuth.getCurrentUser();

        if (currentUser != null) {

            //Capture la date actuelle
            //final String currentDate = DateFormat.getDateTimeInstance().format( new Date());
            mUsersRef.child( "online" ).setValue( ServerValue.TIMESTAMP );

        }
    }

    private void sendToStart (){

        Intent StartIntent = new Intent( MainActivity.this, PrincipalActivity.class );
        startActivity( StartIntent );
        finish();

    }
}
