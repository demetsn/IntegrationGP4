package ephec.noticeme;



import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class MemoList extends Fragment {

    private static final int HIGHLIGHT_COLOR = 0x999be6ff;
    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 15000 ;//en millisecondes
    private static final float MINIMUM_DISTANCECHANGE_FOR_UPDATE = 50;

    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;
    private ArrayList<ListData> mDataList;
    private FillMemoTask mAuthTask;
    private ListView listView;
    private LocationManager locManager;
    private float radius = 50;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_memo_list, container, false);
        mDataList = new ArrayList<>();
        locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATE,
                MINIMUM_DISTANCECHANGE_FOR_UPDATE,
                new myLocationListener());
        locManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATE,
                MINIMUM_DISTANCECHANGE_FOR_UPDATE,
                new myLocationListener());

        Bundle extras = getArguments();
        if(extras != null){
            if(extras.containsKey("sync")){
                System.out.println("Je suis dans le cas d'un sync dans memoList");
                DBHelper db = new DBHelper(getContext());
                db.getWritableDatabase();
                db.deleteAllAlarm();
                System.out.println("Apres del alarm");
                db.close();
                mAuthTask = new FillMemoTask(getActivity());
                mAuthTask.execute((Void) null);
                System.out.println("Apres asynctask");
            }
            if(extras.containsKey("Title")){
                launchMemoAlarms(extras.getString("Title"));
            }
        }



        fillMemoList();
        MainActivity.clearList();
        mDrawableBuilder = TextDrawable.builder()
                .round();
        listView = (ListView) view.findViewById(R.id.listView);

        listView.setAdapter(new SampleAdapter());

        return view;
    }
    private void fillMemoList() {

        DBHelper db = new DBHelper(getActivity());
        db.getWritableDatabase();
        ArrayList<Alarm> memos = db.getAllAlarm();
        Iterator<Alarm> it = memos.iterator();
        while(it.hasNext()){
            Alarm temp = it.next();
            mDataList.add(new ListData(temp));
        }

    }

    public void launchMemoAlarms(String title){
        DBHelper db = new DBHelper(getContext());
        db.getReadableDatabase();
        Alarm memo = db.getAlarm(title);
        db.close();

        if(!memo.getAlarmDate().equals("&")){
            setTimedAlert(memo);
        }
        setProximityAlert(memo);
    }
    @SuppressLint("NewApi")
    private void setTimedAlert(Alarm memo) {

        long time = System.currentTimeMillis();
        long memoTime = getTime(memo);

        if(time < memoTime) {
            Intent intentAlarm = new Intent(getContext(), AlarmReceiver.class);
            intentAlarm.putExtra("memoTitle", memo.getTitle());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), memo.getId() - (2 * memo.getId()), intentAlarm, 0);

            AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            manager.setExact(AlarmManager.RTC_WAKEUP, getTime(memo), pendingIntent);
        }
    }
    public long getTime(Alarm memo) {
        int year;
        int month;
        int day;
        int hour;
        int minute;

        String[] dueAlarm = memo.getAlarmDate().split("&");
        String[] date = dueAlarm[0].split("/");
        String[] hours = dueAlarm[1].split(":");

        year = Integer.parseInt(date[0]);
        month = Integer.parseInt(date[1])-1;
        day = Integer.parseInt(date[2]);

        hour = Integer.parseInt(hours[0]);
        minute = Integer.parseInt(hours[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(year, month, day, hour, minute, 0);

        return calendar.getTimeInMillis();
    }
    private void setProximityAlert(Alarm memo) {
        Intent intent = new Intent(getContext(), GeoReceiver.class);
        intent.putExtra("memoTitle", memo.getTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), memo.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        locManager.addProximityAlert(memo.getLatitude(), memo.getLongitude(), radius, -1, pendingIntent);
    }
    public class myLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }


    private class SampleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public ListData getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(MemoList.this.getActivity(), R.layout.list_item_layout, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ListData item = getItem(position);

            // provide support for selected state
            updateCheckedState(holder, item);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // when the image is clicked, update the selected state
                    ListData data = getItem(position);
                    data.setChecked(!data.isChecked);
                    if(data.isChecked){
                        MainActivity.addAlarm(data.alarm);
                    }else{
                        MainActivity.removeAlarm(data.alarm);
                    }
                    updateCheckedState(holder, data);
                }
            });

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MemoOverviewActivity.class);
                    intent.putExtra("memoTitle",getItem(position).alarm.getTitle());
                    startActivity(intent);
                }
            };
            View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // when the image is clicked, update the selected state
                    ListData data = getItem(position);
                    data.setChecked(!data.isChecked);
                    if(data.isChecked){
                        MainActivity.addAlarm(data.alarm);
                    }else{
                        MainActivity.removeAlarm(data.alarm);
                    }

                    updateCheckedState(holder, data);
                    return true;
                }
            };

            holder.lL.setOnClickListener(onClickListener);
            holder.lL.setOnLongClickListener(onLongClickListener);

            holder.textView.setText(item.alarm.getTitle());

            holder.tv3.setText(item.alarm.getDescription());
            holder.tv4.setText(item.alarm.getAlarmDate().replace('&', ' '));


            return convertView;
        }

        private void updateCheckedState(ViewHolder holder, ListData item){
            if (item.isChecked) {
                holder.imageView.setImageDrawable(mDrawableBuilder.build(" ", 0xff616161));
                holder.view.setBackgroundColor(HIGHLIGHT_COLOR);
                holder.checkIcon.setVisibility(View.VISIBLE);
            }
            else {
                TextDrawable drawable = mDrawableBuilder.build(
                        String.valueOf(item.alarm.getTitle().charAt(0)),
                        mColorGenerator.getColor(item.alarm.getTitle()));
                holder.imageView.setImageDrawable(drawable);
                holder.view.setBackgroundColor(Color.TRANSPARENT);
                holder.checkIcon.setVisibility(View.GONE);
            }
        }
    }

    private static class ViewHolder {

        private View view;
        private ImageView imageView;
        private TextView textView;
        private ImageView checkIcon;
        private TextView tv3;
        private TextView tv4;
        private LinearLayout lL;

        private ViewHolder(View view) {
            this.view = view;
            imageView = (ImageView) view.findViewById(R.id.imageView2);
            textView = (TextView) view.findViewById(R.id.textView2);
            checkIcon = (ImageView) view.findViewById(R.id.check_icon);
            tv3 = (TextView) view.findViewById(R.id.textView3);
            tv4 = (TextView) view.findViewById(R.id.textView4);
            lL = (LinearLayout) view.findViewById(R.id.lineLayoutID);
        }
    }

    private static class ListData {

        private Alarm alarm;

        private boolean isChecked;

        public ListData(Alarm data) {
            this.alarm = data;
        }

        public void setChecked(boolean isChecked) {
            this.isChecked = isChecked;
        }
    }

    private class FillMemoTask extends AsyncTask<Void, Void, Boolean> {
        private Context context;

        FillMemoTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Connector co = new Connector();
            if(!co.connect("http://ephecnoticeme.me/app.php/android/memolist")) return false;
            DBHelper db = new DBHelper(context);
            db.getWritableDatabase();
            User usr = db.getCurrentUSer();
            String response = co.login(usr.getMail(),Connector.decrypt(Connector.decrypt(usr.getPassword())));
            if(response.equals("0")){
                db.setCurrentToFalse(usr);
                db.close();
                co.disconnect();
                System.out.println("Error login !");
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                return false;
            }else{
                response = android.text.Html.fromHtml(response).toString();
                try{
                    JSONObject obj = new JSONObject(response);
                    JSONArray jArray = obj.getJSONArray("json");
                    for (int i=0; i < jArray.length(); i++) {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        String desc = oneObject.getString("desc");
                        String titre = oneObject.getString("title");
                        String date = oneObject.getString("date").substring(0,16).replace(' ','&').replace('-','/');
                        double lat = oneObject.getDouble("lat");
                        double longi = oneObject.getDouble("long");
                        int id = oneObject.getInt("id");
                        Alarm temp = new Alarm();
                        temp.setId(id);
                        temp.setLatitude(lat);
                        temp.setLongitude(longi);
                        temp.setDescription(desc);
                        temp.setTitle(titre);
                        temp.setAlarmDate(date.replace(' ', '&'));
                        db.addAlarm(temp);
                        mDataList.add(new ListData(temp));
                    }

                }catch (Exception e){
                    db.close();
                    co.disconnect();
                    return false;
                }
            }
            db.close();
            co.disconnect();
            return true;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                DBHelper db = new DBHelper(context);
                db.getWritableDatabase();
                ArrayList<Alarm> array = db.getAllAlarm();
                Iterator<Alarm> it = array.iterator();
                while(it.hasNext()){
                    Alarm temp = it.next();
                    launchMemoAlarms(temp.getTitle());
                }
                db.close();
                listView.setAdapter(new SampleAdapter());
            } else {
                Toast.makeText(context,"Cannot refresh with the server",Toast.LENGTH_LONG).show();
            }
        }
    }
}
