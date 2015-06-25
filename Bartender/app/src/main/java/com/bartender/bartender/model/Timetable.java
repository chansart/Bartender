package com.bartender.bartender.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.bartender.bartender.database.MySQLiteHelper;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by charlotte on 6/05/15.
 */
public class Timetable {

    String LOCALE_FRANCAIS = "fr";
    String LOCALE_ENGLISH = "en";
    Locale mLocale;

    private static final String DB_COL_ID = "t_id";
    private static final String DB_COL_BID = "b_id";
    private static final String DB_COL_DAY = "t_day";
    private static final String DB_COL_BEGIN = "t_begin";
    private static final String DB_COL_END = "t_end";

    private static final String DB_TABLE = "timetables";

    private int tId ;
    private int bId ;
    private String day ;
    private String begin = null ;
    private String end = null ;

    public int gettId() {
        return tId;
    }

    public void settId(int tId) {
        this.tId = tId;
    }

    public int getbId() {
        return bId;
    }

    public void setbId(int bId) {
        this.bId = bId;
    }

    public String getDay() {
        return day ;
    }

    public void setDay(int day) {
        mLocale=Locale.getDefault();
        if(mLocale.getLanguage().compareTo(LOCALE_ENGLISH)==0) {
            if (day == 1) {
                this.day = "Monday";
            } else if (day == 2) {
                this.day = "Tuesday";
            } else if (day == 3) {
                this.day = "Wednesday";
            } else if (day == 4) {
                this.day = "Thursday";
            } else if (day == 5) {
                this.day = "Friday";
            } else if (day == 6) {
                this.day = "Saturday";
            } else {
                this.day = "Sunday";
            }

        } else {
            if (day == 1) {
                this.day = "Lundi";
            } else if (day == 2) {
                this.day = "Mardi";
            } else if (day == 3) {
                this.day = "Mercredi";
            } else if (day == 4) {
                this.day = "Jeudi";
            } else if (day == 5) {
                this.day = "Vendredi";
            } else if (day == 6) {
                this.day = "Samedi";
            } else {
                this.day = "Dimanche";
            }
        }
    }

    public String getBegin() {
        return begin ;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end ;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Timetable(int tId){
        this.tId = tId ;
        loadData();
    }

    public Timetable(int bId, int day, String begin, String end){
        this.bId = bId ;
        this.setDay(day);
        this.begin = begin ;
        this.end = end ;
    }

    public static boolean create(int bId, String day, Time begin, Time end) {

        // Récupération de la base de données.
        try {
            SQLiteDatabase db = MySQLiteHelper.get().getWritableDatabase();
            System.out.println(db);

            // Définition des valeurs pour le nouvel élément dans la table "collected_items".
            ContentValues cv = new ContentValues();
            cv.put(DB_COL_BID, bId);
            cv.put(DB_COL_DAY, day);
            cv.put(DB_COL_BEGIN, begin.toString());
            cv.put(DB_COL_END, end.toString());


            // Ajout à la base de données (table users).
            int t_id = (int) db.insert(DB_TABLE, null, cv);

            if (t_id == -1) {
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

    public static Timetable create(int day, String begin, String end) {

        // Récupération de la base de données.
        try {
            SQLiteDatabase db = MySQLiteHelper.get().getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(DB_COL_BID, User.getCurrentBarman().getId());
            cv.put(DB_COL_DAY, day);
            cv.put(DB_COL_BEGIN, begin);
            cv.put(DB_COL_END, end);


            // Ajout à la base de données (table users).
            int t_id = (int) db.insert(DB_TABLE, null, cv);
            if(t_id != -1){
                Timetable t = new Timetable(t_id);
                return t ;
            }
            return null ;

        }
        catch(Exception SQLiteException){
            System.out.println("ERROR");
            return null ;
        }
    }


    public static ArrayList<Timetable> getSchedules(int bId) {

        ArrayList<Timetable> s = new ArrayList<Timetable>();

        try {
            SQLiteDatabase db = MySQLiteHelper.get().getReadableDatabase() ;

            // Colonnes à récupérer
            String[] colonnes = {DB_COL_DAY, DB_COL_BEGIN, DB_COL_END};

            // Critères de sélection de la ligne :
            String selection = DB_COL_BID + " = ? " ;
            String[] selectionArgs = new String[]{String.valueOf(bId)};

            // Requête de selection (SELECT)
            Cursor cursor = db.query(DB_TABLE, colonnes, selection, selectionArgs, null, null, null);

            // Placement du curseur sur la première ligne.
            cursor.moveToFirst();

            // Tant qu'il y a des lignes.
            while (!cursor.isAfterLast()) {
                // Récupération des informations de l'utilisateur pour chaque ligne.
                int day = cursor.getInt(0);
                String begin = cursor.getString(1);
                String end = cursor.getString(2);


                Timetable t = new Timetable(bId, day, begin, end);

                // Ajout de l'utilisateur à la liste.
                s.add(t);

                // Passe à la ligne suivante.
                cursor.moveToNext();
            }

            // Fermeture du curseur et de la base de données.
            cursor.close();
            db.close();
        }
        catch(Exception SQLiteException){
            Log.e("Error", "while getting schedules", SQLiteException);
        }

        return s ;
    }

    private void loadData() {

        try {
            // Récupération de la base de données en mode "lecture".
            SQLiteDatabase db = MySQLiteHelper.get().getReadableDatabase();

            // Colonnes pour lesquelles il nous faut les données.
            String[] columns = new String[]{DB_COL_BID, DB_COL_DAY, DB_COL_BEGIN, DB_COL_END};

            // Critères de sélection de la ligne :
            String selection = DB_COL_ID + " = ? ";
            String[] selectionArgs = new String[]{String.valueOf(tId)};

            // Requête SELECT à la base de données.
            Cursor c = db.query(DB_TABLE, columns, selection, selectionArgs, null, null, null);

            // Placement du curseur sur le  premier résultat (ici le seul puisque l'objet est unique).
            c.moveToFirst();

            // Copie des données de la ligne vers les variables d'instance de l'objet courant.
            this.setbId(c.getInt(0));
            this.setDay(c.getInt(1));
            this.setBegin(c.getString(2));
            this.setEnd(c.getString(3));

        }
        catch(Exception SQLiteException){
            System.out.println("ERREUR IN LOADDATA");
        }
    }
}