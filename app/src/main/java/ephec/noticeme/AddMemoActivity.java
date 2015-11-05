package ephec.noticeme;

import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Random;

public class AddMemoActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView title;
    private TextView description;
    private TextView date;
    private TextView time;
    private Button save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.title = (TextView) this.findViewById(R.id.memo_title);
        this.description = (TextView) this.findViewById(R.id.memo_description);
        this.date = (TextView) this.findViewById(R.id.memo_textDate);
        this.date.setOnClickListener(this);
        this.time = (TextView) this.findViewById(R.id.memo_textTime);
        this.time.setOnClickListener(this);
        this.save = (Button) this.findViewById(R.id.memo_save_button);
        this.save.setOnClickListener(this);

        final ScrollView mainSW = (ScrollView) this.findViewById(R.id.scrollView);
        ImageView transparentImg = (ImageView) this.findViewById(R.id.transparent_image);
        transparentImg.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v,MotionEvent event)  {
               int action = event.getAction();
                switch (action){
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
                                date.setText(dayOfMonth + "-"
                                        + (monthOfYear + 1) + "-" + thisYear);

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
                        time.setText(thisHour+":"+thisMinute);

                    }
                }, hour, minutes, true);
                tpd.show();

                break;

            case R.id.memo_save_button:

                //TODO Check the memo datas to avoid SQL injections.


                //At this point, we consider the possible SQL injections, avoided.
                Alarm memo = new Alarm();

                memo.setTitle(title.getText().toString());
                memo.setDescription(description.getText().toString());
                memo.setAlarmDate(date.getText().toString() + "|" + time.getText().toString()); //Pas top on a xx/yy/zzzz|AA:BB
                memo.setModificationDate(getActualTime());

                //valeurs test
                memo.setLatitude(0.0);
                memo.setLongitude(0.0);
                Random rn1 = new Random();
                memo.setId(rn1.nextInt(10000));
                memo.setGroupId(0);

                DBHelper db = new DBHelper(this);

                if(db.addAlarm(memo))
                {
                    //Toast toast = Toast.makeText(getActivity(), "Memo enregistré", Toast.LENGTH_LONG);
                    //toast.show();

                    launchNotification();

                    Intent save = new Intent(this, MainActivity.class);
                    startActivity(save);
                }

                break;

        }

    }

    public String getActualTime()
    {
        String now;

        Calendar cal = Calendar.getInstance();

        int thisYear = cal.get(Calendar.YEAR);
        int thisMonth = cal.get(Calendar.MONTH);
        int today = cal.get(Calendar.DAY_OF_MONTH);
        int thisHour = cal.get(Calendar.HOUR_OF_DAY);
        int thisMinute = cal.get(Calendar.MINUTE);

        now = today+"/"+thisMonth+"/"+thisYear+"|"+thisHour+":"+thisMinute;

        return now;
    }

    public void launchNotification()
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Memo saved")
                        .setContentText("Congratulations, you just saved a new memo");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this.getApplicationContext(), MainActivity.class);

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
