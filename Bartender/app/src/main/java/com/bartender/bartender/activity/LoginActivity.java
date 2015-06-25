package com.bartender.bartender.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import com.bartender.bartender.BartenderApp;
import com.bartender.bartender.R;
import com.bartender.bartender.model.User;

/**
 * Gère l'écran de connexion des utilisateurs à l'application.
 *
 * @author Damien Mercier && Charlotte Hansart
 */
public class LoginActivity extends Activity implements TextView.OnEditorActionListener {

    private Spinner userSpinner;

    String LOCALE_FRANCAIS = "fr";
    String LOCALE_ENGLISH = "en";
    Locale mLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /**
         @note : Le titre de l'activité de lancement donné dans l'AndroidManifest.xml est repris
         comme nom du lanceur de l'application par Android. Pour ce premier écran, on va donc
         utiliser la méthode setTitle afin de définir le titre de l'activité (s'il est différent du
         titre de l'application).
         */
        setTitle(R.string.login_activity_title);
        /**
         * @note La liste des utilisateurs est affichées dans un Spinner, pour en savoir plus lisez
         * http://d.android.com/guide/topics/ui/controls/spinner.html
         */
        userSpinner = (Spinner) findViewById(R.id.login_username);

        // Obtention de la liste des utilisateurs.
        ArrayList<User> users = User.getClients();
        // Création d'un ArrayAdapter en utilisant la liste des utilisateurs et un layout pour le spinner existant dans Android.
        ArrayAdapter<User> adapter = new ArrayAdapter<User>(this, android.R.layout.simple_spinner_dropdown_item, users);
        // On lie l'adapter au spinner.
        userSpinner.setAdapter(adapter);
        // On indique qu'il faut appeler onEditorAction de cette classe lorsqu'une action (valider ici)
        // est faite depuis le clavier lorsqu'on est en train de remplir le mot de passe.
        EditText passwordEditText = (EditText) findViewById(R.id.login_password);
        passwordEditText.setOnEditorActionListener(this);

}

    @Override
    protected void onResume() {
        super.onResume();

        // On efface le mot de passe qui était écrit quand on se déconnecte.
        EditText passwordEditText = (EditText) findViewById(R.id.login_password);
        passwordEditText.setText("");
    }

    /**
     * Vérifie le mot de passe et connecte l'utilisateur.
     *
     * Cette méthode vérifie le mot de passe saisi. Si celui-ci est bon, connecte l'utilisateur et
     * affiche le menu principal, sinon un message est affiché à l'utilisateur.
     *
     * Cette méthode est appelée grâce à l'attribut onClick indiqué dans le fichier xml de layout
     * sur le bouton de connexion. Elle peut également être appelée depuis la méthode
     * "onEditorAction" de cette classe.
     *
     * @param v Une vue quelconque (n'est pas utilisé ici, mais requis par le onClick)
     */
    public void login(View v) {
        // Lorsqu'on clique sur le bouton "Se connecter" on qu'on valide depuis le clavier.
        User user = (User) userSpinner.getSelectedItem();
        EditText passwordEditText = (EditText) findViewById(R.id.login_password);
        String password = passwordEditText.getText().toString();

        if (user.login(password)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            BartenderApp.notifyShort(R.string.login_wrong_password_msg);
        }
    }

    public void anonym_login(View v){

        System.out.println("ANONYM LOGIN");

        User user = new User(5);

        System.out.println("USER NAME : " + user.getName() + " USER STATUS : " + user.getStatus());

        if(user.login()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else {
            BartenderApp.notifyShort(R.string.login_impossible);
        }
    }

    /**
     * Récupère les actions faites depuis le clavier.
     *
     * Récupère les actions faites depuis le clavier lors de l'édition du champ du mot de passe afin
     * de permettre de se connecter depuis le bouton "Terminer" du clavier. (Cela évite à
     * l'utilisateur de devoir fermer le clavier et de cliquer sur le bouton se connecter).
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // L'attribut android:imeOptions="actionNext" est défini dans le fichier xml de layout
        // (activity_login.xml), L'actionId attendue est donc IME_ACTION_NEXT.
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            login(v);
            return true;
        }
        return false;
    }

    /**
     * Lance l'activité d'ajout d'un élément.
     */
    public void toAddUserForm(View v) {
        Intent intent = new Intent(this, AddUserActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return true;
    }




}

