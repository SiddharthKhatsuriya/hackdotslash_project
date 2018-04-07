package hackdotslash.curlyenigma.resolve;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import hackdotslash.curlyenigma.resolve.fragments.LoginFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout frameLayoutMain = findViewById(R.id.frame_layout_main);
        LoginFragment fragmentLogin = new LoginFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_layout_main, fragmentLogin)
                .addToBackStack("login")
                .commit();
    }
}
