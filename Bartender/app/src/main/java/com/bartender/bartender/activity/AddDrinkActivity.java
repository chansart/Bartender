package com.bartender.bartender.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.bartender.bartender.BartenderApp;
import com.bartender.bartender.R;
import com.bartender.bartender.model.Drink;
import com.bartender.bartender.model.User;

/*
 * Ajout des nouvelles boissons dans la carte (option gestionnaire)
 */
public class AddDrinkActivity extends Activity {
    private Spinner drinkTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drink);

        drinkTypeSpinner = (Spinner) findViewById(R.id.add_drink_type);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.drinkTypes, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        drinkTypeSpinner.setAdapter(adapter);
/*        Button cancelButton = (Button) findViewById(R.id.add_drink_cancel_btn);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
            });*/

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

    //Renvoie à l'activité précédente.
    public void cancelBtn(View v){
        finish();

    }
    /**
     * Enregistre les données saisies par l'utilisateur dans une nouvelle boisson
     *
     * Cette méthode va s'occuper de récupérer les différentes valeurs remplie par l'utilisateur.
     * Ensuite il va créer un nouvel utilisateur avec ces données.
     *
     * @pre Le champ nom ne doit pas être vide.
     * @post Soit une boisson a été créé dans la base de données et l'activité
     * est fermée soit un message d'erreur est affiché à l'utilisateur.
     */
    public void save(View v) {

        String name = getName();
        // Vérification de la présence d'un nom.
        if (name == null) {
            return;
        }

        String type = getType();

        if (type == null) {
            return;
        }

        int stock = getStock();
        if (stock == 0) {
            return;
        }

        int stockMax = getStockMax();
        if (stockMax == 0) {
            return;
        }

        int seuil= getSeuil();
        if (seuil > stockMax) {
            BartenderApp.notifyLong(R.string.modify_seuil_error);
            return;
        }

        String descriptionEN = getDescriptionEN();
        String descriptionFR = getDescriptionFR();

        float  sprice= getSalingPrice();
        float pprice= getPurchasePrice();


        /* Création de l'élément */
        if (Drink.create(name, descriptionEN, descriptionFR, sprice, pprice, type, stock, stockMax, seuil)) {
            BartenderApp.notifyLong(R.string.add_drink_success_msg);
            // finish(); // On termine l'activité d'ajout afin de retourner au menu principal.
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);


        } else {
            BartenderApp.notifyLong(R.string.add_error_on_create);
        }
    }
    private String getName() {
        /* Récupération du nom de la boisson */
        EditText nameEditText = (EditText) findViewById(R.id.add_drink_name);
        String name = String.valueOf(nameEditText.getText());

        if (name.isEmpty()) {
            BartenderApp.notifyShort(R.string.add_error_drinkName_required);
            return null;
        }
        return name;
    }

    private String getDescriptionEN() {
        /* Récupération de la description de la boisson */
        EditText descrEditText = (EditText) findViewById(R.id.add_drink_descrEN_fill);
        String description = String.valueOf(descrEditText.getText());


        return description;
    }
    private String getDescriptionFR() {
        /* Récupération de la description de la boisson */
        EditText descrEditText = (EditText) findViewById(R.id.add_drink_descrFR_fill);
        String description = String.valueOf(descrEditText.getText());


        return description;
    }
    private String getType() {
        /* Récupération du type de boisson */
        Spinner spinnerType=(Spinner) findViewById(R.id.add_drink_type);
        String type = spinnerType.getSelectedItem().toString();
        return type;
    }

    private int getStock(){
        /* Récupération du stock de la boisson
         */
        EditText stockEditText= (EditText) findViewById(R.id.add_drink_stock_fill);
        int stock = Integer.parseInt(stockEditText.getText().toString());

        return stock;

    }

    private float getSalingPrice(){
        /* Récupération du prix de vente de la boisson
         */
        EditText salingPriceEditText= (EditText) findViewById(R.id.add_drink_sprice_fill);
        float salingPrice = Float.parseFloat(salingPriceEditText.getText().toString());

        return salingPrice;

    }

    private float getPurchasePrice(){
        /* Récupération du prix de vente de la boisson
         */
        EditText purchasePriceEditText= (EditText) findViewById(R.id.add_drink_pprice_fill);
        float purchasePrice = Float.parseFloat(purchasePriceEditText.getText().toString());

        return purchasePrice;

    }

    private int getStockMax(){
        /* Récupération du stock maximal de la boisson
         */
        EditText stockMaxEditText= (EditText) findViewById(R.id.add_drink_stockmax_fill);
        int stockmax = Integer.parseInt(stockMaxEditText.getText().toString());

        return stockmax;

    }

    private int getSeuil(){

        /* Récupération du stock minimal de la boisson
         */
        EditText seuilEditText= (EditText) findViewById(R.id.add_drink_seuil_fill);
        int seuil = Integer.parseInt(seuilEditText.getText().toString());

        return seuil;
    }
}
