package com.example.wherearyou;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // 하단 네비게이션 바
    private TextView mTextMessage;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:       // 홈 화면
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_friends:    // 친구 화면
                    mTextMessage.setText(R.string.title_friends);
                    return true;
                case R.id.navigation_setting:    // 설정 화면
                    mTextMessage.setText(R.string.title_setting);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 하단 내비게이션 바 표시
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // TODO: 지도 연동 (김학률)
    }
}
