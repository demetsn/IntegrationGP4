package ephec.noticeme;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class MemoList extends Fragment {

    private static final int HIGHLIGHT_COLOR = 0x999be6ff;

    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;
    private ArrayList<ListData> mDataList = new ArrayList();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_memo_list, container, false);
        fillMemoList();
        mDrawableBuilder = TextDrawable.builder()
                .round();
        ListView listView = (ListView) view.findViewById(R.id.listView);
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
                    updateCheckedState(holder, data);
                }
            });

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Show the memo page", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
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
                MainActivity.removeAlarm(item.alarm);
                holder.imageView.setImageDrawable(mDrawableBuilder.build(" ", 0xff616161));
                holder.view.setBackgroundColor(HIGHLIGHT_COLOR);
                holder.checkIcon.setVisibility(View.VISIBLE);
            }
            else {
                MainActivity.addAlarm(item.alarm);
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

}
