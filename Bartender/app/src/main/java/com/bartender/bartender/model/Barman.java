package com.bartender.bartender.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;
import com.bartender.bartender.database.MySQLiteHelper;

import java.util.ArrayList;

/**
 * Created by charlotte on 27/04/15.
 */
public class Barman extends User {

    private static final String DB_COLUMN_ID = "u_id";
    private static final String DB_COLUMN_NAME = "u_name";
    private static final String DB_COLUMN_PASSWORD = "u_password";
    private static final String DB_COLUMN_STATUS = "u_status";
    private static final String DB_COLUMN_ISMANAGER = "u_ismanager";
    private static final String DB_TABLE = "users";

    private boolean is_manager;
    private String name;
    private String password;

    /**
     * Contient les instances déjà existantes des utilisateurs afin d'éviter de créer deux instances
     * du même utilisateur.
     */
    private static SparseArray<Barman> barmanSparseArray = new SparseArray<Barman>();

    public Barman(int bId) {
        super(bId);

        loadData();
    }

    private static Barman currentBarman = null;

    /**
     * Constructeur de l'utilisateur. Initialise une instance de l'utilisateur présent dans la base
     * de données.
     *
     * @note Ce constructeur est privé (donc utilisable uniquement depuis cette classe). Cela permet
     * d'éviter d'avoir deux instances différentes d'un même utilisateur.
     */

    private Barman(int bId, String bName, String bPassword) {

        super(bId);

        this.setName(bName);
        this.setPassword(bPassword);
        this.setIs_manager(false);

        Barman.barmanSparseArray.put(bId, this);
    }

    /**
     * Fournit la liste des utilisateurs.
     */
    public static ArrayList<User> getBarmans() {

        ArrayList<User> barmans = new ArrayList<User>();


        // Récupération du  SQLiteHelper et de la base de données.
        MySQLiteHelper msh = MySQLiteHelper.get();
        if (msh == null) {
            System.out.println("MSH null");
        } else {
            System.out.println("MSH OK");
        }
        try {
            SQLiteDatabase db = msh.getReadableDatabase();
            System.out.println(db);
            // Colonnes à récupérer
            String[] colonnes = {DB_COLUMN_ID, DB_COLUMN_NAME, DB_COLUMN_PASSWORD, DB_COLUMN_STATUS};

            // Critères de sélection de la ligne :
            String selection = DB_COLUMN_STATUS + " = ? ";
            String[] selectionArgs = new String[]{"barman"};

            // Requête de selection (SELECT)
            Cursor cursor = db.query(DB_TABLE, colonnes, selection, selectionArgs, null, null, null);

            // Placement du curseur sur la première ligne.
            cursor.moveToFirst();

            // Tant qu'il y a des lignes.
            while (!cursor.isAfterLast()) {
                // Récupération des informations de l'utilisateur pour chaque ligne.
                int bId = cursor.getInt(0);
                String bNom = cursor.getString(1);
                String bPassword = cursor.getString(2);

                // Vérification pour savoir s'il y a déjà une instance de cet utilisateur.
                User barman = Barman.barmanSparseArray.get(bId);
                if (barman == null) {
                    // Si pas encore d'instance, création d'une nouvelle instance.
                    barman = new Barman(bId, bNom, bPassword);
                }

                // Ajout de l'utilisateur à la liste.
                barmans.add(barman);

                // Passe à la ligne suivante.
                cursor.moveToNext();
            }

            // Fermeture du curseur et de la base de données.
            cursor.close();
            db.close();
        } catch (Exception SQLiteException) {
            System.out.println("ERROOOOOOOR");
        }

        return barmans;
    }

    public boolean Is_manager() {
        return is_manager;
    }

    public void setIs_manager(boolean is_manager) {
        this.is_manager = is_manager;

    }

    private void loadData() {

        try {
            // Récupération de la base de données en mode "lecture".
            SQLiteDatabase db = MySQLiteHelper.get().getReadableDatabase();

            // Colonnes pour lesquelles il nous faut les données.
            String[] columns = new String[]{DB_COLUMN_ID, DB_COLUMN_NAME, DB_COLUMN_PASSWORD, DB_COLUMN_STATUS, DB_COLUMN_ISMANAGER};

            // Critères de sélection de la ligne :
            String selection = DB_COLUMN_ID + " = ? ";
            String[] selectionArgs = new String[]{String.valueOf(this.getId())};

            // Requête SELECT à la base de données.
            Cursor c = db.query(DB_TABLE, columns, selection, selectionArgs, null, null, null);

            // Placement du curseur sur le  premier résultat (ici le seul puisque l'objet est unique).
            c.moveToFirst();

            // Copie des données de la ligne vers les variables d'instance de l'objet courant.
            this.setName(c.getString(1));
            this.setPassword(c.getString(2));
            this.setStatus(c.getString(3));
            if(c.getInt(4) == 0) {
                this.is_manager = false;
            } else {
                this.is_manager = true ;
            }

            // Fermeture du curseur et de la base de données.
            c.close();
            db.close();
        }
        catch(Exception SQLiteException){
            System.out.println("ERROOOOOOOR");
        }
    }
}
