package com.tuts.vijay.qikpic.databinding;

import android.net.Uri;

/**
 * Created by vijay on 6/5/16.
 */
public class QikPicDetail {
    public String ts;
    public String uriStr;
    public QikPicDetail(String timestamp, Uri uri) {
        this.ts = timestamp;
        this.uriStr = uri.toString();
    }
}
