package hackdotslash.curlyenigma.resolve.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import hackdotslash.curlyenigma.resolve.MainActivity;
import hackdotslash.curlyenigma.resolve.R;
import hackdotslash.curlyenigma.resolve.ResolveService;
import hackdotslash.curlyenigma.resolve.adapters.HomeAdapter;
import hackdotslash.curlyenigma.resolve.models.Category;
import hackdotslash.curlyenigma.resolve.utils.Utility;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.app.Activity.RESULT_OK;

public class CreateComplaintFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerDragListener{

    ImageView img;
    private ResolveService service;
    private EditText editTextDescription, editTextLocation;
    private ProgressDialog progressDialog;
    private Spinner spinner;
    private List<Category> categoryList;
    private String currentPath;
    private File image;

    Marker myMarker;
    private GoogleMap mMap;
    private android.location.LocationManager locationManager;
    private TextView locinfo;
    private Button map, closeMapDialog;

    LatLng issueLoc;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;

    private String imgStr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.title_create_complaint);
        setHasOptionsMenu(true);
        if(container != null)
            container.removeAllViews();


        View view = inflater.inflate(R.layout.fragment_create_complaint, container, false);

        img = view.findViewById(R.id.thumbnail);
        spinner = view.findViewById(R.id.spinner_category);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextLocation = view.findViewById(R.id.editTextLocation);
        map = view.findViewById(R.id.map);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        Button submitButton = view.findViewById(R.id.buttonSubmit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitIssue();
            }
        });

        File storageDir = getActivity().getExternalCacheDir();
        image = new File(Environment.getExternalStorageDirectory() + "/DCIM/image.png");
        currentPath = image.getAbsolutePath();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(ResolveService.BASE_URL).build();
        service = retrofit.create(ResolveService.class);

        fetchCategories();

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.setContentView(R.layout.mapsdialog);
                locinfo = dialog.findViewById(R.id.locinfo);
                dialog.setTitle("Long press on the maps to add a marker. Long press on the marker to change location");
                initializeMap();
                closeMapDialog = dialog.findViewById(R.id.close);
                closeMapDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        map.setText("Location recorded");
                    }
                });
                dialog.show();
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        return view;
    }

    private void initializeMap() {
        locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);

        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void fetchCategories(){
        progressDialog.setMessage("Fetching categories...");
        progressDialog.show();
        Call<String> call = service.categories();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject data = new JSONObject(response.body().toString());
                    if(data.getBoolean("success")){
                        JSONArray categories = data.getJSONArray("categories");
                        categoryList = new ArrayList<>();
                        ArrayList<String> items = new ArrayList<>();
                        for(int i = 0; i < categories.length(); i++){
                            JSONObject t = categories.getJSONObject(i);
                            categoryList.add(new Category(t.getString("_id"), t.getString("title")));
                            items.add(t.getString("title"));
                        }
                        SpinnerAdapter adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, items);
                        spinner.setAdapter(adapter);
                        progressDialog.hide();
                    }else{
                        getActivity().finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.hide();
                getActivity().finish();
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        menu.clear();
//        inflater.inflate(R.menu.check_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.done:

                Log.d("XXX", "Sending issue");
                submitIssue();
                break;
        }
        return true;
    }

    private void submitIssue() {
        progressDialog.setMessage("Submitting complaint...");
        progressDialog.show();
        String description = editTextDescription.getText().toString();
        String category = categoryList.get(spinner.getSelectedItemPosition()).getId();
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String token = preferences.getString("token", "");
        Call<String> call = service.createComplaint(category, description, imgStr, token, issueLoc.latitude, issueLoc.longitude);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject data = new JSONObject(response.body().toString());
                    if(data.getBoolean("success")){
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }else{
                        Snackbar.make(getView(), "Please trying again...", Snackbar.LENGTH_SHORT).show();
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

    @Override
    public void onMarkerDragStart(Marker marker) {
        locinfo.setText(marker.getPosition().latitude+", "+marker.getPosition().longitude);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        locinfo.setText(marker.getPosition().latitude+", "+marker.getPosition().longitude);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        locinfo.setText(marker.getPosition().latitude+", "+marker.getPosition().longitude);
        issueLoc = new LatLng(myMarker.getPosition().latitude,myMarker.getPosition().longitude);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (myMarker == null) {

                    // Marker was not set yet. Add marker:
                    myMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Your marker title")
                            .snippet("Your marker snippet"));
                    locinfo.setText(myMarker.getPosition().latitude+", "+myMarker.getPosition().longitude);
                    issueLoc = new LatLng(myMarker.getPosition().latitude,myMarker.getPosition().longitude);
                    myMarker.setDraggable(true);

                }
                else {

                    // Marker already exists, just update it's position
                    myMarker.setPosition(latLng);
                    locinfo.setText(myMarker.getPosition().latitude+", "+myMarker.getPosition().longitude);
                    issueLoc = new LatLng(myMarker.getPosition().latitude,myMarker.getPosition().longitude);

                }
            }
        });
        googleMap.setOnMarkerDragListener(this);
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(getContext());

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG,100,bytes);
        byte[] imgBytes = bytes.toByteArray();
        imgStr = Base64.encodeToString(imgBytes,Base64.DEFAULT);
        Log.d("XXX", imgStr);
        img.setImageBitmap(thumbnail);
    }

    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        img.setImageBitmap(bm);
    }
}
