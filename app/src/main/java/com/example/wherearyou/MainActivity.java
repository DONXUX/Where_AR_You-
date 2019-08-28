package com.example.wherearyou;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

// 메인 화면 액티비티 클래스
public class MainActivity extends AppCompatActivity {
    public static FragmentManager fm;
    public static FragmentTransaction tran;
    public static FragHome fragHome;
    public static FragFriends fragFriends;
    public static FragSettings fragSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 하단 내비게이션 바 표시
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragHome = new FragHome();
        fragFriends = new FragFriends();
        fragSettings = new FragSettings();
        // 초기설정화면 표시(홈)
        setFrag(0);

    }

    // 하단 네비게이션 바 리스너 메소드
    // (하단 네비게이션 바는 홈, 친구, 설정으로 이루어져있으며
    // 해당 아이콘을 터치 시 플레그먼트를 이용해 화면 전환이 이루어짐)
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                // 홈 터치 시 홈 화면으로 전환
                case R.id.navigation_home:
                    setFrag(0);
                    return true;
                // 친구 터치 시 친구 화면으로 전환
                case R.id.navigation_friends:
                    setFrag(1);
                    return true;
                // 설정 터치 시 설정 화면으로 전환
                case R.id.navigation_setting:
                    setFrag(2);
                    return true;
            }
            return false;
        }
    };

    // 프레그먼트 교체 메소드
    public void setFrag(int n){
        fm = getSupportFragmentManager();
        tran = fm.beginTransaction();
        switch (n){
            case 0:
                tran.replace(R.id.main_frame, fragHome);  //replace의 매개변수는 (프래그먼트를 담을 영역 id, 프래그먼트 객체)
                tran.commit();
                break;
            case 1:
                tran.replace(R.id.main_frame, fragFriends);  //replace의 매개변수는 (프래그먼트를 담을 영역 id, 프래그먼트 객체)
                tran.commit();
                break;
            case 2:
                tran.replace(R.id.main_frame, fragSettings);  //replace의 매개변수는 (프래그먼트를 담을 영역 id, 프래그먼트 객체)
                tran.commit();
                break;
        }
    }
}