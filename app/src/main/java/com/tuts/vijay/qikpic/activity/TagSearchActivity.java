package com.tuts.vijay.qikpic.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.fragment.QikPicGridFragment;

public class TagSearchActivity extends AppCompatActivity {

    private QikPicGridFragment gridFragment;
    private String searchTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_search);
        handleIntent(getIntent());
    }

    private void initFragment(String q) {
        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        gridFragment = new QikPicGridFragment();
        Bundle b = new Bundle();
        b.putBoolean("isSearch", true);
        b.putString("q", q);
        getSupportActionBar().setTitle(q);
        gridFragment.setArguments(b);
        ft.add(R.id.fragmentWrapper, gridFragment);
        ft.commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search
            initFragment(query);
        }
    }
}