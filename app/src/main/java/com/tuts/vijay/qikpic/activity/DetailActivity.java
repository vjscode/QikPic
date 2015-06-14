package com.tuts.vijay.qikpic.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.squareup.picasso.Picasso;
import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.Utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity which displays a registration screen to the user.
 */
public class DetailActivity extends Activity implements View.OnClickListener {

    private String id;
    private ParseImageView imageView;
    private LinearLayout tagPanel;
    private ImageView addTagIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item_detail);
        imageView = (ParseImageView) findViewById(R.id.image);
        tagPanel = (LinearLayout) findViewById(R.id.taggingPanel);
        addTagIcon = (ImageView) findViewById(R.id.addTagIcon);
        addTagIcon.setOnClickListener(this);
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
                saveTag(value);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
   }

   private void saveTag(final String tag) {
       ParseQuery<ParseObject> query = ParseQuery.getQuery("QikPik");
       query.getInBackground(id, new GetCallback<ParseObject>() {
           public void done(ParseObject object, ParseException e) {
               List<String> tags = object.getList("tags");
               if (tags == null) {
                   tags = new ArrayList<String>();
               }
               tags.add(tag);
               object.put("tags", tags);
               object.saveInBackground();
               showTags(object);
           }
       });
   }
}