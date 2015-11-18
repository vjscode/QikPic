package com.tuts.vijay.qikpic.event;

/**
 * Created by vijay on 11/18/15.
 */
public class ScrollEvent {
    public int firstVisiblePos;
    public int numberOfVisItems;

    public ScrollEvent(int firstVisiblePos, int numberOfVisItems) {
        this.firstVisiblePos = firstVisiblePos;
        this.numberOfVisItems = numberOfVisItems;
    }
}
