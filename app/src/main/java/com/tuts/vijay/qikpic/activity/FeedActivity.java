package com.tuts.vijay.qikpic.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.CountCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.tuts.vijay.qikpic.ActivityInteraction;
import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.Utils.Constants;
import com.tuts.vijay.qikpic.async.UploadTask;
import com.tuts.vijay.qikpic.fragment.QikPicGridFragment;
import com.tuts.vijay.qikpic.fragment.QikPicListFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FeedActivity extends Activity implements View.OnClickListener, QikPicListFragment.OnFragmentInteractionListener,
        View.OnTouchListener {

    private static final int TAKE_PHOTO = 0;
    private static final int SHOW_PHOTO = 1;
    private static final String TAG = FeedActivity.class.getSimpleName();

    //UI
    private ImageView captureBtn;
    private ImageView gridIcon;
    private ImageView listIcon;

    //Fragments
    //PhotosGridFragment gridFragment;
    QikPicGridFragment gridFragment;
    //PhotosListFragment listFragment;
    QikPicListFragment listFragment;

    Fragment currentFragment;

    TextView txtQikPikCount;

    RelativeLayout layoutFeed;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);
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
        txtQikPikCount = (TextView) findViewById(R.id.qikpikCount);
        layoutFeed = (RelativeLayout) findViewById(R.id.layout_feed);
        layoutFeed.setOnTouchListener(this);
        //runQikPikCountQuery();
    }

    private void initFragment() {
        FragmentManager fm = getFragmentManager();

        int activePane = readActivePane();
        FragmentTransaction ft = fm.beginTransaction();
        if (activePane == Constants.CONTAINER_GRID) {
            gridFragment = new QikPicGridFragment();
            ft.add(R.id.fragmentContainer, gridFragment);
            ft.commit();
            currentFragment = gridFragment;
        } else {
            listFragment = new QikPicListFragment();//PhotosListFragment();
            ft.add(R.id.fragmentContainer, listFragment);
            ft.commit();
            currentFragment = listFragment;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
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
                listFragment = new QikPicListFragment();
            }
            ft.replace(R.id.fragmentContainer, listFragment);
            ft.commit();
            currentFragment = listFragment;
            saveActivePane(Constants.CONTAINER_LIST);
        } else {
            FragmentTransaction ft = fm.beginTransaction();
            if (gridFragment == null) {
                gridFragment = new QikPicGridFragment();
            }
            ft.replace(R.id.fragmentContainer, gridFragment);
            ft.commit();
            currentFragment = gridFragment;
            saveActivePane(Constants.CONTAINER_GRID);
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
    String mCurrentTimeStamp;

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
        mCurrentTimeStamp = timeStamp;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri);

        startActivityForResult(takePictureIntent, TAKE_PHOTO);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bmp;
        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            startActivityForTagging();
        } else if (requestCode == SHOW_PHOTO && resultCode == RESULT_OK) {
            ((ActivityInteraction)currentFragment).loadObjects();
            runQikPikCountQuery();
        }
    }

    private void startActivityForTagging() {
        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra("oldOrNew", "new");
        Log.d("test", "feed uri:>> " + mCurrentTimeStamp);
        i.putExtra("uri", mCurrentPhotoUri);
        i.putExtra("thumbnailname", mCurrentTimeStamp);
        startActivityForResult(i, SHOW_PHOTO);
    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    private void runQikPikCountQuery() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("QikPik");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    txtQikPikCount.setText(count + "");
                } else {
                    // The request failed
                }
            }
        });
    }

    private int readActivePane() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int activePane = sharedPref.getInt(getString(R.string.active_pane), Constants.CONTAINER_LIST);
        return activePane;
    }

    private void saveActivePane(int activePane) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.active_pane), activePane);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new UploadTask(this).execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentPhotoUri != null) {
            outState.putString("cameraImageUri", mCurrentPhotoUri.toString());
        }
        if (mCurrentTimeStamp != null) {
            outState.putString("picTimeStamp", mCurrentTimeStamp);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("cameraImageUri")) {
            mCurrentPhotoUri = Uri.parse(savedInstanceState.getString("cameraImageUri"));
        }
        if (savedInstanceState.containsKey("picTimeStamp")) {
            mCurrentTimeStamp = savedInstanceState.getString("picTimeStamp");
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (checkFocusRec(searchView)) {
                Rect rect = new Rect();
                searchView.getGlobalVisibleRect(rect);
                if (!rect.contains((int)motionEvent.getRawX(), (int)motionEvent.getRawY())) {
                    searchView.clearFocus();
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    searchView.setIconified(true);
                }
            }
        }
        return false;
    }

    private boolean checkFocusRec(View view) {
        if (view.isFocused())
            return true;

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (checkFocusRec(viewGroup.getChildAt(i)))
                    return true;
            }
        }
        return false;
    }
}
