package com.bartender.bartender.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;

import com.bartender.bartender.database.MySQLiteHelper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Zélie on 27-04-15.
 */
public class Drink implements Serializable {


    private static final String DB_COLUMN_ID = "d_id";
    private static final String DB_COLUMN_NAME= "d_name";
    private static final String DB_COLUMN_DESCR_EN = "d_descr_en";
    private static final String DB_COLUMN_DESCR_FR = "d_descr_fr";
    private static final String DB_COLUMN_SPRICE="d_sprice"; //saling price
    private static final String DB_COLUMN_PPRICE="d_pprice"; //purchase price
    private static final String DB_COLUMN_STOCKM="d_stockm"; //Stock max
    private static final String DB_COLUMN_STOCK="d_stock";
    private static final String DB_COLUMN_SEUIL="d_seuil";
    private static final String DB_COLUMN_TYPE="d_type";
    private static final String DB_TABLE = "Boisson";


    private final int id;

    private String name;

    private String descriptionFR;

    private String descriptionEN;

    private float saling_price;

    private float purchase_price;

    private int stock_max;

    private int stock;

    private int seuil;

    private String type;


    /**
     * Constructeur de la boisson. Initialise une instance de la boisson courante dans la base
     * de données.
     *
     * @note Ce constructeur est privé (donc utilisable uniquement depuis cette classe). Cela permet
     * d'éviter d'avoir deux instances différentes d'un même utilisateur.
     */
    private Drink(int uId, String uName, String descriptionEN, String descriptionFR, float saling_price, float purchase_price, int stock_max, int stock, int seuil, String type ) {

        this.id = uId;
        this.setName(uName);
        this.setDescriptionEN(descriptionEN);
        this.setDescriptionFR(descriptionFR);
        this.setSaling_price(saling_price);
        this.setPurchase_price(purchase_price);
        this.setStock_max(stock_max);
        this.setStock(stock);
        this.setSeuil(seuil);
        this.setType(type);
        
    }

    /**
     * Constructeur de la boisson. Initialise une instance de la boisson courante dans la base
     * de données.
     *
     * @note Ce constructeur est privé (donc utilisable uniquement depuis cette classe). Cela permet
     * d'éviter d'avoir deux instances différentes d'un même utilisateur.
     */
    private Drink(int uId, String uName, String descriptionEN, float saling_price, String type, String descriptionFR ) {

        this.id = uId;
        this.setName(uName);
        this.setDescriptionEN(descriptionEN);
        this.setSaling_price(saling_price);
        this.setType(type);

    }

    public Drink(int id) {

        this.id= id;

        loadData();

    }

    /**
     * Fournit l'id de la boisson courante.
     */
    public int getId() {
        return id;
    }

    /**
     * Fournit le nom de la boisson courante.
     */
    public String getName() {
        return name;
    }
    /**
     * Fournit la description en anglais de la boisson courante.
     */
    public String getDescriptionEN(){ return descriptionEN;}
    /**
     * Fournit la description en français  de la boisson courante.
     */
    public String getDescriptionFR(){ return descriptionFR;}
    /**
     * Fournit le prix de vente de la boisson courante.
     */
    public float getSaling_price(){ return saling_price; }
    /**
     * Fournit le prix d'achat de la boisson courante.
     */
    public float getPurchase_price(){ return purchase_price; }
    /**
     * Fournit le stock de la boisson courante.
     */
    public int getStock(){return stock; }
    /**
     * Fournit le stock max de la boisson courante.
     */
    public int getStock_max(){ return stock_max; }
    /**
     * Fournit le seuil de la boisson courante.
     */
    public int getSeuil(){ return seuil; }
    /**
     * Fournit le type de la boisson courante.
     */
    public String getType() {return type; }


    public String toString() { return this.getName() ; }





    /**
     * Contient les instances déjà existantes des boissons afin d'éviter de créer deux instances
     * de la meme boisson.
     */
    private static SparseArray<Drink> drinkSparseArray = new SparseArray<Drink>();


