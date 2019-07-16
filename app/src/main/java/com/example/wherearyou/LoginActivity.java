package com.example.wherearyou;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;
import android.net.Uri;

/*
    로그인화면 기능을 관리하는 클래스
*/

public class LoginActivity extends AppCompatActivity {
    private VideoView videoBG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 백그라운드 영상 재생 함수
        playBackgroundVideo();
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
