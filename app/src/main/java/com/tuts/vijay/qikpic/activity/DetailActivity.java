package com.tuts.vijay.qikpic.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
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

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.Utils.Constants;
import com.tuts.vijay.qikpic.Utils.DisplayUtils;
import com.tuts.vijay.qikpic.view.DetailQikPikScrollView;
import com.tuts.vijay.qikpic.view.FlowLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Activity which displays a registration screen to the user.
 */
public class DetailActivity extends Activity implements View.OnClickListener {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private String id;
    private ImageView imageView;
    private FlowLayout tagPanel;
    private ImageView addTagIcon;
    private Uri uri;
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
        id = getIntent().getStringExtra("id");
        setProgressBarIndeterminateVisibility(true);
        dimensions = new HashMap<String, String>();
        gestureDetector = new GestureDetector(this, new GestureListener());
        if (!id.equals("new")) {
            loadImage();
        } else {
            isPicNew = true;
            uri = getIntent().getParcelableExtra("uri");
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
                    createQikPik();
                } else {
                    saveQikPik();
                }
                break;
        }
        return true;
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
        ParseQuery<ParseObject> query = ParseQuery.getQuery("QikPik");
        query.getInBackground(id, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                Log.d("test", "parse exception: " + e);
                if (e == null) {
                    showImage(object);
                    showTags(object);
                    setProgressBarIndeterminateVisibility(false);
                } else {
                    // something went wrong
                }
            }
        });
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

    private void showImage(ParseObject object) {
        ParseFile f = object.getParseFile("image");
        Picasso.with(this).load(f.getUrl()).into(imageView);
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

   private void saveQikPik() {
       savingInProgress = true;
       setProgressBarIndeterminateVisibility(true);
       ParseQuery<ParseObject> query = ParseQuery.getQuery("QikPik");
       query.getInBackground(id, new GetCallback<ParseObject>() {
           public void done(ParseObject object, ParseException e) {
               setProgressBarIndeterminateVisibility(false);
               List<String> tags = object.getList("tags");
               if (tags == null) {
                   tags = new ArrayList<String>();
               }
               if (tempTagList != null) {
                   tags.addAll(tempTagList);
               }
               object.put("tags", tags);
               object.saveInBackground();
               setResult(Activity.RESULT_OK);
               savingInProgress = false;
               finish();
           }
       });
   }

   private void createQikPik() {
       try {
           long startTime = System.currentTimeMillis();
           savingInProgress = true;
           setProgressBarIndeterminateVisibility(true);
           Bitmap actuallyUsableBitmap = null;
           if (qikpicBmp == null) {
               setBitmapFromUri();
           }
           actuallyUsableBitmap = qikpicBmp;

           Bitmap thumbnailImage = ThumbnailUtils.extractThumbnail(actuallyUsableBitmap, actuallyUsableBitmap.getWidth()/2, actuallyUsableBitmap.getHeight()/2);

           Log.d("test", "mid: " + System.currentTimeMillis());

           prepareAndSaveParseObject(actuallyUsableBitmap, thumbnailImage, startTime);
           //prepareAndSaveLocally(actuallyUsableBitmap, thumbnailImage, startTime);

           //printLocal();

           //saveRotatedImageAndSendToService();

       } catch (RuntimeException re) {
           savingInProgress = false;
       }
   }

    private void printLocal() {
        ParseQuery query = new ParseQuery("QikPik");
        query.fromPin("qikpic_drafts");
        query.whereEqualTo("isDraft", true);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                Log.d("test", "list: " + list + ", e: " + e);
                for (final ParseObject p : list) {
                    Log.d("test", "p: " + p.get("image"));
                    Log.d("test", "p: " + p.get("thumbnail"));
                }
            }

        });
    }

    private String saveRotatedImageAndSendToService(Bitmap bmp, String img) {
        String path = uri.getPath();
        if (img.equals("thumbnail")) {
            String bmp_name = path.substring(0, path.lastIndexOf(".jpg"));
            ThumbnailUtils.extractThumbnail(bmp, bmp.getWidth()/2, bmp.getHeight()/2);
            path = bmp_name + "_thumbnail.jpg";
        }
        Log.d("test", "uri: " + path);
        File f = new File(path);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fOut.flush();
                fOut.close();
            } catch (IOException ioe) {

            }
        }
        return path;
    }

    private void prepareAndSaveParseObject(Bitmap bmp, Bitmap thumbnail, final long startTime) {
        final ParseObject po = new ParseObject("QikPik");
        po.put("user", ParseUser.getCurrentUser());
        po.put("image", getParseFileFromBitmap(bmp, "profile_pic.jpg"));
        po.put("thumbnail", getParseFileFromBitmap(thumbnail, "thumbnail_pic.jpg"));
        if (tempTagList != null) {
            po.put("tags", tempTagList);
        }
        po.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                Log.d(TAG, "Success saving object: " + e);
                removeFileFromDisk();
                setResult(Activity.RESULT_OK);
                savingInProgress = false;
                dimensions.put(Constants.TIME_TO_UPLOAD, (System.currentTimeMillis() - startTime) + "");
                ParseAnalytics.trackEventInBackground(Constants.QIKPIK_ANALYTICS, dimensions);
                finish();
            }
        });
    }

    private void prepareAndSaveLocally(Bitmap bmp, Bitmap thumbnail, final long startTime) {
        final ParseObject po = new ParseObject("QikPik");
        po.put("user", ParseUser.getCurrentUser());
        po.put("uuid", UUID.randomUUID().toString());
        po.put("isDraft", true);
        po.put("image", saveRotatedImageAndSendToService(bmp, "image"));//getParseFileFromBitmap(bmp, "profile_pic.jpg"));
        po.put("thumbnail", saveRotatedImageAndSendToService(bmp, "thumbnail"));
        if (tempTagList != null) {
            po.put("tags", tempTagList);
        }
        po.pinInBackground("qikpic_drafts",
                new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            //send intent to intent service
                            Log.d("test", "saved to db");
                            //finish activity
                            finish();
                        }
                    }
                });
    }

    private ParseFile getParseFileFromBitmap(Bitmap bmp, String name) {
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100 /* ignored for PNG */,blob);
        byte[] imgArray = blob.toByteArray();
        //Assign Byte array to ParseFile
        ParseFile parseImagefile = new ParseFile(name, imgArray);
        return parseImagefile;
    }

    private void removeFileFromDisk() {
        Log.d("test", "deleted? " + new File(uri.getPath()).delete());
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
            createQikPik();
        } else {
            saveQikPik();
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}