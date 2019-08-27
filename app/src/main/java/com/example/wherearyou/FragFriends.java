package com.example.wherearyou;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragFriends extends Fragment {
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_friends, container, false);

        ImageButton addFriend = (ImageButton) view.findViewById(R.id.friend_add);
        addFriend.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getContext(), PopupFindFriend.class);
                startActivityForResult(intent, 1);
            }
        });
        return view;
    }
}
