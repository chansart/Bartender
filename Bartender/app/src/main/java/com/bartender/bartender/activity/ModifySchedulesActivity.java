package com.bartender.bartender.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.bartender.bartender.R;
import com.bartender.bartender.model.Barman;
import com.bartender.bartender.model.User;
import com.beardedhen.androidbootstrap.BootstrapButton;

import java.util.ArrayList;

/**
 * Created by charlotte on 7/05/15.
 *
 * Permet de sélectionner le barman dont on veut modifier les horaires.
 */
public class ModifySchedulesActivity extends Activity {

    private Spinner barmanSpinner ;
    private BootstrapButton chooseButton ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_schedules);

        /**
         @note : Le titre de l'activité de lancement donné dans l'AndroidManifest.xml est repris
         comme nom du lanceur de l'application par Android. Pour ce premier écran, on va donc
         utiliser la méthode setTitle afin de définir le titre de l'activité (s'il est différent du
         titre de l'application).
         */
        setTitle("Modifier les horaires");


        /**
         * @note La liste des utilisateurs est affichées dans un Spinner, pour en savoir plus lisez
         * http://d.android.com/guide/topics/ui/controls/spinner.html
         */
        barmanSpinner = (Spinner) findViewById(R.id.barmans);

        // Obtention de la liste des utilisateurs.
        ArrayList<User> barmans = Barman.getBarmans();

        // Création d'un ArrayAdapter en utilisant la liste des utilisateurs et un layout pour le spinner existant dans Android.
        ArrayAdapter<User> adapter = new ArrayAdapter<User>(this, android.R.layout.simple_spinner_dropdown_item, barmans);
        // On lie l'adapter au spinner.
        barmanSpinner.setAdapter(adapter);

        chooseButton = (BootstrapButton) findViewById(R.id.choose_btn);

        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Barman barman = (Barman) barmanSpinner.getSelectedItem();
                Barman.setCurrentBarman(barman);

                Intent i = new Intent(getApplicationContext(), AddWorkSchedules.class);
                startActivity(i);
            }
        });

    }

}
