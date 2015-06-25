package com.bartender.bartender.ListAdaptater;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bartender.bartender.BartenderApp;
import com.bartender.bartender.R;
import com.bartender.bartender.activity.AddWorkSchedules;
import com.bartender.bartender.activity.InventoryActivity;
import com.bartender.bartender.activity.ShowOrdersActivity;
import com.bartender.bartender.model.Detail;
import com.bartender.bartender.model.Order;
import com.bartender.bartender.model.Timetable;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by charlotte on 6/05/15.
 */
public class DayListAdapter extends ArrayAdapter<Timetable> {

    private LayoutInflater mInflater;
    private ArrayList<Timetable> schedules ;
    private AddWorkSchedules awsActivity;
    private Context mContext;

    private Calendar cal = Calendar.getInstance();
    private int hour = cal.get(Calendar.HOUR_OF_DAY);
    private int min = cal.get(Calendar.MINUTE);

    static final int TIME_DIALOG_ID=1;
    private String begin = "" ;
    private String end = "" ;

    

    //orders est la liste des models à afficher
    public DayListAdapter(Context context, ArrayList<Timetable> schedules) {

        super(context, 0, schedules);
        mInflater=LayoutInflater.from(context);
        this.schedules=schedules;
        mContext = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.day_item, parent, false);
        }

        DayViewHolder viewHolder = (DayViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new DayViewHolder();
            viewHolder.day = (TextView) convertView.findViewById(R.id.day);
            viewHolder.b_hour = (TextView) convertView.findViewById(R.id.b_hour);
            viewHolder.e_hour = (TextView) convertView.findViewById(R.id.e_hour);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        final int p = position;
        final Timetable schedule = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.day.setText(schedule.getDay());

            viewHolder.b_hour.setText(schedule.getBegin());

            viewHolder.e_hour.setText(schedule.getEnd());

        return convertView ;
    }

    public void setAddWorkSchedules(AddWorkSchedules awsActivity) {
        this.awsActivity = awsActivity;
    }

    private class DayViewHolder{
        public TextView day;
        public TextView b_hour;
        public TextView e_hour;
        public Button no_begin;
        public Button no_end;
    }

}
