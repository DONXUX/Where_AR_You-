package com.example.wherearyou;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class FriendWindow extends AppCompatActivity {

    DatabaseReference mReference;
    ToDB db;
    String userId;
    LinearLayout mRootLinear;
    CircleImageView friendPhoto;
    TextView friendName;
    Button friendSearchBtn;
    Button friendSearchingBtn;
    Button friendLocationApply;
    Button friendLocationReject;
    Button locationSharing;
    FragHome fragHome = new FragHome();
    Bitmap bitmap;
    String friend_id;
    String locationPermissionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_window);

        mReference = FirebaseDatabase.getInstance().getReference();
        userId = db.EmailToId;
        // 친구 목록 수신 리스너
        mReference.child("User").child(userId).child("친구").addChildEventListener(new ChildEventListener() {
            @Override
            // 친구 목록에 데이터가 추가 될 때마다 실행되는 메소드
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mReference.child("User").child(dataSnapshot.getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        friendSearchBtn = (Button)findViewById(R.id.search_btn);
                        friendSearchingBtn = (Button)findViewById(R.id.searching_btn);
                        friendLocationApply = (Button)findViewById(R.id.sub_apply_btn);
                        friendLocationReject = (Button)findViewById(R.id.sub_reject_btn);
                        locationSharing = (Button)findViewById(R.id.sharing_btn);
                        permissionInfo(dataSnapshot);

                        // 버튼 불러오기
                        // 각 프로필 정보의 찾기 버튼마다 다른 id를 지정 (기본적으로 30000부터 1씩 올림)

                        friend_id = dataSnapshot.child("아이디").getValue().toString();
                        final DatabaseReference locationPermission = mReference.child("User").child(friend_id).child("위치정보요청").child("아이디");
                        final DatabaseReference sharing = mReference.child("User").child(friend_id).child("위치정보허용").child("상태");
                        final DatabaseReference locationPermissionMe = mReference.child("User").child(userId).child("위치정보요청").child("아이디");
                        locationPermissionMe.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                locationPermissionId = dataSnapshot.getValue(String.class);

                                if(friend_id.equals(locationPermissionId)){
                                    friendSearchBtn.setVisibility(GONE);
                                    friendSearchingBtn.setVisibility(GONE);
                                    friendLocationApply.setVisibility(VISIBLE);
                                    friendLocationReject.setVisibility(VISIBLE);
                                    //연결끊기 버튼 만들고 액션 생성
                                    friendLocationApply.setOnClickListener(new Button.OnClickListener(){
                                        @Override
                                        public void onClick(View v){
                                            // 수락시 상대방에게 동의했다고 알림
                                            fragHome.locationPermissionBoolean = true;
                                            sharing.setValue("true");
                                            friendLocationApply.setVisibility(GONE);
                                            friendLocationReject.setVisibility(GONE);
                                            locationSharing.setVisibility(VISIBLE);

                                            locationPermissionMe.removeValue();
                                        }
                                    });

                                    friendLocationReject.setOnClickListener(new Button.OnClickListener(){
                                        @Override
                                        public void onClick(View v){

                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                        //본인의 위치정보허용에 요청이 있으면 수락 거절 버튼 생성

                        // 친구 찾기 버튼
                        friendSearchBtn.setOnClickListener(new Button.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                //TODO : 친구 찾기 기능 (권영민)
                                //찾기 버튼을 눌렀을 시 내 아이디가 친구 위치요청디비에 저장 및 버튼이 요청중.. 으로 변경
                                locationPermission.setValue(userId);
                                friendSearchBtn.setVisibility(GONE);
                                friendSearchingBtn.setVisibility(VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void permissionInfo(DataSnapshot dataSnapshot) {
        friendPhoto = (CircleImageView) findViewById(R.id.friend_photo);


        friendName = (TextView) findViewById(R.id.friend_name);

        // URL로 부터 프로필 사진 불러오는 쓰레드
        final String photoUrl = dataSnapshot.child("사진").getValue().toString();
        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = null;
                    try {
                        url = new URL(photoUrl);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = null;
                    try {
                        is = conn.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (IOException ex) {

                }
            }
        };

        mThread.start();
        try {
            mThread.join();
            friendPhoto.setImageBitmap(bitmap);

        } catch (InterruptedException e) {
            e.printStackTrace();

        }
        ///////////////////////////

        // 이름 불러오기
        friendName.setText(dataSnapshot.child("이름").getValue().toString());
    }

}
