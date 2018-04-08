package hackdotslash.curlyenigma.resolve.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import hackdotslash.curlyenigma.resolve.R;

public class LoginFragment extends Fragment {
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
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment f = new HomeFragment();
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.frame_layout_main, new HomeFragment())
                        .commit();
            }
        });
        return view;
    }


}