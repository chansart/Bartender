package com.bartender.bartender.ListAdaptater;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bartender.bartender.R;
import com.bartender.bartender.activity.DrinkDetailsActivity;
import com.bartender.bartender.activity.InventoryActivity;
import com.bartender.bartender.activity.MainActivity;
import com.bartender.bartender.activity.ModifyDrinkActivity;
import com.bartender.bartender.model.Detail;
import com.bartender.bartender.model.Drink;
import com.bartender.bartender.model.Order;
import com.bartender.bartender.model.User;

import java.util.ArrayList;

/**
 * Created by Zélie on 28-04-15.
 */
public class DrinkListAdapter extends ArrayAdapter<Drink> {
    private LayoutInflater mInflater;
    private ArrayList<Drink> drinks;
    private Context mContext;
    private int resource;
    private InventoryActivity inventoryActivity;

    public DrinkListAdapter(Context context, int resource, ArrayList<Drink> drink) {
        super(context, resource, drink);
        mInflater=LayoutInflater.from(context);
        this.drinks=drink;
        this.resource = resource;
        mContext = context;
    }
    
    public View getView(final int position, View convertView, ViewGroup parent){

        if(convertView==null){

            convertView=mInflater.inflate(resource, null);
            //la ligne en commentaire est celle à utiliser pour ajouter des boutons + (ajout de boissons à une commande)
            //convertView=mInflater.inflate(R.layout.drink_list_adding, null);
        }

        DrinkViewHolder viewHolder = (DrinkViewHolder) convertView.getTag();
        if(viewHolder == null) {
            viewHolder = new DrinkViewHolder();
            viewHolder.drinktype = (TextView) convertView.findViewById(R.id.drinkType);
            viewHolder.drinkname = (TextView) convertView.findViewById(R.id.drinkName);
            viewHolder.drinkprice = (TextView) convertView.findViewById(R.id.drinkPrice);
            viewHolder.drinkStock = (TextView) convertView.findViewById(R.id.drinkStock);
            viewHolder.drinkSeuil = (TextView) convertView.findViewById(R.id.drinkSeuil);
            viewHolder.drinkStockMax = (TextView) convertView.findViewById(R.id.drinkStockMax);
            viewHolder.addDrink = (ImageView) convertView.findViewById(R.id.addDrink);
            viewHolder.refresh_stocks = (ImageView) convertView.findViewById(R.id.refresh_stocks);
            convertView.setTag(viewHolder);
        }

        final Drink drink = drinks.get(position);

        viewHolder.drinkname.setText(drink.getName());
        viewHolder.drinktype.setText(drink.getType());

        if(resource == R.layout.drink_list_adding) {

            viewHolder.drinkprice.setText(Float.toString(drink.getSaling_price()) + "€");
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, DrinkDetailsActivity.class);
                    i.putExtra("Drink", drink);
                    mContext.startActivity(i);
                }
            });

        } else {
            viewHolder.drinkStock.setText(String.valueOf(drink.getStock()));
            if(drink.getStock()<drink.getSeuil()){
                viewHolder.drinkStock.setTextColor(mContext.getResources().getColor(R.color.red1)); // couleur rouge si en dessous du seuil
                viewHolder.drinkStock.setTypeface(null, Typeface.BOLD);
            }
            else{
                viewHolder.drinkStock.setTextColor(mContext.getResources().getColor(R.color.black)); // couleur rouge si en dessous du seuil
                viewHolder.drinkStock.setTypeface(null, Typeface.NORMAL);

            }
            viewHolder.drinkSeuil.setText(String.valueOf(drink.getSeuil()));
            viewHolder.drinkStockMax.setText(String.valueOf(drink.getStock_max()));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, ModifyDrinkActivity.class);
                    i.putExtra("Drink", drink);
                    mContext.startActivity(i);
                }
            });
        }



        if(resource == R.layout.drink_list_adding) {

            viewHolder.addDrink.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    RelativeLayout linearLayout = new RelativeLayout(mContext);
                    final NumberPicker aNumberPicker = new NumberPicker(mContext);
                    int max = drink.getStock();

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    if(max > 0) {
                        aNumberPicker.setMaxValue(max);
                        aNumberPicker.setMinValue(0);

                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
                        RelativeLayout.LayoutParams numPickerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        numPickerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

                        linearLayout.setLayoutParams(params);
                        linearLayout.addView(aNumberPicker, numPickerParams);

                        builder.setTitle(mContext.getString(R.string.how_many_drinks, drink.getName()));
                        builder.setView(linearLayout);
                        builder.setPositiveButton(mContext.getString(R.string.add_btn), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (Order.getCurrentOrder() == null) {
                                    Order.create(User.getConnectedUser());
                                }
                                int nb_drinks = aNumberPicker.getValue();
                                if(nb_drinks > 0) {
                                    if (Detail.create(drink.getId(), nb_drinks, Order.getCurrentOrder().getId())) {
                                        int new_stock = drink.getStock() - aNumberPicker.getValue();

                                        Drink.updateStock(drink.getId(), new_stock);
                                        Drink d = new Drink(drink.getId());

                                        remove(drink);
                                        insert(d, position);

                                        notifyDataSetChanged();
                                        System.out.println("Ajout de " + aNumberPicker.getValue() + " " + drink.getName());
                                    } else {
                                        Toast.makeText(mContext, mContext.getString(R.string.error_add_choice), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(mContext, mContext.getString(R.string.error_add_zero_choice), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        builder.setNegativeButton(mContext.getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

                    } else {
                        builder.setTitle(mContext.getString(R.string.sorry_no_drinks, drink.getName()));
                    }
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }
            });

        } else {

            viewHolder.refresh_stocks.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    RelativeLayout linearLayout = new RelativeLayout(mContext);
                    final NumberPicker aNumberPicker = new NumberPicker(mContext);
                    int max = drink.getStock_max() - drink.getStock() ;
                    aNumberPicker.setMaxValue(max);
                    aNumberPicker.setMinValue(0);

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
                    RelativeLayout.LayoutParams numPickerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    numPickerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

                    linearLayout.setLayoutParams(params);
                    linearLayout.addView(aNumberPicker, numPickerParams);

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    builder.setTitle(mContext.getString(R.string.refresh_stocks_dialog));
                    builder.setView(linearLayout);
                    builder.setPositiveButton(mContext.getString(R.string.add_btn), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            drink.setStock(drink.getStock()+aNumberPicker.getValue());
                            drink.update();
                            inventoryActivity.refreshDB();
                        }
                    });
                    builder.setNeutralButton(mContext.getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.out.println("Annulation");
                        }
                    });
                    builder.setNegativeButton(mContext.getString(R.string.remove_btn), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            int newStock = drink.getStock() - aNumberPicker.getValue();
                            if (newStock < 0) {
                                drink.setStock(0);
                            } else {
                                drink.setStock(newStock);

                            }
                            drink.update();
                            inventoryActivity.refreshDB();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });

        }


        return convertView;

    }

    public void setInventoryActivity(InventoryActivity inventoryActivity) {
        this.inventoryActivity = inventoryActivity;
    }

    private class DrinkViewHolder {
        public TextView drinktype;
        public TextView drinkname;
        public TextView drinkprice;
        public TextView drinkStock;
        public TextView drinkStockMax;
        public TextView drinkSeuil;
        public ImageView refresh_stocks;
        public ImageView addDrink;
    }
}