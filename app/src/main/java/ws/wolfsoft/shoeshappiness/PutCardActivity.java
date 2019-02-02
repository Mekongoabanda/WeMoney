package ws.wolfsoft.shoeshappiness;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.craftman.cardform.Card;
import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnPayBtnClickListner;

public class PutCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_put_card );

        CardForm cardForm = (CardForm) findViewById( R.id.carbe_bancaire );
        TextView textCart = (TextView) findViewById( R.id.payment_amount );
        Button mbtn_valider = (Button) findViewById( R.id.btn_pay );

       textCart.setText( "NA" );
       mbtn_valider.setText( "Valider l'ajout" );

       cardForm.setPayBtnClickListner( new OnPayBtnClickListner() {
           @Override
           public void onClick(Card card) {
               Toast.makeText( PutCardActivity.this, "Name : "+ card.getName() + " || Number: "+card.getNumber() + " || CVC: " +
                       card.getCVC() + String.format( " || Expire : %d/%d", card.getExpMonth(), card.getExpYear() ), Toast.LENGTH_LONG ).show();
           }
       } );

    }
}
