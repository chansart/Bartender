package com.bartender.bartender.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Date;

import com.bartender.bartender.database.MySQLiteHelper;

/**
 * Created by charlotte on 27/04/15.
 */
public class Order {

    /*
     * Noms des tables et des colonnes dans la base de données.
     */
    public static final String DB_TABLE_S = "orders";

    public static final String DB_COL_ID = "o_id";
    public static final String DB_COL_BID = "b_id";
    public static final String DB_COL_UID = "u_id";
    public static final String DB_COL_AMOUNT = "o_amountorder";
    public static final String DB_COL_TABLE = "o_table";
    public static final String DB_COL_DATE = "o_date";
    public static final String DB_COL_ISSENT = "o_issent";
    public static final String DB_COL_ISSERVED = "o_isserved";
    public static final String DB_COL_ISPAID = "o_ispaid";


    /* Pour éviter les ambiguités dans les requêtes, il faut utiliser le format
     *      nomDeTable.nomDeColonne
     * lorsque deux tables possèdent le même nom de colonne.
     */
    public static final String DB_COL_S_ID = DB_TABLE_S + "." + DB_COL_ID;

    /**
     * Nom de colonne sur laquelle le tri est effectué
     */
    public static String order_by = DB_COL_DATE;
    /**
     * Ordre de tri : ASC pour croissant et DESC pour décroissant
     */
    public static String order = "DESC";

    /**
     * ID unique de notre élément courant. Correspond à ci_id dans la base de données.
     */
    private final int id;

    /**
     * Nom du barman chargé de la commande
     */
    private Barman barman;

    /**
     * Montant de la commande
     */
    private float amountOrder;

    /**
     * Client ayant commandé
     */
    private User client;

    /**
     * Numéro de la table ayant commandé
     */
    private int table;

    /**
     * Date de la commande
     */
    private Date date;

    /**
     * Détails de la commande
     */
    private ArrayList<Detail> detailsOrdered;

    private boolean isSent;

    private boolean isServed ;

    /**
     * La commande a-t-elle été payée ?
     */
    private boolean isPaid;

    /**
     * Constructeur de notre élément de collection. Initialise une instance de l'élément présent
     * dans la base de données.
     *
     * @note Ce constructeur est privé (donc utilisable uniquement depuis cette classe). Cela permet
     * d'éviter d'avoir deux instances différentes d'un même élément dans la base de données, nous
     * utiliserons la méthode statique get(sId) pour obtenir une instance d'un élément de notre
     * collection.
     */
    public Order(int oId) {

        this.id = oId;

        // On enregistre l'instance de l'élément de collection courant dans la hashMap.
        //Order.orderSparseArray.put(oId, this);

        // On charge les données depuis la base de données.
        loadData();
    }


    /**
     * Fournit l'id de l'élément de collection courant.
     */
    public int getId() {
        return id;
    }

    /**
     * Fournit le client ayant commandé
     */
    public User getClient() {
        return client;
    }

    public String getClientName() { return client.getName() ; }

    /**
     * Fournit le barman responsable de la commande
     */
    public Barman getBarman() {
        return barman;
    }

    /**
     * Fournit le montant de la commande
     */
    public float getAmount() { return amountOrder; }

    /**
     * Fournit la date de la commande
     */
    public Date getDate() { return date; }

    /**
     * Fournit la table ayant passé la commande
     */
    public int getTable() { return table; }

    public boolean get_isSent() { return isSent ; }

    public boolean get_isServed() { return isServed ; }

    /**
     * La commande est-elle payée ?
     */
    public boolean get_isPaid() { return isPaid; }

    public void setCurrentOrder() { Order.currentOrder = this ; }


