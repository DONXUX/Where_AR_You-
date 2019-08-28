package com.example.wherearyou;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsSub extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    LayoutInflater mInflater;
    LinearLayout mRootLinear;
    CircleImageView friendPhoto;
    TextView friendName;
    Button friendSubApply;
    Button friendSubRefuse;
    String userId;
    ToDB db;
    Bitmap bitmap;
    int sub_info_id = 0;                // 친구신청 프로필의 프로필 사진과 이름 ID 시작 값
    int sub_info_btn_id = 10000;        // 친구신청 프로필의 수락 버튼과 거절 버튼 ID 시작 값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_sub);

        mRootLinear = (LinearLayout) findViewById(R.id.friends_sub_list);
        mInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();
        userId = db.EmailToId;

        // 친구 신청 수신 리스너
        mReference.child("User").child(userId).child("친구신청").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mReference.child("User").child(dataSnapshot.getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        final View view = mInflater.inflate(R.layout.activity_friends_sub_info, mRootLinear, true);

                        friendPhoto = (CircleImageView)view.findViewById(R.id.friend_sub_photo);
                        friendPhoto.setId(sub_info_id++);
                        friendName = (TextView)view.findViewById(R.id.friend_sub_name);
                        friendName.setId(sub_info_id++);

                        final String photoUrl = dataSnapshot.child("사진").getValue().toString();
                        Thread mThread = new Thread() {
                            @Override
                            public void run(){
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
                                } catch(IOException ex) {

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
                        friendName.setText(dataSnapshot.child("이름").getValue().toString());

                        // 수락 버튼
                        friendSubApply = (Button)view.findViewById(R.id.sub_apply_btn);
                        friendSubApply.setId(sub_info_btn_id++);
                        friendSubApply.setOnClickListener(new Button.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                // 친구 정보에 저장
                                saveFriend(dataSnapshot);
                                // 친구 신청 목록에서 삭제
                                deleteSubFriend(dataSnapshot);
                            }
                        });

                        // 거절 버튼
                        friendSubRefuse = (Button)view.findViewById(R.id.sub_refuse_btn);
                        friendSubRefuse.setId(sub_info_btn_id++);
                        friendSubRefuse.setOnClickListener(new Button.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                deleteSubFriend(dataSnapshot);
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

            public void deleteSubFriend(DataSnapshot dataSnapshot) {
                // 친구 신청 목록에서 삭제
                String friendId = dataSnapshot.getKey();
                Query query = mReference.child("User").child(userId).child("친구신청").orderByValue().equalTo(friendId);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot idSnapshot : dataSnapshot.getChildren()) {
                            String idKey = idSnapshot.getKey();
                            mReference.child("User").child(userId).child("친구신청").child(idKey).removeValue();

                            //TODO : 친구 신청 목록에서 수락 및 거절할 경우 실시간으로 삭제되는 뷰 구현 (이동욱)
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            public void saveFriend(DataSnapshot dataSnapshot) {
                String friendId = dataSnapshot.getKey();
                mReference.child("User").child(userId).child("친구").push().setValue(friendId);
                mReference.child("User").child(friendId).child("친구").push().setValue(userId);
            }
        });
        ////////////////////////////////////


    }
}
