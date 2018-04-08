package hackdotslash.curlyenigma.resolve.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import org.json.JSONException;
import org.json.JSONObject;

import hackdotslash.curlyenigma.resolve.MainActivity;
import hackdotslash.curlyenigma.resolve.R;
import hackdotslash.curlyenigma.resolve.ResolveService;
import hackdotslash.curlyenigma.resolve.adapters.HomeAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RegisterFragment extends Fragment {


    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword, editTextCPassword;
    private ResolveService service;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.title_register);
        if(container != null)
            container.removeAllViews();
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        Button buttonRegister = view.findViewById(R.id.buttonRegister);

        editTextFirstName = view.findViewById(R.id.editTextFirstName);
        editTextLastName = view.findViewById(R.id.editTextLastName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextCPassword = view.findViewById(R.id.editTextCPassword);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(ResolveService.BASE_URL).build();
        service = retrofit.create(ResolveService.class);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        return view;
    }

    private void register() {
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        String fname = editTextFirstName.getText().toString().trim();
        String lname = editTextLastName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String cpassword = editTextCPassword.getText().toString().trim();
        if(fname.length() > 0 && lname.length() > 0 && email.length() > 0 && password.length() > 0 && cpassword.length() > 0){
            if(password.equals(cpassword)){
                Call<String> call = service.register(fname, lname, email, password);
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
            }else{
                Snackbar.make(getView(), "Both passwords do not match", Snackbar.LENGTH_SHORT).show();
            }
        }else{
            Snackbar.make(getView(), "Please complete all the fields", Snackbar.LENGTH_SHORT).show();
        }
    }
}
