package com.example.wherearyou;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

// 메인 화면 액티비티 클래스
public class MainActivity extends AppCompatActivity {
    FragmentManager fm;
    FragmentTransaction tran;
    FragHome fragHome;
    FragFriends fragFriends;
    FragSettings fragSettings;

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

// 홈 화면 플레그먼트 클래스
class FragHome extends Fragment implements OnMapReadyCallback {
    View view;
    private MapView mapView = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_home, container, false);

        mapView = (MapView)view.findViewById(R.id.map);
        mapView.getMapAsync(this);

        return view;
    }

    // 이하 Google Map API 메소드
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }

    @Override
    // 액티비티가 처음 생성될 때 실행되는 메소드
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MapsInitializer.initialize(getActivity().getApplicationContext());

        if(mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

    // 구글 맵 초기설정 메소드
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng SEOUL = new LatLng(37.56, 126.97);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("여기는 서울이다 씨발");
        markerOptions.snippet("여기는 씨발 서울이다");
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));

        // TODO : 사용자 위치 정보 권한 획득, 현재 위치 View
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}

// 친구 화면 플레그먼트 클래스
class FragFriends extends Fragment {
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_friends, container, false);

        return view;
    }
}

// 설정 화면 플레그먼트 클래스
class FragSettings extends Fragment {
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_settings, container, false);

        return view;
    }
}

