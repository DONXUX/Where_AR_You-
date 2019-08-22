package com.example.wherearyou;

import android.location.Location;
import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToDB{
    private FirebaseAuth mAuth;
    private String currentUser;
    private FirebaseUser user;
    public static String EmailToId;

    //////////////////////현재시간//////////////////////
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat sdf = new SimpleDateFormat();
    String getTime = sdf.format(date);
    ////////////////////////////////////////////////////

    /*
    transferToDB : 파이어베이스 데이터베이스에 ID, 현재 위도와 경도, 주소, 현재 시간을 넘겨줌
   */
    public void transferToDB(Location location, String markerTitle){

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        currentUser = mAuth.getCurrentUser().getEmail();
        Pattern p = Pattern.compile("([a-zA-Z0-9]*)@(.*)");
        Matcher m = p.matcher(currentUser);
        if(m.find()){
            EmailToId = m.group(1);
        }

        String userName = user.getDisplayName();
        Uri photoUri = user.getPhotoUrl();

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = mRootRef.child("User");
        DatabaseReference nameRef = userRef.child(EmailToId);
        DatabaseReference userNameRef = nameRef.child("이름");
        DatabaseReference photoRef = nameRef.child("사진");
        DatabaseReference latitudeRef = nameRef.child("위도");
        DatabaseReference longitudeRef = nameRef.child("경도");
        DatabaseReference addressRef = nameRef.child("주소");
        DatabaseReference timeRef = nameRef.child("시간");

        userNameRef.setValue(userName);
        photoRef.setValue(photoUri.toString());
        latitudeRef.setValue(location.getLatitude());
        longitudeRef.setValue(location.getLongitude());
        addressRef.setValue(markerTitle);
        timeRef.setValue(getTime);
    }
}
