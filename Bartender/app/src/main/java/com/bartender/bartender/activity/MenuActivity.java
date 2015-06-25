package com.bartender.bartender.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.app.AlertDialog;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.bartender.bartender.ListAdaptater.DrinkListAdapter;
import com.bartender.bartender.R;
import com.bartender.bartender.model.Drink;
import com.bartender.bartender.model.Order;
import com.beardedhen.androidbootstrap.BootstrapButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/*
 * Permet d'interagir avec la carte, en rajoutant des boissons au panier et validant la commande.
 * Une fois validée, ouvre une fenêtre permettant de sélectionner la table qui commande.
 */

public class MenuActivity extends Activity {

    final Context context = this;
    private ArrayList<Drink> drinks;
    private DrinkListAdapter drinkListAdapter;
    private BootstrapButton validateButton;
    private BootstrapButton cancelButton;
    final CharSequence tableList[] = { "1", "2", "3", "4", "5", "6", "7" };
    boolean bl[] = new boolean[tableList.length];
    ArrayList<Integer> selList=new ArrayList();
    RelativeLayout rl;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_menu);

        //la ligne en commentaire est celle à utiliser pour ajouter des boutons + (ajout de boissons à une commande)
        setContentView(R.layout.activity_menu_for_ordering);

        //recupere toutes les entrees de la base de donnes
        drinks = Drink.getDrinks();

        //ListView drinksToShow = (ListView) findViewById(R.id.drinkListView);
        //drinkListAdapter = new DrinkListAdapter(this, R.layout.drink_list_item, drinks);

        //la ligne en commentaire est celle à utiliser pour ajouter des boutons + (ajout de boissons à une commande)
        ListView drinksToShow = (ListView) findViewById(R.id.drinkListView);
        drinkListAdapter = new DrinkListAdapter(this, R.layout.drink_list_adding, drinks);
        
        drinksToShow.setAdapter(drinkListAdapter);

        rl = (RelativeLayout) findViewById(R.id.myRL);
        validateButton = (BootstrapButton) findViewById(R.id.validate_order);

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle(getString(R.string.which_table));
                builder.setMultiChoiceItems(tableList, bl, new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1, boolean arg2) {

                        if (arg2) {
                            // If user select a item then add it in selected items
                            selList.add(arg1);
                        } else if (selList.contains(arg1)) {
                            // if the item is already selected then remove it
                            selList.remove(Integer.valueOf(arg1));
                        }
                    }
                });
                builder.setPositiveButton(getString(R.string.validate_order_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        String msg="";
                        if(selList.size() != 1) {
                            Toast.makeText(getApplicationContext(), getString(R.string.select_table_error), Toast.LENGTH_LONG).show();
                        } else {
                            int table = selList.get(0) + 1;
                            String[] results = Order.addTableAndValidate(table);
                            if(Integer.parseInt(results[3]) == 0) {
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.your_table) + results[0] + "\n" +
                                                getString(R.string.your_barman) + results[1] + "\n" +
                                                getString(R.string.your_bill) + results[2],
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "20% de PROMO pour votre 20e commande ! \n" +
                                                getString(R.string.your_table) + results[0] + "\n" +
                                                getString(R.string.your_barman) + results[1] + "\n" +
                                                getString(R.string.your_bill) + results[2],
                                        Toast.LENGTH_LONG).show();
                            }
                            finish();
                            if(results == null){
                                Toast.makeText(getApplicationContext(), R.string.empty_order, Toast.LENGTH_SHORT).show();


                            }
                            else {
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.your_table) + results[0] + "\n" +
                                                getString(R.string.your_barman) + results[1] + "\n" +
                                                getString(R.string.your_bill) + results[2],
                                        Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    }
                });
                builder.setNeutralButton(getString(R.string.return_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        int oId = Order.getCurrentOrder().getId();
                        if(Order.order_isCancelled(oId)) {
                            Toast.makeText(getApplicationContext(), getString(R.string.is_cancelled_toaster, oId), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        //OPTION DE RECHERCHE
       ((SearchView) findViewById(R.id.menu_searchView)).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
           @Override
           public boolean onQueryTextSubmit(String query) {
               ArrayList<Drink> searchResultDrinks;
               if(!query.isEmpty()) {
                   searchResultDrinks = Drink.searchDrinksByName(query);
                   if (searchResultDrinks.isEmpty()) {
                       Toast.makeText(getApplicationContext(), getString(R.string.no_results_search), Toast.LENGTH_SHORT).show();
                       searchResultDrinks = Drink.getDrinks();
                   }
                   drinks.clear();
                   for(Drink drink : searchResultDrinks){
                       drinks.add(drink);
                   }
                   drinkListAdapter.notifyDataSetChanged();
                   return true;
               }
               else{
                   searchResultDrinks = Drink.getDrinks();
                   drinks.clear();
                   for(Drink drink : searchResultDrinks){
                       drinks.add(drink);
                   }

                   drinkListAdapter.notifyDataSetChanged();
                   return true;
               }

           }

           @Override
           public boolean onQueryTextChange(String newText) {
               return false;
           }
       });

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

    public void sortBySalingPrice(View v) {
        Collections.sort(drinks, new Comparator<Drink>() {
            public int compare(Drink d1, Drink d2) {
                return Float.compare(d1.getSaling_price(), d2.getSaling_price());
            }
        });
        this.drinkListAdapter.notifyDataSetChanged();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