    public String toString() {
        return getClient() + " - " + getBarman() ;
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
            String[] columns = new String[]{DB_COL_UID, DB_COL_BID, DB_COL_AMOUNT, DB_COL_DATE, DB_COL_ISPAID, DB_COL_TABLE, DB_COL_ISSENT, DB_COL_ISSERVED};

            // Critères de sélection de la ligne :
            String selection = DB_COL_ID + " = ? ";
            String[] selectionArgs = new String[]{String.valueOf(id)};

            // Requête SELECT à la base de données.
            Cursor c = db.query(DB_TABLE_S, columns, selection, selectionArgs, null, null, null);

            // Placement du curseur sur le  premier résultat (ici le seul puisque l'objet est unique).
            c.moveToFirst();

            // Copie des données de la ligne vers les variables d'instance de l'objet courant.
            this.client = new User(c.getInt(0));
            this.barman = new Barman(c.getInt(1));
            this.amountOrder = c.getFloat(2);
            this.date = new Date();
            if(c.getInt(4) == 0){
                this.isPaid = false;
            }
            else {
                this.isPaid = true;
            }
            this.table = c.getInt(5);
            if(c.getInt(6) == 0){
                this.isSent = false;
            } else {
                this.isSent = true ;
            }
            if(c.getInt(7) == 0){
                this.isServed = false ;
            }
            else {
                this.isServed = true ;
            }

            // Fermeture du curseur et de la base de données.
            c.close();
            db.close();
        }
        catch(Exception SQLiteException){
            System.out.println("ERROOOOOOOR");
            //return false;
        }
    }

    public static String[] addTableAndValidate(int table){

        if(currentOrder==null){
            return null;
        }
        float amount = Detail.getTotalAmount(currentOrder.getId());

        try {
            SQLiteDatabase db = MySQLiteHelper.get().getWritableDatabase();

            // Critères de sélection de la ligne :
            String selection = DB_COL_ID + " = ? " ;
            String[] selectionArgs = new String[]{String.valueOf(currentOrder.getId())};

            User u = currentOrder.getClient();

            ContentValues args = new ContentValues();
            args.put(DB_COL_TABLE, table);
            args.put(DB_COL_ISSENT, 1);
            args.put(DB_COL_AMOUNT, amount);
            if(db.update(DB_TABLE_S, args, selection, selectionArgs) > 0){
                int promo = getPromos();
                currentOrder = null ;
                if((promo % 20) == 0 && u.getStatus().compareTo("client") == 0) {
                    float am = ( amount / 5 ) * 4 ;
                    return new String[]{String.valueOf(table), "barman", String.valueOf(am), String.valueOf(1)};
                }
                else {
                    return new String[]{String.valueOf(table), "barman", String.valueOf(amount), String.valueOf(0)};
                }
            } else {
                System.out.println("NOT UPDATE");
                return new String[]{null, null, null} ;
            }

        }
        catch(SQLiteException e){
            System.out.println("ERROR FROM ADDTABLEANDVALIDATE.");
            System.out.println(e.getMessage());
            return new String[]{ "null", "null", "null"} ;
        }
    }

    private static int getPromos() {

        try {
            // Récupération de la base de données en mode "lecture".
            SQLiteDatabase db = MySQLiteHelper.get().getReadableDatabase();

            // Colonnes pour lesquelles il nous faut les données.
            String[] columns = new String[]{DB_COL_ID};

            User u = getCurrentOrder().getClient();
            int uId = u.getId();

            // Critères de sélection de la ligne :
            String selection = DB_COL_UID + " = ? ";
            String[] selectionArgs = new String[]{String.valueOf(uId)};

            // Requête SELECT à la base de données.
            Cursor c = db.query(DB_TABLE_S, columns, selection, selectionArgs, null, null, null);

            // Placement du curseur sur le  premier résultat (ici le seul puisque l'objet est unique).
            int count = c.getCount();
            return count ;
        }
        catch(SQLiteException e){
            System.out.println("ERROR FROM GETPROMOS.");
            System.out.println(e.getMessage());
            return 0 ;
        }
    }


    /******************************************************************************
     * Partie static de la classe.
     ******************************************************************************/

    /**
     * Contient les instances déjà existantes des objets afin d'éviter de créer deux instances du
     * même objet.
     */
    private static final SparseArray<Order> orderSparseArray = new SparseArray<Order>();

    /**
     * Utilisateur actuellement connecté à l'application. Correspond à null si aucun utilisateur
     * n'est connecté.
     */
    private static Order currentOrder = null;

    public static Order getCurrentOrder() { return currentOrder; }


    /**
     * Crée un nouvel élément dans la base de données et l'associe à l'utilisateur actuellement
     * connecté.
     *
     * @return Vrai (true) en cas de succès, faux (false) en cas d'échec.
     * @post Enregistre le nouvel objet dans la base de données.
     */
    public static boolean create(User user, Barman barman, int table) {

        // Récupération de la base de données.
        SQLiteDatabase db = MySQLiteHelper.get().getWritableDatabase();
        Date date = new Date();

        // Définition des valeurs pour le nouvel élément dans la table "orders".
        ContentValues cv = new ContentValues();
        cv.put(DB_COL_UID, user.getId());
        cv.put(DB_COL_BID, barman.getId());
        cv.put(DB_COL_TABLE, table);
        cv.put(DB_COL_AMOUNT, 0);
        cv.put(DB_COL_DATE, date.toString());
        cv.put(DB_COL_ISSENT, 0);
        cv.put(DB_COL_ISSERVED, 0);
        cv.put(DB_COL_ISPAID, 0);


        // Ajout à la base de données (table collected_items).
        int s_id = (int) db.insert(DB_TABLE_S, null, cv);

        if (s_id == -1) {
            return false ; // En cas d'erreur d'ajout, on retourne false directement.
        }

        Order order = new Order(s_id);
        order.setCurrentOrder();
        cv.clear();

        return true;
    }

    /**
     * Crée un nouvel élément dans la base de données et l'associe à l'utilisateur actuellement
     * connecté.
     *
     * @return Vrai (true) en cas de succès, faux (false) en cas d'échec.
     * @post Enregistre le nouvel objet dans la base de données.
     */
    public static boolean create(User user) {

        // Récupération de la base de données.
        SQLiteDatabase db = MySQLiteHelper.get().getWritableDatabase();
        Date date = new Date();

        // Définition des valeurs pour le nouvel élément dans la table "orders".
        ContentValues cv = new ContentValues();

        if(user.getStatus().compareTo("barman") == 0){
            cv.put(DB_COL_BID, user.getId());
            cv.put(DB_COL_UID, 5);
        } else {
            cv.put(DB_COL_UID, user.getId());
            cv.put(DB_COL_BID, 1);
        }

        cv.put(DB_COL_TABLE, 0);
        cv.put(DB_COL_AMOUNT, 0);
        cv.put(DB_COL_DATE, date.toString());
        cv.put(DB_COL_ISSENT, 0);
        cv.put(DB_COL_ISPAID, 0);
        cv.put(DB_COL_ISSERVED, 0);


        // Ajout à la base de données (table collected_items).
        int s_id = (int) db.insert(DB_TABLE_S, null, cv);

        if (s_id == -1) {
            return false ; // En cas d'erreur d'ajout, on retourne false directement.
        }

        Order order = new Order(s_id);
        order.setCurrentOrder();
        cv.clear();

        return true;
    }

    public static boolean order_isServed(int oId) {

        try {
            SQLiteDatabase db = MySQLiteHelper.get().getWritableDatabase();

            // Critères de sélection de la ligne :
            String selection = DB_COL_ID + " = ? " ;
            String[] selectionArgs = new String[]{String.valueOf(oId)};

            ContentValues args = new ContentValues();
            args.put(DB_COL_ISSERVED, 1);
            if(db.update(DB_TABLE_S, args, selection, selectionArgs) > 0){
                System.out.println("UPDATE");
                return true ;
            } else {
                System.out.println("NOT UPDATE");
                return false ;
            }

        }
        catch(SQLiteException e){
            System.out.println("ERROR FROM ORDER_ISSERVED.");
            System.out.println(e.getMessage());
            return false ;
        }

    }

    public static boolean order_isPaid(int oId) {

        try {
            SQLiteDatabase db = MySQLiteHelper.get().getWritableDatabase();

            // Critères de sélection de la ligne :
            String selection = DB_COL_ID + " = ? " ;
            String[] selectionArgs = new String[]{String.valueOf(oId)};

            ContentValues args = new ContentValues();
            args.put(DB_COL_ISPAID, 1);
            if(db.update(DB_TABLE_S, args, selection, selectionArgs) > 0){
                System.out.println("UPDATE");
                return true ;
            } else {
                System.out.println("NOT UPDATE");
                return false ;
            }

        }
        catch(SQLiteException e){
            System.out.println("ERROR FROM ORDER_ISPAID.");
            System.out.println(e.getMessage());
            return false ;
        }

    }

    public static boolean order_isCancelled(int oId){

        try {
            SQLiteDatabase db = MySQLiteHelper.get().getWritableDatabase();

            String[] colonnes = {DB_COL_ID};

            // Critères de sélection de la ligne :
            String selection = DB_COL_ID + " = ? " ;
            String[] selectionArgs = new String[]{String.valueOf(oId)};

            String[] colonnes_2 = { "o_id" };
            String selection_2 = "o_id = ? ";
            String[] selectionArgs_2 = new String[]{String.valueOf(oId)};

            int d = db.delete(DB_TABLE_S, selection, selectionArgs);
            int d_2 = db.delete("details", selection_2, selectionArgs_2);

            if(d > 0 && d_2 > 0){
                System.out.println("DELETING " + d + " ROWS IN CMD " + oId + "; " + d_2 + " ROWS IN DETAILS");
                return true ;
            } else {
                System.out.println("NOT UPDATE");
                return false ;
            }

        }
        catch(SQLiteException e){
            System.out.println("ERROR FROM ORDER_ISPAID.");
            System.out.println(e.getMessage());
            return false ;
        }

    }

    /**
     * Fournit la liste de tous les objets correspondant aux critères de sélection demandés.
     *
     * Cette méthode est une sous-méthode de getOrders et de searchSongs.
     *
     * @param selection     Un filtre déclarant quels éléments retourner, formaté comme la clause
     *                      SQL WHERE (excluant le WHERE lui-même). Donner null retournera tous les
     *                      éléments.
     * @param selectionArgs Vous pouvez inclure des ? dans selection, qui seront remplacés par les
     *                      valeurs de selectionArgs, dans leur ordre d'apparition dans selection.
     *                      Les valeurs seront liées en tant que chaînes.
     *
     * @return Liste d'objets. La liste peut être vide si aucun objet ne correspond.
     */
    private static ArrayList<Order> getOrders(String selection, String[] selectionArgs) {
        // Initialisation de la liste des orders.
        ArrayList<Order> orders = new ArrayList<Order>();
        try {
            // Récupération du SQLiteHelper pour récupérer la base de données.
            SQLiteDatabase db = MySQLiteHelper.get().getWritableDatabase();

            // Colonnes à récupérer. Ici uniquement l'id de l'élément, le reste sera récupéré par
            // loadData() à la création de l'instance de l'élément. (choix de développement).
            String[] columns = new String[]{DB_COL_S_ID};

            // Requête SELECT à la base de données.
            Cursor c = db.query(DB_TABLE_S, columns, selection, selectionArgs, null, null, Order.order_by + " " + Order.order);

            c.moveToFirst();
            while (!c.isAfterLast()) {
                // Id de l'élément.
                int sId = c.getInt(0);
                // L'instance de l'élément de collection est récupéré avec la méthode get(sId)
                // (Si l'instance n'existe pas encore, elle est créée par la méthode get)
                Order order = Order.get(sId);

                // Ajout de l'élément de collection à la liste.
                orders.add(order);

                c.moveToNext();
            }

            // Fermeture du curseur et de la base de données.
            c.close();
            db.close();
        }
        catch(Exception SQLiteException){
            System.out.println("ERROOOOOOOR");
        }
        return orders ;
    }

    public static ArrayList<Order> getToPrepareOrders() {
        // Initialisation de la liste des orders.
        ArrayList<Order> orders = new ArrayList<Order>();

        // Récupération du SQLiteHelper pour récupérer la base de données.
        SQLiteDatabase db = MySQLiteHelper.get().getWritableDatabase();
        System.out.println("LOCKED (TO PREPARE) ? " + db.isDbLockedByCurrentThread());

        // Colonnes à récupérer. Ici uniquement l'id de l'élément, le reste sera récupéré par
        // loadData() à la création de l'instance de l'élément. (choix de développement).
        String[] columns = new String[]{DB_COL_S_ID};

        // Critères de sélection de la ligne :
        String selection = DB_COL_ISSENT + " = ? AND " + DB_COL_ISPAID + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(1), String.valueOf(0)};

        // Requête SELECT à la base de données.
        Cursor c = db.query(DB_TABLE_S, columns, selection, selectionArgs, null, null, Order.order_by + " " + Order.order);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            // Id de l'élément.
            int sId = c.getInt(0);
            // L'instance de l'élément de collection est récupéré avec la méthode get(sId)
            // (Si l'instance n'existe pas encore, elle est créée par la méthode get)
            Order order = new Order(sId);

            // Ajout de l'élément de collection à la liste.
            orders.add(order);

            c.moveToNext();
        }

        // Fermeture du curseur et de la base de données.
        db.close();
        return orders;
    }


    /**
     * Fournit l'instance d'un élément de collection présent dans la base de données. Si l'élément
     * de collection n'est pas encore instancié, une instance est créée.
     *
     * @param sId Id de l'élément de collection.
     *
     * @return L'instance de l'élément de collection.
     * @pre L'élément correspondant à l'id donné doit exister dans la base de données.
     */
    public static Order get(int sId) {
        Order ci = Order.orderSparseArray.get(sId);
        if (ci != null) {
            return ci;
        }
        return new Order(sId);
    }


    /**
     * Inverse l'ordre de tri actuel.
     *
     * @pre La valeur de Order.order est soit ASC soit DESC.
     * @post La valeur de Order.order a été modifiée et est soit ASC soit DESC.
     */
    public static void reverseOrder() {
        if (Order.order.equals("ASC")) {
            Order.order = "DESC";
        } else {
            Order.order = "ASC";
        }
    }

}
