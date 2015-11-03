package ephec.noticeme;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static FragmentManager fragmentManager;
    private String value="";
    private Alarm memo;

    AlarmManager manager;
    PendingIntent pendingIntent;
    BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("email")) {
                value = extras.getString("email");
                //Toast.makeText(getApplicationContext(), "Passage de l'intent email", Toast.LENGTH_LONG).show();
            }
            if(extras.containsKey("Title"))
            {
                //Toast.makeText(getApplicationContext(), "Passage de l'intent titre", Toast.LENGTH_LONG).show();
                launchMemoAlarms(extras.getString("Title"));
            }
        }

        TextView userTxtView = (TextView)findViewById(R.id.username);
        userTxtView.setText(value);

        fragmentManager = getSupportFragmentManager();
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            MemoList firstFragment = new MemoList();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Button to add a memo", Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                AddMemo fragment = new AddMemo();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment newFragment=null;


        if (id == R.id.nav_list) {
            newFragment = new MemoList();
        } else if (id == R.id.nav_add) {
            newFragment = new AddMemo();
        } else if (id == R.id.nav_maps) {
            newFragment = new MapFragment();
        } else if (id == R.id.nav_profile) {
            newFragment = new Profile();
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_edit) {
            Intent intent = new Intent(this,EditProfile.class);
            startActivity(intent);
        }
        if (newFragment != null) {
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void launchMemoAlarms(String title)
    {
        DBHelper db = new DBHelper(this.getApplicationContext());
        db.getReadableDatabase();

        memo = db.getAlarm(title);
        db.close();
        setTimedAlert(memo);
    }

    private void setTimedAlert(Alarm memo)
    {
        Intent intent = new Intent("ephec.noticeme");
        intent.putExtra("memoTitle", memo.getTitle());
        pendingIntent = PendingIntent.getBroadcast(this, memo.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        manager = (AlarmManager) (this
                .getSystemService(Context.ALARM_SERVICE));
        manager.set(AlarmManager.RTC, getTime(memo),
                pendingIntent);

        setup();
    }

    public long getTime(Alarm memo)
    {
        int year;
        int month;
        int day;
        int hour;
        int minute;

        String[] dueAlarm = memo.getAlarmDate().split("&");
        String[] date = dueAlarm[0].split("-");
        String[] hours = dueAlarm[1].split(":");

        year = Integer.parseInt(date[2]);
        month = Integer.parseInt(date[1]);
        day = Integer.parseInt(date[0]);
        hour = Integer.parseInt(hours[0]);
        minute = Integer.parseInt(hours[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, 0);

        long value = calendar.getTimeInMillis();

        return value;
    }

    // Prépare the alarm.
    public void setup() {

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {

                launchNotification(i.getExtras().getString("memoTitle"));
                c.unregisterReceiver(br);
            }
        };
        registerReceiver(br, new IntentFilter("ephec.noticeme"));
    }

    public void launchNotification(String title)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle("You have a new memo!")
                        .setContentText("Click to see you memo.");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this.getApplicationContext(), MainActivity.class);
        resultIntent.putExtra("memoTitle", title);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        int mId = 1;
        mNotificationManager.notify(mId, mBuilder.build());
    }
}
