package com.tuts.vijay.qikpic.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.Utils.Constants;
import com.tuts.vijay.qikpic.Utils.DisplayUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
    private String id;
    private ParseImageView imageView;
    private LinearLayout tagPanel;
    private ImageView addTagIcon;
    private Uri uri;
    private List<String> tempTagList;
    private boolean isPicNew = false;
    private boolean savingInProgress = false;
    private Map<String, String> dimensions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_list_item_detail);
        imageView = (ParseImageView) findViewById(R.id.image);
        tagPanel = (LinearLayout) findViewById(R.id.taggingPanel);
        addTagIcon = (ImageView) findViewById(R.id.addTagIcon);
        addTagIcon.setOnClickListener(this);
        id = getIntent().getStringExtra("id");
        setProgressBarIndeterminateVisibility(true);
        dimensions = new HashMap<String, String>();
        if (!id.equals("new")) {
            loadImage();
        } else {
            isPicNew = true;
            uri = getIntent().getParcelableExtra("uri");
            loadImageFromUri();
        }
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

    private void loadImageFromUri() {

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
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int)DisplayUtils.fromDpToPx(this, 32));
        if (tagList == null) {
            return;
        }
        emptyTagPanel();
        for (String tag: tagList) {
            LayoutInflater inflater = LayoutInflater.from(this);
            TextView tagView= (TextView) inflater.inflate(R.layout.tag_view, null, false);
            tagView.setText(tag);
            tagPanel.addView(tagView, 1, lp);
        }
    }

    private void emptyTagPanel() {
        for (int i = 0; i < tagPanel.getChildCount(); i++) {
            if (!isViewAnchor(tagPanel.getChildAt(i))) {
                tagPanel.removeViewAt(i);
            }
        }
    }

    private boolean isViewAnchor(View childAt) {
        int id = childAt.getId();
        if (id == R.id.addTagIcon || id == R.id.tagIcon) {
            return true;
        }
        return false;
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
                ViewGroup.LayoutParams.WRAP_CONTENT);

        LayoutInflater inflater = LayoutInflater.from(this);
        TextView tagView = (TextView) inflater.inflate(R.layout.tag_view, null, false);
        tagView.setText(tag);
        tagPanel.addView(tagView, 1, lp);
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
           long startTime = SystemClock.elapsedRealtimeNanos();
           savingInProgress = true;
           setProgressBarIndeterminateVisibility(true);
           AssetFileDescriptor fileDescriptor = null;
           fileDescriptor =
                   getContentResolver().openAssetFileDescriptor(uri, "r");

           float targetW = imageView.getWidth();
           float targetH = imageView.getHeight();

           // Get the dimensions of the bitmap
           BitmapFactory.Options bmOptions = new BitmapFactory.Options();
           bmOptions.inJustDecodeBounds = true;
           BitmapFactory.decodeFileDescriptor(
                   fileDescriptor.getFileDescriptor(), null, bmOptions);
           float photoW = bmOptions.outWidth;
           float photoH = bmOptions.outHeight;

           float scale = Math.max(photoW/targetW, photoH/targetH);

           // Determine how much to scale down the image
           int scaleFactor = (int)Math.ceil((double)scale) * 2;

           // Decode the image file into a Bitmap sized to fill the View
           bmOptions.inJustDecodeBounds = false;
           bmOptions.inSampleSize = scaleFactor;
           bmOptions.inPurgeable = true;

           Bitmap actuallyUsableBitmap
                   = BitmapFactory.decodeFileDescriptor(
                   fileDescriptor.getFileDescriptor(), null, bmOptions);

           actuallyUsableBitmap = adjustImageOrientation(actuallyUsableBitmap);

           bmOptions.inSampleSize = scaleFactor * 2;

           Bitmap thumbnailImage
                   = BitmapFactory.decodeFileDescriptor(
                   fileDescriptor.getFileDescriptor(), null, bmOptions);

           thumbnailImage = adjustImageOrientation(thumbnailImage);

           Log.d("test", "mid: " + SystemClock.elapsedRealtimeNanos());

           prepareAndSaveParseObject(actuallyUsableBitmap, thumbnailImage, startTime);

       } catch (FileNotFoundException e) {
           e.printStackTrace();
           savingInProgress = false;
       } catch (IOException e) {
           e.printStackTrace();
           savingInProgress = false;
       } catch (RuntimeException re) {
           savingInProgress = false;
       }
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
                dimensions.put(Constants.TIME_TO_UPLOAD, (SystemClock.elapsedRealtimeNanos() - startTime) + "");
                ParseAnalytics.trackEventInBackground(Constants.QIKPIK_ANALYTICS, dimensions);
                finish();
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
}