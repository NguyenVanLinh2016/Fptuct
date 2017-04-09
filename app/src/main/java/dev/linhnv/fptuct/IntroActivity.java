package dev.linhnv.fptuct;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by linhnv on 01/03/2017.
 */

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        //Thêm slide vào Intro
        //Có thể thay thế bằng Fragment
        addSlide(AppIntroFragment.newInstance("Xin chào, đây là Đại học FPT Cần Thơ", "Đăng ký và kết nối với chúng tôi", R.drawable.logo,
                getResources().getColor(R.color.colorOrangeLight)));
        addSlide(AppIntroFragment.newInstance("Trắc nghiệm tính cách về nghề nghiệp", "Bạn sẽ nhận được một món quà sau khi làm bài trắc nghiệm. Nhanh tay lên nào.", R.drawable.present,
                getResources().getColor(R.color.colorOrangeLight)));
        addSlide(AppIntroFragment.newInstance("Kết nối", "Đây sẽ là nơi chia sẻ kiến thức cũng như giải đáp các thắc mắc của bạn về Đại học FPT Cần Thơ", R.drawable.connect,
                getResources().getColor(R.color.colorOrangeLight)));

        //Màu nên cho thanh điều hường
        setBarColor(getResources().getColor(R.color.colorOrangeLight));
        //Màu của vạch ngăn cách
        setSeparatorColor(getResources().getColor(R.color.colorOrangeLight));

        //Nút Skip và nút tiến trình
        showSkipButton(true);
        setProgressButtonEnabled(true);

        setVibrate(true);
        setVibrateIntensity(30);
    }
    //Sự kiện khi bỏ qua Intro
    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startActivity(new Intent(IntroActivity.this, LoginActivity.class));
        finish();
        SharedPreferences prefs = getSharedPreferences("intro", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("check", 1);
        editor.commit();
    }

    //Sự kiện khi hoàn thành Intro
    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(new Intent(IntroActivity.this, LoginActivity.class));
        finish();
        SharedPreferences prefs = getSharedPreferences("intro", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("check", 1);
        editor.commit();
    }

    //Sự kiện khi slide thay đổi
    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }
}
