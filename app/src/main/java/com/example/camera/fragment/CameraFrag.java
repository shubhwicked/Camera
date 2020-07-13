package com.example.camera.fragment;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;



import com.daimajia.androidanimations.library.YoYo;
import com.example.camera.activity.MainActivity;
import com.example.camera.R;

import com.example.camera.baseclass.BaseActivity;
import com.example.camera.gpuImagePlusHelper.Effect;
import com.example.camera.gpuImagePlusHelper.EffectsHelper;

import com.example.camera.utility.AnimUtils;
import com.example.camera.utility.ICallBack;
import com.example.camera.utility.ImageUtility;




import org.wysaid.camera.CameraInstance;
import org.wysaid.myUtils.FileUtil;
import org.wysaid.myUtils.ImageUtil;
import org.wysaid.view.CameraRecordGLSurfaceView;
import org.wysaid.view.VideoPlayerGLSurfaceView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;


import static com.example.camera.gpuImagePlusHelper.EffectsHelper.effects;
import static org.wysaid.myUtils.FileUtil.LOG_TAG;


public class CameraFrag extends Fragment {
    boolean shouldFit = true;
    boolean frontflash = false;
    String[] flashModes = {
            Camera.Parameters.FLASH_MODE_AUTO,
            Camera.Parameters.FLASH_MODE_ON,
            Camera.Parameters.FLASH_MODE_TORCH,
            Camera.Parameters.FLASH_MODE_OFF,
            Camera.Parameters.FLASH_MODE_RED_EYE,
    };
    private AnimUtils animUtils;
    public static String lastVideoPathFileName = FileUtil.getPath() + "/lastVideoPath.txt";
    @BindView(R.id.myGLSurfaceView)
    CameraRecordGLSurfaceView mCameraView;
    @BindView(R.id.change)
    ImageView change;
 @BindView(R.id.beautySeek)
    SeekBar beautySeek;


    @BindView(R.id.snacklayout)
    RelativeLayout snacklayout;
    @BindView(R.id.snack)
    TextView snack;


    @BindView(R.id.cameratimertext)
    TextView cameratimertext;
    @BindView(R.id.filterrecycle)
    RecyclerView rvFilters;

    @BindView(R.id.capture)
    ImageView capture;

    String mCurrentConfig;
    float intensitymain = 1.0f;




    @BindView(R.id.timer)
    TextView labeltimer;
    int passedSenconds;
    Boolean isActivityRunning = false;
    Timer timer;
    TimerTask timerTask;

    @BindView(R.id.cameraswitch)
    ImageView cameraswitch;

    @BindView(R.id.album)
    RelativeLayout album;
    @BindView(R.id.beauty)
    ImageView beauty;
    @BindView(R.id.ivFlash)
    ImageView ivFlash;

    /* @BindView(R.id.beautyseekbar)
     DiscreteSlider beautyseekbar;*/
    private Effect selectedEffect;
    String mCurrentBeauty = EffectsHelper.beauty1;
    boolean isValid = true;
    String recordFilename;

    YoYo.YoYoString yoyoToy, yoyoBark, yoyoWhistle;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    /*  Bitmap[] sparkle=new Bitmap[5];
      Handler mHandler = new Handler();*/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mCurrentConfig = effects[0].getConfig();
            selectedEffect = effects[0];
            animUtils = AnimUtils.getInstance();
            preferences = getActivity().getSharedPreferences(getActivity().getPackageName().toLowerCase(), Context.MODE_PRIVATE);
            editor = preferences.edit();
            mCurrentBeauty = EffectsHelper.beauty1;



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*   Runnable __runnable = new Runnable()
       {
           @Override
           public void run()
           {
               Log.d("Runnable","Running");
               Random ran = new Random();
               if(maskcheck)
               mCameraView.setMaskBitmap(sparkle[ran.nextInt(sparkle.length)],false);
               mHandler.postDelayed(this, 500);
           }
       };*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((BaseActivity) getActivity()).getSupportActionBar().hide();

        ((BaseActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ((MainActivity) getActivity()).showMenuDone(false);


        View view = inflater.inflate(R.layout.camerafrag, container, false);
        ButterKnife.bind(this, view);


        return view;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        mCameraView.presetCameraForward(true);
        passedSenconds = 0;

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });


