package com.bartender.bartender.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.bartender.bartender.ListAdaptater.DrinkListAdapter;
import com.bartender.bartender.R;
import com.bartender.bartender.model.Drink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/*
 * Permet de gérer manuellement les stocks (augmenter et diminuer le stock des boissons)
 * Permet aussi d'accéder à la modification de boisson et à la création d'une nouvelle boisson.
 */
public class InventoryActivity extends Activity {

    private ArrayList<Drink> drinks;
    private DrinkListAdapter drinkListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        drinks = Drink.getDrinks(); //récupérer toutes les infos de toutes les boissons.

        ListView drinksToShow = (ListView) findViewById(R.id.inventory_ListView);
        drinkListAdapter = new DrinkListAdapter(this, R.layout.drink_list_item, drinks);
        drinkListAdapter.setInventoryActivity(this);
        drinksToShow.setAdapter(drinkListAdapter);
    }

    @Override
    protected void onResume() {
        refreshDB();
        super.onResume();

    }

    public void sortByName(View v) {

        Collections.sort(drinks, new Comparator<Drink>() {
            public int compare(Drink d1, Drink d2) {
                return d1.getName().compareToIgnoreCase(d2.getName());
            }

        });
        this.drinkListAdapter.notifyDataSetChanged();
    }


    public void sortByType(View v) {
        Collections.sort(drinks, new Comparator<Drink>() {
            public int compare(Drink d1, Drink d2) {
                return d1.getType().compareToIgnoreCase(d2.getType());
            }
        });
        this.drinkListAdapter.notifyDataSetChanged();
    }

    public void sortByStock(View v) {

        Collections.sort(drinks, new Comparator<Drink>() {
            public int compare(Drink d1, Drink d2) {
                return compareInteger(d1.getStock(), d2.getStock());
            }

        });
        this.drinkListAdapter.notifyDataSetChanged();
    }

    public void sortByStockMax(View v) {

        Collections.sort(drinks, new Comparator<Drink>() {
            public int compare(Drink d1, Drink d2) {
                return compareInteger(d1.getStock_max(), d2.getStock_max());
            }

        });
        this.drinkListAdapter.notifyDataSetChanged();
    }

    private int compareInteger(int d1, int d2){
        return d1 < d2 ? -1 : (d1 == d2 ? 0 : 1);
    }

    public void sortBySeuil(View v) {
        Collections.sort(drinks, new Comparator<Drink>() {
            public int compare(Drink d1, Drink d2) {
                return compareInteger(d1.getSeuil(), d2.getSeuil());
            }
        });
        this.drinkListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
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

    public void refreshDB(){
        ArrayList<Drink> newDrinks = Drink.getDrinks();
        drinks.clear();
        for(Drink drink : newDrinks){
            drinks.add(drink);
        }
        this.drinkListAdapter.notifyDataSetChanged();
        return;

    }

    public void showAddDrink(View v){
        Intent i = new Intent(getApplicationContext(), AddDrinkActivity.class);
        startActivity(i);

    }
}
