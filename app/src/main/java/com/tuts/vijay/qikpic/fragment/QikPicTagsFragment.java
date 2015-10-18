package com.tuts.vijay.qikpic.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.Utils.DisplayUtils;
import com.tuts.vijay.qikpic.activity.DetailActivity;
import com.tuts.vijay.qikpic.listener.TagListener;
import com.tuts.vijay.qikpic.view.FlowLayout;

import java.util.List;

/**
 * Created by vijay on 10/14/15.
 */
public class QikPicTagsFragment extends DialogFragment {

    private List<String> tags;
    private TagListener tagListener;
    private FlowLayout taggingPanel;
    private ImageView addImgTag;

    public QikPicTagsFragment() {
    }

    public QikPicTagsFragment(TagListener tagListener) {
        this.tagListener = tagListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tag_dialog_view, container, false);
        taggingPanel = (FlowLayout) rootView.findViewById(R.id.taggingPanel);
        addImgTag = (ImageView) rootView.findViewById(R.id.addTagIcon);
        addImgTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInputFromUser();
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.TagDialogWindowAnimation;
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.verticalMargin = 100;
        getDialog().getWindow().setAttributes(params);
        loadTags();
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    private void loadTags() {
        if (tags == null) {
            return;
        }
        for (String tag : tags) {
            addTagView(tag);
        }
    }

    private void getInputFromUser() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle("Tag");

        final EditText input = new EditText(getActivity());
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
        addTagView(tag);
        ((DetailActivity)getActivity()).addTagToList(tag);
    }

    private void addTagView(String tag) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                (int) DisplayUtils.fromDpToPx(getActivity(), 48));

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        TextView tagView = (TextView) inflater.inflate(R.layout.tag_view, null, false);
        tagView.setText(tag);
        taggingPanel.addView(tagView, 0, lp);
    }
}
