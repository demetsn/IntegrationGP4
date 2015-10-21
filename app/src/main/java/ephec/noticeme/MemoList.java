package ephec.noticeme;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MemoList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MemoList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemoList extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    String alarmTitle;
    private ListView lv;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MemoList.
     */
    // TODO: Rename and change types and number of parameters
    public static MemoList newInstance(String param1, String param2) {
        MemoList fragment = new MemoList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MemoList() {
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
        View view = inflater.inflate(R.layout.fragment_memo_list, container, false);

        this.lv = (ListView) view.findViewById(R.id.memoList);
        fillMemoList("title","");

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

    private void fillMemoList(String category, String param) {

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        DBHelper db = new DBHelper(getActivity());

        db.getWritableDatabase();

        ArrayList<String> memos = db.getAllTitles(category,param);
        memos.toArray();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_single_choice,memos );
        lv.setAdapter(arrayAdapter);
        lv.setItemChecked(0, true);
        lv.requestFocusFromTouch();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                alarmTitle = (String) adapter.getItemAtPosition(position);
                Fragment newFragment = new AddMemo();

                //Ajout d'une variable dans au frgament grace au Bundle
                Bundle args = new Bundle();
                args.putString("title", alarmTitle);
                newFragment.setArguments(args);

                transaction.replace(R.id.fragment_container, newFragment);
                transaction.setBreadCrumbTitle(alarmTitle);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        lv.setSelection(0);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        Integer Position = lv.getCheckedItemPosition();
        alarmTitle = lv.getItemAtPosition(Position).toString();

        return super.onOptionsItemSelected(item);

    }
    public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
        alarmTitle = (String) adapter.getItemAtPosition(position);

        Intent modMemo = new Intent(getActivity(), AddMemo.class);
        modMemo.putExtra("title", alarmTitle);
        startActivity(modMemo);
    }

    private String getMemoTitle(int Position){
        String memoFound;
        memoFound = (String) lv.getItemAtPosition(Position);
        return memoFound;
    }

}
