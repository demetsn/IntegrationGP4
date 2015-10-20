package ephec.noticeme;

import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddMemo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddMemo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddMemo extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView title;
    private TextView description;
    private TextView date;
    private TextView time;
    private Button save;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddMemo.
     */
    // TODO: Rename and change types and number of parameters
    public static AddMemo newInstance(String param1, String param2) {
        AddMemo fragment = new AddMemo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AddMemo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_memo, container, false);

        this.title = (TextView) view.findViewById(R.id.memo_title);
        this.description = (TextView) view.findViewById(R.id.memo_description);
        this.date = (TextView) view.findViewById(R.id.memo_textDate);
        this.date.setOnClickListener(this);
        this.time = (TextView) view.findViewById(R.id.memo_textTime);
        this.time.setOnClickListener(this);
        this.save = (Button) view.findViewById(R.id.memo_save_button);
        this.save.setOnClickListener(this);

        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void onClick(View v) throws NullPointerException {

        switch (v.getId()) {
            case R.id.memo_textDate:

                Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
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

                TimePickerDialog tpd = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {

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

                DBHelper db = new DBHelper(getActivity());

                if(db.addAlarm(memo))
                {
                    /*Toast toast = Toast.makeText(getActivity(), "Memo enregistré", Toast.LENGTH_LONG);
                    toast.show();*/
                    launchNotification();
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
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle("Memo saved")
                        .setContentText("Congratulations, you just saved a new memo");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(getContext().getApplicationContext(), MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());

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
        NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        int mId = 1;
        mNotificationManager.notify(mId, mBuilder.build());
    }
}
