package com.bartender.bartender.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;

import com.bartender.bartender.database.MySQLiteHelper;


/**
 * Représente un utilisateur et gère l'authentification de celui-ci à l'application.
 *
 * Cette classe représente un utilisateur de l'application. Elle utilise pour cela la base de
 * données par l'intermédiaire du MySQLiteHelper.
 *
 * Les méthodes statiques permettent de récupérer la liste des utilisateurs, récupérer l'utilisateur
 * actuellement connecté (s'il y en a un) et de déconnecter l'utilisateur.
 *
 * @author Damien Mercier && Charlotte Hansart
 */
public class User {

    private static final String DB_COLUMN_ID = "u_id";
    private static final String DB_COLUMN_NAME = "u_name";
    private static final String DB_COLUMN_PASSWORD = "u_password";
    private static final String DB_COLUMN_STATUS = "u_status";
    private static final String DB_COLUMN_ISMAN = "u_ismanager";
    private static final String DB_TABLE = "users";
    private final int id;
    private String name;
    private String password;
    private String status;
    private boolean is_manager;

    public User(int uId){

        this.id = uId;

        // On enregistre l'instance de l'élément de collection courant dans la hashMap.
        User.userSparseArray.put(uId, this);

        // On charge les données depuis la base de données.
        loadData();

    }
    /**
     * Constructeur de l'utilisateur. Initialise une instance de l'utilisateur présent dans la base
     * de données.
     *
     * @note Ce constructeur est privé (donc utilisable uniquement depuis cette classe). Cela permet
     * d'éviter d'avoir deux instances différentes d'un même utilisateur.
     */
    private User(int uId, String uName, String uPassword, String uStatus, boolean is_manager) {

        this.id = uId;
        this.setName(uName);
        this.setPassword(uPassword);
        this.setStatus(uStatus);
        this.is_manager = is_manager;
        User.userSparseArray.put(uId, this);
    }

    /**
     * Fournit l'id de l'utilisateur courant.
     */
    public int getId() {
        return id;
    }

    /**
     * Fournit le nom de l'utilisateur courant.
     */
    public String getName() {
        return name;
    }

    public String getStatus() { return status ; }

    public boolean getIs_manager() { return is_manager ; }

    public boolean isClient() { return status == "client" ; }

    /**
     * Connecte l'utilisateur courant.
     *
     * @param passwordToTry le mot de passe entré.
     *
     * @return Vrai (true) si l'utilisateur à l'autorisation de se connecter, false sinon.
     */
    public boolean login(String passwordToTry) {
        if (this.password.equals(passwordToTry)) {
            // Si le mot de passe est correct, modification de l'utilisateur connecté.
            User.connectedUser = this;
            return true;
        }
        return false;
    }


    public boolean login() {
        User.connectedUser = this ;
        return true ;
    }

    /**
     * Fournit une représentation textuelle de l'utilisateur courant. (Ici le nom)
     *
     * @note Cette méthode est utilisée par l'adaptateur ArrayAdapter afin d'afficher la liste des
     * utilisateurs. (Voir LoginActivity).
     */
    public String toString() {
        return getName();
    }

    /******************************************************************************
     * Partie static de la classe.
     ******************************************************************************/

    /**
     * Contient les instances déjà existantes des utilisateurs afin d'éviter de créer deux instances
     * du même utilisateur.
     */
    private static SparseArray<User> userSparseArray = new SparseArray<User>();

    /**
     * Utilisateur actuellement connecté à l'application. Correspond à null si aucun utilisateur
     * n'est connecté.
     */
    private static User connectedUser = null;
    private static User currentBarman = null;

    /**
     * Fournit l'utilisateur actuellement connecté.
     */
    public static User getConnectedUser() {
        return User.connectedUser;
    }

    public static User getCurrentBarman() {
        return User.currentBarman;
    }

    public static void setCurrentBarman(Barman b) { currentBarman = b; }


    /**
     * Déconnecte l'utilisateur actuellement connecté à l'application.
     */
    public static void logout() {

        User.connectedUser = null;

    }

