package com.bartender.bartender.ListAdaptater;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bartender.bartender.R;
import com.bartender.bartender.model.Detail;
import com.bartender.bartender.model.Drink;
import com.bartender.bartender.model.Order;

import java.util.ArrayList;

/**
 * Created by charlotte on 30/04/15.
 */
public class DetailListAdapter extends ArrayAdapter<Detail> {
    private LayoutInflater mInflater;
    private ArrayList<Detail> details;
    private DetailListAdapter detailListAdapter;
    private Context mContext;

    //details est la liste des models à afficher
    public DetailListAdapter(Context context, ArrayList<Detail> details) {

        super(context, 0, details);
        mInflater=LayoutInflater.from(context);
        this.details =details;
        mContext = context;

    }

    public DetailListAdapter(Context context, int resource, ArrayList<Detail> details) {
        super(context, resource, details);
        mInflater=LayoutInflater.from(context);
        this.details=details;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.detail_detail,parent, false);
        }

        OrderViewHolder viewHolder = (OrderViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new OrderViewHolder();
            viewHolder.quantity = (TextView) convertView.findViewById(R.id.detailQuantity);
            viewHolder.drink = (TextView) convertView.findViewById(R.id.detailDrink);
            viewHolder.amount = (TextView) convertView.findViewById(R.id.detailAmount);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        Detail detail = getItem(position);
        Drink d = new Drink(detail.getDrinkId());
        float amount = d.getSaling_price() * detail.getQuantity() ;

        //il ne reste plus qu'à remplir notre vue
        viewHolder.quantity.setText(String.valueOf(detail.getQuantity()));
        viewHolder.drink.setText(String.valueOf(d.getName()));
        viewHolder.amount.setText(String.valueOf(amount));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("NOTHING TO DO...");
            }
        });

        return convertView;
    }

    private class OrderViewHolder{
        public TextView quantity;
        public TextView drink;
        public TextView amount;
    }
}
