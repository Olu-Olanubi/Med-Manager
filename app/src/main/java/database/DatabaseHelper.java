package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Drugs_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create drugs table
        db.execSQL(Drugs.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Drugs.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertDrug(String drugName, String description /**, int interval, String startDate, String endDate*/){
// get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Drugs.COLUMN_NAME, drugName);
        values.put(Drugs.COLUMN_DESC, description);
        //values.put(Drugs.COLUMN_INTERVAL, interval);
         /**
        values.put(Drugs.COLUMN_START_DATE, startDate);
        values.put(Drugs.COLUMN_END_DATE, endDate);
        */
        // insert row
        long id = db.insert(Drugs.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Drugs getDrugs(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Drugs.TABLE_NAME,
                new String[]{Drugs.COLUMN_ID, Drugs.COLUMN_NAME, Drugs.COLUMN_DESC /**, Drugs.COLUMN_INTERVAL, Drugs.COLUMN_START_DATE, Drugs.COLUMN_END_DATE*/},
                Drugs.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare drug object
        Drugs drugs = new Drugs(
                cursor.getInt(cursor.getColumnIndex(Drugs.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_DESC))
                /**,
                cursor.getInt(cursor.getColumnIndex(Drugs.COLUMN_INTERVAL))

                cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_START_DATE)),
                cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_END_DATE))*/);

        // close the db connection
        cursor.close();

        return drugs;
    }

    public List<Drugs> getAllDrugs() {
        List<Drugs> drugs = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Drugs.TABLE_NAME + " ORDER BY " +
                Drugs.COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Drugs drug = new Drugs();
                drug.setId(cursor.getInt(cursor.getColumnIndex(Drugs.COLUMN_ID)));
                drug.setDrugName(cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_NAME)));
                drug.setDescription(cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_DESC)));
               //drug.setInterval(cursor.getInt(cursor.getColumnIndex(Drugs.COLUMN_INTERVAL)));
               // drug.setStartDate(cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_START_DATE)));
               // drug.setEndDate(cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_END_DATE)));

                drugs.add(drug);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return drugs;
    }

    public int getDrugsCount() {
        String countQuery = "SELECT  * FROM " + Drugs.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    /**UPDATING DATA*/

    public int updateDrug(Drugs drug) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Drugs.COLUMN_NAME, drug.getDrugName());
        values.put(Drugs.COLUMN_DESC, drug.getDescription());
        //values.put(Drugs.COLUMN_INTERVAL, drug.getInterval());
       // values.put(Drugs.COLUMN_START_DATE, drug.getStartDate());
       // values.put(Drugs.COLUMN_END_DATE, drug.getEndDate());

        // updating row
        return db.update(Drugs.TABLE_NAME, values, Drugs.COLUMN_ID + " = ?",
                new String[]{String.valueOf(drug.getId())});
    }

    /**DELETING DATA*/
    public void deleteDrugs(Drugs drug) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Drugs.TABLE_NAME, Drugs.COLUMN_ID + " = ?",
                new String[]{String.valueOf(drug.getId())});
        db.close();
    }
}
