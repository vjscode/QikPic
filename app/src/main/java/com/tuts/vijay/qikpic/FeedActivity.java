package com.tuts.vijay.qikpic;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.tuts.vijay.qikpic.adapter.QikPicParseQueryAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FeedActivity extends Activity implements View.OnClickListener {

    private static final int TAKE_PHOTO = 0;
    ImageView captureBtn;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        captureBtn = (ImageView) findViewById(R.id.capture);
        captureBtn.setOnClickListener(this);
        ParseQueryAdapter<ParseObject> adapter = createParseAdapter();
        list = (ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);
    }

    private ParseQueryAdapter createParseAdapter() {
        ParseQueryAdapter<ParseObject> adapter = new QikPicParseQueryAdapter(this, "QikPik");//ParseQueryAdapter<ParseObject>(this, "QikPik");
        //adapter.setImageKey("image");
        return adapter;
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

        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
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
                //bmp = MediaStore.Images.Media.getBitmap( this.getContentResolver(), mCurrentPhotoUri);
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
                Log.d("test", "Success: " + e);
            }
        });
    }

    private ParseFile getParseFileFromBitmap(Bitmap bmp) {
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 30 /* ignored for PNG */,blob);
        byte[] imgArray = blob.toByteArray();
        //Assign Byte array to ParseFile
        ParseFile parseImagefile = new ParseFile("profile_pic.jpg", imgArray);
        return parseImagefile;
    }


}
