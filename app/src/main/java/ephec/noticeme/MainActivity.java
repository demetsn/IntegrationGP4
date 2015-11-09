package ephec.noticeme;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static FragmentManager fragmentManager;
    private MenuItem itemMenu;
    private Toolbar toolbar;
    private AlarmManager manager;
    private PendingIntent pendingIntent;
    private BroadcastReceiver br;
    private float radius = 50f;
    private ArrayList<Alarm> LAlarm = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Memo List");


        Bundle extras = getIntent().getExtras();
        if(extras != null){
            if(extras.containsKey("Title")){
                launchMemoAlarms(extras.getString("Title"));
            }
        }

        TextView userTxtView = (TextView)findViewById(R.id.username);
        /*File file = new File(this.getFilesDir(), "user.save");
        String line;
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            line = br.readLine();
            br.close();
        }catch(FileNotFoundException e){
            return;
        }
        catch(Exception e){
            return;
        }
        //separation du email et du mdp venant du fichier
        userTxtView.setText(line.split("£")[0]);*/

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
                Intent intent = new Intent(view.getContext(), AddMemoActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_list);
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
        String toolbarTitle = toolbar.getTitle().toString();
        itemMenu = menu.findItem(R.id.action_delete);
        if(!toolbarTitle.equals("Memo List")){
            itemMenu.setVisible(false);
        }else{
            itemMenu.setVisible(true);
        }
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
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if(id == R.id.action_refresh){
            Snackbar.make(
                    findViewById(android.R.id.content),
                    "refresh the list with the server",
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return true;
        }
        if(id == R.id.action_delete){
            Snackbar.make(
                    findViewById(android.R.id.content),
                    "del the selected item",
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            return true;
        }
        if(id == R.id.action_deco){
            String filename = "user.save";
            File file = new File(this.getFilesDir(), filename);
            Boolean del = file.delete();
            System.out.println(del);
            if(del){
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
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
            toolbar.setTitle("Memo List");
            itemMenu.setVisible(true);
        } else if (id == R.id.nav_add) {
            //newFragment = new AddMemo();
            Intent intent= new Intent(this, AddMemoActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_maps) {
            newFragment = new MapFragment();
            toolbar.setTitle("Memo on map");
            itemMenu.setVisible(false);
        } else if (id == R.id.nav_profile) {
            newFragment = new Profile();
            toolbar.setTitle("Profile");
            itemMenu.setVisible(false);
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

    public void launchMemoAlarms(String title){
        DBHelper db = new DBHelper(this.getApplicationContext());
        db.getReadableDatabase();

        Alarm memo = db.getAlarm(title);
        db.close();
        setTimedAlert(memo);
        setProximityAlert(memo);
    }

    private void setTimedAlert(Alarm memo) {
        Intent intent = new Intent("ephec.noticeme");
        intent.putExtra("memoTitle", memo.getTitle());
        pendingIntent = PendingIntent.getBroadcast(this, memo.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        manager = (AlarmManager) (this
                .getSystemService(Context.ALARM_SERVICE));
        manager.set(AlarmManager.RTC, getTime(memo),
                pendingIntent);

        setup();
    }

    public long getTime(Alarm memo) {
        int year;
        int month;
        int day;
        int hour;
        int minute;

        String[] dueAlarm = memo.getAlarmDate().split(" ");
        String[] date = dueAlarm[0].split("/");
        String[] hours = dueAlarm[1].split(":");

        year = Integer.parseInt(date[0]);
        month = Integer.parseInt(date[1]);
        day = Integer.parseInt(date[2]);
        hour = Integer.parseInt(hours[0]);
        minute = Integer.parseInt(hours[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, 0);

        long value = calendar.getTimeInMillis();

        return value;
    }

    private void setProximityAlert(Alarm memo) {
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Intent intent = new Intent("ephec.noticeme");
        intent.putExtra("memoTitle", memo.getTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), memo.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        locManager.addProximityAlert(memo.getLatitude(), memo.getLongitude(), radius, -1, pendingIntent);

        setup();
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

    public void launchNotification(String title) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("You have a new memo!")
                        .setContentText("Click to see your memo.");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this.getApplicationContext(), MemoOverviewActivity.class);
        //Intent resultIntent = new Intent(this.getApplicationContext(), MainActivity.class);
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

    public void addAlarm(Alarm alarm){
        LAlarm.add(alarm);
    }
}
