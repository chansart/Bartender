package com.bartender.bartender.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.bartender.bartender.BartenderApp;
import com.bartender.bartender.R;
import com.bartender.bartender.model.Drink;

public class ModifyDrinkActivity extends ActionBarActivity {
    private Spinner drinkTypeSpinner;
    private Drink drink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_drink);
        //CREATION DU SPINNER POUR LE TYPE DE BOISSON
        drinkTypeSpinner = (Spinner) findViewById(R.id.modify_drink_type);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.drinkTypes, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        drinkTypeSpinner.setAdapter(adapter);

        Bundle b = getIntent().getExtras();
        if (b != null) {

            drink = (Drink) b.getSerializable("Drink");
            String type = drink.getType();
            ((EditText) findViewById(R.id.modify_drink_descrEN_fill)).setText(drink.getDescriptionEN());
            ((EditText) findViewById(R.id.modify_drink_descrFR_fill)).setText(drink.getDescriptionFR());
            ((EditText) findViewById(R.id.modify_drink_name_fill)).setText(drink.getName());
            ((EditText) findViewById(R.id.modify_drink_pprice_fill)).setText(Float.toString(drink.getPurchase_price()));
            ((EditText) findViewById(R.id.modify_drink_sprice_fill)).setText(Float.toString(drink.getSaling_price()));
            ((EditText) findViewById(R.id.modify_drink_seuil_fill)).setText(Integer.toString(drink.getSeuil()));
            ((EditText) findViewById(R.id.modify_drink_stock_fill)).setText(Integer.toString(drink.getStock()));
            ((EditText) findViewById(R.id.modify_drink_stockmax_fill)).setText(Integer.toString(drink.getStock_max()));

            if(type.equals("Vin")) {
                drinkTypeSpinner.setSelection(2);
            }
            else if (type.equals("Biere")){
                drinkTypeSpinner.setSelection(1);

            }
            else if (type.equals("Soft")){

                drinkTypeSpinner.setSelection(0);
            }
            else if (type.equals("Cocktail")){

                drinkTypeSpinner.setSelection(3);
            }
            else if (type.equals("Boisson Chaude")){

                drinkTypeSpinner.setSelection(4);
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modify_drink, menu);
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

    public void cancelModification(View v){
        finish();
    }

    public void saveModification(View v){
        String name = getName();
        // Vérification de la présence d'un nom.
        if (name == null) {
            BartenderApp.notifyLong(R.string.modify_name_error);
            return;
        }
        String type = getType();

        int stock = getStock();


        int stockMax = getStockMax();

        int seuil= getSeuil();
        if (seuil > stockMax) {
            BartenderApp.notifyLong(R.string.modify_seuil_error);
            return;
        }

        String descriptionEN = getDescriptionEN();
        String descriptionFR = getDescriptionFR();


        float  sprice= getSalingPrice();
        float pprice= getPurchasePrice();

             /* enregistrement de l'élément */
        drink.setStock(stock);
        drink.setSaling_price(sprice);
        drink.setName(name);
        drink.setType(type);
        drink.setDescriptionEN(descriptionEN);
        drink.setDescriptionFR(descriptionFR);
        drink.setPurchase_price(pprice);
        drink.setSeuil(seuil);
        drink.setStock_max(stockMax);
        drink.update();

        finish();

    }

    public void removeDrink(View v){
        String name= getName();
        Drink.delete(name);
        finish();

    }


    private String getName() {
        /* Récupération du nom de la boisson */
        EditText nameEditText = (EditText) findViewById(R.id.modify_drink_name_fill);
        String name = String.valueOf(nameEditText.getText());

        if (name.isEmpty()) {
            BartenderApp.notifyShort(R.string.add_error_drinkName_required);
            return null;
        }
        return name;
    }

    private String getDescriptionEN() {
        /* Récupération de la description anglais de la boisson */
        EditText descrEditText = (EditText) findViewById(R.id.modify_drink_descrEN_fill);
        String description = String.valueOf(descrEditText.getText());


        return description;
    }

    private String getDescriptionFR() {
        /* Récupération de la description anglais de la boisson */
        EditText descrEditText = (EditText) findViewById(R.id.modify_drink_descrFR_fill);
        String description = String.valueOf(descrEditText.getText());

        return description;
    }

    private String getType() {
        /* Récupération du type de boisson */
        Spinner spinnerType=(Spinner) findViewById(R.id.modify_drink_type);
        String type = spinnerType.getSelectedItem().toString();
        return type;
    }

    private int getStock(){
        /* Récupération du stock de la boisson
         */
        EditText stockEditText= (EditText) findViewById(R.id.modify_drink_stock_fill);
        int stock = Integer.parseInt(stockEditText.getText().toString());

        return stock;

    }

    private float getSalingPrice(){
        /* Récupération du prix de vente de la boisson
         */
        EditText salingPriceEditText= (EditText) findViewById(R.id.modify_drink_sprice_fill);
        float salingPrice = Float.parseFloat(salingPriceEditText.getText().toString());

        return salingPrice;

    }

    private float getPurchasePrice(){
        /* Récupération du prix de vente de la boisson
         */
        EditText purchasePriceEditText= (EditText) findViewById(R.id.modify_drink_pprice_fill);
        float purchasePrice = Float.parseFloat(purchasePriceEditText.getText().toString());

        return purchasePrice;

    }

    private int getStockMax(){
        /* Récupération du stock maximal de la boisson
         */
        EditText stockMaxEditText= (EditText) findViewById(R.id.modify_drink_stockmax_fill);
        int stockmax = Integer.parseInt(stockMaxEditText.getText().toString());

        return stockmax;

    }

    private int getSeuil(){

        /* Récupération du stock minimal de la boisson
         */
        EditText seuilEditText= (EditText) findViewById(R.id.modify_drink_seuil_fill);
        int seuil = Integer.parseInt(seuilEditText.getText().toString());

        return seuil;
    }
}
