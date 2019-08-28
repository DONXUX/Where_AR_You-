package com.example.wherearyou;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class FragFriends extends Fragment {
    View view;
    DatabaseReference mReference;
    ToDB db;
    String userId;
    LinearLayout mRootLinear;
    CircleImageView friendPhoto;
    TextView friendName;
    Button friendSearchBtn;
    Bitmap bitmap;
    int friend_id_num = 20000;          // 프로필 사진과 이름의 ID 시작 값
    int friend_search_btn_id = 30000;   // 프로필의 찾기 버튼 ID 시작 값
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_friends, container, false);
        mReference = FirebaseDatabase.getInstance().getReference();
        userId = db.EmailToId;
        mRootLinear = (LinearLayout) view.findViewById(R.id.friends_list);

        // 친구 목록 수신 리스너
        mReference.child("User").child(userId).child("친구").addChildEventListener(new ChildEventListener() {
            @Override
            // 친구 목록에 데이터가 추가 될 때마다 실행되는 메소드
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mReference.child("User").child(dataSnapshot.getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        View mView = inflater.inflate(R.layout.layout_friends_info, mRootLinear, true);

                        friendPhoto = (CircleImageView)mView.findViewById(R.id.friend_photo);
                        // 각 프로필 정보의 사진마다 다른 id를 지정 (기본적으로 20000~29999 짝수 id에 해당)
                        friendPhoto.setId(friend_id_num++);
                        friendName = (TextView)mView.findViewById(R.id.friend_name);
                        // 각 프로필 정보의 이름마다 다른 id를 지정 (기본적으로 20000~29999 홀수 id에 해당)
                        friendName.setId(friend_id_num++);

                        // URL로 부터 프로필 사진 불러오는 쓰레드
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
                        ///////////////////////////

                        // 이름 불러오기
                        friendName.setText(dataSnapshot.child("이름").getValue().toString());

                        // 버튼 불러오기
                        friendSearchBtn = (Button)mView.findViewById(R.id.search_btn);
                        // 각 프로필 정보의 찾기 버튼마다 다른 id를 지정 (기본적으로 30000부터 1씩 올림)
                        friendSearchBtn.setId(friend_search_btn_id++);

                        // 친구 찾기 버튼
                        friendSearchBtn.setOnClickListener(new Button.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                //TODO : 친구 찾기 기능 (권영민)
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

        // 친구 추가 팝업
        ImageButton addFriend = (ImageButton) view.findViewById(R.id.friend_add);
        addFriend.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getContext(), PopupFindFriend.class);
                startActivityForResult(intent, 1);
            }
        });
        ////////////////////

        // 친구 신청 목록
        ImageButton subFriendList = (ImageButton) view.findViewById(R.id.friends_sub_list_btn);
        subFriendList.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getContext(), FriendsSub.class);
                startActivityForResult(intent, 1);
            }
        });
        ////////////////////
        return view;
    }
}