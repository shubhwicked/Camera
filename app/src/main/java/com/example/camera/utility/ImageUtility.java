package com.example.camera.utility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class ImageUtility {
    public static final String TEMP = ".Temp";
    public static final String IMAGES = "Images";
    public static final String VIDEOS = "Videos";
    public static final int THUMB_SIZE = 600;
    public static final String rootDirName = Constants.APP_NAME;
    public static final String rootDirName1 = Constants.APP_NAME1;
    private static volatile ImageUtility imageUtility;
    public final String PICTURE_TYPE = ".jpg";
    public final String VIDEO_TYPE = ".mp4";

    private ImageUtility() {
    }

    public static synchronized ImageUtility getInstance() {
        if (imageUtility == null) {
            imageUtility = new ImageUtility();
        }
        return imageUtility;
    }

    public File getOutputMediaFile(Context context, String mediaType) {
        File mediaStorageDir = new File(SharedPrefUtils.getInstance().getSaveDirectoryPath(context));
        if (mediaStorageDir == null)
            return null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + mediaType);
        return mediaFile;
    }

    public File getTempDir() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), rootDirName + File.separator + TEMP);
        return mediaStorageDir;
    }


    public File getOutputMediaTempFile(Context context) {
        File mediaStorageDir = getTempDir();
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".mp4");

        return mediaFile;
    }


    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getFileDirectory() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
              , rootDirName);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return mediaStorageDir;
    }  public File getFileDirectoryVideo() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
              , rootDirName1);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return mediaStorageDir;
    }

    public String getFileDirectoryPath() {
        String dirPath = null;
        File file = getFileDirectory();
        if (file != null) {
            dirPath = file.getPath();
        }
        return dirPath;
    }

    public File getOutputMediaFile(String fileName, String dirName, String mediaType) {
        File mediaStorageDir;
        if (dirName != null) {
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), rootDirName + File.separator + dirName);
        } else {
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), rootDirName);
        }
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        String path;
        if (fileName != null)
            path = fileName /*+ "_" + timeStamp*/ + mediaType;
        else
            path = "img_" + timeStamp + mediaType;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + path);


        return mediaFile;
    }

    private String downloadFromUrl(String imageLink, String fileName) {
        String filepath = null;
        try {
            URL url = new URL(imageLink);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            File file = getOutputMediaFileForDownload(fileName);
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();
            int totalSize = urlConnection.getContentLength();
            int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;

                Log.i("Progress:", "downloadedSize:" + downloadedSize + "totalSize:" + totalSize);
            }
            fileOutput.close();
            if (downloadedSize == totalSize) filepath = file.getPath();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            filepath = null;
            e.printStackTrace();
        }

        Log.i("filepath:", " " + filepath);
        return filepath;
    }

    public String saveInputStream(InputStream inputStream, String fileName, long totalSize) {
        String filepath = null;
        try {
            File file = getOutputMediaFileForDownload(fileName);
            if (file == null)
                return null;
            FileOutputStream fileOutput = new FileOutputStream(file);
            long downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;

                Log.i("Progress:", "downloadedSize:" + downloadedSize + "totalSize:" + totalSize);
            }
            fileOutput.close();
            if (downloadedSize == totalSize) filepath = file.getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filepath;
    }

    private File getOutputMediaFileForDownload(String fileNameWithExtension) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), rootDirName + File.separator + IMAGES);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileNameWithExtension);
        return mediaFile;
    }

    public boolean deleteFile(final String filePath) {
        boolean isFileExists = false;
        if (filePath != null) {
            File mFilePath = new File(filePath);
            if (mFilePath.exists()) {
                isFileExists = mFilePath.delete();
            }
        }
        return isFileExists;
    }

    public String selectedImage(Context context, Intent data) {
        String imgDecodableString = null;
        if (data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            imgDecodableString = selectedImage(context, selectedImage, PICTURE_TYPE);
        }
        return imgDecodableString;
    }

    public String selectedImage(Context context, Uri selectedImage, String fileType) {
        String imgDecodableString = null;
        try {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            // Get the cursor
            Cursor cursor = context.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgDecodableString = cursor.getString(columnIndex);
            if (imgDecodableString == null) {
                File file = getOutputMediaFile(null, IMAGES, fileType);
                if (file != null) {
                    imgDecodableString = file.getAbsolutePath();
                    ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver()
                            .openFileDescriptor(selectedImage, "r");
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    Bitmap image = BitmapFactory
                            .decodeFileDescriptor(fileDescriptor);
                    parcelFileDescriptor.close();
                    saveBitmapToPath(image, imgDecodableString);
                }
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (imgDecodableString == null) {
            imgDecodableString = selectedImage.getPath();
        }
        if (imgDecodableString != null && !new File(imgDecodableString).exists()) {
            imgDecodableString = null;
        }
        return imgDecodableString;
    }

    public long fileSize(Context context, Uri returnUri) {
        long size = 0;
        Cursor returnCursor =
                context.getContentResolver().query(returnUri, null, null, null, null);
        if (returnCursor != null) {
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            size = returnCursor.getLong(sizeIndex);
            returnCursor.close();
        }
        return size;
    }

    public String getInternalTempPath(Context context) {
        File direct = context.getCacheDir();
        File file = new File(direct, System.currentTimeMillis() + ".jpeg");
        return file.getAbsolutePath();
    }

    public String getExternalTempPath(Context context) {
        File direct = Environment.getExternalStorageDirectory();
        File file = new File(direct, "temp.jpeg");
        return file.getAbsolutePath();
    }
 public File getExternalTempFile(Context context) {
        File direct = Environment.getExternalStorageDirectory();
        File file = new File(direct, "temp.jpeg");
        return file;
    }

    public String saveBitmapToPath(Bitmap bitmap, String path) {
        try {
            File file = new File(path);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return file.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String saveBitmapToTempPath(Context context,Bitmap bitmap) {
        try {
            File file = getExternalTempFile(context);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return file.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String saveBitmap(Bitmap bitmap, String dirName) {
        try {
            File file = getOutputMediaFile(null, dirName, PICTURE_TYPE);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return file.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /*public String saveBitmap(Context context,Bitmap bitmap) {
        try {
            File file = new File(SharedPrefUtils.getInstance().getDirectoryPath(context));
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile;
            String path;

                path = "img_" + timeStamp + PICTURE_TYPE;
            mediaFile = new File(file.getPath() + File.separator + path);

            FileOutputStream out = new FileOutputStream(mediaFile);
            int size=SharedPrefUtils.getInstance().getImageSize(context);
            if(size==0){
                bitmap=getExactSizeBitmap(bitmap,(int)((float)bitmap.getWidth()*0.8f),(int)((float)bitmap.getHeight()*0.8f),false);
            }else if(size==1){
                bitmap=getExactSizeBitmap(bitmap,(int)((float)bitmap.getWidth()*0.9f),(int)((float)bitmap.getHeight()*0.9f),false);
            }else if(size==2){

            }
           int a= SharedPrefUtils.getInstance().getImageQuality(context);
            if(a==0) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }else{
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            }
            out.flush();
            out.close();
            return mediaFile.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/

    public String getDataPathForAppMemory(Context context) {
        final String mDirName = IMAGES;
        File mSDCardDataPath = new File(context.getFilesDir(), mDirName);
        if (!mSDCardDataPath.exists()) {
            if (!mSDCardDataPath.mkdirs()) {
                return null;
            }
        }
        return mSDCardDataPath.toString();
    }

    public String saveBitmapWithName(Bitmap bitmap, String name, String dirName) {
        try {
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            File file = getOutputMediaFile(name, dirName, PICTURE_TYPE);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return file.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUriAsString(String imgPath) {
        Uri uri = getUri(imgPath);
        if (uri != null) {
            return Uri.decode(uri.toString());
        } else
            return null;
    }

    public Uri getUri(String imgPath) {
        if (imgPath != null)
            return Uri.fromFile(new File(imgPath));
        else
            return null;
    }

    public AsyncTask<String, Void, Bitmap> fetchBitmapInBackground(ICallBack iCallBack, String mPath, int width, int height) {
        return new FetchBitmap(iCallBack, mPath, width, height).execute(mPath);
    }
 public AsyncTask<String, Void, Bitmap> fetchBitmapInBackground1(ICallBack1 iCallBack, String mPath, int width, int height) {
        return new FetchBitmap1(iCallBack, mPath, width, height).execute(mPath);
    }

    public Bitmap stretchBitmap(Bitmap normalImage) {
        int w = normalImage.getWidth();
        int h = normalImage.getHeight();
        int progress = h / 2;

        Bitmap stretchImage = Bitmap.createBitmap(w, h + progress, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(stretchImage);

        //draw top bit
        c.drawBitmap(normalImage, new Rect(0, 0, w, h / 3), new Rect(0, 0, w, h / 3), null);

        //draw middle bit
        c.drawBitmap(normalImage, new Rect(0, h / 3, w, (h / 3) * 2), new Rect(0, h / 3, w, (h / 3) * 2 + progress), null);

        //draw right bit
        c.drawBitmap(normalImage, new Rect(0, (h / 3) * 2, w, h), new Rect(0, (h / 3) * 2 + progress, w, h + progress), null);

        return stretchImage;
    }

    private class FetchBitmap extends AsyncTask<String, Void, Bitmap> {
        private ICallBack iCallBack;
        String mPath;
        int width;
        int height;

        FetchBitmap(ICallBack iCallBack, String mPath, int width, int height) {
            this.iCallBack = iCallBack;
            this.mPath = mPath;
            this.width = width;
            this.height = height;
        }

        @Override
        protected Bitmap doInBackground(String... data) {
            if (isCancelled())
                return null;
            if (width == 0 && height == 0) {
                return checkExifAndManageRotation(mPath);
            } else {
                Bitmap bitmap = checkExifAndManageRotation(mPath, width, height);
                if (bitmap.getHeight() < height && bitmap.getWidth() < width) {
                    Bitmap bmp = getExactSizeBitmap(bitmap, width, height);
                    if (bmp != bitmap) {
                        bitmap.recycle();
                    }
                    bitmap = bmp;
                }
                return bitmap;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (iCallBack != null)
                iCallBack.onComplete(bitmap);
        }
    }
 private class FetchBitmap1 extends AsyncTask<String, Void, Bitmap> {
        private ICallBack1 iCallBack;
        String mPath;
        int width;
        int height;

        FetchBitmap1(ICallBack1 iCallBack, String mPath, int width, int height) {
            this.iCallBack = iCallBack;
            this.mPath = mPath;
            this.width = width;
            this.height = height;
        }

        @Override
        protected Bitmap doInBackground(String... data) {
            if (isCancelled())
                return null;
            if (width == 0 && height == 0) {
                return checkExifAndManageRotation(mPath);
            } else {
                Bitmap bitmap = checkExifAndManageRotation(mPath, width, height);
                if (bitmap.getHeight() < height && bitmap.getWidth() < width) {
                    Bitmap bmp = getExactSizeBitmap(bitmap, width, height);
                    if (bmp != bitmap) {
                        bitmap.recycle();
                    }
                    bitmap = bmp;
                }
                return bitmap;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (iCallBack != null)
                iCallBack.onComplete(bitmap,0);
        }
    }

    public Bitmap checkExifAndManageRotation(String mPath, int width, int height) {
        Bitmap bitmap = null;
        int rotation = -1;
        long fileSize = new File(mPath).length();
        //Suppose Device Supports ExifInterface
        ExifInterface exif;
        try {
            exif = new ExifInterface(mPath);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                case ExifInterface.ORIENTATION_UNDEFINED:
                    rotation = 0;
                    break;
            }

            if (rotation != -1) {
                bitmap = decodeAndScaleBitmap(mPath, width, height, rotation);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return bitmap;

    }

    public Bitmap decodeAndScaleBitmap(String f, int width, int height,
                                       int rotation) {

        try {
            File file = new File(f);
            // Decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);

            // The new size we want to scale to

            // Find the correct scale value. It should be the power of 2.
            int scale;
            scale = calculateInSampleSize(options, width, height);

            // Decode with inSampleSize

            options = new BitmapFactory.Options();
            options.inSampleSize = scale;
            options.inPurgeable = true;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inMutable = true;
            if (rotation != 0) {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(
                        file), null, options);
                // New rotation matrix
                Matrix matrix = new Matrix();
                matrix.preRotate(rotation);
                Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
                if (bmp != bitmap)
                    bitmap.recycle();
                return bmp;
            } else
                return BitmapFactory.decodeStream(new FileInputStream(file), null,
                        options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (width > reqWidth || height > reqHeight) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    || (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    public Bitmap checkExifAndManageRotation(String mPath) {
        if (mPath == null)
            return null;
        Bitmap bitmap = null;
        int rotation = -1;
        long fileSize = new File(mPath).length();
        //Suppose Device Supports ExifInterface
        ExifInterface exif;
        try {
            exif = new ExifInterface(mPath);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                case ExifInterface.ORIENTATION_UNDEFINED:
                    rotation = 0;
                    break;
            }

            Log.i("Exif", "Exif:rotation " + rotation);

            if (rotation != -1) {
                bitmap = processImage(mPath, rotation);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return bitmap;

    }

    private Bitmap processImage(final String filePath, final int rotation) throws FileNotFoundException {
        File file = new File(filePath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inPurgeable = true;
        //Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            /*int width = options.outWidth;
            int height = options.outHeight;*/

            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
            if (bmp != bitmap)
                bitmap.recycle();
            bitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
            bmp.recycle();
        }
        return bitmap;
    }

    public Bundle calculateInSampleSize(Bitmap bitmap,
                                        int reqWidth, int reqHeight) {
        final int height = bitmap.getHeight();
        final int width = bitmap.getWidth();
        int inSampleSize = 1;
        Bundle bundle = new Bundle();

        bundle.putInt("width", width);
        bundle.putInt("height", height);

        if (width > reqWidth || height > reqHeight) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight || (halfWidth / inSampleSize) > reqWidth) {
                bundle.putInt("width", halfWidth / inSampleSize);
                bundle.putInt("height", halfHeight / inSampleSize);
                inSampleSize *= 2;
            }
        }
        return bundle;
    }

    public String optimizeImage(String urlAvatar) {
        Bitmap thumbnail = ImageUtility.getInstance().checkExifAndManageRotation(urlAvatar);
        Bundle bundle = ImageUtility.getInstance().calculateInSampleSize(thumbnail, THUMB_SIZE, THUMB_SIZE);
        thumbnail = Bitmap.createScaledBitmap(thumbnail, bundle.getInt("width"), bundle.getInt("height"), true);

        return ImageUtility.getInstance().saveBitmap(thumbnail, ImageUtility.IMAGES);
    }

    public void savePictureInBackground(byte[] data, ICallBack iCallBack, String dirName) {
        new SaveImageTask(iCallBack, dirName).execute(data);
    }


    private class SaveImageTask extends AsyncTask<byte[], Void, File> {
        private ICallBack iCallBack;
        private String dirName;

        SaveImageTask(ICallBack iCallBack, String dirName) {
            this.iCallBack = iCallBack;
            this.dirName = dirName;
        }

        @Override
        protected File doInBackground(byte[]... data) {
            File pictureFile = getOutputMediaFile(null, dirName, PICTURE_TYPE);
            if (pictureFile == null) {

            } else try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data[0]);
                fos.flush();
                fos.close();
                //setImageOnSaving(String.valueOf(pictureFile));
            } catch (FileNotFoundException e) {

            } catch (IOException e) {

            }
            return pictureFile;
        }

        @Override
        protected void onPostExecute(File pictureFile) {
            super.onPostExecute(pictureFile);
            if (iCallBack != null)
                iCallBack.onComplete(String.valueOf(pictureFile));
        }
    }

    public Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            Bitmap resBitmap = ((BitmapDrawable) drawable).getBitmap();
            bitmap = ThumbnailUtils.extractThumbnail(resBitmap, width, height);
            return bitmap;
        }

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public Bitmap rotateBitmap(Bitmap bitmap, int rotationDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public Bitmap hFlipBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public Bitmap vFlipBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(1, -1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /*public Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap;
        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }*/

    public Bitmap cropBitmapTransparency(Bitmap sourceBitmap) {
        int minX = sourceBitmap.getWidth();
        int minY = sourceBitmap.getHeight();
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < sourceBitmap.getHeight(); y++) {
            for (int x = 0; x < sourceBitmap.getWidth(); x++) {
                int alpha = (sourceBitmap.getPixel(x, y) >> 24) & 255;
                if (alpha > 0)   // pixel is not 100% transparent
                {
                    if (x < minX)
                        minX = x;
                    if (x > maxX)
                        maxX = x;
                    if (y < minY)
                        minY = y;
                    if (y > maxY)
                        maxY = y;
                }
            }
        }
        if ((maxX < minX) || (maxY < minY))
            return null; // Bitmap is entirely transparent

        // crop bitmap to non-transparent area and return:
        return Bitmap.createBitmap(sourceBitmap, minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
    }

    public Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);
        //v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }  public Bitmap loadBitmapFromView1(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);
        //v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return (b);
    }

    public Bitmap getExactSizeBitmap(String mPath, int width, int height, boolean increaseIfBelow) {
        Bitmap bitmap = checkExifAndManageRotation(mPath, width, height);
        if (bitmap.getHeight() < height && bitmap.getWidth() < width) {
            Bitmap bmp = getExactSizeBitmap(bitmap, width, height, increaseIfBelow);
            if (bmp != bitmap) {
                bitmap.recycle();
            }
            bitmap = bmp;
        }
        return bitmap;
    }

    public Bitmap getExactSizeBitmap(Bitmap bitmap, int width, int height, boolean increaseIfBelow) {
        if (!increaseIfBelow) {
            if (bitmap.getHeight() < height && bitmap.getWidth() < width)
                return bitmap;
        }
        float ratio = (float) bitmap.getWidth() / (float) bitmap.getHeight();
        if (bitmap.getHeight() > bitmap.getWidth()) {
            width = (int) ((float) height * ratio);
        } else {
            height = (int) ((float) width / ratio);
        }
        return resizeBitmap(bitmap, width, height);
    }

    public Bitmap getExactSizeBitmap(String mPath, int width, int height) {
        Bitmap bitmap = checkExifAndManageRotation(mPath, width, height);
        if (bitmap.getHeight() < height && bitmap.getWidth() < width) {
            Bitmap bmp = getExactSizeBitmap(bitmap, width, height);
            if (bmp != bitmap) {
                bitmap.recycle();
            }
            bitmap = bmp;
        }
        return bitmap;
    }

    public Bitmap getExactSizeBitmap(Bitmap bitmap, int width, int height) {
        float ratio = (float) bitmap.getWidth() / (float) bitmap.getHeight();
        if (bitmap.getHeight() > bitmap.getWidth()) {
            width = (int) ((float) height * ratio);
        } else {
            height = (int) ((float) width / ratio);
        }
        return resizeBitmap(bitmap, width, height);
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }

    public boolean moveFile(Context context, String inputPath, String inputFile, String outputPath) {
        if (!outputPath.endsWith(File.separator)) {
            outputPath = outputPath + File.separator;
        }
        if (!inputPath.endsWith(File.separator)) {
            inputPath = inputPath + File.separator;
        }
        String inputFilePath = inputPath + inputFile;
        String outputFilePath = outputPath + inputFile;
        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            in = new FileInputStream(inputFilePath);
            out = new FileOutputStream(outputFilePath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            File file = new File(inputFilePath);
            deleteFile(file);
            deleteFileFromMediaStore(context.getContentResolver(), file);
            updateGallery(context, outputFilePath);
            return true;
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
        return false;
    }

    public boolean deleteFile(File mFilePath) {
        boolean isFileExists = false;
        if (mFilePath.exists()) {
            isFileExists = mFilePath.delete();
        }
        return isFileExists;
    }

    public void updateGallery(Context context, String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);

        /*MediaScannerConnection.scanFile(context,
                new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(final String path, final Uri uri) {

                    }
                });*/
    }

    public void deleteFileFromMediaStore(final ContentResolver contentResolver, final File file) {
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = file.getAbsolutePath();
        }
        final Uri uri = MediaStore.Files.getContentUri("external");
        final int result = contentResolver.delete(uri, MediaStore.Files.FileColumns.DATA + "=?", new String[]{canonicalPath});
        if (result == 0) {
            final String absolutePath = file.getAbsolutePath();
            if (!absolutePath.equals(canonicalPath)) {
                contentResolver.delete(uri,
                        MediaStore.Files.FileColumns.DATA + "=?", new String[]{absolutePath});
            }
        }
    }

    public Bitmap decodeBitmap(Resources resources, int res) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        return BitmapFactory.decodeResource(resources, res, options);
    }

    public Bitmap mergeBitmap(Bitmap bmp1, Bitmap bmp2, int point) {

        Bitmap bmOverlay = Bitmap.createBitmap(bmp2.getWidth(), bmp2.getHeight(), bmp2.getConfig());
        Canvas canvas = new Canvas(bmOverlay);

        canvas.drawBitmap(bmp1, -(point), 0, null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }
}
