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
 * Fragment permettant de faire un graphique affichant les 5 meilleurs clients et combien ils ont dépensé
 */
public class BestClientsFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_statistics_barchart, container, false);

        BarChart chartTest = (BarChart) rootView.findViewById(R.id.firstChart);
        chartTest.setLogEnabled(true);
        chartTest.setNoDataTextDescription(getString(R.string.no_data));
        chartTest.animateXY(2000,2000);

        ArrayList<String[]> bestClient = getBestClient();

        ArrayList<BarEntry> valsDrinksQuant = new ArrayList<BarEntry>(); //creation d'un arrayList d'entrées pour le tableau
        ArrayList<String> xVals = new ArrayList<String>(); // creation d'une arrayList pour les valeurs des abscisses

        Iterator<String[]> it = bestClient.iterator();
        for (int i=0; it.hasNext() && i<5; i++) {
            String[] s = it.next();
            if(s[0].compareTo(getString(R.string.anonymous))==0){
               i--; // pour que l'index du graphique soit toujours bon
               //ne fait rien si on se trouve en présence de l'utilisateur anonyme
            }
            else {
                BarEntry element = new BarEntry(Integer.parseInt(s[1]), i);
                valsDrinksQuant.add(element);
                xVals.add(s[0]);
            }

        }


        BarDataSet setBestDrinks = new BarDataSet(valsDrinksQuant, getString(R.string.regular_customers));
        setBestDrinks.setColors(new int[] { R.color.red1, R.color.red2, R.color.red3, R.color.red4 }, rootView.getContext());

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(setBestDrinks);

        BarData data = new BarData(xVals, dataSets);
        chartTest.setData(data);

        return rootView;
    }



    private ArrayList<String[]> getBestClient(){
        final String MY_QUERY = "SELECT u_name, SUM(o_amountorder) as bill from users JOIN orders WHERE (orders.u_id=users.u_id) GROUP BY u_name ORDER BY bill DESC";
        // Initialisation de la liste des orders.
        ArrayList<String[]> clients = new ArrayList<String[]>();

        // Récupération du SQLiteHelper pour récupérer la base de données.
        SQLiteDatabase db = MySQLiteHelper.get().getReadableDatabase();

        Cursor c = db.rawQuery(MY_QUERY, null);

        c.moveToFirst();
        while (!c.isAfterLast()) {

            String clientName = c.getString(0);
            int amountSpend = c.getInt(1);
            String[] tab = {clientName, Integer.toString(amountSpend)};
            clients.add(tab);
            c.moveToNext();
        }

        // Fermeture du curseur et de la base de données.
        c.close();
        db.close();

        return clients;


    }

}
