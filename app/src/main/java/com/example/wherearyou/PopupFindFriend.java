package com.example.wherearyou;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PopupFindFriend extends Activity {
    private EditText editText;
    private String friendId;
    private Button addBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser myInfo;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;
    boolean existAccount, existSubFriend, existFriend, existSubRecptionFriend, equalId;
    ToDB db = new ToDB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_find_friend);
        mAuth = FirebaseAuth.getInstance();
        myInfo = mAuth.getCurrentUser();
        editText = (EditText) findViewById(R.id.editText_user_id);
        addBtn = (Button) findViewById(R.id.add_friend_btn);
        mAuth = FirebaseAuth.getInstance();

        // 친구 신청 이벤트
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReference = FirebaseDatabase.getInstance().getReference();
                friendId = editText.getText().toString();
                mReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        existAccount = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if(snapshot.getKey().equals(friendId)) {
                                existAccount = true;

                                // 자신의 아이디인지 검사87
                                equalId = false;
                                if(db.EmailToId.equals(friendId)){
                                    Toast.makeText(getApplicationContext(), "자신의 계정은 추가 할 수 없습니다.", Toast.LENGTH_LONG).show();
                                    equalId = true;
                                    break;
                                }
                                //////////////////////////

                                // 중복 검사
                                existSubFriend = false;
                                for(DataSnapshot snapshotSubFriends : dataSnapshot.child(friendId).child("친구신청").getChildren()) {
                                    if(snapshotSubFriends.getValue().equals(db.EmailToId)) {
                                        Toast.makeText(getApplicationContext(), "이미 친구 신청을 보냈습니다.", Toast.LENGTH_LONG).show();
                                        existSubFriend = true;
                                        break;
                                    }
                                }
                                //////////////////////////

                                // 친구 목록 중복 검사
                                existFriend = false;
                                for(DataSnapshot snapshotFriends : dataSnapshot.child(db.EmailToId).child("친구").getChildren()){
                                    if(snapshotFriends.getValue().equals(friendId)) {
                                        Toast.makeText(getApplicationContext(), "이미 친구입니다.", Toast.LENGTH_LONG).show();
                                        existFriend = true;
                                        break;
                                    }
                                }
                                //////////////////////////

                                // 이미 상대방으로부터 신청이 왔는데 친구 신청 하려는 경우 검사
                                existSubRecptionFriend = false;
                                for(DataSnapshot snapshotFriends : dataSnapshot.child(db.EmailToId).child("친구신청").getChildren()){
                                    if(snapshotFriends.getValue().equals(friendId)) {
                                        Toast.makeText(getApplicationContext(), "이미 친구신청이 와있습니다.\n친구신청 목록을 확인해주세요.", Toast.LENGTH_LONG).show();
                                        existSubRecptionFriend = true;
                                        break;
                                    }
                                }
                                //////////////////////////

                                if(!existSubRecptionFriend && !existFriend && !existSubFriend && !equalId) {
                                    Toast.makeText(getApplicationContext(), "친구 신청을 보냈습니다.", Toast.LENGTH_LONG).show();
                                    mReference.child("User").child(friendId).child("친구신청").push().setValue(db.EmailToId);
                                    break;

                                }
                            }
                        }
                        if(!existAccount){
                            Toast.makeText(getApplicationContext(), "존재하지 않는 계정입니다.", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
