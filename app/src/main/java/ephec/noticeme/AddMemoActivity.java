package ephec.noticeme;

import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AddMemoActivity extends AppCompatActivity
        implements
        View.OnClickListener,
        OnMapReadyCallback{

    private TextView title;
    private TextView description;
    private TextView date;
    private TextView time;
    private GoogleMap mMap;
    private LatLng loc;
    private Marker mMarker;
    private Geocoder geocode;
    private static int markerCount;
    private boolean isUpdate;
    private int id;
    private AddTask mAuthTask;
    private String address;
    private Switch sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        markerCount = 0;

        this.title = (TextView) this.findViewById(R.id.memo_title);
        this.description = (TextView) this.findViewById(R.id.memo_description);
        this.date = (TextView) this.findViewById(R.id.memo_textDate);
        this.date.setOnClickListener(this);
        this.time = (TextView) this.findViewById(R.id.memo_textTime);
        this.time.setOnClickListener(this);
        this.isUpdate = false;
        this.sw = (Switch) findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    date.setVisibility(View.VISIBLE);
                    time.setVisibility(View.VISIBLE);
                    String actualTime = getActualTime();
                    date.setText(actualTime.split("&")[0]);
                    time.setText(actualTime.split("&")[1]);
                }else{
                    date.setVisibility(View.INVISIBLE);
                    time.setVisibility(View.INVISIBLE);
                    date.setText("");
                    time.setText("");
                }
            }
        });
        date.setVisibility(View.INVISIBLE);
        time.setVisibility(View.INVISIBLE);

        Bundle extras = getIntent().getExtras();
        String alarmTitle = "";
        Alarm memo;
        if(extras != null){
            this.isUpdate = true;
            alarmTitle = extras.getString("memoTitle");
            DBHelper db = new DBHelper(this);
            db.getReadableDatabase();

            memo = db.getAlarm(alarmTitle);
            db.close();
            this.id = memo.getId();
            this.title.setText(memo.getTitle());
            this.description.setText(memo.getDescription());
            if(!memo.getAlarmDate().equals("&")){
                String temp[] = memo.getAlarmDate().split("&");
                this.date.setText(temp[0]);
                this.time.setText(temp[1]);
            }

            this.loc = new LatLng(memo.getLatitude(),memo.getLongitude());
        }



        final ScrollView mainSW = (ScrollView) this.findViewById(R.id.scrollView);
        ImageView transparentImg = (ImageView) this.findViewById(R.id.transparent_image);
        transparentImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mainSW.requestDisallowInterceptTouchEvent(true);
                        return false;
                    case MotionEvent.ACTION_UP:
                        mainSW.requestDisallowInterceptTouchEvent(false);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        mainSW.requestDisallowInterceptTouchEvent(true);
                        return false;
                    default:
                        return true;
                }
            }
        });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_frag);
        mapFragment.getMapAsync(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_memo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                //TODO Check the memo datas to avoid SQL injections.
                //At this point, we consider the possible SQL injections, avoided.
                Alarm memo = new Alarm();

                if(title.getText().toString().equals("")){
                    title.setError("The title cannot be empty");
                    title.requestFocus();
                    return true;
                }

                memo.setTitle(title.getText().toString());
                memo.setDescription(description.getText().toString());
                memo.setAlarmDate(
                        date.getText() + "&" + time.getText().toString());
                memo.setModificationDate(getActualTime());
                memo.setLatitude(mMarker.getPosition().latitude);
                memo.setLongitude(mMarker.getPosition().longitude);

                if(!isUpdate){
                    //ID A -1 pour le server
                    memo.setId(-1);
                }else{
                    memo.setId(this.id);
                }
                DBHelper db = new DBHelper(this);
                db.getWritableDatabase();
                User usr = db.getCurrentUSer();
                mAuthTask = new AddTask(usr.getMail(),Connector.decrypt(Connector.decrypt(usr.getPassword())),memo);
                mAuthTask.execute((Void) null);
                db.close();

                return true;
            case R.id.action_cancel:

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

                return true;
            case R.id.action_deco:

                DBHelper db1 = new DBHelper(this);
                db1.getReadableDatabase();
                User current = db1.getCurrentUSer();
                db1.setCurrentToFalse(current);
                db1.close();
                Intent intentLog = new Intent(this, LoginActivity.class);
                startActivity(intentLog);

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap map){
        mMap = map;
        mMap.setMyLocationEnabled(true);

        GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                List<Address> addresses = null;
                if(!isUpdate){
                    loc = new LatLng(location.getLatitude(),location.getLongitude());
                }

                if (markerCount == 0) {
                    try{
                        geocode = new Geocoder(getApplicationContext(), Locale.getDefault());
                        addresses = geocode.getFromLocation(loc.latitude, loc.longitude,1);
                    }catch (IOException e){
                        System.out.println(e);
                    }
                    try{
                        address = addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getLocality();
                        mMarker = mMap.addMarker(new MarkerOptions()
                                        .position(loc)
                                        .title("Tap the screen to update location")
                                        .snippet(address)
                                        .draggable(true)
                        );
                        mMarker.showInfoWindow();
                    }catch (NullPointerException e){
                        System.out.println(e);
                    }
                    mMap.animateCamera((CameraUpdateFactory.newLatLngZoom(loc, 13)));
                    markerCount = 1;
                }
            }
        };
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                List<Address> addresses = null;
                try{
                    addresses = geocode.getFromLocation(latLng.latitude, latLng.longitude,1);
                    mMarker.setPosition(latLng);
                    mMarker.setSnippet(addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getLocality());
                    mMarker.showInfoWindow();
                }catch (IOException e){

                }catch (NullPointerException e){

                }

            }
        });

    }

    public void onClick(View v) throws NullPointerException {
        switch (v.getId()) {
            case R.id.memo_textDate:
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int thisYear,
                                                  int monthOfYear, int dayOfMonth) {
                                String m;
                                String d;
                                if(dayOfMonth<10){
                                    d = "0"+dayOfMonth;
                                }else{
                                    d = ""+dayOfMonth;
                                }if(monthOfYear<10){
                                    m = "0"+(monthOfYear + 1);
                                }else{
                                    m = ""+(monthOfYear + 1);
                                }
                                date.setText(thisYear + "/"
                                        + m + "/" + d);

                            }
                        }, year, month, day);
                dpd.show();
                break;

            case R.id.memo_textTime:
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);

                TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int thisHour, int thisMinute) {
                        String SHours;
                        String SMinutes;
                        if(thisHour<10){
                            SHours="0"+thisHour;
                        }else{
                            SHours=""+thisHour;
                        }
                        if(thisMinute<10){
                            SMinutes="0"+thisMinute;
                        }else{
                            SMinutes=""+thisMinute;
                        }
                        time.setText(SHours+":"+SMinutes);
                    }
                }, hour, minutes, true);
                tpd.show();
                break;
        }
    }

    public String getActualTime() {
        String now;
        Calendar cal = Calendar.getInstance();

        int thisYear = cal.get(Calendar.YEAR);
        int thisMonth = cal.get(Calendar.MONTH);
        int today = cal.get(Calendar.DAY_OF_MONTH);
        int thisHour = cal.get(Calendar.HOUR_OF_DAY);
        int thisMinute = cal.get(Calendar.MINUTE);
        String m;
        String d;
        if(today<10){
            d = "0"+today;
        }else{
            d = ""+today;
        }if(thisMonth<10){
            m = "0"+(thisMonth + 1);
        }else{
            m = ""+(thisMonth + 1);
        }
        String SHours;
        String SMinutes;
        if(thisHour<10){
            SHours="0"+thisHour;
        }else{
            SHours=""+thisHour;
        }
        if(thisMinute<10){
            SMinutes="0"+thisMinute;
        }else{
            SMinutes=""+thisMinute;
        }

        now = thisYear+"/"+m+"/"+d+"&"+SHours+":"+SMinutes;

        return now;
    }

    public class AddTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private Alarm memo;

        AddTask(String email, String password, Alarm memo) {
            mEmail = email;
            mPassword = password;
            this.memo = memo;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Connector connect = new Connector();
            connect.connect("http://ephecnoticeme.me/app.php/android/editmemo");
            DBHelper db = new DBHelper(AddMemoActivity.this);
            db.getWritableDatabase();
            User current = db.getCurrentUSer();
            String response = connect.addMemo(mEmail, mPassword, memo,address);
            if(response.equals("0")){
                connect.disconnect();
                db.setCurrentToFalse(current);
                db.close();
                Intent disconnect = new Intent(AddMemoActivity.this, LoginActivity.class);
                startActivity(disconnect);
                return false;
            }
            db.close();
            System.out.println(response);
            if(!isUpdate){
                memo.setId(Integer.parseInt(response));
            }
            connect.disconnect();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                DBHelper db = new DBHelper(AddMemoActivity.this);
                db.getWritableDatabase();
                if(isUpdate){
                    db.modifyAlarm(memo);
                    db.close();
                    Intent save = new Intent(AddMemoActivity.this, MainActivity.class);
                    save.putExtra("Title",memo.getTitle());
                    startActivity(save);

                }else{
                    if(db.addAlarm(memo)) {
                        db.close();
                        Intent save = new Intent(AddMemoActivity.this, MainActivity.class);
                        save.putExtra("Title",memo.getTitle());
                        startActivity(save);
                    }
                }
            } else {
                Toast.makeText(AddMemoActivity.this,"Error adding memo",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }

    }
}
