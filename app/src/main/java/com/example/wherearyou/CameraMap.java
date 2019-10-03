package com.example.wherearyou;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.google.ar.sceneform.rendering.ModelRenderable;

public class CameraMap extends Activity {
    private static final String TAG = CameraMap.class.getSimpleName();

    private CheckBox mCheckBox;
    private GLSurfaceView mSurfaceView;
    private MainRenderer mRenderer;
    private ModelRenderable modelRenderable;

    private boolean mUserRequestedInstall = true;

    private Session mSession;
    private Config mConfig;

    private float[] mProjMatrix = new float[16];
    private float[] mViewMatrix = new float[16];

    private float mLastX;
    private float mLastY;
    private float[] mLastPoint = new float[] { 0.0f, 0.0f, 0.0f };
    private boolean mNewPath = false;
    private boolean mPointAdded = false;

    private static float MIN_DISTANCE = 0.000625f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBarAndTitleBar();
        setContentView(R.layout.activity_camera_map);

        mSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surface_view);

        DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        if (displayManager != null) {
            displayManager.registerDisplayListener(new DisplayManager.DisplayListener() {
                @Override
                public void onDisplayAdded(int displayId) {
                }

                @Override
                public void onDisplayChanged(int displayId) {
                    synchronized (this) {
                        mRenderer.onDisplayChanged();
                    }
                }

                @Override
                public void onDisplayRemoved(int displayId) {
                }
            }, null);
        }

        mRenderer = new MainRenderer(new MainRenderer.RenderCallback() {
            @Override
            // 순서 : 디스플레이 방향에 맞는 설정 ->
            // Renderer에서 생성한 텍슻쳐를 Session 객체와 연결 ->
            // 새로운 프레임이 그려질 때마다 Session 객체 업데이트
            public void preRender() {
                Log.d("TAG", "CameraMap 내 MainRenderer preRender 함수 시작");
                if (mRenderer.isViewportChanged()) {
                    Display display = getWindowManager().getDefaultDisplay();
                    int displayRotation = display.getRotation();
                    // 디스플레이 방향 설정
                    mRenderer.updateSession(mSession, displayRotation);
                }

                // Renderer에서 생성한 텍스쳐를 Session 객체와 연결
                mSession.setCameraTextureName(mRenderer.getTextureId());

                // 프레임 업데이트
                // 카메라 영상, 포인트 클라우드 정보, 디바이스 포즈 등
                Frame frame = null;
                try {
                    frame = mSession.update();
                } catch (CameraNotAvailableException e) {
                    e.printStackTrace();
                }
                if (frame.hasDisplayGeometryChanged()) {
                    // 텍스쳐의 좌표를 디스플레이 방향에 맞게 설정해 주는 함수
                    mRenderer.transformDisplayGeometry(frame);
                }

                // 포인트 클라우드 (현재 프레임에서 추출된 특징 점들의 3차원 좌표값)
                PointCloud pointCloud = frame.acquirePointCloud();
                mRenderer.updatePointCloud(pointCloud);
                pointCloud.release();

                Camera camera = frame.getCamera();
                camera.getProjectionMatrix(mProjMatrix, 0, 0.1f, 100.0f);
                camera.getViewMatrix(mViewMatrix, 0);

                mRenderer.setProjectionMatrix(mProjMatrix);
                mRenderer.updateViewMatrix(mViewMatrix);


                // 스크린 상의 좌표 보다 조금 앞에 있는 터치된 3차원 좌표 값을 구하는 함수(허공)
                //TODO : GPS 친구 좌표 -> 화면의 방향 좌표로 전환
                //TODO : 친구좌표를 전달
                float[] screenPoint = getScreenPoint(mLastX, 300.0f,
                        mRenderer.getWidth(), mRenderer.getHeight(),
                        mProjMatrix, mViewMatrix);

                float[] t = screenPoint;
                float[] r = new float[4];
                r[0]=0.0f; r[1]=0.0f; r[2]=0.0f; r[3]=0.0f;

                Pose pose = new Pose(t, r);
                float[] modelMatrix = new float[16];
                pose.toMatrix(modelMatrix, 0);
                mRenderer.setCubeModelMatrix(modelMatrix);
                Log.d("TAG", "CameraMap 내 MainRenderer preRender 함수 끝");
            }
        });
        mSurfaceView.setPreserveEGLContextOnPause(true);
        mSurfaceView.setEGLContextClientVersion(2);
        mSurfaceView.setRenderer(mRenderer);
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        ModelRenderable.builder()
                .setSource(this, R.raw.splorgdiamond)
                .build()
                .thenAccept(renderable -> modelRenderable = renderable)
                .exceptionally(throwable -> {
                    Toast toast = Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return null;
                });


        Log.d(TAG, "onCreate 끝");

    }

    @Override
    protected void onPause() {
        super.onPause();

        mSurfaceView.onPause();
        mSession.pause();
        Log.d(TAG, "onPause 끝");
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestCameraPermission();

        try {
            if (mSession == null) {
                // ARCore SDK 설치 유무 확인
                switch (ArCoreApk.getInstance().requestInstall(this, mUserRequestedInstall)) {
                    case INSTALLED:
                        mSession = new Session(this);
                        Log.d(TAG, "ARCore 세션 연결");
                        break;
                    case INSTALL_REQUESTED:
                        mUserRequestedInstall = false;
                        Log.d(TAG, "ARCore를 설치하셔아 합니다.");
                        break;
                }
            }
        }
        catch (UnsupportedOperationException e) {
            Log.e(TAG, e.getMessage());
        } catch (UnavailableApkTooOldException e) {
            e.printStackTrace();
        } catch (UnavailableDeviceNotCompatibleException e) {
            e.printStackTrace();
        } catch (UnavailableUserDeclinedInstallationException e) {
            e.printStackTrace();
        } catch (UnavailableArcoreNotInstalledException e) {
            e.printStackTrace();
        } catch (UnavailableSdkTooOldException e) {
            e.printStackTrace();
        }

        mConfig = new Config(mSession);
        if (!mSession.isSupported(mConfig)) {
            Log.d(TAG, "이 기기는 ARCore를 지원하지 않습니다.");
        }
        mSession.configure(mConfig);
        try {
            mSession.resume();
        } catch (CameraNotAvailableException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "onResume 끝");
        mSurfaceView.onResume();
    }

    // 터치로 객체 이동하는 함수 (디버그 용)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mLastX = event.getX();
        mLastY = event.getY();
        Log.d("mLastX : " , Float.toString(mLastX));
        Log.d("mLastY : " , Float.toString(mLastY));
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mNewPath = true;
                mPointAdded = true;
                break;
            case MotionEvent.ACTION_MOVE:
                mPointAdded = true;
                break;
            case MotionEvent.ACTION_UP:
                mPointAdded = false;
                break;
        }
        return true;
    }

    // 스크린 상의 좌표보다 조금 앞에 있는 3차원 좌표 값을 구하는 함수
    public float[] getScreenPoint(float x, float y, float w, float h,
                                  float[] projMat, float[] viewMat) {
        float[] position = new float[3];
        float[] direction = new float[3];

        x = x * 2 / w - 1.0f;
        y = (h - y) * 2 / h - 1.0f;

        float[] viewProjMat = new float[16];
        Matrix.multiplyMM(viewProjMat, 0, projMat, 0, viewMat, 0);

        float[] invertedMat = new float[16];
        Matrix.setIdentityM(invertedMat, 0);
        Matrix.invertM(invertedMat, 0, viewProjMat, 0);

        float[] farScreenPoint = new float[]{x, y, 1.0F, 1.0F};
        float[] nearScreenPoint = new float[]{x, y, -1.0F, 1.0F};
        float[] nearPlanePoint = new float[4];
        float[] farPlanePoint = new float[4];

        Matrix.multiplyMV(nearPlanePoint, 0, invertedMat, 0, nearScreenPoint, 0);
        Matrix.multiplyMV(farPlanePoint, 0, invertedMat, 0, farScreenPoint, 0);

        position[0] = nearPlanePoint[0] / nearPlanePoint[3];
        position[1] = nearPlanePoint[1] / nearPlanePoint[3];
        position[2] = nearPlanePoint[2] / nearPlanePoint[3];

        direction[0] = farPlanePoint[0] / farPlanePoint[3] - position[0];
        direction[1] = farPlanePoint[1] / farPlanePoint[3] - position[1];
        direction[2] = farPlanePoint[2] / farPlanePoint[3] - position[2];

        normalize(direction);

        position[0] += (direction[0] * 0.1f);
        position[1] += (direction[1] * 0.1f);
        position[2] += (direction[2] * 0.1f);

        return position;
    }

    private void normalize(float[] v) {
        double norm = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        v[0] /= norm;
        v[1] /= norm;
        v[2] /= norm;
    }

    public boolean checkDistance(float x1, float y1, float z1, float x2, float y2, float z2) {
        float x = x1 - x2;
        float y = y1 - y2;
        float z = z1 - z2;
        return (Math.sqrt(x*x + y*y + z*z) > MIN_DISTANCE);
    }


    // 카메라 권한 부여 함수
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 0);
        }
        Log.d("카메라 권한 부여", " 권한부여 됨");
    }

    // 타이틀 바 제거
    private void hideStatusBarAndTitleBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
