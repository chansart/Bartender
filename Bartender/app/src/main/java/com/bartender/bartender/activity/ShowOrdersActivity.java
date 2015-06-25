package com.bartender.bartender.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bartender.bartender.ListAdaptater.DrinkListAdapter;
import com.bartender.bartender.ListAdaptater.OrderListAdapter;
import com.bartender.bartender.R;
import com.bartender.bartender.model.Order;
import com.bartender.bartender.model.Order;
import com.bartender.bartender.model.User;
import com.beardedhen.androidbootstrap.BootstrapButton;

import java.util.ArrayList;

/**
 * Created by charlotte on 29/04/15.
 */
public class ShowOrdersActivity extends Activity {

    private static ArrayList<Order> orders;
    ListView ordersListView;
    private OrderListAdapter orderListAdapter;
    private BootstrapButton cancel ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_orders);

        ordersListView = (ListView) findViewById(R.id.ordersView);

        orders = Order.getToPrepareOrders();

        orderListAdapter = new OrderListAdapter(ShowOrdersActivity.this, orders);
        orderListAdapter.setShowOrdersActivity(this);
        ordersListView.setAdapter(orderListAdapter);

        cancel = (BootstrapButton) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        refreshDB();
        super.onResume();

    }

    public void refreshDB(){
        ArrayList<Order> newOrders = Order.getToPrepareOrders();
        orders.clear();
        for(Order order : newOrders){
            orders.add(order);
        }
        this.orderListAdapter.notifyDataSetChanged();
        return;

    }


}
