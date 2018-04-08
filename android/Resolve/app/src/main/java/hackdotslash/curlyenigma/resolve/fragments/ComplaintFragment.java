package hackdotslash.curlyenigma.resolve.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import hackdotslash.curlyenigma.resolve.R;
import hackdotslash.curlyenigma.resolve.ResolveService;
import hackdotslash.curlyenigma.resolve.adapters.HomeAdapter;
import hackdotslash.curlyenigma.resolve.models.Complaint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ComplaintFragment extends Fragment {

    private ResolveService service;
    private String id;
    private ProgressDialog progressDialog;
    private Complaint complaint;
    private TextView textViewDescription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.title_home);
        if(container != null)
            container.removeAllViews();
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Bundle args = getArguments();
        id = args.getString("id");

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        textViewDescription = view.findViewById(R.id.textViewDescription);;

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(ResolveService.BASE_URL).build();
        service = retrofit.create(ResolveService.class);

        fetchDetails();

        return view;
    }

    private void fetchDetails() {
        progressDialog.setMessage("Fetching details...");
        progressDialog.show();
        Call<String> call = service.complaint(id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject data = new JSONObject(response.body().toString());
                    if(data.getBoolean("success")){
                        complaint = Complaint.fromJSON(data.getJSONObject("complaint"));
                        updateViews();
                    }else{
                        Snackbar.make(getView(), "Please try again...", Snackbar.LENGTH_SHORT).show();
                    }
                    progressDialog.hide();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Snackbar.make(getView(), "Please try again...", Snackbar.LENGTH_SHORT).show();
                progressDialog.hide();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Snackbar.make(getView(), "Please try again...", Snackbar.LENGTH_SHORT).show();
                progressDialog.hide();
            }
        });
    }

    private void updateViews() {
        textViewDescription.setText(complaint.getDescription());
    }
}
