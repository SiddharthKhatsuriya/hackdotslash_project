package hackdotslash.curlyenigma.resolve.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;

import hackdotslash.curlyenigma.resolve.R;
import hackdotslash.curlyenigma.resolve.ResolveService;
import hackdotslash.curlyenigma.resolve.SplashActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoginFragment extends Fragment {
    EditText editTextEmail, editTextPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            getActivity().setTitle(R.string.login_title);
        }catch (Exception e){
            Log.d("HACKDOTSLASH", e.getMessage());
        }
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Button buttonLogin = view.findViewById(R.id.buttonLogin);
        Button buttonRegister = view.findViewById(R.id.buttonRegister);


        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment f = new RegisterFragment();
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .add(f, "register")
                        .commit();
            }
        });


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment f = new RegisterFragment();
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.frame_layout_main, f)
                        .addToBackStack("register")
                        .commit();
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(ResolveService.BASE_URL).build();
        final ResolveService service = retrofit.create(ResolveService.class);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                Call<String> token = service.authenticate(email, password);
                token.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            JSONObject data = new JSONObject(response.body().toString());
                            if(data.getBoolean("success")){
                                // save token
                                SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("token", data.getString("token"));
                                editor.commit();

                                Intent splash = new Intent(getActivity().getBaseContext(), SplashActivity.class);
                                startActivity(splash);
                                getActivity().finish();
                            }else{
                                Snackbar.make(getView(), "Incorrect email/password", Snackbar.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("XXX", t.getMessage());
                    }
                });
            }
        });
        return view;
    }


}