    public static boolean alreadyUsedPseudo(String name) {

        try {
            SQLiteDatabase db = MySQLiteHelper.get().getReadableDatabase() ;
            System.out.println(db);
            // Colonnes à récupérer
            String[] colonnes = {DB_COLUMN_ID};

            // Critères de sélection de la ligne :
            String selection = DB_COLUMN_NAME + " = ? " ;
            String[] selectionArgs = new String[]{name};

            // Requête de selection (SELECT)
            Cursor cursor = db.query(DB_TABLE, colonnes, selection, selectionArgs, null, null, null);

            int c = cursor.getCount();
            if(c == 0){
                return false ;
            }

            // Fermeture du curseur et de la base de données.
            cursor.close();
            db.close();
        }
        catch(Exception SQLiteException){
            Log.e("Error", "while getting users", SQLiteException) ;
        }

        return true ;
    }

    /**
     * Fournit la liste des utilisateurs.
     */
    public static ArrayList<User> getUsers() {

        ArrayList<User> users = new ArrayList<User>();


        // Récupération du  SQLiteHelper et de la base de données.
        MySQLiteHelper msh = MySQLiteHelper.get() ;
        if(msh == null){ System.out.println("MSH null"); }
        else { System.out.println("MSH OK"); }
        try {
            SQLiteDatabase db = msh.getReadableDatabase() ;
            System.out.println(db);
            // Colonnes à récupérer
            String[] colonnes = {DB_COLUMN_ID, DB_COLUMN_NAME, DB_COLUMN_PASSWORD, DB_COLUMN_STATUS, DB_COLUMN_ISMAN};

            // Requête de selection (SELECT)
            Cursor cursor = db.query(DB_TABLE, colonnes, null, null, null, null, null);

            // Placement du curseur sur la première ligne.
            cursor.moveToFirst();

            // Tant qu'il y a des lignes.
            while (!cursor.isAfterLast()) {
                // Récupération des informations de l'utilisateur pour chaque ligne.
                int uId = cursor.getInt(0);
                String uNom = cursor.getString(1);
                String uPassword = cursor.getString(2);
                String uStatus = cursor.getString(3);
                boolean uIs_manager = (cursor.getInt(4) == 1);

                if(uStatus == "user"){
                    break;
                }
                // Vérification pour savoir s'il y a déjà une instance de cet utilisateur.
                User user = User.userSparseArray.get(uId);

                if (user == null) {
                    // Si pas encore d'instance, création d'une nouvelle instance.
                    user = new User(uId, uNom, uPassword, uStatus, uIs_manager);
                }

                // Ajout de l'utilisateur à la liste.
                users.add(user);

                // Passe à la ligne suivante.
                cursor.moveToNext();
            }

            // Fermeture du curseur et de la base de données.
            cursor.close();
            db.close();
        }
        catch(Exception SQLiteException){
            Log.e("Error", "while getting users", SQLiteException);
        }

        return users ;
    }


    /**
     * Fournit la liste des utilisateurs.
     */
    public static ArrayList<User> getClients() {

        ArrayList<User> users = new ArrayList<User>();

        try {
            SQLiteDatabase db = MySQLiteHelper.get().getReadableDatabase() ;
            System.out.println(db);
            // Colonnes à récupérer
            String[] colonnes = {DB_COLUMN_ID, DB_COLUMN_NAME, DB_COLUMN_PASSWORD, DB_COLUMN_STATUS, DB_COLUMN_ISMAN};

            // Critères de sélection de la ligne :
            String selection = DB_COLUMN_STATUS + " = ? OR " + DB_COLUMN_STATUS + " = ? " ;
            String[] selectionArgs = new String[]{"client", "barman"};

            // Requête de selection (SELECT)
            Cursor cursor = db.query(DB_TABLE, colonnes, selection, selectionArgs, null, null, null);

            // Placement du curseur sur la première ligne.
            cursor.moveToFirst();

            // Tant qu'il y a des lignes.
            while (!cursor.isAfterLast()) {
                // Récupération des informations de l'utilisateur pour chaque ligne.
                int uId = cursor.getInt(0);
                String uNom = cursor.getString(1);
                String uPassword = cursor.getString(2);
                String uStatus = cursor.getString(3);
                boolean uIs_manager = (cursor.getInt(4) == 1);

                // Vérification pour savoir s'il y a déjà une instance de cet utilisateur.
                User user = User.userSparseArray.get(uId);
                if (user == null) {
                    // Si pas encore d'instance, création d'une nouvelle instance.
                    user = new User(uId, uNom, uPassword, uStatus, uIs_manager);
                }

                // Ajout de l'utilisateur à la liste.
                users.add(user);

                // Passe à la ligne suivante.
                cursor.moveToNext();
            }

            // Fermeture du curseur et de la base de données.
            cursor.close();
            db.close();
        }
        catch(Exception SQLiteException){
            Log.e("Error", "while getting users", SQLiteException);
        }

        return users ;
    }

