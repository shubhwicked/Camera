package com.example.camera.utility;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.example.camera.R;
import com.example.camera.activity.MainActivity;

import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;




import java.io.File;


public class AppUtilityMethods {
    private static AppUtilityMethods utility;
    public static final int RESULT_LOAD_IMAGE = 201;
    public static final int LOAD_IMG_CAMERA = 202;

    private AppUtilityMethods() {
    }
    public void showKeyboard(Context context, EditText editText) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideKeyboard(Context context, EditText editText) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                editText.getApplicationWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
    public Point getScreenHeightWidth(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return new Point(width,height);
    }
    public void updateGallery(Context context, String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static AppUtilityMethods getInstance() {
        if (utility == null) {
            utility = new AppUtilityMethods();
        }
        return utility;
    }
    public int dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics));
    }
    public void openGallery(Context context) {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        try {
            ((MainActivity) context).startActivityForResult(pickIntent, RESULT_LOAD_IMAGE);
          //  ((CameraPreviewActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void openCamera(Context context, Uri imageUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        try {
            ((MainActivity) context).startActivityForResult(intent, LOAD_IMG_CAMERA);
           // ((MainActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void shareUsingEmail(Context context, String subject, String text, String to, String chooserTitle) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", to, null));
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, text);
            context.startActivity(Intent.createChooser(intent, chooserTitle));
        } catch (ActivityNotFoundException an) {
            an.printStackTrace();
        }
    }

    public void showAppInPlayStore(Context context) {

        Uri uri = Uri.parse("market://details?id=" + ( context.getPackageName()));
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + ( context.getPackageName()))));
        }
    }

    public void copyText(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(text, text);
        clipboard.setPrimaryClip(clip);
    }

    public void showSnakeBar(View view, String message) {
        if (view != null)
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public void showSnakeBar(View view, String message, String actionText, View.OnClickListener clickListener) {
        if (view != null)
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction(actionText, clickListener).show();
    }

    public boolean isNetworkOnline(Context context) {
        boolean status = false;
        if (context != null) {
            try {
                ConnectivityManager connectivityManager
                        = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return status;

    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

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
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
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
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
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
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    public void moreAppInPlayStore(Context context, String developer) {

        Uri uri = Uri.parse("https://play.google.com/store/apps/developer?id=" + developer);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=" + developer)));
        }
    }
    public Bitmap CropBitmapTransparency(Bitmap sourceBitmap)
    {
        int minX = sourceBitmap.getWidth();
        int minY = sourceBitmap.getHeight();
        int maxX = -1;
        int maxY = -1;
        for(int y = 0; y < sourceBitmap.getHeight(); y++)
        {
            for(int x = 0; x < sourceBitmap.getWidth(); x++)
            {
                int alpha = (sourceBitmap.getPixel(x, y) >> 24) & 255;
                if(alpha > 0)   // pixel is not 100% transparent
                {
                    if(x < minX)
                        minX = x;
                    if(x > maxX)
                        maxX = x;
                    if(y < minY)
                        minY = y;
                    if(y > maxY)
                        maxY = y;
                }
            }
        }
        if((maxX < minX) || (maxY < minY))
            return null; // Bitmap is entirely transparent

        // crop bitmap to non-transparent area and return:
        return Bitmap.createBitmap(sourceBitmap, minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
    }
    public void shareImage(Context context, String url, String packageName,String type) {
        try {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType(type);

            File file = new File(url);
            Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileProvider", file);
            share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.app_name));
            if (packageName != null) {
                share.setPackage(packageName);
                context.startActivity(share);
            } else {
                context.startActivity(Intent.createChooser(share, context.getString(R.string.share_using)));
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
    public DisplayMetrics getDisplayMatrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((AppCompatActivity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public int getActionBarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return mActionBarSize;
    }


    public boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        boolean installed;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }
    public String getHumanReadableSize(long sizeBytes) {
        final String bytes = "%1$.1f B", kb = "%1$.1f KB", mb = "%1$.1f MB", gb = "%1$.1f GB";
        String humanReadableSize;
        if (sizeBytes < 1024) {
            humanReadableSize = String.format(bytes, (double) sizeBytes);
        } else if (sizeBytes < Math.pow(1024, 2)) {
            humanReadableSize = String.format(kb, (double) (sizeBytes / 1024));
        } else if (sizeBytes < Math.pow(1024, 3)) {
            humanReadableSize = String.format(mb, (double) (sizeBytes / Math.pow(1024, 2))
            );
        } else {
            humanReadableSize = String.format(gb, (double) (sizeBytes / Math.pow(1024, 3)));
        }
        return humanReadableSize;
    }

    public String convertMillisecondsToHours(long millis) {
        long hour = 0, min = 0, sec = 0;
        sec = millis / 1000l;
        if (sec > 60) {
            min = sec / 60;
            sec = sec % 60;
        }
        if (min > 60) {
            hour = min / 60;
            min = min % 60;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(roundWithoutDecimal(hour));
        buffer.append(":");
        buffer.append(roundWithoutDecimal(min));
        buffer.append(":");
        buffer.append(roundWithoutDecimal(sec));
        String duration = new String(buffer);
        if (duration.equals("00:00:00")) {
            duration = "--:--:--";
        }
        return duration;

    }

    private String roundWithoutDecimal(long value) {
        String val = String.valueOf(value);
        if (val.length() == 1) {
            val = "0" + val;
        }
        return val;
    }

}
