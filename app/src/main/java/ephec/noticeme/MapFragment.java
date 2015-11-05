package ephec.noticeme;


import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public class MapFragment extends Fragment {

    MapView mMapView;
    private GoogleMap mMap;
    private Geocoder geocode;
    public static int markerCount = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container,
                false);

        markerCount = 0;

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMap = mMapView.getMap();
        geocode = new Geocoder(getActivity(), Locale.getDefault());
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                if (markerCount == 0) {
                    mMap.animateCamera((CameraUpdateFactory.newLatLngZoom(loc, 12)));
                    markerCount = 1;
                }
            }
        });

        DBHelper db = new DBHelper(getActivity());
        db.getWritableDatabase();

        ArrayList<Alarm> memosTitle = db.getAllAlarm();
        Iterator<Alarm> it = memosTitle.iterator();

        while(it.hasNext()){
            Alarm temp = it.next();
            mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(
                                    temp.getLatitude(), temp.getLongitude()))
                            .title(temp.getTitle())
                            .snippet(temp.getDescription())
            );
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(getActivity(),"Show detail for : "+marker.getTitle(),Toast.LENGTH_LONG);
            }
        });
        return v;
    }
}
