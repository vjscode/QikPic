package com.tuts.vijay.qikpic.views;

import android.content.Context;
import android.util.AttributeSet;

import com.parse.ParseImageView;

/**
 * Created by vijay on 6/5/15.
 */
public class AspectRatioImageView extends ParseImageView {

    private static final float aspectRatio = 1.0f;
    private String objectId;

    public AspectRatioImageView(Context context) {
        super(context);
    }

    public AspectRatioImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AspectRatioImageView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = getMeasuredWidth();
        int resultingHeight = Math.round(measuredWidth * aspectRatio);
        setMeasuredDimension(measuredWidth, resultingHeight);
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
