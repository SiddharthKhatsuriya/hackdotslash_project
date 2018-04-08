package hackdotslash.curlyenigma.resolve.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import hackdotslash.curlyenigma.resolve.R;
import hackdotslash.curlyenigma.resolve.ResolveService;
import hackdotslash.curlyenigma.resolve.adapters.HomeAdapter;
import hackdotslash.curlyenigma.resolve.models.Category;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.app.Activity.RESULT_OK;

public class CreateComplaintFragment extends Fragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PICK_PHOTO_FOR_AVATAR = 2;
    ImageView thumbnail;
    private ResolveService service;
    private EditText editTextDescription, editTextLocation;
    private ProgressDialog progressDialog;
    private Spinner spinner;
    private List<Category> categoryList;
    private String currentPath;
    private File image;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.title_create_complaint);
        setHasOptionsMenu(true);
        if(container != null)
            container.removeAllViews();


        View view = inflater.inflate(R.layout.fragment_create_complaint, container, false);

        thumbnail = view.findViewById(R.id.thumbnail);
        spinner = view.findViewById(R.id.spinner_category);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextLocation = view.findViewById(R.id.editTextLocation);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        File storageDir = getActivity().getExternalCacheDir();
        image = new File(Environment.getExternalStorageDirectory() + "/DCIM/image.png");
        currentPath = image.getAbsolutePath();

        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(ResolveService.BASE_URL).build();
        service = retrofit.create(ResolveService.class);

        fetchCategories();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            thumbnail.setImageBitmap(imageBitmap);
        }else if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                image = new File(getActivity().getRea);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                        dispatchTakePictureIntent();
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

    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
////            Uri photoUri = FileProvider.getUriForFile(getContext(), "hackdotslash.curlyenigma.resolve.files", image);
////            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.check_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.done:

                Log.d("XXX", "Sending issue");
                submitIssue();
                break;
        }
        return false;
    }

    private void submitIssue() {
        progressDialog.setMessage("Submitting complaint...");
        progressDialog.show();
        String description = editTextDescription.getText().toString();
        // WARNING: currently using location as title
        String location = editTextLocation.getText().toString();
        String category = categoryList.get(spinner.getSelectedItemPosition()).getId();
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", image.getName(), RequestBody.create(MediaType.parse("image/*"), image));
        MultipartBody.Part dataPart = MultipartBody.Part.createFormData("title", location);
        MultipartBody.Part dataDescription = MultipartBody.Part.createFormData("description", description);
        MultipartBody.Part dataCategory = MultipartBody.Part.createFormData("category", category);
        Call<String> call = service.createComplaint(filePart);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject data = new JSONObject(response.body().toString());
                    if(data.getBoolean("success")){
                        Snackbar.make(getView(), "Complaint registered successfully...", Snackbar.LENGTH_SHORT).show();
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
}
