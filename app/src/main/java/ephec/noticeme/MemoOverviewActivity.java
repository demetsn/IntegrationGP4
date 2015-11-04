package ephec.noticeme;

import android.graphics.Paint;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class MemoOverviewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Alarm memo;

    TextView title;
    TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_overview);

        Bundle extra = getIntent().getExtras();

        DBHelper db = new DBHelper(this.getApplicationContext());
        db.getReadableDatabase();

        memo = db.getAlarm(extra.getString("memoTitle"));
        db.close();

        title = (TextView) this.findViewById(R.id.memo_overview_title);
        title.setText(memo.getTitle());
        title.setPaintFlags(title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        description = (TextView) this.findViewById(R.id.memo_overview_description);
        description.setText(memo.getDescription());
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        return false;
    }
}
