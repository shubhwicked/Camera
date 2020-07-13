package com.example.camera.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.annotation.NonNull;


import com.example.camera.R;
import com.example.camera.baseclass.BaseActivity;
import com.example.camera.fragment.CameraFrag;
import com.example.camera.fragment.DoneFragment;
import com.example.camera.utility.AppUtilityMethods;
import com.example.camera.utility.Constants;
import com.example.camera.utility.ImageUtility;
import com.example.camera.utility.PermissionsUtility;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;


import com.kila.apprater_dialog.lars.AppRater;

import org.wysaid.common.Common;
import org.wysaid.nativePort.CGENativeLibrary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;



public class MainActivity extends BaseActivity {
    public static final String ACTION_EDIT = "EDIT";
    public static final String ACTION_EDIT1 = "EDIT1";
    public static final String ACTION_CAMERA = "CAMERA";
    public static final String ACTION_COLLAGE = "COLLAGE";
    public static final String ACTION_GALLERY_CREATIONS = "ACTION_GALLERY_CREATIONS";
    public static final String ACTION_PIP = "PIP";
    public static final String ACTION_INTENT_FILTER = "ACTION_INTENT_FILTER";
    public static final int CAMERA_REQUEST = 101;
    public static final int CAMERA_REQUEST1 = 1011;
    public static final int CROP_REQUEST = 1001;
    public static final int REQUEST_CODE_PICK_VIDEO = 1;
    public static final int REQUEST_VIDEO_TRIMMER = 2;
    private String action;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private AppUtilityMethods appUtilityMethods;
    private ImageUtility imageUtility;
    private Uri uriCamera;

    Uri imageUri;
    public int adCount = 0;


    public boolean incrementCount() {


        adCount++;
        if (adCount >= 3) {
            adCount = 0;
            return true;

        }
        return false;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);



        toolbar.setTitleTextColor(ContextCompat.getColor(MainActivity.this, R.color.material_gray));

        showMenuDone(false);

