package com.example.wherearyou;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
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
import com.google.firebase.database.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class FragFriends extends Fragment {
    View view;
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
    int friend_id_num = 20000;          // 프로필 사진과 이름의 ID 시작 값
    int friend_search_btn_id = 30000;   // 프로필의 찾기 버튼 ID 시작 값


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mReference = FirebaseDatabase.getInstance().getReference();
        userId = db.EmailToId;
        view = inflater.inflate(R.layout.layout_friends, container, false);

        mReference.child("User").child(userId).child("친구").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    buttonEvent(inflater);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // 친구 추가 팝업
        ImageButton addFriend = (ImageButton) view.findViewById(R.id.friend_add);
        addFriend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PopupFindFriend.class);
                startActivityForResult(intent, 1);
            }
        });
        ////////////////////

        // 친구 신청 목록
        ImageButton subFriendList = (ImageButton) view.findViewById(R.id.friends_sub_list_btn);
        subFriendList.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FriendsSub.class);
                startActivityForResult(intent, 1);
            }
        });
        ////////////////////
        return view;
    }

    private void buttonEvent(final LayoutInflater inflater){
        mReference = FirebaseDatabase.getInstance().getReference();
        userId = ToDB.EmailToId;
        mRootLinear = (LinearLayout) view.findViewById(R.id.friends_list);
        // 친구 목록 수신 리스너
        mReference.child("User").child(userId).child("친구").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                final View mView = inflater.inflate(R.layout.layout_friends_info, mRootLinear, true);
                friendSearchBtn = (Button)mView.findViewById(R.id.search_btn);
                friendSearchingBtn = (Button)mView.findViewById(R.id.searching_btn);
                friendLocationApply = (Button)mView.findViewById(R.id.sub_apply_btn);
                friendLocationReject = (Button)mView.findViewById(R.id.sub_reject_btn);
                locationSharing = (Button)mView.findViewById(R.id.sharing_btn);
                permissionInfo(mView, dataSnapshot);

                // 버튼 불러오기
                // 각 프로필 정보의 찾기 버튼마다 다른 id를 지정 (기본적으로 30000부터 1씩 올림)
                friendSearchBtn.setId(friend_search_btn_id++);

                if(dataSnapshot.child("아이디").getValue() != null){
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
                }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

    private void permissionInfo(View mView, DataSnapshot dataSnapshot) {
        friendPhoto = (CircleImageView) mView.findViewById(R.id.friend_photo);
        Log.d("TAG", "에러원인1: " + friendPhoto);
        Log.d("TAG", "에러원인2: " + friend_id_num);
        // 각 프로필 정보의 사진마다 다른 id를 지정 (기본적으로 20000~29999 짝수 id에 해당)
        friendPhoto.setId(friend_id_num++);
        Log.d("TAG", "에러원인3: " + friend_id_num);
        friendName = (TextView) mView.findViewById(R.id.friend_name);
        Log.d("TAG", "프렌드네임: " + friend_id_num);
        // 각 프로필 정보의 이름마다 다른 id를 지정 (기본적으로 20000~29999 홀수 id에 해당)
        friendName.setId(friend_id_num++);
        Log.d("TAG", "에러원인4: " + friend_id_num);
        // URL로 부터 프로필 사진 불러오는 쓰레드
        if (dataSnapshot.child("사진").getValue() != null) {
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
}