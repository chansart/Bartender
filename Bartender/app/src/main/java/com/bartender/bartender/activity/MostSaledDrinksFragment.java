package com.bartender.bartender.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bartender.bartender.R;
import com.bartender.bartender.database.MySQLiteHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Zélie on 02-05-15.
 *
 * Permet d'afficher un graphique reprenant les  boissons les plus vendues.
 */
public class MostSaledDrinksFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_statistics_barchart, container, false);

         BarChart chartTest = (BarChart) rootView.findViewById(R.id.firstChart);
        chartTest.setLogEnabled(true);
        chartTest.setNoDataTextDescription(getString(R.string.no_data));
        chartTest.animateXY(2000,2000);

        ArrayList<String[]> mostSaleDrinks = getMostSaleDrinks();

        ArrayList<BarEntry> valsDrinksQuant = new ArrayList<BarEntry>(); //creation d'un arrayList d'entrées pour le tableau
        ArrayList<String> xVals = new ArrayList<String>(); // creation d'une arrayList pour les valeurs des abscisses

         Iterator<String[]> it = mostSaleDrinks.iterator();
        for (int i=0; it.hasNext() && i<5; i++) {
            String[] s = it.next();
            BarEntry element = new BarEntry(Integer.parseInt(s[1]),i);
            valsDrinksQuant.add(element);
            xVals.add(s[0]);

        }


        BarDataSet setBestDrinks = new BarDataSet(valsDrinksQuant, getString(R.string.top_selling_drinks));
        setBestDrinks.setColors(new int[] { R.color.green1, R.color.green2, R.color.green3, R.color.green4 }, rootView.getContext());

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(setBestDrinks);

        BarData data = new BarData(xVals, dataSets);
        chartTest.setData(data);

        return rootView;
    }



    private ArrayList<String[]> getMostSaleDrinks(){
        final String MY_QUERY = "SELECT d_name, SUM(det_quant) as quantite FROM details JOIN Boisson WHERE (Boisson.d_id=details.d_id) GROUP BY d_name ORDER BY quantite DESC";
        // Initialisation de la liste des orders.
        ArrayList<String[]> drinks = new ArrayList<String[]>();

        // Récupération du SQLiteHelper pour récupérer la base de données.
        SQLiteDatabase db = MySQLiteHelper.get().getReadableDatabase();

        Cursor c = db.rawQuery(MY_QUERY, null);

        c.moveToFirst();
        while (!c.isAfterLast()) {

            String drinkName = c.getString(0);
            int drinkQuantity = c.getInt(1);
            String[] tab = {drinkName, Integer.toString(drinkQuantity)};
            drinks.add(tab);
            c.moveToNext();
        }

        // Fermeture du curseur et de la base de données.
        c.close();
        db.close();

        return drinks;


    }
}
