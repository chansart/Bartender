package com.bartender.bartender.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.bartender.bartender.R;
import com.bartender.bartender.BartenderApp;
import com.bartender.bartender.model.Barman;
import com.bartender.bartender.model.User;

/**
 * Created by charlotte on 26/04/15.
 * Permet d'ajouter un nouvel utilisateur.
 */
public class AddUserActivity extends Activity {

    private boolean user_is_manager = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(User.getConnectedUser() == null) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_adduser);
            findViewById(R.id.check_barman).setVisibility(View.INVISIBLE);
            findViewById(R.id.check_manager).setVisibility(View.INVISIBLE);
        }
        else if(User.getConnectedUser().getIs_manager()) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_adduser);
                user_is_manager = true ;
                System.out.println("CONNECTED AS MANAGER");
        }
    }


    public void quit(View v){
        finish();
    }


    /**
     * Enregistre les données saisies par l'utilisateur dans un nouvel élément.
     *
     * Cette méthode va s'occuper de récupérer les différentes valeurs remplie par l'utilisateur.
     * Ensuite il va créer un nouvel utilisateur avec ces données.
     *
     * @pre Le champ nom ne doit pas être vide.
     * @post Soit un nouvel utilisateur a été créé dans la base de données et l'activité
     * est fermée soit un message d'erreur est affiché à l'utilisateur.
     */
    public void save(View v) {

        String name = getName();
        // Vérification de la présence d'un nom.
        if (name == null) {
            return;
        }

        String password = getPassword();

        if(password==null){
            return;
        }

        String status = "client" ;
        int is_manager = 0 ;
        if(user_is_manager) {
            status = "barman" ;
            is_manager = getStatus() ;

            if(is_manager == -1){
                return ;
            }
        }

        /* Création de l'élément */
        if (User.create(name, password, status, is_manager)) {
            BartenderApp.notifyLong(R.string.add_success_msg);
            // finish(); // On termine l'activité d'ajout afin de retourner au menu principal.
            if(!user_is_manager) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(this, AddWorkSchedules.class);
                startActivity(intent);
            }
        } else {
            BartenderApp.notifyLong(R.string.add_error_on_create);
        }

    }

    private String getName() {
        /* Récupération du client  */
        EditText nameEditText = (EditText) findViewById(R.id.add_pseudo);
        String name = String.valueOf(nameEditText.getText());

        if (name.isEmpty()) {
            BartenderApp.notifyShort(R.string.add_error_pseudo_required);
            return null;
        }

        if(User.alreadyUsedPseudo(name)){
            BartenderApp.notifyShort(R.string.error_pseudo_already_used);
            return null;
        }
        return name;
    }

    private String getPassword() {
        /* Récupération du mot de passe */
        EditText passwordEditText = (EditText) findViewById(R.id.add_password);
        String password = String.valueOf(passwordEditText.getText());

        EditText verifPasswordEditText = (EditText) findViewById(R.id.verif_password);
        String passwordVerif = String.valueOf(verifPasswordEditText.getText());

        if(password.isEmpty()){
            BartenderApp.notifyShort(R.string.add_error_password_required);
            return null;
        }
        else if(passwordVerif.isEmpty()){
            BartenderApp.notifyShort(R.string.add_error_password_verif_required);
            return null;
        }
        else if(password.compareTo(passwordVerif) != 0){
            BartenderApp.notifyShort(R.string.add_error_same_passwords_required);
            return null;
        }

        return password;
    }

    private int getStatus() {
            /* Récupération du statut */
            CheckBox barman_checkbox = (CheckBox) findViewById(R.id.check_barman);
            CheckBox manager_checkbox = (CheckBox) findViewById(R.id.check_manager);

            if(barman_checkbox.isChecked() && manager_checkbox.isChecked()){
                BartenderApp.notifyLong(R.string.only_one_checkbox);
                return -1 ;
            }
            else if((!barman_checkbox.isChecked() && !manager_checkbox.isChecked())){
                BartenderApp.notifyLong(R.string.only_one_checkbox);
                return -1 ;
            }


            if(manager_checkbox.isChecked()){
                return 1 ;
            }
            else {
                return 0 ;
            }

    }

}
