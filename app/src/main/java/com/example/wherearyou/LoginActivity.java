package com.example.wherearyou;

import android.content.Intent;
import android.media.MediaPlayer;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.VideoView;
import android.net.Uri;

/*
    로그인화면 기능을 관리하는 클래스
*/

public class LoginActivity extends AppCompatActivity {
    private VideoView videoBG;
    private Button Sign_in_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 백그라운드 영상 재생
        playBackgroundVideo();

        // SIGN IN 버튼을 누를 시 메인 화면으로 전환
        Sign_in_btn = (Button)findViewById(R.id.sign_in_btn);
        Sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                // TODO: 로그인 연동 (김청은)

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // 백그라운드 영상 재생 함수
    public void playBackgroundVideo(){
        videoBG = (VideoView)findViewById(R.id.title_video);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_title_video);
        videoBG.setVideoURI(uri);
        videoBG.start();
        videoBG.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            // 반복재생
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });
    }
}
