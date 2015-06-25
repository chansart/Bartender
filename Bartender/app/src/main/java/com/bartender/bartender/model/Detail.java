package com.bartender.bartender.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.bartender.bartender.database.MySQLiteHelper;

import java.util.ArrayList;
/**
 * Created by charlotte on 27/04/15.
 */

/**
 * Représente un détail.
 *
 * Cette classe représente un détail d'une commande. Elle utilise pour cela la base de
 * données par l'intermédiaire du MySQLiteHelper.
 *
 */
public class Detail {

    private static final String DB_COLUMN_ID = "det_id";
    private static final String DB_COLUMN_DRINK = "d_id";
    private static final String DB_COLUMN_QUANTITY = "det_quant";
    private static final String DB_COLUMN_ORDER = "o_id";
    private static final String DB_TABLE = "details";
    private static final String DB_TABLE_B = "Boisson";
    private static final String DB_COLUMN_SPRICE = "d_sprice";




    /**
     * Identifiant unique du detail courant. Correspond à _id dans la base de données.
     */
    private final int id;

    /**
     * id de la boisson du détail courant. Correspond à _drinkOrdered dans la base de données.
     */
    private int dId;

    /**
     * quantité de la boisson du détail courant. Correspond à _quantityOrdered dans la base de données.
     */
    private int quantity;

    /**
     * Id de la commande à laquelle est rattaché le détail courant. correspond à _idOrder dans la base de données
     */
    private int oid;


    public Detail(int det_Id){

        this.id = det_Id;

        loadData();

    }

    /**
     * Constructeur du détail. Initialise une instance du détail présent dans la base
     * de données.
     *
     * @note Ce constructeur est privé (donc utilisable uniquement depuis cette classe). Cela permet
     * d'éviter d'avoir deux instances différentes d'un même détail.
     */
    private Detail(int dId, int ddrink, int dquantity, int doid) {
        this.id = dId;
        this.dId = ddrink;
        this.quantity = dquantity;
        this.oid=doid;
    }

    /**
     * Fournit l'id du détail courant.
     */
    public int getId() {
        return id;
    }

    /**
     * Fournit l'id de la boisson du détail courant.
     */
    public int getDrinkId() {
        return dId;
    }

    /**
     * Fournit la quantité de la boisson du détail courant.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Fournit l'id de la commande à laquelle est rattaché le détail.
     */
    public int getOid() {
        return oid;
    }

    public void setDrinkId(int dId) { this.dId = dId ; }

    public void setQuantity(int quant) { this.quantity = quant ; }

    public void setOid(int oid) { this.oid = oid; }

    /**
     * fournit une représentation textuelle du détail courant. (exemple : 10 fantas pour la commande numéro 502350)
     */
    public String toString(){
        return this.getQuantity() + " " + new Drink(this.getDrinkId()).getName() + " pour la commande numéro " + this.getOid()  ;
    }


