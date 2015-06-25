package com.bartender.bartender.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bartender.bartender.ListAdaptater.DayListAdapter;
import com.bartender.bartender.R;
import com.bartender.bartender.model.Timetable;
import com.bartender.bartender.model.User;
import com.beardedhen.androidbootstrap.BootstrapButton;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by charlotte on 6/05/15.
 * Permet d'ajouter des plages horaires de travail pour les serveurs et les managers via trois boites de dialogues successives :
 * 1) choix du jour
 * 2) choix de l'heure de début de la plage horaire
 * 3) choix de l'heure de la fin de la plage horaire
 */
public class AddWorkSchedules extends Activity {

    String LOCALE_FRANCAIS = "fr";
    String LOCALE_ENGLISH = "en";
    Locale mLocale;

    ListView daysListView ;
    private ArrayList<Timetable> schedules ;
    private DayListAdapter dayListAdapter;
    private Calendar cal;
    private int hour;
    private int min;

    private int day = 0 ;
    private String begin = "" ;
    private String end = "" ;

    final CharSequence dayList[] = { "lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi", "dimanche" };
    final CharSequence dayList_en[] = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    CharSequence dayList_local[] ;
    boolean bl[] = new boolean[dayList.length];
    ArrayList<Integer> selList=new ArrayList();

    BootstrapButton add_schedule ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_schedules);

        // Pour récupérer la langue (configuration)
        mLocale=Locale.getDefault();

        daysListView = (ListView) findViewById(R.id.daysView);
        cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR_OF_DAY);
        min = cal.get(Calendar.MINUTE);

        //Création de la ArrayList qui nous permettra de remplire la listView
        schedules = Timetable.getSchedules(User.getCurrentBarman().getId());

        //Création d'un SimpleAdapter qui se chargera de mettre les items présent dans notre list (listItem) dans la vue affichageitem
        dayListAdapter = new DayListAdapter(this, schedules);
        //dayListAdapter.setAddWorkSchedules(this);

        //On attribut à notre listView l'adapter que l'on vient de créer
        daysListView.setAdapter(dayListAdapter);

        add_schedule = (BootstrapButton) findViewById(R.id.add_schedule);


        final TimePicker timePicker = new TimePicker(getApplicationContext());
        timePicker.setIs24HourView(false);

        final AlertDialog.Builder builder3 = new AlertDialog.Builder(AddWorkSchedules.this);

        builder3.setTitle(getString(R.string.e_hour));
        builder3.setView(timePicker);
        builder3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                int h = timePicker.getCurrentHour();
                int m = timePicker.getCurrentMinute();
                String temp ;
                if(m < 10) {
                    temp = h + ":0" + m + ":00";
                }
                else {
                    temp = h + ":" + m + ":00";
                }
                end = temp ;
                Timetable t = Timetable.create(day, begin, end) ;
                if(t != null){
                    refreshDB();
                }
            }
        });
        builder3.setNegativeButton(getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                dialog.cancel();
            }
        });
        builder3.create();

        final TimePicker timePicker2 = new TimePicker(getApplicationContext());
        timePicker2.setIs24HourView(false);


        final AlertDialog.Builder builder2 = new AlertDialog.Builder(AddWorkSchedules.this);
        builder2.setTitle(getString(R.string.b_hour));
        builder2.setView(timePicker2);
        builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                int h1 = timePicker2.getCurrentHour();
                int m1 = timePicker2.getCurrentMinute();
                String temp ;
                if(m1 < 10) {
                    temp = h1 + ":0" + m1 + ":00";
                }
                else {
                    temp = h1 + ":" + m1 + ":00";
                }
                begin = temp ;
                builder3.show();
            }
        });
        builder2.setNegativeButton(getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                dialog.cancel();
            }
        });
        builder2.create();

        TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            // the callback received when the user "sets" the TimePickerDialog in the dialog
            public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                Toast.makeText(getApplicationContext(), "DONE.", Toast.LENGTH_LONG).show();
            }
        };

        TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                int hour;
                String am_pm;
                if (hourOfDay > 12) {
                    hour = hourOfDay - 12;
                    am_pm = "PM";
                } else {
                    hour = hourOfDay;
                    am_pm = "AM";
                }
                System.out.println(hour + " : " + minute + " " + am_pm);
            }
        };


        final AlertDialog.Builder builder = new AlertDialog.Builder(AddWorkSchedules.this);

        builder.setTitle(getString(R.string.which_day));
        if(mLocale.getLanguage().compareTo(LOCALE_ENGLISH)==0){
            dayList_local = dayList_en ;
        } else {
            dayList_local = dayList ;
        }
        builder.setMultiChoiceItems(dayList_local, bl, new DialogInterface.OnMultiChoiceClickListener() {

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
                if(selList.size() != 1) {
                    Toast.makeText(getApplicationContext(), getString(R.string.select_day_error), Toast.LENGTH_LONG).show();
                } else {
                    day = selList.get(0) + 1;
                    builder2.show();
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
                Toast.makeText(getApplicationContext(), getString(R.string.schedule_cancelled_toaster), Toast.LENGTH_LONG).show();
                finish();
            }
        });
        builder.create();






        add_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });

    }

    @Override
    protected void onResume() {
        refreshDB();
        super.onResume();

    }


    public void refreshDB(){
        ArrayList<Timetable> newT = Timetable.getSchedules(User.getCurrentBarman().getId());
        schedules.clear();
        for(Timetable timetable : newT){
            schedules.add(timetable);
        }
        this.dayListAdapter.notifyDataSetChanged();
        return;

    }
}
