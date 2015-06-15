package com.tuts.vijay.qikpic.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.Utils.DisplayUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item_detail);
        imageView = (ParseImageView) findViewById(R.id.image);
        tagPanel = (LinearLayout) findViewById(R.id.taggingPanel);
        addTagIcon = (ImageView) findViewById(R.id.addTagIcon);
        addTagIcon.setOnClickListener(this);
        id = getIntent().getStringExtra("id");
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
    }

    private void loadImage() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("QikPik");
        query.getInBackground(id, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                Log.d("test", "parse exception: " + e);
                if (e == null) {
                    showImage(object);
                    showTags(object);
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
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int)DisplayUtils.fromDpToPx(this, 32));

        LayoutInflater inflater = LayoutInflater.from(this);
        TextView tagView = (TextView) inflater.inflate(R.layout.tag_view, null, false);
        tagView.setText(tag);
        tagPanel.addView(tagView, 1, lp);
    }

   private void saveQikPik() {
       ParseQuery<ParseObject> query = ParseQuery.getQuery("QikPik");
       query.getInBackground(id, new GetCallback<ParseObject>() {
           public void done(ParseObject object, ParseException e) {
               List<String> tags = object.getList("tags");
               if (tags == null) {
                   tags = new ArrayList<String>();
               }
               tags.addAll(tempTagList);
               object.put("tags", tags);
               object.saveInBackground();
               setResult(Activity.RESULT_OK);
               finish();
           }
       });
   }

   private void createQikPik() {
       try {
           BitmapFactory.Options options = new BitmapFactory.Options();
           options.inSampleSize = 4;

           AssetFileDescriptor fileDescriptor =null;
           fileDescriptor =
                   getContentResolver().openAssetFileDescriptor(uri, "r");

           Bitmap actuallyUsableBitmap
                 = BitmapFactory.decodeFileDescriptor(
               fileDescriptor.getFileDescriptor(), null, options);
           prepareAndSaveParseObject(actuallyUsableBitmap);

       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

    private void prepareAndSaveParseObject(Bitmap bmp) {
        final ParseObject po = new ParseObject("QikPik");
        po.put("user", ParseUser.getCurrentUser());
        po.put("image", getParseFileFromBitmap(bmp));
        if (tempTagList != null) {
            po.put("tags", tempTagList);
        }
        po.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d(TAG, "Success saving object: " + e);
                removeFileFromDisk();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    private ParseFile getParseFileFromBitmap(Bitmap bmp) {
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100 /* ignored for PNG */,blob);
        byte[] imgArray = blob.toByteArray();
        //Assign Byte array to ParseFile
        ParseFile parseImagefile = new ParseFile("profile_pic.jpg", imgArray);
        return parseImagefile;
    }

    private void removeFileFromDisk() {
        Log.d("test", "deleted? " + new File(uri.getPath()).delete());
    }
}