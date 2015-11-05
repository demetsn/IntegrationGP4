package ephec.noticeme;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;



public class MemoList extends Fragment {


    Alarm item;
    private ListView lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_memo_list, container, false);

        this.lv = (ListView) view.findViewById(R.id.memoList);
        fillMemoList("title","");

        return view;
    }

    private void fillMemoList(String category, String param) {

        DBHelper db = new DBHelper(getActivity());
        db.getWritableDatabase();

        ArrayList<String> memos = db.getAllTitles(category,param);
        memos.toArray();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_single_choice,memos );
        lv.setAdapter(arrayAdapter);
        lv.setItemChecked(0,true);
        lv.requestFocus();
        lv.setSelection(0);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
        item = (Alarm) adapter.getItemAtPosition(position);
        Toast.makeText(getActivity(), "Test " + item.getTitle(), Toast.LENGTH_LONG).show();
    }

    private Alarm getPlace(int Position){
        Alarm placeFound = new Alarm();
        placeFound = (Alarm) lv.getItemAtPosition(Position);
        return placeFound;
    }

}
