package ephec.noticeme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Olivier on 29-09-15.
 */

public class DBHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "noticeMe.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_ALARMS = "alarms";

    private static final String COlUMN_ID = "id";
    private static final String COLUMN_GROUP_ID = "groupeId";
    private static final String COLUMN_MODIF_DATE = "modificationDate";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_ALARM_DATE = "alarmDate";

    private static final String REQUETE_CREATION_PLACE = "create table "
            + TABLE_ALARMS + " ( "
            + COlUMN_ID + " integer primary key not null,"
            + COLUMN_GROUP_ID + " integer , "
            + COLUMN_MODIF_DATE + " datetime, "
            + COLUMN_TITLE + " String not null, "
            + COLUMN_DESCRIPTION + " , "
            + COLUMN_LATITUDE + " , "
            + COLUMN_LONGITUDE + " , "
            + COLUMN_ALARM_DATE
            + " ); ";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(REQUETE_CREATION_PLACE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
        onCreate(db);
    }


    public boolean addAlarm(Alarm alarm) {

        boolean result = false;

        //Adding the values to TABLE_ALARMS
        ContentValues alarmValue = new ContentValues();

        alarmValue.put(COlUMN_ID, alarm.getId());
        alarmValue.put(COLUMN_GROUP_ID, alarm.getGroupId());
        alarmValue.put(COLUMN_MODIF_DATE, alarm.getModificationDate());
        alarmValue.put(COLUMN_TITLE, alarm.getTitle());
        alarmValue.put(COLUMN_DESCRIPTION, alarm.getDescription());
        alarmValue.put(COLUMN_LATITUDE, alarm.getLatitude());
        alarmValue.put(COLUMN_LONGITUDE, alarm.getLongitude());
        alarmValue.put(COLUMN_ALARM_DATE, alarm.getAlarmDate());


        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_ALARMS, null, alarmValue);

        db.close();

        result = true;

        return result;
    }

    public Alarm getAlarm(int id){

        String query = "Select * FROM " + TABLE_ALARMS + " WHERE " + COlUMN_ID + " =  \"" + id + "\"";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Alarm alarm = new Alarm();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            alarm.setID((cursor.getInt(cursor.getColumnIndex(COlUMN_ID))));
            alarm.setGroupId((cursor.getInt(cursor.getColumnIndex(COLUMN_GROUP_ID))));
            alarm.setModificationDate((cursor.getString(cursor.getColumnIndex(COLUMN_MODIF_DATE))));
            alarm.setTitle((cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))));
            alarm.setDescription((cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))));
            alarm.setLatitude(Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_LATITUDE))));
            alarm.setLongitude(Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_LONGITUDE))));
            alarm.setAlarmDate((cursor.getString(cursor.getColumnIndex(COLUMN_ALARM_DATE))));

            cursor.close();
        } else {
            alarm = null;
        }
        db.close();
        return alarm;
    }

    public boolean deletePlace(String name) {

        boolean result = false;

        String query = "Select * FROM " + TABLE_PLACE + " WHERE " + COLUMN_NAME + " =  \"" + name + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Place place = new Place();

        if (cursor.moveToFirst()) {
            place.setID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COlUMN_ID))));
            db.delete(TABLE_PLACE, COlUMN_ID + " = ?",
                    new String[]{String.valueOf(place.getID())});
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public boolean modifyPlace(String name, Place place) {

        boolean result = false;

        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, place.getName());
        values.put(COLUMN_DESCRIPTION, place.getDescription());
        values.put(COLUMN_CITY, place.getCity());
        values.put(COLUMN_CATEGORY, place.getCategory());
        values.put(COLUMN_ADDRESS, place.getAddress());
        values.put(COLUMN_PHONE, place.getPhone());
        values.put(COLUMN_LATITUDE, place.getLatitude());
        values.put(COLUMN_LONGITUDE, place.getLongitude());

        SQLiteDatabase db = this.getWritableDatabase();

        db.update(TABLE_PLACE, values, COlUMN_ID + "=" + place.getID(), null);

        result = true;
        return result;
    }

    public ArrayList<Place> getAllNames(String select, String search){

        ArrayList<Place> places = new ArrayList<Place>();

        String query = "";

        switch (select) {
            case COLUMN_NAME : query = query + "SELECT * FROM " + TABLE_PLACE ;
                break;
            case COLUMN_CATEGORY : query = query + "SELECT * FROM " + TABLE_PLACE + " WHERE " + COLUMN_CATEGORY + " = \"" + search + "\"";
                break;
            case COLUMN_CITY : query = query + "SELECT * FROM " + TABLE_PLACE + " WHERE " + COLUMN_CITY + " = \"" + search + "\"";
                break;
            case COLUMN_DESCRIPTION : query = query + "SELECT * FROM " + TABLE_PLACE + " WHERE " + COLUMN_DESCRIPTION + " = \"%" + search + "%\"";
                break;
            case COLUMN_ADDRESS : query = query + "SELECT * FROM " + TABLE_PLACE + " WHERE " + COLUMN_ADDRESS + " = \"%" + search + "%\"";
                break;
        }

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            while (cursor.isAfterLast() == false) {
                Place place = new Place();

                place.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                place.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                place.setCity(cursor.getString(cursor.getColumnIndex(COLUMN_CITY)));
                place.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)));
                place.setAddress(cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)));
                place.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
                place.setLatitude(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_LATITUDE))));
                place.setLongitude(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_LONGITUDE))));

                cursor.moveToNext();

                places.add(place);
            }
            cursor.close();
        }

        db.close();

        return places;
    }
}