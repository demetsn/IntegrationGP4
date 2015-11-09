package ephec.noticeme;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MemoOverviewActivity extends AppCompatActivity {

    Alarm memo;
    TextView title;
    TextView description;
    String Stitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        Stitle = extras.getString("memoTitle");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO CHECK IN ADDMEMO FOR AN EDIT OR A NEW MEMO
                Intent intent = new Intent(view.getContext(), AddMemoActivity.class);
                intent.putExtra("memoTitle",Stitle);
                startActivity(intent);
            }
        });

        getSupportActionBar().setTitle(Stitle);
        DBHelper db = new DBHelper(this);
        db.getReadableDatabase();

        memo = db.getAlarm(Stitle);
        db.close();

        title = (TextView) this.findViewById(R.id.memo_overview_title);
        title.setText(memo.getTitle());

        description = (TextView) this.findViewById(R.id.memo_overview_description);
        description.setText(memo.getDescription());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.overview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_cancel:

                DBHelper db = new DBHelper(this.getApplicationContext());
                db.getReadableDatabase();

                db.deleteAlarm(title.getText().toString());
                db.close();

                //Toast.makeText(getApplicationContext(), "Your memo has been deleted.", Toast.LENGTH_LONG).show();

                Intent delete = new Intent(this, MainActivity.class);
                startActivity(delete);

                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
