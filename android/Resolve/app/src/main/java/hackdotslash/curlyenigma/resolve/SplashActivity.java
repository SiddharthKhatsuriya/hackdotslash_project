package hackdotslash.curlyenigma.resolve;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import hackdotslash.curlyenigma.resolve.fragments.LoginFragment;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent activity = new Intent(this, MainActivity.class);
        startActivity(activity);
        finish();
    }
}
