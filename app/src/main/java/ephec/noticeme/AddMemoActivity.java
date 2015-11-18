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
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

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

//TODO BUG SI AJOUT JUSTE DE DATE OU JUSTE TIME
//TODO AMELIORER L INTERFACE AVEC AJOUT DE LA DATE FACULTATIVE VIA UN LISTENER SUR TEXTVIEW
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
                    Random rn1 = new Random();
                    memo.setId(rn1.nextInt(10000));
                }else{
                    memo.setId(this.id);
                }
                memo.setGroupId(0);

                DBHelper db = new DBHelper(this);
                if(isUpdate){
                    db.modifyAlarm(memo);
                    Intent save = new Intent(this, MainActivity.class);
                    save.putExtra("Title",memo.getTitle());
                    startActivity(save);

                }else{
                    if(db.addAlarm(memo))
                    {
                        //launchNotification();
                        Intent save = new Intent(this, MainActivity.class);
                        save.putExtra("Title",memo.getTitle());
                        startActivity(save);
                    }
                }


                return true;
            case R.id.action_cancel:

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

                return true;
            case R.id.action_deco:
                //TODO METTRE LE TAG CURRENT A FALSE

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
                        mMarker = mMap.addMarker(new MarkerOptions()
                                        .position(loc)
                                        .title("Tap the screen to update location")
                                        .snippet(addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getLocality())
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

        now = thisYear+"/"+thisMonth+"/"+today+"&"+thisHour+":"+thisMinute;

        return now;
    }
}