    /**
     * Fournit la liste des boissons avec leur id, leur nom, leur type, leur description et leur prix.
     */
    public static ArrayList<Drink> getDrinks() {

        ArrayList<Drink> drinks = new ArrayList<Drink>();

        // Récupération du  SQLiteHelper et de la base de données.
        MySQLiteHelper msh = MySQLiteHelper.get() ;
        if(msh == null){ System.out.println("MSH null"); }
        else { System.out.println("MSH OK"); }
        try {
            SQLiteDatabase db = msh.getReadableDatabase() ;
            System.out.println(db);
            // Colonnes à récupérer
            String[] colonnes = {DB_COLUMN_ID};

            // Requête de selection (SELECT)
            Cursor cursor = db.query(DB_TABLE, colonnes, null, null, null, null, null);

            // Placement du curseur sur la première ligne.
            cursor.moveToFirst();

            // Tant qu'il y a des lignes.
            while (!cursor.isAfterLast()) {
                // Récupération des informations de la boisson pour chaque ligne.
                int dId = cursor.getInt(0);

                // Vérification pour savoir s'il y a déjà une instance de cette boisson.
                //Drink drink = Drink.drinkSparseArray.get(dId);
                //if (drink == null) {
                    // Si pas encore d'instance, création d'une nouvelle instance.
                Drink drink = new Drink(dId);
                //}

                // Ajout de la boisson à la liste.
                drinks.add(drink);

                // Passe à la ligne suivante.
                cursor.moveToNext();
            }

            // Fermeture du curseur et de la base de données.
            cursor.close();
            db.close();
        }
        catch(Exception SQLiteException){
            System.out.println("ERROOOOOOOR");
        }

        return drinks ;
    }

    public static ArrayList<Drink> searchDrinksByName(String name){
        ArrayList<Drink> drinks = new ArrayList<Drink>();

        // Récupération du  SQLiteHelper et de la base de données.
        MySQLiteHelper msh = MySQLiteHelper.get() ;
        if(msh == null){ System.out.println("MSH null"); }
        else { System.out.println("MSH OK"); }
        try {
            SQLiteDatabase db = msh.getReadableDatabase() ;
            System.out.println(db);
            // Colonnes à récupérer
            String[] colonnes = {DB_COLUMN_ID};
            String selection= "Boisson.d_name LIKE '%" + name + "%'";

            // Requête de selection (SELECT)
            Cursor cursor = db.query(DB_TABLE, colonnes, selection, null, null, null, null);

            // Placement du curseur sur la première ligne.
            cursor.moveToFirst();

            // Tant qu'il y a des lignes.
            while (!cursor.isAfterLast()) {
                // Récupération des informations de la boisson pour chaque ligne.
                int dId = cursor.getInt(0);
                // Vérification pour savoir s'il y a déjà une instance de cette boisson.
                Drink drink = Drink.drinkSparseArray.get(dId);
                if (drink == null) {
                    // Si pas encore d'instance, création d'une nouvelle instance.
                    drink = new Drink(dId);
                }

                // Ajout de la boisson à la liste.
                drinks.add(drink);

                // Passe à la ligne suivante.
                cursor.moveToNext();
            }

            // Fermeture du curseur et de la base de données.
            cursor.close();
            db.close();
        }
        catch(Exception SQLiteException){
            Log.e("Error getting drinks","", SQLiteException);
        }

        return drinks ;

    }


    /**
     * Crée une nouvelle boisson dans la base de données.
     *
     * @param name        Nom de la nouvelle boisson
     * @param descriptionEN description de la nouvelle boisson en anglais
     * @param descriptionFR description de la nouvelle boisson en français
     *@param saling_price prix de vente de la nouvelle boisson
     *@param purchase_price prix d'achat de la nouvelle boisson
     *@param type type de la nouvelle boisson
     *@param stock stock de la nouvelle boisson
     *@param stock_max stock maximum de la nouvelle boisson
     * @param seuil seuil de la nouvelle boisson
     *
     * @return Vrai (true) en cas de succès, faux (false) en cas d'échec.
     * @post Enregistre le nouvel objet dans la base de données.
     */
    public static boolean create(String name, String descriptionEN, String descriptionFR,  float saling_price, float purchase_price, String type, int stock, int stock_max, int seuil) {

        // Récupération de la base de données.
        try {
            SQLiteDatabase db = MySQLiteHelper.get().getWritableDatabase();
            System.out.println(db);

            // Définition des valeurs pour le nouvel élément dans la table "collected_items".
            ContentValues cv = new ContentValues();
            cv.put(DB_COLUMN_NAME, name);
            cv.put(DB_COLUMN_DESCR_EN, descriptionEN);
            cv.put(DB_COLUMN_DESCR_FR, descriptionFR);
            cv.put(DB_COLUMN_SPRICE, saling_price);
            cv.put(DB_COLUMN_PPRICE, purchase_price);
            cv.put(DB_COLUMN_TYPE, type);
            cv.put(DB_COLUMN_STOCK, stock);
            cv.put(DB_COLUMN_STOCKM, stock_max);
            cv.put(DB_COLUMN_SEUIL, seuil);


            // Ajout à la base de données (table users).
            int s_id = (int) db.insert(DB_TABLE, null, cv);

            if (s_id == -1) {
                System.out.println("FALSE");
                return false; // En cas d'erreur d'ajout, on retourne false directement.
            }
            cv.clear();

            System.out.println("TRUE");
            return true;
        }
        catch(Exception SQLiteException){
            System.out.println("ERROOOOOOOR");
            return false;
        }
    }
    public static boolean delete(String name) {

        // Récupération de la base de données.
        try {
            SQLiteDatabase db = MySQLiteHelper.get().getWritableDatabase();
            System.out.println(db);

            // Retrait de la base de donnée à la base de données (table drink).
            int s_id = (int) db.delete(DB_TABLE, "d_name="+ "\"" + name + "\"", null);

            if (s_id == -1) {
                System.out.println("FALSE");
                return false; // En cas d'erreur d'ajout, on retourne false directement.
            }
            return true;

        }
        catch(Exception SQLiteException){
            System.out.println("ERROOOOOOOR");
            return false;
        }
    }

