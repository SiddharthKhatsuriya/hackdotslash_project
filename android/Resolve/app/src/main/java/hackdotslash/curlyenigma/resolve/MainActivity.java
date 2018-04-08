package hackdotslash.curlyenigma.resolve;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import hackdotslash.curlyenigma.resolve.fragments.BrowseFragment;
import hackdotslash.curlyenigma.resolve.fragments.HomeFragment;
import hackdotslash.curlyenigma.resolve.fragments.LoginFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout frameLayoutMain = findViewById(R.id.frame_layout_main);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String token = preferences.getString("token", "");

        if(token.length() == 0) {
            LoginFragment fragmentLogin = new LoginFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frame_layout_main, fragmentLogin)
                    .commit();
        }else{
//            HomeFragment homeFragment = new HomeFragment();
            BrowseFragment f = new BrowseFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frame_layout_main, f)
                    .commit();
        }
    }
}
