package com.example.wherearyou;

import android.content.Intent;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;
import android.net.Uri;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


/*
    로그인화면 기능을 관리하는 클래스
*/

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    SignInButton Google_Login;
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "Login Activity";

    private VideoView videoBG;
    private Button Sign_in_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 백그라운드 영상 재생
        playBackgroundVideo();

        // 구글 로그인 옵션 객체 : 옵션을 관리해주는 클래스로 API 키 값과 요청할 값이 저장되어 있음.
        // 이를 이용해 Google API 클라이언트 생성
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();
        Google_Login = findViewById(R.id.Google_Login);

        Google_Login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                // API로부터 인텐트를 불러와 액티비티를 시작
                Toast.makeText(LoginActivity.this, "액티비티 실행", Toast.LENGTH_SHORT).show();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    // 백그라운드 영상 재생 메소드
    public void playBackgroundVideo(){
        videoBG = (VideoView)findViewById(R.id.title_video);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_title_video);
        videoBG.setVideoURI(uri);
        videoBG.start();
        Log.d(TAG, "영상시작: ");
        videoBG.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            // 반복재생
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });
    }

    public void stopBackgroundVideo(){
        videoBG = (VideoView)findViewById(R.id.title_video);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_title_video);
        videoBG.setVideoURI(uri);
        videoBG.stopPlayback();
        Log.d(TAG, "영상멈춤: ");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Toast.makeText(LoginActivity.this, "ActivityResult Run", Toast.LENGTH_SHORT).show();
            // 로그인 성공 시
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(LoginActivity.this, "구글 로그인 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "인증 실패", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "구글 로그인 인증 성공", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onPause(){
        super.onPause();
        try {
            stopBackgroundVideo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        try {
            playBackgroundVideo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
