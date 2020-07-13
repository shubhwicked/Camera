package com.example.camera.baseclass;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


import com.example.camera.R;
import com.example.camera.activity.SubActivity;



/**
 * Created by Dell on 2/10/2016.
 */
public class BaseActivity extends AppCompatActivity {
    public static int adViewHeight = 0;

    public void replaceFragment(String fragmentTag, String backStackTag, Bundle bundle) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        fragment = Fragment.instantiate(this, fragmentTag, bundle);
        if (backStackTag != null)
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_right).replace(R.id.content_main, fragment, fragmentTag).addToBackStack(backStackTag).commitAllowingStateLoss();
        else
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_right).replace(R.id.content_main, fragment, fragmentTag).commitAllowingStateLoss();

    }
  public void replaceFragmentSubActivity(String fragmentTag, String backStackTag, Bundle bundle) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        fragment = Fragment.instantiate(this, fragmentTag, bundle);
        if (backStackTag != null)
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_right).replace(R.id.content_main, fragment, fragmentTag).addToBackStack(backStackTag).commitAllowingStateLoss();
        else
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_right).replace(R.id.content_main, fragment, fragmentTag).commitAllowingStateLoss();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /* @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/

    public void launchSubActivity(String fragmentTag, Bundle bundle) {
        Intent intent = new Intent(this, SubActivity.class);
        intent.setAction(fragmentTag);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivity(intent);
    }

    public void launchSubActivityForResult(String fragmentTag, Bundle bundle, int requestCode) {
        Intent intent = new Intent(this, SubActivity.class);
        intent.setAction(fragmentTag);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    protected MenuItem menuDone, menuReset, menuForgetPassword, menuPrivacyPolicy, menuDelete;

    public void showMenuDone(boolean show) {
        if (menuDone != null) {
            menuDone.setVisible(show);
        }
    }

    public void showMenuReset(boolean show) {
        if (menuReset != null) {
            menuReset.setVisible(show);
        }
    }

    public void showMenuDelete(boolean show) {
        if (menuDelete != null) {
            menuDelete.setVisible(show);
        }
    }

    public void showMenuForgetPassword(boolean show) {
        if (menuForgetPassword != null) {
            menuForgetPassword.setVisible(show);
        }
    }

    public void showMenuPrivacyPolicy(boolean show) {
        if (menuPrivacyPolicy != null) {
            menuPrivacyPolicy.setVisible(show);
        }
    }

    public void setDoneIconSave() {
        if (menuDone != null)
            menuDone.setIcon(getResources().getDrawable(R.mipmap.done));
    }

    public void setDoneIconDone() {
        if (menuDone != null)
            menuDone.setIcon(getResources().getDrawable(R.mipmap.done));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuDone = menu.findItem(R.id.action_done);

        /*
        menuForgetPassword = menu.findItem(R.id.actionForgetPassword);
        menuPrivacyPolicy = menu.findItem(R.id.actionPrivacyPolicy);
        */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;




            case R.id.action_done:
               /* EditFrag editFrag2 = (EditFrag) getSupportFragmentManager().findFragmentByTag(EditFrag.class.getName());
                if (editFrag2 != null && editFrag2.isVisible()) {
                    editFrag2.resetAll();
                }*/
               /* GalleryFragment gal = (GalleryFragment) getSupportFragmentManager().findFragmentByTag(GalleryFragment.class.getName());
                if (gal != null && gal.isVisible()) {
                    gal.deleteSelectedImages();
                }
*/

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public static Bitmap TrimBitmap(Bitmap bmp) {
        int imgHeight = bmp.getHeight();
        int imgWidth = bmp.getWidth();


        //TRIM WIDTH - LEFT
        int startWidth = 0;
        for (int x = 0; x < imgWidth; x++) {
            if (startWidth == 0) {
                for (int y = 0; y < imgHeight; y++) {
                    if (bmp.getPixel(x, y) != Color.TRANSPARENT) {
                        startWidth = x;
                        break;
                    }
                }
            } else break;
        }


        //TRIM WIDTH - RIGHT
        int endWidth = 0;
        for (int x = imgWidth - 1; x >= 0; x--) {
            if (endWidth == 0) {
                for (int y = 0; y < imgHeight; y++) {
                    if (bmp.getPixel(x, y) != Color.TRANSPARENT) {
                        endWidth = x;
                        break;
                    }
                }
            } else break;
        }


        //TRIM HEIGHT - TOP
        int startHeight = 0;
        for (int y = 0; y < imgHeight; y++) {
            if (startHeight == 0) {
                for (int x = 0; x < imgWidth; x++) {
                    if (bmp.getPixel(x, y) != Color.TRANSPARENT) {
                        startHeight = y;
                        break;
                    }
                }
            } else break;
        }


        //TRIM HEIGHT - BOTTOM
        int endHeight = 0;
        for (int y = imgHeight - 1; y >= 0; y--) {
            if (endHeight == 0) {
                for (int x = 0; x < imgWidth; x++) {
                    if (bmp.getPixel(x, y) != Color.TRANSPARENT) {
                        endHeight = y;
                        break;
                    }
                }
            } else break;
        }


        return Bitmap.createBitmap(
                bmp,
                startWidth,
                startHeight,
                endWidth - startWidth,
                endHeight - startHeight
        );

    }

}
