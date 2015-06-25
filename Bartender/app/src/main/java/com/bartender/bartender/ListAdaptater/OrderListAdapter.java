package com.bartender.bartender.ListAdaptater;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bartender.bartender.BartenderApp;
import com.bartender.bartender.R;
import com.bartender.bartender.activity.DrinkDetailsActivity;
import com.bartender.bartender.activity.InventoryActivity;
import com.bartender.bartender.activity.LoginActivity;
import com.bartender.bartender.activity.MainActivity;
import com.bartender.bartender.activity.ShowOrdersActivity;
import com.bartender.bartender.model.Detail;
import com.bartender.bartender.model.Drink;
import com.bartender.bartender.model.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by charlotte on 30/04/15.
 */
public class OrderListAdapter extends ArrayAdapter<Order> {
    private LayoutInflater mInflater;
    private ArrayList<Order> orders;
    private ArrayList<Detail> details;
    private DetailListAdapter detailListAdapter;
    private Context mContext;
    private ShowOrdersActivity soa;

    //orders est la liste des models à afficher
    public OrderListAdapter(Context context, ArrayList<Order> orders) {

        super(context, 0, orders);
        mInflater=LayoutInflater.from(context);
        this.orders=orders;
        mContext = context;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.detail_order,parent, false);
        }

        OrderViewHolder viewHolder = (OrderViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new OrderViewHolder();
            viewHolder.client = (TextView) convertView.findViewById(R.id.orderClient);
            viewHolder.amount = (TextView) convertView.findViewById(R.id.orderAmount);
            viewHolder.table = (TextView) convertView.findViewById(R.id.orderTable);
            viewHolder.served = (ImageView) convertView.findViewById(R.id.served);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        final Order order = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.client.setText(order.getClientName());
        viewHolder.amount.setText(String.valueOf(order.getAmount()));
        viewHolder.table.setText(String.valueOf(order.getTable()));
        if(!order.get_isServed()){
            viewHolder.served.setVisibility(View.INVISIBLE);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                details = Detail.getDetails(order.getId());

                LayoutInflater li = LayoutInflater.from(mContext);
                View my_v = li.inflate(R.layout.dialog_orders, null);

                ListView lv = (ListView) my_v.findViewById(R.id.orderDetailsView);
                detailListAdapter = new DetailListAdapter(mContext, R.layout.detail_detail, details);
                lv.setAdapter(detailListAdapter);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle(mContext.getString(R.string.table) + order.getTable());
                builder.setView(my_v);
                builder.setPositiveButton(mContext.getString(R.string.is_paid_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        if (Order.order_isPaid(order.getId())) {
                            System.out.println("PAYE");
                            Toast.makeText(mContext, mContext.getString(R.string.is_paid_toaster, order.getId()), Toast.LENGTH_LONG).show();
                            orders.remove(position);
                            orders.add(position, new Order(order.getId()));
                            soa.refreshDB();
                        } else {
                            System.out.println("ERROR");
                        }
                    }
                });
                builder.setNeutralButton(mContext.getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       if (Order.order_isCancelled(order.getId())) {
                           System.out.println("ANNULE");
                           Toast.makeText(mContext, mContext.getString(R.string.is_cancelled_toaster, order.getId()), Toast.LENGTH_LONG).show();
                           orders.remove(position);
                           soa.refreshDB();
                       } else {
                           System.out.println("ERROR");
                       }
                   }
                });
                builder.setNegativeButton(mContext.getString(R.string.is_served_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Order.order_isServed(order.getId())) {
                            System.out.println("SERVI");
                            Toast.makeText(mContext, mContext.getString(R.string.is_served_toaster, order.getId()), Toast.LENGTH_LONG).show();
                            orders.remove(position);
                            orders.add(position, new Order(order.getId()));
                            soa.refreshDB();
                        } else {
                            System.out.println("ERROR");
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return convertView;
    }

    public void setShowOrdersActivity(ShowOrdersActivity soa) {
        this.soa = soa;
    }

    private class OrderViewHolder{
        public TextView client;
        public TextView amount;
        public TextView table;
        public ImageView served;
    }
}
