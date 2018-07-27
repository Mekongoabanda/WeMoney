package ws.wolfsoft.shoeshappiness;

/**
 * Created by HP on 07/10/2017.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import customfonts.MyTextView;
public class confirmer_num extends AppCompatActivity {

    MyTextView buttonvalidezcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmer_num);

        buttonvalidezcode = (MyTextView) findViewById(R.id.buttonvalidezCode);
    }
}
