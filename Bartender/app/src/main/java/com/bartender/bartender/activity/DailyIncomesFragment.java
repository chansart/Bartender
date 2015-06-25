package com.bartender.bartender.activity;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bartender.bartender.R;
import com.bartender.bartender.database.MySQLiteHelper;
import com.bartender.bartender.model.DrinkIncome;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Classe pour calculer les revenus des 5 derniers jours
 */
public class DailyIncomesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); //défini un nouveau format de date en string
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_statistics_linechart, container, false);

        LineChart chart = (LineChart) rootView.findViewById(R.id.firstChart);
        chart.setLogEnabled(true);
        chart.setNoDataTextDescription(getString(R.string.no_data));

        ArrayList<DrinkIncome> incomesByDrink = getIncomes();

        ArrayList<Entry> Income1 = new ArrayList<Entry>(); //creation d'un arrayList d'entrées pour le tableau
        ArrayList<Entry[]> drink2 = new ArrayList<Entry[]>();
        ArrayList<Entry> drink3 = new ArrayList<Entry>();
        ArrayList<Entry> drink4 = new ArrayList<Entry>();
        ArrayList<Entry> drink5 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>(); // creation d'une arrayList pour les valeurs des abscisses

        Iterator<DrinkIncome> it = incomesByDrink.iterator();
        for (int i=0; it.hasNext() && i<5; i++) {
            DrinkIncome s = it.next();
             Entry element = new Entry(s.getIncome(), i);
             Income1.add(element);
             //xVals.add(df.format(s.getDateOrder()));
               xVals.add("J-"+i);

        }


        LineDataSet setBestDrinks = new LineDataSet(Income1, getString(R.string.daily_incomes));
        setBestDrinks.setDrawFilled(true);
        setBestDrinks.setColors(new int[] { R.color.blue1, R.color.blue2, R.color.blue3, R.color.blue1 }, rootView.getContext());

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setBestDrinks);

        LineData data = new LineData(xVals, dataSets);
        chart.setData(data);

        return rootView;
    }



    private ArrayList<DrinkIncome> getIncomes(){
        final String MY_QUERY = "SELECT SUM((d_sprice-d_pprice)*det_quant) as benefice, o_date from Boisson, details, orders WHERE (orders.o_id=details.o_id) AND (details.d_id=Boisson.d_id) GROUP BY o_date ORDER BY o_date DESC";
        // Initialisation de la liste des orders.
        ArrayList<DrinkIncome> drinksIncome = new ArrayList<DrinkIncome>();

        // Récupération du SQLiteHelper pour récupérer la base de données.
        SQLiteDatabase db = MySQLiteHelper.get().getReadableDatabase();

        Cursor c = db.rawQuery(MY_QUERY, null);

        c.moveToFirst();
        while (!c.isAfterLast()) {

            String drinkName = getString(R.string.no_drink);
            float drinkBenefits = c.getFloat(0);
            Date dateOrder= new Date(c.getLong(1));
            DrinkIncome element = new DrinkIncome(drinkName, drinkBenefits, dateOrder);
            drinksIncome.add(element);
            c.moveToNext();
        }

        // Fermeture du curseur et de la base de données.
        c.close();
        db.close();

        return drinksIncome;


    }

}
