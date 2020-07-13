package com.example.camera.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;


import com.example.camera.R;
import com.example.camera.activity.SubActivity;
import com.example.camera.baseclass.BaseActivity;
import com.example.camera.utility.AppUtilityMethods;
import com.example.camera.utility.ImageUtility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DoneFragment extends Fragment {
    @BindView(R.id.imageView)
    VideoView imageView;
    @BindView(R.id.imageView1)
    ImageView imageView1;

    @BindView(R.id.tvPath)
    TextView tvPath;

    private String url;
    private AppUtilityMethods appUtilityMethods;
    String path;
    Uri uriimage;
    boolean isVideo = false;


    public static Bundle getBundle(String url, String path, Uri uri, boolean isVideo) {
        Bundle bundle = new Bundle();
        bundle.putString("frameLayout", url);
        if (path != null)
            bundle.putString("path", path);
        if (uri != null)
            bundle.putString("imageUri", uri.toString());
        bundle.putBoolean("isVideo", isVideo);

        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString("frameLayout");
        isVideo = getArguments().getBoolean("isVideo", false);
        if (getActivity() instanceof SubActivity) {
            path = getArguments().getString("path");
            uriimage = Uri.parse(getArguments().getString("imageUri"));
        }
        appUtilityMethods = AppUtilityMethods.getInstance();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((BaseActivity) getActivity()).getSupportActionBar().hide();
        /*((BaseActivity) getActivity()).getSupportActionBar().setTitle(R.string.share);
        ((BaseActivity) getActivity()).showMenuDone(false);*/

        View view = inflater.inflate(R.layout.frag_done, container, false);
        ButterKnife.bind(this, view);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
      {
            imageView1.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            Bitmap photo = ImageUtility.getInstance().checkExifAndManageRotation(url, 800, 800);
            imageView1.setImageBitmap(photo);

        }
        tvPath.setText(url);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVideo) {
            if (!imageView.isPlaying())
                imageView.start();
        }
    }

    @OnClick({ R.id.fabBack})
    protected void onClick(View view) {

        switch (view.getId()) {
            case R.id.fabBack:
                onBackpressed();
                break;

        }
    }

    /*  private void handleClick(final View view) {
          animUtils.pulseAnimation(view, new ICallBack() {
              @Override
              public void onComplete(Object object) {

              }
          });
      }
  */
    public void delete() {
        ImageUtility.getInstance().deleteFile(url);
        getActivity().onBackPressed();
    }

    public void onBackpressed() {
        if (getActivity() instanceof SubActivity) {
            //goToEdit(path, uriimage);
            getActivity().supportFinishAfterTransition();
        } else {
            getActivity().getSupportFragmentManager().popBackStack();
        }

    }


}
