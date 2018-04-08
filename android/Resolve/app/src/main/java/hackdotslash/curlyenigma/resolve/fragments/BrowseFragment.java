package hackdotslash.curlyenigma.resolve.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import hackdotslash.curlyenigma.resolve.R;
import hackdotslash.curlyenigma.resolve.ResolveService;
import hackdotslash.curlyenigma.resolve.models.Complaint;
import hackdotslash.curlyenigma.resolve.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BrowseFragment extends Fragment implements OnMapReadyCallback {

    ArrayList<LatLng> latlngs;
    ArrayList<Complaint> complaints;
    double lat,lng;
    ArrayList<Integer> types, userIds, issueIds;
    int uid;
    String userId;
    private GoogleMap mMap;
    private HashMap<Marker, String> hashMap;
    ResolveService service;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.title_map);
        setHasOptionsMenu(true);
        if(container != null)
            container.removeAllViews();
        hashMap = new HashMap<>();
        View view = inflater.inflate(R.layout.fragment_browse, container, false);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(ResolveService.BASE_URL).build();
        service = retrofit.create(ResolveService.class);

        fetchComplaints();
        return view;
    }

    private void fetchComplaints() {
        progressDialog.setMessage("Fetching complaints...");
        progressDialog.show();
        Call<String> call = service.complaints();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject data = new JSONObject(response.body().toString());
                    if(data.getBoolean("success")){
                        JSONArray array = data.getJSONArray("complaints");
                        complaints = new ArrayList<>();
                        latlngs = new ArrayList<>();
                        for(int i = 0; i < array.length(); i++){
                            try {
                                Complaint c = new Complaint();
                                JSONObject o = array.getJSONObject(i);
                                c.setId(o.getString("_id"));
                                c.setLat(o.getDouble("lat"));
                                c.setLng(o.getDouble("lng"));
                                c.setDescription(o.getString("description"));
                                c.setAuthorId(o.getString("author"));

                                JSONObject p = o.getJSONObject("author");
                                User a = new User();
                                a.setId(p.getString("_id"));
                                a.setFname(p.getString("fname"));
                                a.setLname(p.getString("lname"));
                                c.setAuthor(a);
                                complaints.add(c);
                            }catch (JSONException e){
                                Log.d("XXXERR", e.getMessage());
                            }
                        }
                        initializeMap();
                    }else{
                        Snackbar.make(getView(), "Please try again...", Snackbar.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.hide();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Snackbar.make(getView(), "Please try again...", Snackbar.LENGTH_SHORT).show();
                progressDialog.hide();
            }
        });

    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for(int i=0;i<complaints.size();i++){
            try {
                Log.d("XXXSET", complaints.get(i).getDescription());
                Marker m = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(complaints.get(i).getLat(), complaints.get(i).getLng()))
                        .title(complaints.get(i).getDescription())
                        .snippet("Posted by: " + complaints.get(i).getAuthor().getFname()));
                hashMap.put(m, complaints.get(i).getId());

            }catch (Exception e){
                Log.d("XXX", e.getMessage());
            }
        }
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle = new Bundle();
                bundle.putString("id", hashMap.get(marker));
                Fragment f = new ComplaintFragment();
                f.setArguments(bundle);
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.frame_layout_main, f)
                        .addToBackStack("browse")
                        .commit();
                return false;
            }
        });
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.logout, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_item:
                SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("token", "");
                editor.commit();
                getActivity().finish();
                break;
            case R.id.register_item:

                Fragment f = new CreateComplaintFragment();
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.frame_layout_main, f)
                        .addToBackStack("create")
                        .commit();
                break;
        }
        return true;
    }
}
