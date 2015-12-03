package ephec.noticeme;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MemoOverviewActivity extends AppCompatActivity {

    Alarm memo;
    TextView title;
    TextView description;
    TextView date;
    TextView location;
    String Stitle;
    RemoveTask mAuthTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if(extras != null){

            Stitle = extras.getString("memoTitle");

            //getSupportActionBar().setTitle("Memo Detail");
            DBHelper db = new DBHelper(this);
            db.getReadableDatabase();
            memo = db.getAlarm(Stitle);

            db.close();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddMemoActivity.class);
                intent.putExtra("memoTitle", Stitle);
                startActivity(intent);
            }
        });

        if (memo != null) {
            title = (TextView) this.findViewById(R.id.memo_overview_title);
            title.setText(memo.getTitle());

            description = (TextView) this.findViewById(R.id.memo_overview_description);
            description.setText(memo.getDescription());

            date = (TextView) this.findViewById(R.id.memo_overview_date);
            if(!memo.getAlarmDate().equals("&")){
                String temp = memo.getAlarmDate().replace('&',' ');
                date.setText(temp);
            }

            location = (TextView) this.findViewById(R.id.memo_overview_location);
            Geocoder geocode = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try{
                addresses = geocode.getFromLocation(memo.getLatitude(), memo.getLongitude(),1);
            }catch(IOException e){

            }
            location.setText(addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getLocality());

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.overview_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent back = new Intent(this, MainActivity.class);
        startActivity(back);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_cancel:

                DBHelper db = new DBHelper(this);
                db.getWritableDatabase();
                User usr = db.getCurrentUSer();
                mAuthTask = new RemoveTask(usr.getMail(),Connector.decrypt(Connector.decrypt(usr.getPassword())));
                mAuthTask.execute((Void) null);
                return true;

            case R.id.action_deco:
                DBHelper db1 = new DBHelper(this);
                db1.getReadableDatabase();
                User usr1 = db1.getCurrentUSer();
                db1.setCurrentToFalse(usr1);
                db1.close();
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    public class RemoveTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        RemoveTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            DBHelper db = new DBHelper(MemoOverviewActivity.this);
            db.getReadableDatabase();

            Connector co = new Connector();
            if(!co.connect("http://ephecnoticeme.me/app.php/android/removememo")) return false;
            Alarm temp = db.getAlarm(Stitle);
            String response = co.delMemo(mEmail,mPassword,temp.getId());
            if(response.equals("ERROR")) return false;
            if(response.equals("0")){
                System.out.println("Error Login");
                return false;
            }
            if(!co.disconnect()) return false;
            db.deleteAlarm(temp.getTitle());

            db.close();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                Intent intent = new Intent(MemoOverviewActivity.this,MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(MemoOverviewActivity.this,"error during suppression",Toast.LENGTH_LONG).show();
            }
        }
    }

}
