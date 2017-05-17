package com.example.wesley.dtime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Ryan on 4/25/17.
 */

public class OperateSQLForPrevLocations extends SQLiteOpenHelper implements Runnable {
    @Override
    public void run()
    {
        if (isUpdate)
        {
            addAddress(address, coordinates);
        }
        else
        {
            //This entirely depends upon how the address will be chosen and then
        }
    }

    //Database Version
    private static final int DATABASE_VERSION = 2;

    //Database Name
    private static final String DATABASE_NAME = "addressManager";

    //Table name for the Contacts
    private static final String TABLE_CONTACTS = "addresses";

    //Contacts Table Column names
    private static final String KEY_ID = "id";
    private static final String KEY_ADDRESS = "Addresses";
    private static final String KEY_COORDINATES = "Coordinates";

    private boolean isUpdate = true;
    private String address = "";
    private String coordinates = "";

    public OperateSQLForPrevLocations(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //For option, pick 0 for updating table and 1 for taking item from table
        isUpdate = false;
    }
    public OperateSQLForPrevLocations(Context context, String address, String coordinates) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //For option, pick 0 for updating table and 1 for taking item from table
        this.address = address;
        this.coordinates = coordinates;
        isUpdate = true;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("starting creating database");
        String CREATE_ADDRESS_TABLE = "CREATE TABLE " + TABLE_CONTACTS
                + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_COORDINATES + " TEXT"
                + ")";

        db.execSQL(CREATE_ADDRESS_TABLE);
        System.out.println("finished");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        onCreate(db);

    }


//=======================   DATABASE CRUD FUNCTIONS    ===========================================================================

    //Creates a new Contact in the database
    public void addAddress(String address, String coordinates) {
        SQLiteDatabase db = getWritableDatabase();

        //ContentValue is list that holds key (column names) and values pairs
        ContentValues values = new ContentValues();
        values.put(KEY_ADDRESS, address);
        values.put(KEY_COORDINATES, coordinates);

        //INserts a Row in the database with the new Contact
        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }

    public void updateAddress(String address, String coordinates){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ADDRESS, address);
        values.put(KEY_COORDINATES, coordinates);

        db.update(TABLE_CONTACTS, values, KEY_ID + " = ? ", new String[]{String.valueOf(getId(address))});
        db.close();
    }

    public String getAddress(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {KEY_ID, KEY_ADDRESS, KEY_COORDINATES};

        Cursor cursor = db.query(TABLE_CONTACTS, columns, KEY_ID + " = ? ", new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
        {
            cursor.moveToFirst();
        }

        System.out.println(Integer.parseInt(cursor.getString(0)));
        System.out.println(cursor.getString(1));
        System.out.println(cursor.getString(2));
        //Contact contact = new Contact(cursor.getString(1),cursor.getString(2),Integer.parseInt(cursor.getString(0)));
        db.close();
        return cursor.getString(1);
    }
    public String getCoordinates(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {KEY_ID, KEY_ADDRESS, KEY_COORDINATES};

        Cursor cursor = db.query(TABLE_CONTACTS, columns, KEY_ID + " = ? ", new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
        {
            cursor.moveToFirst();
        }

        System.out.println(Integer.parseInt(cursor.getString(0)));
        System.out.println(cursor.getString(1));
        System.out.println(cursor.getString(2));
        //Contact contact = new Contact(cursor.getString(1),cursor.getString(2),Integer.parseInt(cursor.getString(0)));
        db.close();
        cursor.close();
        return cursor.getString(2);
    }
    public int getId(String address) throws NoSuchElementException
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "  + TABLE_CONTACTS;
        Cursor cursor = db.rawQuery(selectQuery, null);
        String foundAddress = "";
        int i = 0;

        if (cursor.moveToFirst())
        {
            do {
                foundAddress = cursor.getString(1);
                i = Integer.parseInt(cursor.getString(0));
            } while (cursor.moveToNext() && !foundAddress.equals(address));
        }
        db.close();
        cursor.close();
        if (foundAddress.equals(address))
        {
            throw new NoSuchElementException();
        }

        return i;
    }

    public List<String> getAllAddresses()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> contactList = new ArrayList<String>();

        String selectQuery = "SELECT * FROM "  + TABLE_CONTACTS;

        Cursor cursor = db.rawQuery(selectQuery, null);

        //loop through cursor, build contact, and add to the arrayList

        if (cursor.moveToFirst())
        {
            do {
                //Contact contact = new Contact();
                //contact.set_id(Integer.parseInt(cursor.getString(0)));
                //contact.set_name(cursor.getString(1));
                //contact.set_phone_number(cursor.getString(2));

                //Add contact to the ArrayLIst
                contactList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return contactList;
    }

    public void deleteAddress(int id){
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_CONTACTS, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        db.close();
    }
}
