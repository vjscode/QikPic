package com.tuts.vijay.qikpic.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.Utils.Constants;
import com.tuts.vijay.qikpic.Utils.DisplayUtils;
import com.tuts.vijay.qikpic.db.QikPikContentProvider;
import com.tuts.vijay.qikpic.view.DetailQikPikScrollView;
import com.tuts.vijay.qikpic.view.FlowLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity which displays a registration screen to the user.
 */
public class DetailActivity extends Activity implements View.OnClickListener {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private String oldOrNew;
    private ImageView imageView;
    private FlowLayout tagPanel;
    private ImageView addTagIcon;
    private Uri uri;
    private String timeStampForFileName;
    private List<String> tempTagList;
    private boolean isPicNew = false;
    private boolean savingInProgress = false;
    private Map<String, String> dimensions;
    private boolean needsSave = false;
    private Bitmap qikpicBmp;
    private int scaleFactor = 1;
    private GestureDetector gestureDetector;
    private DetailQikPikScrollView scrollView;
    private boolean mScrollable = false;
    private boolean listenForScaling = true;
    ScaleGestureDetector mScaleDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_list_item_detail);
        imageView = (ImageView) findViewById(R.id.image);
        tagPanel = (FlowLayout) findViewById(R.id.taggingPanel);
        addTagIcon = (ImageView) findViewById(R.id.addTagIcon);
        addTagIcon.setOnClickListener(this);
        scrollView = (DetailQikPikScrollView) findViewById(R.id.scrollContainer);
        scrollView.setScrollable(true);
        oldOrNew = getIntent().getStringExtra("oldOrNew");
        setProgressBarIndeterminateVisibility(true);
        dimensions = new HashMap<String, String>();
        gestureDetector = new GestureDetector(this, new GestureListener());
        if (!oldOrNew.equals("new")) {
            loadImage();
        } else {
            isPicNew = true;
            uri = getIntent().getParcelableExtra("uri");
            Log.d("test", "uri:>> " + uri);
            timeStampForFileName = getIntent().getStringExtra("thumbnailname");
            Log.d("test", "timeStampForFileName:>> " + timeStampForFileName);
            Bitmap bmp = setBitmapFromUri();
            loadImageFromBitmap(bmp);
        }
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                mScaleDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
        initScaleGestureDetector();
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll (MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mScrollable) {
                float currentX = imageView.getScrollX();
                float currentY = imageView.getScrollY();
                imageView.setScrollX((int) (currentX + distanceX));
                imageView.setScrollY((int) (currentY + distanceY));
            }
            return true;
        }

        /*@Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mScrollable == false) {
                imageView.setScaleX(2.0f);
                imageView.setScaleY(2.0f);
                mScrollable = true;
                scrollView.setScrollable(false);
            } else {
                imageView.setScaleX(1.0f);
                imageView.setScaleY(1.0f);
                imageView.setScrollX(0);
                imageView.setScrollY(0);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                mScrollable = false;
                scrollView.setScrollable(true);
            }
            //mScrollable = scrollMode;
            return super.onDoubleTap(e);
        }*/
    }

    private void initScaleGestureDetector() {
        mScaleDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
            float scaleStart;
            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                listenForScaling = true;
                ParseAnalytics.trackEventInBackground(Constants.ZOOMED);
            }
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                scaleStart = detector.getScaleFactor();
                return true;
            }
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (listenForScaling) {
                    if (detector.getScaleFactor() - scaleStart > 0/*mScrollable == false*/) {
                        imageView.setScaleX(2.0f);
                        imageView.setScaleY(2.0f);
                        mScrollable = true;
                        scrollView.setScrollable(false);
                        listenForScaling = false;
                    } else if (detector.getScaleFactor() - scaleStart < 0) {
                        imageView.setScaleX(1.0f);
                        imageView.setScaleY(1.0f);
                        imageView.setScrollX(0);
                        imageView.setScrollY(0);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        mScrollable = false;
                        scrollView.setScrollable(true);
                        listenForScaling = false;
                    } else {
                        listenForScaling = true;
                    }

                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_save:
                if (isPicNew) {
                    //createQikPik();
                    createThumbnail();
                } else {
                    saveQikPik();
                }
                break;
        }
        return true;
    }

    private void createThumbnail() {
        Bitmap actuallyUsableBitmap = null;
        if (qikpicBmp == null) {
            setBitmapFromUri();
        }
        actuallyUsableBitmap = qikpicBmp;
        Bitmap thumbnailImage = ThumbnailUtils.extractThumbnail(actuallyUsableBitmap, actuallyUsableBitmap.getWidth()/2, actuallyUsableBitmap.getHeight()/2);
        createDirIfNotPresent();
        FileOutputStream fOut = null;
        try {
            //save full file
            fOut = new FileOutputStream(getFilesDir() + "/full/full_" + timeStampForFileName + ".jpg");
            qikpicBmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            //save thumbnail file
            fOut = new FileOutputStream(getFilesDir() + "/thumbnail/thumbnail_" + timeStampForFileName + ".jpg");
            thumbnailImage.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            saveToDB();
            removeFileFromDisk();
            finish();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void createDirIfNotPresent() {
        String filesPath = getFilesDir().getPath();
        File full = new File(filesPath, "full");
        if (!full.exists()) {
            if (!full.mkdir()) {
                Log.e("test", "could not create directory");
            }
        }
        File thumbnail = new File(filesPath, "thumbnail");
        if (!thumbnail.exists()) {
            if (!thumbnail.mkdir()) {
                Log.e("test", "could not create directory");
            }
        }
    }

    private void saveToDB() {
        ContentValues values = createContentValues();
        getContentResolver().insert(QikPikContentProvider.CONTENT_URI, values);
    }

    private ContentValues createContentValues() {
        ContentValues values = new ContentValues();
        values.put("image", getFilesDir() + "/full/full_" + timeStampForFileName + ".jpg");
        values.put("userId", ParseUser.getCurrentUser().getObjectId());
        values.put("createdAt", System.currentTimeMillis());
        values.put("updatedAt", System.currentTimeMillis());
        values.put("tags", getTagListAsString());
        values.put("thumbnail", getFilesDir() + "/thumbnail/thumbnail_" + timeStampForFileName + ".jpg");
        values.put("draft", 1);
        values.put("qikpicId", timeStampForFileName);
        return values;
    }

    private String getTagListAsString() {
        if (tempTagList == null) {
            tempTagList = new ArrayList<String>();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tempTagList.size(); i++) {
            String str = tempTagList.get(i).toString();
            sb.append(str+",");
        }
        if (sb.length() > 1) {
            sb = sb.deleteCharAt(sb.lastIndexOf(","));
        }
        return sb.toString();
    }

    private void loadImageFromBitmap(Bitmap bmp) {
        needsSave = true;
        imageView.setImageBitmap(bmp);
        setProgressBarIndeterminateVisibility(false);
    }

    private void loadImageFromUri() {
        needsSave = true;
        imageView.setImageURI(uri);
        setProgressBarIndeterminateVisibility(false);
    }

    private void loadImage() {
        //load image from db table
        Cursor c = getContentResolver().query(QikPikContentProvider.CONTENT_URI,
                new String[]{"image", "tags"}, "qikpicId=?", new String[]{oldOrNew}, null);
        c.moveToFirst();
        String imgFile = /*"/data/data/com.tuts.vijay.qikpic/files/full/full_20150928_025528.jpg";*/c.getString(c.getColumnIndex("image"));
        imageView.setImageURI(Uri.parse(imgFile));
        //load tags into list
        String tagStr = c.getString(c.getColumnIndex("tags"));
        loadTags(tagStr);
    }

    private void loadTags(String tagStr) {
        String[] tags = tagStr.split(",");
        for (String tag : tags) {
            if (!tag.equals("")) {
                addTagToList(tag);
            }
        }
    }

    private void showTags(ParseObject object) {
        List<String> tagList = object.getList("tags");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int)DisplayUtils.fromDpToPx(this, 48));
        if (tagList == null) {
            return;
        }
        emptyTagPanel();
        for (String tag: tagList) {
            LayoutInflater inflater = LayoutInflater.from(this);
            TextView tagView= (TextView) inflater.inflate(R.layout.tag_view, null, false);
            tagView.setText(tag);
            tagPanel.addView(tagView, 0, lp);
        }
    }

    private void emptyTagPanel() {
        for (int i = 0; i < tagPanel.getChildCount(); i++) {
            tagPanel.removeViewAt(i);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addTagIcon) {
            getInputFromUser();
        }
    }

    private void getInputFromUser() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Tag");

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                addTagToList(value);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
   }

    private void addTagToList(String tag) {
        if (tempTagList == null) {
            tempTagList = new ArrayList<String>();
        }
        tempTagList.add(tag);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                (int)DisplayUtils.fromDpToPx(this, 48));

        LayoutInflater inflater = LayoutInflater.from(this);
        TextView tagView = (TextView) inflater.inflate(R.layout.tag_view, null, false);
        tagView.setText(tag);
        tagPanel.addView(tagView, 0, lp);
        needsSave = true;
    }

    @Override
    public void onBackPressed() {
        if (savingInProgress) {
            return;
        }
        if (!needsSave) {
            super.onBackPressed();
            return;
        }
        if (isPicNew) {
            //createQikPik();
            createThumbnail();
        } else {
            saveQikPik();
        }
    }

    private void saveQikPik() {
        ContentValues values = new ContentValues();
        values.put("tags", getTagListAsString());
        values.put("draft", 1);
        //update db table with id..(Uri uri, ContentValues values, String selection, String[] selectionArgs)
        int count = getContentResolver().update(QikPikContentProvider.CONTENT_URI,
                values, "qikpicId=?", new String[]{oldOrNew});
        Log.d("test", "count of updated records: " + count);
        finish();
    }
    private Bitmap adjustImageOrientation(Bitmap image) {
        ExifInterface exif;
        try {

            exif = new ExifInterface(new File(uri.getPath()).getAbsolutePath());
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int rotate = 0;
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            if (rotate != 0) {
                int w = image.getWidth();
                int h = image.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap & convert to ARGB_8888, required by tess
                image = Bitmap.createBitmap(image, 0, 0, w, h, mtx, false);
            }
        } catch (IOException e) {
            return null;
        }
        return image.copy(Bitmap.Config.ARGB_8888, true);
    }

    private Bitmap setBitmapFromUri() {
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor =
                    getContentResolver().openAssetFileDescriptor(uri, "r");

            int widthPixels = DisplayUtils.getWidth(this);
            int heightPixels = DisplayUtils.getHeight(this) / 2;

            float targetW = widthPixels;
            float targetH = heightPixels;

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(
                    fileDescriptor.getFileDescriptor(), null, bmOptions);
            float photoW = bmOptions.outWidth;
            float photoH = bmOptions.outHeight;

            float scale = Math.max(photoW / targetW, photoH / targetH);

            Log.d("test", "SCALE: " + scale);

            // Determine how much to scale down the image
            scaleFactor = (int) Math.ceil((double) scale) * 2;

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            Bitmap actuallyUsableBitmap
                    = BitmapFactory.decodeFileDescriptor(
                    fileDescriptor.getFileDescriptor(), null, bmOptions);

            qikpicBmp =  adjustImageOrientation(actuallyUsableBitmap);
            return qikpicBmp;
        } catch (IOException io) {
            return null;
        }
    }

    private void removeFileFromDisk() {
        Log.d("test", "deleted? " + new File(uri.getPath()).delete());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}