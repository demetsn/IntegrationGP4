/**
 * Created by Olivier on 29-09-15.
 */

package ephec.noticeme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

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

    private static final String REQUETE_CREATION_ALARM = "create table "
            + TABLE_ALARMS + " ( "
            + COlUMN_ID + " integer primary key not null,"
            + COLUMN_GROUP_ID + " integer , "
            + COLUMN_MODIF_DATE + " datetime, "
            + COLUMN_TITLE + " String not null, "
            + COLUMN_DESCRIPTION + " , "
            + COLUMN_LATITUDE + " , "
            + COLUMN_LONGITUDE + " , "
            + COLUMN_ALARM_DATE + " datetime, "
            + " ); ";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(REQUETE_CREATION_ALARM);
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
        alarmValue.put(COLUMN_MODIF_DATE, (alarm.getModificationDate()));
        alarmValue.put(COLUMN_TITLE, alarm.getTitle());
        alarmValue.put(COLUMN_DESCRIPTION, alarm.getDescription());
        alarmValue.put(COLUMN_LATITUDE, alarm.getLatitude());
        alarmValue.put(COLUMN_LONGITUDE, alarm.getLongitude());
        alarmValue.put(COLUMN_ALARM_DATE, (alarm.getAlarmDate()));


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

            alarm.setId((cursor.getInt(cursor.getColumnIndex(COlUMN_ID))));
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


    public boolean deleteAlarm(int id) {

        boolean result = false;

        String query = "Select * FROM " + TABLE_ALARMS + " WHERE " + COlUMN_ID + " =  \"" + id + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Alarm alarm = new Alarm();

        if (cursor.moveToFirst()) {
            alarm.setId((cursor.getInt(cursor.getColumnIndex(COlUMN_ID))));
            db.delete(TABLE_ALARMS, COlUMN_ID + " = ?",
                    new String[]{String.valueOf(alarm.getId())});
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }


    public boolean modifyAlarm(String name, Alarm alarm) {

        boolean result = false;

        ContentValues values = new ContentValues();

        values.put(COlUMN_ID, alarm.getId());
        values.put(COLUMN_GROUP_ID, alarm.getGroupId());
        values.put(COLUMN_MODIF_DATE, alarm.getModificationDate());
        values.put(COLUMN_TITLE, alarm.getTitle());
        values.put(COLUMN_DESCRIPTION, alarm.getDescription());
        values.put(COLUMN_LATITUDE, alarm.getLatitude());
        values.put(COLUMN_LONGITUDE, alarm.getLongitude());
        values.put(COLUMN_ALARM_DATE, alarm.getAlarmDate());

        SQLiteDatabase db = this.getWritableDatabase();

        db.update(TABLE_ALARMS, values, COlUMN_ID + "=" + alarm.getId(), null);

        result = true;
        return result;
    }

    public ArrayList<Alarm> getAllTitles(String select, String search){

        ArrayList<Alarm> alarms = new ArrayList<Alarm>();

        String query = "";

        switch (select) {

            case COlUMN_ID : query = query + "SELECT * FROM " + TABLE_ALARMS + " WHERE " + COlUMN_ID + " = \"" + search + "\"";
                break;
            case COLUMN_GROUP_ID : query = query + "SELECT * FROM " + TABLE_ALARMS + " WHERE " + COLUMN_GROUP_ID + " = \"" + search + "\"";
                break;
            case COLUMN_MODIF_DATE : query = query + "SELECT * FROM " + TABLE_ALARMS + " WHERE " + COLUMN_MODIF_DATE + " = \"" + search + "\"";
                break;
            case COLUMN_TITLE : query = query + "SELECT * FROM " + TABLE_ALARMS + " WHERE " + COLUMN_TITLE + " = \"" + search + "\"";
                break;
            case COLUMN_DESCRIPTION : query = query + "SELECT * FROM " + TABLE_ALARMS + " WHERE " + COLUMN_DESCRIPTION + " = \"" + search + "\"";
                break;
            case COLUMN_LATITUDE : query = query + "SELECT * FROM " + TABLE_ALARMS + " WHERE " + COLUMN_LATITUDE + " = \"" + search + "\"";
                break;
            case COLUMN_LONGITUDE : query = query + "SELECT * FROM " + TABLE_ALARMS + " WHERE " + COLUMN_LONGITUDE + " = \"" + search + "\"";
                break;
            case COLUMN_ALARM_DATE : query = query + "SELECT * FROM " + TABLE_ALARMS + " WHERE " + COLUMN_ALARM_DATE + " = \"" + search + "\"";
                break;

        }

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            while (cursor.isAfterLast() == false) {
                Alarm alarm = new Alarm();

                alarm.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COlUMN_ID))));
                alarm.setGroupId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_GROUP_ID))));
                alarm.setModificationDate(cursor.getString(cursor.getColumnIndex(COLUMN_MODIF_DATE)));
                alarm.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                alarm.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                alarm.setLatitude(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_LATITUDE))));
                alarm.setLongitude(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_LONGITUDE))));
                alarm.setAlarmDate(cursor.getString(cursor.getColumnIndex(COLUMN_ALARM_DATE)));

                cursor.moveToNext();

                alarms.add(alarm);
            }
            cursor.close();
        }

        db.close();

        return alarms;
    }
}