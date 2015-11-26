package ephec.noticeme;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MemoList extends Fragment {

    private static final int HIGHLIGHT_COLOR = 0x999be6ff;
    private static int compteur = 0;

    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;
    private ArrayList<ListData> mDataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_memo_list, container, false);
        mDataList = new ArrayList<>();


        //ASYNCTASK
        if(compteur == 0){
            compteur++;
            FillMemoTask syncTask = new FillMemoTask(getActivity());
            syncTask.execute((Void) null);
        }



        fillMemoList();
        MainActivity.clearList();
        mDrawableBuilder = TextDrawable.builder()
                .round();
        ListView listView = (ListView) view.findViewById(R.id.listView);

        listView.setAdapter(new SampleAdapter());

        return view;
    }
    private void fillMemoList() {

        DBHelper db = new DBHelper(getActivity());
        db.getWritableDatabase();
        User usr = db.getCurrentUSer();
        db.close();
        ArrayList<Alarm> memos = db.getAllAlarm(usr.getId());
        Iterator<Alarm> it = memos.iterator();
        while(it.hasNext()){
            Alarm temp = it.next();
            mDataList.add(new ListData(temp));
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
            String mail = "";
            String pass = "";
            DBHelper db1 = new DBHelper(context);
            db1.getWritableDatabase();
            User usr = db1.getCurrentUSer();
            mail = usr.getMail();
            pass = Connector.decrypt(Connector.decrypt(usr.getPassword()));
            System.out.println(mail);
            System.out.println(pass);
            db1.close();
            String response = co.login(mail,pass);
            System.out.println("reponse : "+response);
            if(response.equals("0")){
                try{
                    //TODO AFFICHER UN TOAST QUI PREVIENT DE LA DECO
                    Thread.sleep(2000);
                }catch (Exception e){

                }
                co.disconnect();
                //Intent intent = new Intent(context, LoginActivity.class);
                //startActivity(intent);
                System.out.println("Je suis ici mais le log est ok");
                return false;
            }else{
                response = android.text.Html.fromHtml(response).toString();
                System.out.println("responce : "+response);
                DBHelper db = new DBHelper(context);
                db.getWritableDatabase();
                try{
                    JSONObject obj = new JSONObject(response);
                    JSONArray jArray = obj.getJSONArray("json");
                    for (int i=0; i < jArray.length(); i++)
                    {
                        try{
                            JSONObject oneObject = jArray.getJSONObject(i);
                            String desc = oneObject.getString("desc");
                            String titre = oneObject.getString("title");
                            String date = oneObject.getString("date");
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

                        }catch (SQLiteConstraintException e){

                        }
                    }

                }catch (Exception e){
                    //TODO AFFICHER TOAST ERREUR CO SERVER
                    db.close();
                    co.disconnect();
                    return false;
                }
                db.close();
            }
            co.disconnect();
            return true;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Intent intent= new Intent(MemoList.this.getContext(),MainActivity.class);
                startActivity(intent);
                //finish();
            } else {

            }
        }
    }
}