    public float getPrice(){

        SQLiteDatabase db = MySQLiteHelper.get().getReadableDatabase();

        // Colonnes pour lesquelles il nous faut les données.
        String[] columns = new String[]{DB_COLUMN_SPRICE};

        // Critères de sélection de la ligne :
        String selection = DB_COLUMN_DRINK + " = ? " ;
        String[] selectionArgs = new String[]{String.valueOf(this.getDrinkId())};

        Cursor cursor = db.query(DB_TABLE_B, columns, selection, selectionArgs, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        return cursor.getFloat(0)*this.getQuantity();
    }

    /**
     *
     * @param dId       Id de la boisson du nouveau détail
     * @param quantity  Quantité de la boisson du nouveau détail
     * @param oid       Id de la commande à laquelle est rattaché le nouveau détail
     * @return          true si aucune erreur, false sinon.
     */
    public static boolean create(int dId, int quantity, int oid){

        try {
            SQLiteDatabase db = MySQLiteHelper.get().getWritableDatabase();
            System.out.println(db);

            String[] colonnes = {DB_COLUMN_ID, DB_COLUMN_DRINK, DB_COLUMN_QUANTITY, DB_COLUMN_ORDER};

            // Critères de sélection de la ligne :
            String selection = DB_COLUMN_DRINK + " = ? AND " + DB_COLUMN_ORDER + " = ? " ;
            String[] selectionArgs = new String[]{ String.valueOf(dId), String.valueOf(oid)};

            // Requête de selection (SELECT)
            Cursor cursor = db.query(DB_TABLE, colonnes, selection, selectionArgs, null, null, null);

            //SI UN DETAIL AVEC LA MEME BOISSON POUR LA MEME COMMANDE EXISTE DEJA,
            //ON UPDATE LA LIGNE
            System.out.println(cursor.getCount());

                // Définition des valeurs pour le nouvel élément dans la table "collected_items".
                ContentValues cv = new ContentValues();
                cv.put(DB_COLUMN_DRINK, dId);
                cv.put(DB_COLUMN_QUANTITY, quantity);
                cv.put(DB_COLUMN_ORDER, oid);

                // Ajout à la base de données (table detail).
                int d_id = (int) db.insert(DB_TABLE, null, cv);

                if (d_id == -1) {
                    return false; // En cas d'erreur d'ajout, on retourne false directement.
                }
                cv.clear();

                return true;
            //}
        }
        catch(Exception SQLiteException){
            System.out.println("create detail Error.");
            return false;
        }
    }

    public static ArrayList<Detail> getDetails(int oId){

        // Initialisation de la liste des orders.
        ArrayList<Detail> details = new ArrayList<Detail>();

        try {
            // Récupération de la base de données en mode "lecture".
            SQLiteDatabase db = MySQLiteHelper.get().getReadableDatabase();

            // Colonnes pour lesquelles il nous faut les données.
            String[] columns = new String[]{DB_COLUMN_ID, DB_COLUMN_DRINK, DB_COLUMN_QUANTITY, DB_COLUMN_ORDER};

            // Critères de sélection de la ligne :
            String selection = DB_COLUMN_ORDER + " = ? ";
            String[] selectionArgs = new String[]{String.valueOf(oId)};

            // Requête SELECT à la base de données.
            Cursor c = db.query(DB_TABLE, columns, selection, selectionArgs, null, null, null);

            c.moveToFirst();
            while (!c.isAfterLast()) {
                // Id de l'élément.
                int dId = c.getInt(0);
                // L'instance de l'élément de collection est récupéré avec la méthode get(sId)
                // (Si l'instance n'existe pas encore, elle est créée par la méthode get)
                Detail detail = new Detail(dId);

                // Ajout de l'élément de collection à la liste.
                details.add(detail);

                c.moveToNext();
            }

            // Fermeture du curseur et de la base de données.
            c.close();
            db.close();

            return details ;
        }
        catch(Exception SQLiteException){
            System.out.println("ERROOOOOOOR");
            return details ;
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
            String[] columns = new String[]{DB_COLUMN_ID, DB_COLUMN_DRINK, DB_COLUMN_QUANTITY, DB_COLUMN_ORDER};

            // Critères de sélection de la ligne :
            String selection = DB_COLUMN_ID + " = ? ";
            String[] selectionArgs = new String[]{String.valueOf(id)};

            // Requête SELECT à la base de données.
            Cursor c = db.query(DB_TABLE, columns, selection, selectionArgs, null, null, null);

            // Placement du curseur sur le  premier résultat (ici le seul puisque l'objet est unique).
            c.moveToFirst();

            // Copie des données de la ligne vers les variables d'instance de l'objet courant.
            this.setDrinkId(c.getInt(1));
            this.setQuantity(c.getInt(2));
            this.setOid(c.getInt(3));

            // Fermeture du curseur et de la base de données.
            c.close();
            db.close();
        }
        catch(Exception SQLiteException){
            System.out.println("ERROOOOOOOR");
            //return false;
        }
    }

    public static float getTotalAmount(int oId) {

        float total_amount = 0 ;

        try {
            // Récupération de la base de données en mode "lecture".
            SQLiteDatabase db = MySQLiteHelper.get().getReadableDatabase();

            // Colonnes pour lesquelles il nous faut les données.
            String[] columns = new String[]{DB_COLUMN_ID, DB_COLUMN_DRINK, DB_COLUMN_QUANTITY, DB_COLUMN_ORDER};

            // Critères de sélection de la ligne :
            String selection = DB_COLUMN_ORDER + " = ? ";
            String[] selectionArgs = new String[]{ String.valueOf(oId) };

            // Requête SELECT à la base de données.
            Cursor cursor = db.query(DB_TABLE, columns, selection, selectionArgs, null, null, null);

            // Placement du curseur sur le  premier résultat (ici le seul puisque l'objet est unique).
            cursor.moveToFirst();
            System.out.println("CURSOR : " + cursor.getCount());
            // Tant qu'il y a des lignes.
            while (!cursor.isAfterLast()) {
                // Récupération des informations de l'utilisateur pour chaque ligne.
                int det_Id = cursor.getInt(0);
                int dId = cursor.getInt(1);
                int quant = cursor.getInt(2);

                Drink temp = new Drink(dId);
                System.out.println("OLD AMOUNT : " + total_amount);
                total_amount += (temp.getSaling_price() * quant) ;
                System.out.println("NEW AMOUNT ; " + total_amount);

                // Passe à la ligne suivante.
                cursor.moveToNext();
            }

            cursor.close();
            db.close();
        }
        catch(Exception SQLiteException){
            System.out.println("ERROOOOOOOR");
        }
        return total_amount ;
    }


}
