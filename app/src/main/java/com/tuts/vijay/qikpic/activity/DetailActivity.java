package com.tuts.vijay.qikpic.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;
import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.Utils.DisplayUtils;

import java.util.List;

/**
 * Activity which displays a registration screen to the user.
 */
public class DetailActivity extends Activity {

    private String id;
    ParseImageView imageView;
    LinearLayout tagPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item_detail);
        imageView = (ParseImageView) findViewById(R.id.image);
        tagPanel = (LinearLayout) findViewById(R.id.taggingPanel);
        id = getIntent().getStringExtra("id");
        loadImage();
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
        for (String tag: tagList) {
            TextView tagView = new TextView(this);
            tagPanel.addView(tagView, 1, lp);
        }
    }

    private void showImage(ParseObject object) {
        ParseFile f = object.getParseFile("image");
        Picasso.with(this).load(f.getUrl()).into(imageView);
    }
}