        ivFlash.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                animUtils.pulseAnimation(ivFlash, null);
                setFlash();
            }
        });


        beautySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                switch (i){
                    case 0:
                        mCurrentBeauty=EffectsHelper.beauty0;
                        setFilterConfig();
                        break;
                        case 1:
                        mCurrentBeauty=EffectsHelper.beauty1;
                        setFilterConfig();
                        break;
                        case 2:
                        mCurrentBeauty=EffectsHelper.beauty2;
                        setFilterConfig();
                        break;
                        case 3:
                        mCurrentBeauty=EffectsHelper.beauty3;
                        setFilterConfig();
                        break;
                        case 4:
                        mCurrentBeauty=EffectsHelper.beauty4;
                        setFilterConfig();
                        break;
                        case 5:
                        mCurrentBeauty=EffectsHelper.beauty5;
                        setFilterConfig();
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //Recording video size
        //  mCameraView.presetRecordingSize(480, 640);
        mCameraView.presetRecordingSize(720, 1280);

        //Taking picture size.
        mCameraView.setPictureSize(1800, 3200, true); // > 4MP
        mCameraView.setZOrderOnTop(false);
        mCameraView.setZOrderMediaOverlay(true);
        mCameraView.setFitFullView(shouldFit);
        mCameraView.setOnCreateCallback(new CameraRecordGLSurfaceView.OnCreateCallback() {
            @Override
            public void createOver(boolean success) {
                if (success) {
                    Log.i(LOG_TAG, "view create OK");
                } else {
                    Log.e(LOG_TAG, "view create failed!");
                }
            }
        });
        mCameraView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN: {
                        Log.i(LOG_TAG, String.format("Tap to focus: %g, %g", event.getX(), event.getY()));
                        final float focusX = event.getX() / mCameraView.getWidth();
                        final float focusY = event.getY() / mCameraView.getHeight();

                        mCameraView.focusAtPoint(focusX, focusY, new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                                if (success) {
                                    Log.e(LOG_TAG, String.format("Focus OK, pos: %g, %g", focusX, focusY));
                                } else {
                                    Log.e(LOG_TAG, String.format("Focus failed, pos: %g, %g", focusX, focusY));
                                    mCameraView.cameraInstance().setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                                }
                            }
                        });
                    }
                    break;
                    default:
                        break;
                }

                return false;
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(getActivity()==null)
                    return;
                setFilterConfig();
            }
        }, 300);


        //mCameraView.setPictureSize(600, 800, true);

        cameraswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animUtils.pulseAnimation(cameraswitch, null);
                switchCamera();

            }
        });
        beauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(beautySeek.getVisibility()==View.VISIBLE)
                    beautySeek.setVisibility(View.GONE);
                else
                    beautySeek.setVisibility(View.VISIBLE);
            }
        });
        manageFlashButton();

    }



    @Override
    public void onDestroy() {
      /*  maskcheck=false;
        mHandler.removeCallbacks(__runnable);*/
        super.onDestroy();

    }





    private void captureImage() {
        if (time == 0) {

            animUtils.pulseAnimation(capture, null);
            capture.setClickable(false);

            mCameraView.takeShot(bmp -> {
                if(mCameraView==null)
                    return;
                if (bmp != null) {
                    if (mCameraView.isCameraBackForward() && Build.MODEL.toLowerCase().contains("nexus 5x")) {
                        bmp = ImageUtility.getInstance().rotateBitmap(bmp, 180);
                    } else if (!mCameraView.isCameraBackForward() && Build.MODEL.toLowerCase().contains("nexus 6")) {
                        bmp = ImageUtility.getInstance().rotateBitmap(bmp, 180);
                    }




                    String s = ImageUtil.saveBitmap(bmp);
                    if(bmp!=null && !bmp.isRecycled()) {
                        bmp.recycle();
                        bmp=null;
                    }


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(getActivity()==null)
                                return;
                            setSnack("Image Captured");
                            capture.setClickable(true);
                            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + s)));

                               ((BaseActivity) getActivity()).replaceFragment(DoneFragment.class.getName(), CameraFrag.this.getTag(), DoneFragment.getBundle(s, null, null, false));


                        }
                    });

                } else{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(getActivity()==null)
                                return;
                            setSnack("Some error happens!");
                        }
                    });
                }

            }, false);

        } else {
            setTimer(new ICallBack() {

                @Override
                public void onComplete(Object object) {
                    if (rvFilters.getVisibility() != View.VISIBLE) {
                        mCameraView.takePicture(new CameraRecordGLSurfaceView.TakePictureCallback() {
                            @Override
                            public void takePictureOK(Bitmap bmp) {
                                if(mCameraView==null)
                                    return;
                                cameratimertext.setText("" + time);
                                cameratimertext.setVisibility(View.VISIBLE);
                                if (bmp != null) {
                                    if (mCameraView.isCameraBackForward() && Build.MODEL.toLowerCase().contains("nexus 5x")) {
                                        bmp = ImageUtility.getInstance().rotateBitmap(bmp, 180);
                                    } else if (!mCameraView.isCameraBackForward() && Build.MODEL.toLowerCase().contains("nexus 6")) {
                                        bmp = ImageUtility.getInstance().rotateBitmap(bmp, 180);
                                    }



                                    String s = ImageUtil.saveBitmap(bmp);
                                    if(bmp!=null && !bmp.isRecycled()) {
                                        bmp.recycle();
                                        bmp=null;
                                    }

                                    setSnack("Image Captured");
                                    getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + s)));
                                    ((BaseActivity) getActivity()).replaceFragment(DoneFragment.class.getName(), CameraFrag.this.getTag(), DoneFragment.getBundle(s, null, null, false));

                                } else
                                    setSnack("Some error happens!");
                            }
                        }, null, mCurrentBeauty, 1.0f, true);
                    }
                }
            });
        }
    }

    private void setFilterConfig() {

        mCameraView.setFilterWithConfig(mCurrentBeauty);
    }


    private void switchCamera() {
        if (!mCameraView.isRecording()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(getActivity()==null)
                        return;
                    change.setVisibility(View.VISIBLE);
                }
            });

        }
        mCameraView.switchCamera();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(getActivity()==null)
                    return;
                change.setVisibility(View.GONE);
            }
        }, 300);

        manageFlashButton();
    }

    private void manageFlashButton() {
        if (mCameraView.isCameraBackForward()) {

            ivFlash.setVisibility(View.VISIBLE);
            ivFlash.setVisibility(View.VISIBLE);
            String flashMode = flashmode1;
            //  boolean isSet = mCameraView.setFlashLightMode(flashMode);

            // flashIndex %= flashModes.length;

            switch (flashMode) {
                case Camera.Parameters.FLASH_MODE_AUTO:
                    ivFlash.setImageResource(R.mipmap.camera_flash_auto);
                    break;
                case Camera.Parameters.FLASH_MODE_ON:
                    ivFlash.setImageResource(R.mipmap.camera_flash_on);
                    break;
                case Camera.Parameters.FLASH_MODE_TORCH:
                    ivFlash.setImageResource(R.mipmap.camera_flash_torch);
                    break;
                case Camera.Parameters.FLASH_MODE_OFF:
                    ivFlash.setImageResource(R.mipmap.camera_flash_off);
                    break;
                case Camera.Parameters.FLASH_MODE_RED_EYE:
                    ivFlash.setImageResource(R.mipmap.camera_flash_red_eye);
                    break;


            }


        } else {

            ivFlash.setVisibility(View.VISIBLE);
            ivFlash.setImageResource(R.mipmap.camera_flash_off);
        }
    }

    int flashIndex = 0;
    String flashmode1 = flashModes[3];

    private void setFlash() {
        if (mCameraView.isCameraBackForward()) {
            String flashMode = flashModes[flashIndex];
            flashmode1 = flashMode;
           /* boolean isSet =*/ mCameraView.setFlashLightMode(flashMode);
            ++flashIndex;
            flashIndex %= flashModes.length;
          /*  if (!isSet) {
                setFlash();
            } else*/ {
                switch (flashMode) {
                    case Camera.Parameters.FLASH_MODE_AUTO:
                        ivFlash.setImageResource(R.mipmap.camera_flash_auto);
                        break;
                    case Camera.Parameters.FLASH_MODE_ON:
                        ivFlash.setImageResource(R.mipmap.camera_flash_on);
                        break;
                    case Camera.Parameters.FLASH_MODE_TORCH:
                        ivFlash.setImageResource(R.mipmap.camera_flash_torch);
                        break;
                    case Camera.Parameters.FLASH_MODE_OFF:
                        ivFlash.setImageResource(R.mipmap.camera_flash_off);
                        break;
                    case Camera.Parameters.FLASH_MODE_RED_EYE:
                        ivFlash.setImageResource(R.mipmap.camera_flash_red_eye);
                        break;
                }
                // Toast.makeText(getActivity(), flashMode, Toast.LENGTH_SHORT).show();
            }
        } else {

            frontflash = false;
            ivFlash.setImageResource(R.mipmap.camera_flash_off);

        }
    }


    @Override
    public void onPause() {
        if (mCameraView != null) {
            // stopRecording();
            CameraInstance.getInstance().stopCamera();
            Log.i(VideoPlayerGLSurfaceView.LOG_TAG, "activity onPause...");
            mCameraView.release(null);
            mCameraView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraView.onResume();




    }

    private class myTimerTask extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if(getActivity()==null)
                return;
            passedSenconds++;
            updateLabel.sendEmptyMessage(0);
        }
    }

    private Handler updateLabel = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            //super.handleMessage(msg);

            int seconds = passedSenconds % 60;
            int minutes = (passedSenconds / 60) % 60;
            int hours = (passedSenconds / 3600);
            labeltimer.setText(String.format("%02d : %02d : %02d", hours, minutes, seconds));
        }
    };

    public void reScheduleTimer() {
        timer = new Timer();
        timerTask = new myTimerTask();
        timer.schedule(timerTask, 0, 1000);
    }

    private void setSnack(String text) {
        snack.setText(text);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(getActivity()==null)
                    return;
                snacklayout.setVisibility(View.VISIBLE);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(getActivity()==null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(getActivity()==null)
                            return;
                        snacklayout.setVisibility(View.GONE);
                    }
                });

            }
        }, 500);
    }

    public void onBackPressed() {
        if(beautySeek.getVisibility()==View.VISIBLE)
            beautySeek.setVisibility(View.GONE);
       else{
            getActivity().supportFinishAfterTransition();
        }
    }

    int time = 0;

    private void setTimer(final ICallBack callback) {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(getActivity()==null)
                    return;
                new CountDownTimer(time * 1000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        if(getActivity()==null)
                            return;
                        cameratimertext.setVisibility(View.GONE);
                        cameratimertext.setText("" + millisUntilFinished / 1000);
                        cameratimertext.setVisibility(View.VISIBLE);
                        //time--;
                    }

                    public void onFinish() {
                        if(getActivity()==null)
                            return;
                        cameratimertext.setVisibility(View.GONE);
                        callback.onComplete(new Object());
                    }

                }.start();
            }
        }, 1000);


    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }





    static Bitmap trim(Bitmap source) {
        int firstX = 0, firstY = 0;
        int lastX = source.getWidth();
        int lastY = source.getHeight();
        int[] pixels = new int[source.getWidth() * source.getHeight()];
        source.getPixels(pixels, 0, source.getWidth(), 0, 0, source.getWidth(), source.getHeight());
        loop:
        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {
                if (pixels[x + (y * source.getWidth())] != Color.TRANSPARENT) {
                    firstX = x;
                    break loop;
                }
            }
        }
        loop:
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = firstX; x < source.getWidth(); x++) {
                if (pixels[x + (y * source.getWidth())] != Color.TRANSPARENT) {
                    firstY = y;
                    break loop;
                }
            }
        }
        loop:
        for (int x = source.getWidth() - 1; x >= firstX; x--) {
            for (int y = source.getHeight() - 1; y >= firstY; y--) {
                if (pixels[x + (y * source.getWidth())] != Color.TRANSPARENT) {
                    lastX = x;
                    break loop;
                }
            }
        }
        loop:
        for (int y = source.getHeight() - 1; y >= firstY; y--) {
            for (int x = source.getWidth() - 1; x >= firstX; x--) {
                if (pixels[x + (y * source.getWidth())] != Color.TRANSPARENT) {
                    lastY = y;
                    break loop;
                }
            }
        }
        Bitmap a= Bitmap.createBitmap(source, firstX, firstY, lastX - firstX, lastY - firstY);
        if(source!=null && !source.isRecycled()){
            source.recycle();
            source=null;
        }
        return a;
    }
}
