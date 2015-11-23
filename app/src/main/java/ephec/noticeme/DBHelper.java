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

    //Table for the memos
    private static final String TABLE_ALARMS = "alarms";

    private static final String COlUMN_ID = "id";
    private static final String COLUMN_GROUP_ID = "groupeId";
    private static final String COLUMN_MODIF_DATE = "modificationDate";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_ALARM_DATE = "alarmDate";

    //Table for the users
    private static final String TABLE_USERS = "users";

    private static final String COlUMN_USER_ID = "userId";
    private static final String COLUMN_USER_GROUP_ID = "userGroupeId";
    private static final String COlUMN_USER_NAME = "name";
    private static final String COLUMN_USER_FIRSTNAME = "firstname";
    private static final String COlUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PWD = "pwd";
    private static final String COLUMN_ISCURRENT = "isCurrent";

    //Create the memos table
    private static final String REQUETE_CREATION_ALARM = "create table "
            + TABLE_ALARMS + " ( "
            + COlUMN_ID + " integer primary key not null,"
            + COLUMN_GROUP_ID + " integer , "
            + COLUMN_MODIF_DATE + " datetime, "
            + COLUMN_TITLE + " String not null, "
            + COLUMN_DESCRIPTION + " , "
            + COLUMN_LATITUDE + " , "
            + COLUMN_LONGITUDE + " , "
            + COLUMN_ALARM_DATE + " datetime ); ";

    private static final String REQUETE_CREATION_USERS = "create table "
            + TABLE_USERS + " ( "
            + COlUMN_USER_ID + " integer primary key not null, "
            + COLUMN_USER_GROUP_ID + " integer, "
            + COlUMN_USER_NAME + " , "
            + COLUMN_USER_FIRSTNAME + " , "
            + COlUMN_USER_EMAIL + " , "
            + COLUMN_USER_PWD +" , "
            + COLUMN_ISCURRENT + " integer ); ";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(REQUETE_CREATION_ALARM);
        db.execSQL(REQUETE_CREATION_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
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

    public boolean addUser(User user) {

        boolean result = false;

        ContentValues userValues = new ContentValues();

        userValues.put(COlUMN_USER_ID, user.getId());
        userValues.put(COLUMN_USER_GROUP_ID, user.getGroup());
        userValues.put(COlUMN_USER_NAME, user.getNom());
        userValues.put(COLUMN_USER_FIRSTNAME, user.getPrenom());
        userValues.put(COlUMN_USER_EMAIL, user.getMail());
        userValues.put(COLUMN_USER_PWD, user.getPassword());
        userValues.put(COLUMN_ISCURRENT, 1);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_USERS, null, userValues);

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
            alarm.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_LATITUDE))));
            alarm.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_LONGITUDE))));
            alarm.setAlarmDate((cursor.getString(cursor.getColumnIndex(COLUMN_ALARM_DATE))));

            cursor.close();
        } else {
            alarm = null;
        }
        db.close();
        return alarm;
    }

    public User getUser(int id)
    {
        String query = "Select * FROM " + TABLE_USERS + " WHERE " + COlUMN_USER_ID + " = \"" + id + "\"";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        User user = new User();

        if (cursor.moveToFirst()){
            cursor.moveToFirst();

            user.setId((cursor.getInt(cursor.getColumnIndex(COlUMN_USER_ID))));
            user.setGroup((cursor.getInt(cursor.getColumnIndex(COLUMN_USER_GROUP_ID))));
            user.setNom((cursor.getString(cursor.getColumnIndex(COlUMN_USER_NAME))));
            user.setPrenom((cursor.getString(cursor.getColumnIndex(COLUMN_USER_FIRSTNAME))));
            user.setMail((cursor.getString(cursor.getColumnIndex(COlUMN_USER_EMAIL))));
            user.setPassword((cursor.getString(cursor.getColumnIndex(COLUMN_USER_PWD))));

        } else {
            user = null;
        }
        db.close();
        return user;
    }

    public User getCurrentUSer()
    {
        String query = "Select * FROM " + TABLE_USERS + " WHERE " + COLUMN_ISCURRENT + " = \"1\" ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        User user = new User();

        if (cursor.moveToFirst()){
            cursor.moveToFirst();

            user.setId((cursor.getInt(cursor.getColumnIndex(COlUMN_USER_ID))));
            user.setGroup((cursor.getInt(cursor.getColumnIndex(COLUMN_USER_GROUP_ID))));
            user.setNom((cursor.getString(cursor.getColumnIndex(COlUMN_USER_NAME))));
            user.setPrenom((cursor.getString(cursor.getColumnIndex(COLUMN_USER_FIRSTNAME))));
            user.setMail((cursor.getString(cursor.getColumnIndex(COlUMN_USER_EMAIL))));
            user.setPassword((cursor.getString(cursor.getColumnIndex(COLUMN_USER_PWD))));

        } else {
            user = null;
        }
        db.close();
        return user;
    }

    public Alarm getAlarm(String title) {
        String query = "Select * FROM " + TABLE_ALARMS + " WHERE " + COLUMN_TITLE + " =  \"" + title + "\"";

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
            alarm.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_LATITUDE))));
            alarm.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_LONGITUDE))));
            alarm.setAlarmDate((cursor.getString(cursor.getColumnIndex(COLUMN_ALARM_DATE))));

            cursor.close();
        } else {
            alarm = null;
        }
        db.close();
        return alarm;
    }

    public ArrayList<Alarm> getAllAlarm(){

        String query = "Select * FROM " + TABLE_ALARMS + " ORDER BY "+COLUMN_ALARM_DATE+" ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Alarm> alarms = new ArrayList<Alarm>();

        if (cursor.moveToFirst()) {

            while (cursor.isAfterLast() == false) {
                Alarm alarm = new Alarm();

                alarm.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COlUMN_ID))));
                alarm.setGroupId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_GROUP_ID))));
                alarm.setModificationDate(cursor.getString(cursor.getColumnIndex(COLUMN_MODIF_DATE)));
                alarm.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                alarm.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                alarm.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_LATITUDE))));
                alarm.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_LONGITUDE))));
                alarm.setAlarmDate(cursor.getString(cursor.getColumnIndex(COLUMN_ALARM_DATE)));

                cursor.moveToNext();

                alarms.add(alarm);
            }
            cursor.close();
        }

        db.close();

        return alarms;
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

    public boolean deleteAlarm(String title) {

        boolean result = false;

        String query = "Select * FROM " + TABLE_ALARMS + " WHERE " + COLUMN_TITLE + " =  \"" + title + "\"";

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

    public boolean deleteUser(int id){

        boolean result = false;

        String query = "Select * FROM " + TABLE_USERS + " WHERE " + COLUMN_TITLE + " =  \"" + id + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        User user = new User();

        if (cursor.moveToFirst())
        {
            user.setId((cursor.getInt(cursor.getColumnIndex(COlUMN_USER_ID))));
            db.delete(TABLE_USERS, COlUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(user.getId())});
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public boolean modifyAlarm(Alarm alarm) {

        boolean result = false;

        ContentValues values = new ContentValues();

        //values.put(COlUMN_ID, alarm.getId());
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

    public boolean modifyUser(User user){

        boolean result = false;

        ContentValues userValues = new ContentValues();

        userValues.put(COlUMN_USER_ID, user.getId());
        userValues.put(COLUMN_USER_GROUP_ID, user.getGroup());
        userValues.put(COlUMN_USER_NAME, user.getNom());
        userValues.put(COLUMN_USER_FIRSTNAME, user.getPrenom());
        userValues.put(COlUMN_USER_EMAIL, user.getMail());
        userValues.put(COLUMN_USER_PWD, user.getPassword());
        //userValues.put(COLUMN_ISCURRENT, 1);

        SQLiteDatabase db = this.getWritableDatabase();

        db.update(TABLE_USERS, userValues, COlUMN_USER_ID + " = " + user.getId(), null);

        db.close();

        result = true;

        return result;
    }

    public boolean setCurrentToFalse(){

        boolean result = false;

        SQLiteDatabase db = this.getWritableDatabase();

        User current = getCurrentUSer();

        ContentValues userValues = new ContentValues();

        userValues.put(COlUMN_USER_ID, current.getId());
        userValues.put(COLUMN_USER_GROUP_ID, current.getGroup());
        userValues.put(COlUMN_USER_NAME, current.getNom());
        userValues.put(COLUMN_USER_FIRSTNAME, current.getPrenom());
        userValues.put(COlUMN_USER_EMAIL, current.getMail());
        userValues.put(COLUMN_USER_PWD, current.getPassword());
        userValues.put(COLUMN_ISCURRENT, 0);

        db.update(TABLE_USERS, userValues, COlUMN_USER_ID + " = " + current.getId(), null);

        db.close();

        result = true;

        return result;
    }

    public ArrayList<String> getAllTitles(String select, String search){

        ArrayList<String> alarms = new ArrayList<String>();

        String query = "";

        if(search.equals(""))
        {
            query = query + "SELECT * FROM "+TABLE_ALARMS;
        }
        else {
            switch (select) {

                case COlUMN_ID:
                    query = query + "SELECT * FROM " + TABLE_ALARMS + " WHERE " + COlUMN_ID + " = \"" + search + "\"";
                    break;
                case COLUMN_GROUP_ID:
                    query = query + "SELECT * FROM " + TABLE_ALARMS + " WHERE " + COLUMN_GROUP_ID + " = \"" + search + "\"";
                    break;
                case COLUMN_MODIF_DATE:
                    query = query + "SELECT * FROM " + TABLE_ALARMS + " WHERE " + COLUMN_MODIF_DATE + " = \"" + search + "\"";
                    break;
                case COLUMN_TITLE:
                    query = query + "SELECT * FROM " + TABLE_ALARMS + " WHERE " + COLUMN_TITLE + " = \"" + search + "\"";
                    break;
                case COLUMN_DESCRIPTION:
                    query = query + "SELECT * FROM " + TABLE_ALARMS + " WHERE " + COLUMN_DESCRIPTION + " = \"" + search + "\"";
                    break;
                case COLUMN_LATITUDE:
                    query = query + "SELECT * FROM " + TABLE_ALARMS + " WHERE " + COLUMN_LATITUDE + " = \"" + search + "\"";
                    break;
                case COLUMN_LONGITUDE:
                    query = query + "SELECT * FROM " + TABLE_ALARMS + " WHERE " + COLUMN_LONGITUDE + " = \"" + search + "\"";
                    break;
                case COLUMN_ALARM_DATE:
                    query = query + "SELECT * FROM " + TABLE_ALARMS + " WHERE " + COLUMN_ALARM_DATE + " = \"" + search + "\"";
                    break;
            }
        }

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            while (cursor.isAfterLast() == false) {
                Alarm alarm = new Alarm();

                //alarm.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COlUMN_ID))));
                //alarm.setGroupId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_GROUP_ID))));
                //alarm.setModificationDate(cursor.getString(cursor.getColumnIndex(COLUMN_MODIF_DATE)));
                alarm.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                //alarm.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                //alarm.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_LATITUDE))));
                //alarm.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_LONGITUDE))));
                //alarm.setAlarmDate(cursor.getString(cursor.getColumnIndex(COLUMN_ALARM_DATE)));

                cursor.moveToNext();

                alarms.add(alarm.getTitle());

            }
            cursor.close();
        }

        db.close();

        return alarms;
    }
}