    /**
     * Crée un nouvel utilisateur dans la base de données.
     *
     * @param name        Pseudo du nouvel utilisateur
     * @param password Mot de passe du nouvel utilisateur
     *
     * @return Vrai (true) en cas de succès, faux (false) en cas d'échec.
     * @post Enregistre le nouvel objet dans la base de données.
     */
    public static boolean create(String name, String password, String status, int is_manager) {

        // Récupération de la base de données.
        try {
            SQLiteDatabase db = MySQLiteHelper.get().getWritableDatabase();
            System.out.println(db);

            // Définition des valeurs pour le nouvel élément dans la table "collected_items".
            ContentValues cv = new ContentValues();
            cv.put(DB_COLUMN_NAME, name);
            cv.put(DB_COLUMN_PASSWORD, password);
            cv.put(DB_COLUMN_STATUS, status);
            cv.put(DB_COLUMN_ISMAN, is_manager);


            // Ajout à la base de données (table users).
            int s_id = (int) db.insert(DB_TABLE, null, cv);

            if (s_id == -1) {
                System.out.println("FALSE");
                return false; // En cas d'erreur d'ajout, on retourne false directement.
            }
            cv.clear();

            if(status.compareTo("barman") == 0){
                currentBarman = new User(s_id);
            }

            System.out.println("TRUE");
            return true;
        }
        catch(Exception SQLiteException){
            System.out.println("ERROOOOOOOR");
            return false;
        }
    }

    /**
     * (Re)charge les informations depuis la base de données.
     *
     * @pre L'id de l'élément est indiqué dans this.id et l'élément existe dans la base de données.
     * @post Les informations de l'élément sont chargées dans les variables d'instance de la
     * classe.
     */
    private void loadData() {

        try {
            // Récupération de la base de données en mode "lecture".
            SQLiteDatabase db = MySQLiteHelper.get().getReadableDatabase();

            // Colonnes pour lesquelles il nous faut les données.
            String[] columns = new String[]{DB_COLUMN_ID, DB_COLUMN_NAME, DB_COLUMN_PASSWORD, DB_COLUMN_STATUS, DB_COLUMN_ISMAN};

            // Critères de sélection de la ligne :
            String selection = DB_COLUMN_ID + " = ? ";
            String[] selectionArgs = new String[]{String.valueOf(id)};

            // Requête SELECT à la base de données.
            Cursor c = db.query(DB_TABLE, columns, selection, selectionArgs, null, null, null);

            // Placement du curseur sur le  premier résultat (ici le seul puisque l'objet est unique).
            c.moveToFirst();

            // Copie des données de la ligne vers les variables d'instance de l'objet courant.
            this.setName(c.getString(1));
            this.setPassword(c.getString(2));
            this.setStatus(c.getString(3));
            this.setIs_manager(c.getInt(4));

            // Fermeture du curseur et de la base de données.
            c.close();
            db.close();
        }
        catch(Exception SQLiteException){
            System.out.println("ERROOOOOOOR");
            //return false;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStatus(String status) { this.status = status ; }

    public void setIs_manager(int is_manager) {
        if(is_manager == 0){
            this.is_manager = false ;
        } else {
            this.is_manager = true ;
        }
    }

}


