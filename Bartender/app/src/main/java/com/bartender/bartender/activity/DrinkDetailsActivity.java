package com.bartender.bartender.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bartender.bartender.R;
import com.bartender.bartender.model.Drink;

import java.util.Locale;

//Permet d'afficher les détails d'une boisson lorsque l'on clique dessus à partir de la carte.


public class DrinkDetailsActivity extends Activity {
    String LOCALE_FRANCAIS = "fr";
    String LOCALE_ENGLISH = "en";
    Locale mLocale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_details);
        Bundle b = getIntent().getExtras();
        mLocale=Locale.getDefault();
        if(b!= null){

            Drink drink= (Drink) b.getSerializable("Drink");
            String type= drink.getType();
            ((TextView) findViewById(R.id.details_name_drink)).setText(drink.getName());

            if(mLocale.getLanguage().compareTo(LOCALE_ENGLISH)==0){
                ((TextView) findViewById(R.id.details_description_drink)).setText(drink.getDescriptionEN());

            }
            else{
                ((TextView) findViewById(R.id.details_description_drink)).setText(drink.getDescriptionFR());
            }

            ((TextView) findViewById(R.id.details_prix_drink)).setText(getString(R.string.unit_price, drink.getSaling_price())+ " " + drink.getSaling_price() + "€");
            ((TextView) findViewById(R.id.details_type_drink)).setText(getString(R.string.drink_type) + " " + type);

            if(type.equals("Vin")) {
                ((ImageView) findViewById(R.id.details_image)).setImageResource(R.drawable.vin);
            }
            else if (type.equals("Biere")){
                ((ImageView) findViewById(R.id.details_image)).setImageResource(R.drawable.biere);

            }
            else if (type.equals("Soft")){

                ((ImageView) findViewById(R.id.details_image)).setImageResource(R.drawable.soft);
            }
            else if (type.equals("Cocktail")){

                ((ImageView) findViewById(R.id.details_image)).setImageResource(R.drawable.cocktail);
            }
            else if (type.equals("Boisson Chaude")){

                ((ImageView) findViewById(R.id.details_image)).setImageResource(R.drawable.boisson_chaude);
            }


        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drink_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
