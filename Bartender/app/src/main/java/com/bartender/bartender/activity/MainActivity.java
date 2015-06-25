package com.bartender.bartender.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bartender.bartender.R;
import com.bartender.bartender.model.Barman;
import com.bartender.bartender.model.User;

import java.util.Locale;

/**
 * Gère l'affichage du menu principal de l'application.
 *
 * @author Damien Mercier && Charlotte Hansart
 */
public class MainActivity extends Activity {
    String LOCALE_FRANCAIS = "fr";
    String LOCALE_ENGLISH = "en";
    Locale mLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("STATUS : " + User.getConnectedUser().getStatus());

        if(User.getConnectedUser().getStatus().compareTo("client") == 0 || User.getConnectedUser().getStatus().compareTo("user") == 0) {
            setContentView(R.layout.activity_main);
        }
        else {
            Barman b = new Barman(User.getConnectedUser().getId());
            if(!b.Is_manager()){
                setContentView(R.layout.activity_main_barman);
            }
            else {
                setContentView(R.layout.activity_main_manager);
            }
        }

        // Affichage du message de bienvenue.
        TextView welcomeTxt = (TextView) findViewById(R.id.welcomeTxt);
        welcomeTxt.setText(getString(R.string.main_welcome) + " " + User.getConnectedUser().getName() );

    }

    /*
    Défini le menu (barre supérieure) de la vue
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    /*
   Défini l'action a réaliser lorsqu'on clique sur un des boutons du menu
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Configuration config;
        switch (item.getItemId()) {
            case R.id.action_english:
                mLocale = new Locale(LOCALE_ENGLISH);
                Locale.setDefault(mLocale);
                config = new Configuration();
                config.locale = mLocale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
                if(User.getConnectedUser().getStatus().compareTo("client") == 0 || User.getConnectedUser().getStatus().compareTo("user") == 0) {
                    setContentView(R.layout.activity_main);
                }
                else {
                    Barman b = new Barman(User.getConnectedUser().getId());
                    if(!b.Is_manager()){
                        setContentView(R.layout.activity_main_barman);
                    }
                    else {
                        setContentView(R.layout.activity_main_manager);
                    }
                }
                return true;

            case R.id.action_french:
                mLocale = new Locale(LOCALE_FRANCAIS);
                Locale.setDefault(mLocale);
                config = new Configuration();
                config.locale = mLocale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
                if(User.getConnectedUser().getStatus().compareTo("client") == 0 || User.getConnectedUser().getStatus().compareTo("user") == 0) {
                    setContentView(R.layout.activity_main);
                }
                else {
                    Barman b = new Barman(User.getConnectedUser().getId());
                    if(!b.Is_manager()){
                        setContentView(R.layout.activity_main_barman);
                    }
                    else {
                        setContentView(R.layout.activity_main_manager);
                    }
                }
                return true;
        }
        return false;
    }


    public void showOrders(View v) {
        Intent intent = new Intent(this, ShowOrdersActivity.class);
        startActivity(intent);
    }

    public void addBarman(View v) {
        Intent intent = new Intent(this, AddUserActivity.class);
        startActivity(intent);
    }


    /**
     * Déconnecte l'utilisateur actuellement connecté et retourne vers l'écran de connexion.
     */
    public void logout(View v) {
        User.logout();
        finish();
    }


    /**
     * Désactive le bouton de retour. Désactive le retour à l'activité précédente (donc l'écran de
     * connexion dans ce cas-ci) et affiche un message indiquant qu'il faut se déconnecter.
     */
    //@Override
    /** public void onBackPressed() {
        // On désactive le retour (car on se trouve au menu principal) en ne faisant
        // rien dans cette méthode si ce n'est afficher un message à l'utilisateur.
        MusicPlayerApp.notifyShort(R.string.main_back_button_disable);
    } */


    /**
     * Methode appelée quand on clique sur le boutton "inventaire-carte", change de vue
     * @param v
     */
    public void showInventaire(View v){
        Intent i= new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(i);

    }

    /**
     * Methode appelée quand on clique sur le boutton "Statistiques", change de vue
     * @param v
     */
    public void showStats(View v) {
        Intent i = new Intent(getApplicationContext(), StatisticsActivity.class);
        startActivity(i);

    }

    public void showInventory(View v){
        Intent i = new Intent(getApplicationContext(),InventoryActivity.class);
        startActivity(i);

    }


    public void showModifySchedules(View v){
        Intent i = new Intent(getApplicationContext(),ModifySchedulesActivity.class);
        startActivity(i);

    }



}
