package com.my2048.linzg.lin2048;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by lin on 2017/8/29.
 */

public class Info {
    private static final String TAG = "Info";
    private int smallX,smallY;
    private int smallEndX,smallEndY;
    private int bigX,bigY;
    private int bigEndX,bigEndY;
    private int time = 150 ;
    private long elapseTime;
    public long lastRcordTime;
    public boolean isDone;
    public boolean isBigger;
    private Drawable drawable = null;
    private Drawable content = null;
    private Game2048View view;

    public Info(Game2048View view) {
        this.view = view;
        bigX = view.cardBaseX;
        bigY = view.cardBaseY;
        bigEndX = bigX + view.cardBaseSize;
        bigEndY = bigY + view.cardBaseSize;
        smallX = view.aboutButX;
        smallY = view.bottomY;
        smallEndX = smallX + view.bottomIconW;
        smallEndY = smallY + view.bottomIconH;
        drawable = ContextCompat.getDrawable(view.getContext(),R.drawable.cardbase_color);
    }
    public void resetting(Game2048View view){
        this.bigX = view.cardBaseX;
        bigY = view.cardBaseY;
        bigEndX = bigX + view.cardBaseSize;
        bigEndY = bigY + view.cardBaseSize;
        smallX = view.aboutButX;
        smallY = view.bottomY;
        smallEndX = smallX + view.bottomIconW;
        smallEndY = smallY + view.bottomIconH;
        if (content != null) {
            content.setBounds(bigX, bigY, bigEndX, bigEndY);
        }
    }
    private float percent(){
        long newElapseTime = System.currentTimeMillis() - lastRcordTime;
        lastRcordTime = System.currentTimeMillis();
        if (isBigger){
            if (elapseTime + newElapseTime >= time){
                isDone = true;
                elapseTime = time;
                return 1.0f;
            }else
                elapseTime += newElapseTime;
        }else{
            if (elapseTime + newElapseTime <= 0){
                isDone = true;
                elapseTime = 0;
            }else
                elapseTime -= newElapseTime;
        }
        return elapseTime * 1.0f / time;
    }

    public void draw(Canvas canvas){
        if (isDone) {
            Log.d(TAG, "draw: isDone");
            if (isBigger) {
                Log.d(TAG, "draw: isBigger");
                drawable.setBounds(bigX, bigY, bigEndX, bigEndY);
                drawable.draw(canvas);
                if (content == null) {
                    content = ContextCompat.getDrawable(view.getContext(), R.drawable.info_content);
                    content.setBounds(bigX, bigY, bigEndX, bigEndY);
                }
                if (content != null)
                content.draw(canvas);
            }else{
                Log.d(TAG, "draw: isSmaller");
                view.logic.infoIsShow = false;
                view.invalidate();
            }
        }else{
            float percent = percent();
            Log.d(TAG, "draw: percent:" + percent);
            drawable.setBounds((int)(smallX - percent *(smallX - bigX)),(int)(smallY - percent *(smallY - bigY)),
                    (int)(smallEndX - percent *(smallEndX - bigEndX)),(int)(smallEndY - percent *(smallEndY - bigEndY)));
            drawable.draw(canvas);
            view.invalidate();
        }

    }
}
