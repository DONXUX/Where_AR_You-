package com.example.wherearyou;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FragFriends extends Fragment {
    View view;
    DatabaseReference mReference;
    ToDB db;
    String userId;

    static String[] friend = new String[100];
    static String[] photoUrl = new String[100];
    int i = 0;
    int j = 0;
    int cnt = 0;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        mReference = FirebaseDatabase.getInstance().getReference();
        userId = db.EmailToId;
        view = inflater.inflate(R.layout.layout_friends, null);

        mReference.child("User").child(userId).child("친구").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                ListView listView;
                ListViewAdapter adapter = new ListViewAdapter();
                listView = (ListView) view.findViewById(R.id.friends_list);
                listView.setAdapter(adapter);
                i=0;
                j=0;
                cnt = 0;
                Log.d(TAG, "로그" + dataSnapshot.getChildrenCount());

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    friend[i] = dataSnapshot1.getValue(String.class);
                    mReference.child("User").child(friend[i]).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            photoUrl[j] = dataSnapshot.child("사진").getValue(String.class);
                            Log.d(TAG, "포토사진1" + photoUrl[i]);
                            j++;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    i++;
                    cnt++;
                    if(dataSnapshot.getChildrenCount() == cnt){
                        for(int a = 0; a < i ; a++){
                            adapter.addItem(friend[a], photoUrl[a]);
                        }
                    }
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
}