package com.example.camera.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;


import com.example.camera.R;
import com.example.camera.baseclass.BaseActivity;
import com.example.camera.fragment.DoneFragment;

import butterknife.BindView;
import butterknife.ButterKnife;




public class SubActivity extends BaseActivity {
    private String tag;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static final int PHOTO_EDIT = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitleTextColor(ContextCompat.getColor(SubActivity.this,R.color.material_gray));
        tag = getIntent().getAction();

        Bundle bundle = getIntent().getExtras();
        replaceFragment(tag, null, bundle);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(tag.equals(DoneFragment.class.getName())){
            getSupportActionBar().show();
            getSupportActionBar().setTitle(R.string.haveSavedToGallery);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            showMenuDone(false);
            showMenuDelete(false);

            showMenuReset(false);

        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

/*        if (resultCode !=0 && requestCode == PickConfig.PICK_PHOTO_DATA) {
            ArrayList<String> paths = (ArrayList<String>) data.getSerializableExtra(PickConfig.INTENT_IMG_LIST_SELECT);
          *//* // ArrayList<String> paths = new ArrayList<>();

            for (Uri uri : uris) {
                paths.add(imageUtility.selectedImage(this, uri, imageUtility.PICTURE_TYPE));
            }
*//*

            if (paths != null && paths.size() > 0) {
             *//*   Bundle bundle = new Bundle();
                bundle.putStringArrayList(Constants.SELECTED_IMAGES, paths);

                PhotoGridFragCollage photoGridFrag = (PhotoGridFragCollage) getSupportFragmentManager().findFragmentByTag(PhotoGridFragCollage.class.getName());

                if (photoGridFrag != null)
                    photoGridFrag.changeImage(paths);
*//*

            }
        }else*/

            super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onBackPressed() {

        DoneFragment donefreg = (DoneFragment) getSupportFragmentManager().findFragmentByTag(DoneFragment.class.getName());
        if (donefreg != null && donefreg.isVisible()) {
            donefreg.onBackpressed();
        } {
            super.onBackPressed();
        }

    }
}