    public void update(){
        try {
            SQLiteDatabase db = MySQLiteHelper.get().getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(DB_COLUMN_NAME, name);
            cv.put(DB_COLUMN_DESCR_EN, descriptionEN);
            cv.put(DB_COLUMN_DESCR_FR, descriptionFR);
            cv.put(DB_COLUMN_SPRICE, saling_price);
            cv.put(DB_COLUMN_PPRICE, purchase_price);
            cv.put(DB_COLUMN_TYPE, type);
            cv.put(DB_COLUMN_STOCK, stock);
            cv.put(DB_COLUMN_STOCKM, stock_max);
            cv.put(DB_COLUMN_SEUIL, seuil);


            int s_id = (int) db.update(DB_TABLE, cv,"Boisson.d_id="+this.getId(),null);


            if (s_id == -1) {
                 return; // En cas d'erreur d'ajout, on retourne false directement.
            }
            cv.clear();
            return;
        }
        catch(Exception SQLiteException){
            System.out.println("ERROOOOOOOR");
            return;
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
            String[] columns = new String[]{DB_COLUMN_ID, DB_COLUMN_NAME, DB_COLUMN_DESCR_EN, DB_COLUMN_DESCR_FR, DB_COLUMN_SPRICE, DB_COLUMN_PPRICE, DB_COLUMN_STOCKM, DB_COLUMN_STOCK, DB_COLUMN_SEUIL, DB_COLUMN_TYPE};

            // Critères de sélection de la ligne :
            String selection = DB_COLUMN_ID + " = ? ";
            String[] selectionArgs = new String[]{String.valueOf(id)};

            // Requête SELECT à la base de données.
            Cursor c = db.query(DB_TABLE, columns, selection, selectionArgs, null, null, null);

            // Placement du curseur sur le  premier résultat (ici le seul puisque l'objet est unique).
            c.moveToFirst();

            // Copie des données de la ligne vers les variables d'instance de l'objet courant.
            this.setName(c.getString(1));
            this.setDescriptionEN(c.getString(2));
            this.setDescriptionFR(c.getString(3));
            this.setSaling_price(c.getFloat(4));
            this.setPurchase_price(c.getFloat(5));
            this.setStock_max(c.getInt(6));
            this.setStock(c.getInt(7));
            this.setSeuil(c.getInt(8));
            this.setType(c.getString(9));
        }
        catch(Exception SQLiteException){
            System.out.println("ERROOOOOOOR");
            //return false;
        }
    }

    public static boolean updateStock(int dId, int new_quantity) {
        try {
            SQLiteDatabase db = MySQLiteHelper.get().getWritableDatabase();
            System.out.println(db);
            // Colonnes à récupérer
            String[] colonnes = {DB_COLUMN_ID, DB_COLUMN_STOCK};

            // Critères de sélection de la ligne :
            String selection = DB_COLUMN_ID + " = ? ";
            String[] selectionArgs = new String[]{String.valueOf(dId)};

            ContentValues args = new ContentValues();
            args.put(DB_COLUMN_STOCK, new_quantity);

            Drink temp = new Drink(dId);
            System.out.println("TEMP : " + temp.toString());
            if (db.update(DB_TABLE, args, selection, selectionArgs) > 0) {
                System.out.println("NEW STOCK FOR " + temp.getName() + ": " + new_quantity);
                return true ;
            } else {
                System.out.println("ECHEC UPDATE STOCK");
                return false ;
            }
        }
        catch(Exception SQLiteException){
            Log.e("Error", "while getting users", SQLiteException);
            System.out.println("ERROR in UPDATE STOCKS");
            return false ;
        }


    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescriptionEN(String description) {
        this.descriptionEN = description;
    }

    public void setDescriptionFR(String description) {
        this.descriptionFR = description;
    }

    public void setSaling_price(float saling_price) {
        this.saling_price = saling_price;
    }

    public void setPurchase_price(float purchase_price) {
        this.purchase_price = purchase_price;
    }

    public void setStock_max(int stock_max) {
        this.stock_max = stock_max;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setSeuil(int seuil) {
        this.seuil = seuil;
    }

    public void setType(String type) {
        this.type = type;
    }
}





