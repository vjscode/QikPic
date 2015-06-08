package com.tuts.vijay.qikpic.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.fragment.PhotosGridFragment;
import com.tuts.vijay.qikpic.fragment.PhotosListFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FeedActivity extends Activity implements View.OnClickListener, PhotosListFragment.OnFragmentInteractionListener {

    private static final int TAKE_PHOTO = 0;
    private static final String TAG = FeedActivity.class.getSimpleName();

    //UI
    private ImageView captureBtn;
    private ImageView gridIcon;
    private ImageView listIcon;

    //Fragments
    PhotosGridFragment gridFragment;
    PhotosListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        initUI();
        initFragment();
    }

    private void initUI() {
        gridIcon = (ImageView) findViewById(R.id.grid_icon);
        gridIcon.setOnClickListener(this);
        listIcon = (ImageView) findViewById(R.id.list_icon);
        listIcon.setOnClickListener(this);
        captureBtn = (ImageView) findViewById(R.id.capture);
        captureBtn.setOnClickListener(this);
    }

    private void initFragment() {
        FragmentManager fm = getFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        gridFragment = new PhotosGridFragment();
        ft.add(R.id.fragmentContainer, gridFragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            performLogOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void performLogOut() {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                startActivity(new Intent(FeedActivity.this, NewUserActivity.class));
                FeedActivity.this.finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.capture) {
            startCamera();
        } else if (view.getId() == R.id.list_icon) {
            switchPanes(R.id.list_icon);
        } else if (view.getId() == R.id.grid_icon) {
            switchPanes(R.id.grid_icon);
        }
    }

    private void switchPanes(int id) {
        FragmentManager fm = getFragmentManager();
        if (id == R.id.list_icon) {
            FragmentTransaction ft = fm.beginTransaction();
            if (listFragment == null) {
                listFragment = new PhotosListFragment();
            }
            ft.replace(R.id.fragmentContainer, listFragment);
            ft.commit();
        } else {
            FragmentTransaction ft = fm.beginTransaction();
            if (gridFragment == null) {
                gridFragment = new PhotosGridFragment();
            }
            ft.replace(R.id.fragmentContainer, gridFragment);
            ft.commit();
        }
    }

    private void startCamera() {
        try {
            createImageFileUri();
            dispatchTakePictureIntent();
        } catch (IOException e) {
            Toast.makeText(this, "Could n't start camera", Toast.LENGTH_SHORT);
        }
    }

    Uri mCurrentPhotoUri;

    private void createImageFileUri() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoUri = Uri.fromFile(image);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri);
        startActivityForResult(takePictureIntent, TAKE_PHOTO);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bmp;
        if (resultCode == RESULT_OK) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;

                AssetFileDescriptor fileDescriptor =null;
                fileDescriptor =
                        getContentResolver().openAssetFileDescriptor(mCurrentPhotoUri, "r");

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
    }

    private void prepareAndSaveParseObject(Bitmap bmp) {
        ParseObject po = new ParseObject("QikPik");
        po.put("user", ParseUser.getCurrentUser());
        po.put("image", getParseFileFromBitmap(bmp));
        po.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d(TAG, "Success saving object: " + e);
                //adapter.loadObjects();
                removeFileFromDisk();
            }
        });
    }

    private void removeFileFromDisk() {
        Log.d("test", "deleted? " + new File(mCurrentPhotoUri.getPath()).delete());
    }

    private ParseFile getParseFileFromBitmap(Bitmap bmp) {
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100 /* ignored for PNG */,blob);
        byte[] imgArray = blob.toByteArray();
        //Assign Byte array to ParseFile
        ParseFile parseImagefile = new ParseFile("profile_pic.jpg", imgArray);
        return parseImagefile;
    }


    @Override
    public void onFragmentInteraction(String id) {

    }
}
