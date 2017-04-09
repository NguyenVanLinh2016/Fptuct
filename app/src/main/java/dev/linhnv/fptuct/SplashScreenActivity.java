package dev.linhnv.fptuct;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity{
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    ImageView img_logo;
    TextView tv_logo;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        img_logo = (ImageView)findViewById(R.id.img_logo);


        ObjectAnimator anim1 = ObjectAnimator.ofFloat(img_logo, "translationY", -200, 0);
        anim1.setDuration(2500);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(img_logo, "alpha", 0, 1);
        anim2.setDuration(2500);
        AnimatorSet ans1 = new AnimatorSet();
        ans1.play(anim1).with(anim2);
        ans1.start();


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences("intro", Context.MODE_PRIVATE);
                SharedPreferences shared = getSharedPreferences("token", Context.MODE_PRIVATE);
                if(prefs!=null){
                    int firstTime = prefs.getInt("check", 0);
                    if(firstTime==0){
                        Intent i = new Intent(SplashScreenActivity.this, IntroActivity.class);
                        startActivity(i);
                        finish();
                    }else {
                        if(shared != null){
                            token = shared.getString("token", "");
                            String password = shared.getString("password", "");

                            if(token.length() == 0 && password.length() == 0){
                                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                                finish();
                            }else{
                                startActivity(new Intent(SplashScreenActivity.this, MenuQuizActivity.class));
                                finish();
                            }
                        }
                    }
                }else {
                    Intent i = new Intent(SplashScreenActivity.this, IntroActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
