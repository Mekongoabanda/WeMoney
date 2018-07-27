package ws.wolfsoft.shoeshappiness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Mekongo ABANDA on 04/10/17.
 */

public class MainActivity extends AppCompatActivity {

    TextView signin;
    TextView signup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);




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
}