        appUtilityMethods = AppUtilityMethods.getInstance();
        imageUtility = ImageUtility.getInstance();
        if (PermissionsUtility.getInstance().checkCameraPermission(MainActivity.this)  && PermissionsUtility.getInstance().checkStoragePermission(this)) {
            //  openVideoCapture();
            startAppFunctionality();

        }

    }

    private void startAppFunctionality() {




        //  replaceFragment(HomeFrag.class.getName(), null, null);

        deleteAllTempFiles();
        CGENativeLibrary.setLoadImageCallback(mLoadImageCallback, null);

        checkPermission(ACTION_EDIT);

    }



    private void openVideoCapture() {
        Intent videoCapture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(videoCapture, REQUEST_VIDEO_TRIMMER);
    }

    public void checkPermission(String action) {
        if (action == null)
            return;
        this.action = action;
        if (action.equals(ACTION_EDIT)) {
            if (PermissionsUtility.getInstance().checkCameraPermission(MainActivity.this) && PermissionsUtility.getInstance().checkStoragePermission(this)) {
                //  openVideoCapture();
                replaceFragment(CameraFrag.class.getName(), null, null);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionsUtility.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:

            case PermissionsUtility.MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (PermissionsUtility.getInstance().checkCameraPermission(MainActivity.this) && PermissionsUtility.getInstance().checkStoragePermission(MainActivity.this)) {
                        //  openVideoCapture();
                        startAppFunctionality();

                    }
                } else {
                    // supportFinishAfterTransition();
                    Snackbar snackbar=Snackbar.make(toolbar,"Selfie needs Camera, Storage permission to function. Please provide these permission to start. Thank You",Snackbar.LENGTH_INDEFINITE);
                   snackbar.setAction("OK", new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                          snackbar.dismiss();
                           if (PermissionsUtility.getInstance().checkCameraPermission(MainActivity.this) && PermissionsUtility.getInstance().checkStoragePermission(MainActivity.this)) {
                               //  openVideoCapture();
                               startAppFunctionality();

                           }
                       }
                   });
                    snackbar.show();

                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK && requestCode == 10113) {
                CameraFrag cameraFrag = (CameraFrag) getSupportFragmentManager().findFragmentByTag(CameraFrag.class.getName());
                if (cameraFrag != null && cameraFrag.isVisible()) {
                    cameraFrag.onActivityResult(requestCode, resultCode, data);
                }

            }
        }
      /*  if (requestCode == AppUtilityMethods.RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            String path = imageUtility.selectedImage(this, data);
            goToEdit(path);
        } else if (requestCode == AppUtilityMethods.LOAD_IMG_CAMERA && resultCode == RESULT_OK) {
            if (uriCamera != null) {
                String path = imageUtility.selectedImage(this, uriCamera, imageUtility.PICTURE_TYPE);
                goToEdit(path);
            }
        }*/
    }

    public void goToEdit1(String path) {
        if (path != null) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.SELECTED_IMAGES, path);
            //  launchSubActivityForResult(EditFragCrop.class.getName(),bundle,CROP_REQUEST);
            // replaceFragment(EditFrag.class.getName(), backStackTag, bundle);
        }
    }

    public void goToEdit(String path, String backStackTag) {
        if (path != null) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.SELECTED_IMAGES, path);
            //replaceFragment(EditFrag.class.getName(), backStackTag, bundle);
            //replaceFragment(EditFrag.class.getName(), backStackTag, bundle);
        }
    }


 /*   public void goToEdit(String path) {
        if (path != null) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.SELECTED_IMAGES, path);
            //launchSubActivityForResult(EditFragCrop.class.getName(),bundle,CROP_REQUEST);

        }
    }
*/

    @Override
    public void onBackPressed() {
       /* EditFrag editFrag = (EditFrag) getSupportFragmentManager().findFragmentByTag(EditFrag.class.getName());
        HomeFrag home = (HomeFrag) getSupportFragmentManager().findFragmentByTag(HomeFrag.class.getName());
       if (editFrag != null && editFrag.isVisible()) {
            editFrag.onBackPressed();
        }else if (home != null && home.isVisible()) {
            home.onBackPressed();
        } else if (galleryFragment != null && galleryFragment.isVisible()) {
            galleryFragment.onBackPressed();
        } else {*/
        // super.onBackPressed();
        /* }*/

        // HomeFrag homeFrag = (HomeFrag) getSupportFragmentManager().findFragmentByTag(HomeFrag.class.getName());
          DoneFragment donefreg = (DoneFragment) getSupportFragmentManager().findFragmentByTag(DoneFragment.class.getName());
        CameraFrag cameraFrag = (CameraFrag) getSupportFragmentManager().findFragmentByTag(CameraFrag.class.getName());
      if (donefreg != null && donefreg.isVisible()) {
            donefreg.onBackpressed();
        } else if (cameraFrag != null && cameraFrag.isVisible()) {
            cameraFrag.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    public void goToMainFrag(ArrayList<String> imagesPaths, Integer layoutID) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", layoutID);
        bundle.putStringArrayList(Constants.SELECTED_IMAGES, imagesPaths);

        //   replaceFragment(MainFrag.class.getName(), CameraFrag.class.getName(), bundle);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            deleteAllTempFiles();
        }
    }

    private void deleteAllTempFiles() {
        if (PermissionsUtility.getInstance().checkStoragePermission(this)) {
            try {
                File file = imageUtility.getTempDir();
                if (file.exists()) {
                    if (file.isDirectory())
                        for (File child : file.listFiles()) {
                            child.delete();
                        }
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        } else {
            String pathnew = getRealPathFromURI_API11to18(MainActivity.this, uri);
            return pathnew;
        }

        return null;
    }

    public String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public CGENativeLibrary.LoadImageCallback mLoadImageCallback = new CGENativeLibrary.LoadImageCallback() {
        @Override
        public Bitmap loadImage(String name, Object arg) {
            AssetManager am = getAssets();
            InputStream is;
            try {
                is = am.open(name);
            } catch (IOException e) {
                Log.e(Common.LOG_TAG, "Can not open file " + name);
                return null;
            }
            return BitmapFactory.decodeStream(is);
        }

        @Override
        public void loadImageOK(Bitmap bmp, Object arg) {
            bmp.recycle();
        }
    };

    private void uploadVideo() {
        try {
            Intent intent = new Intent();
            intent.setType("video");


            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_CODE_PICK_VIDEO);

        } catch (Exception e) {

        }
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();
    }


}
