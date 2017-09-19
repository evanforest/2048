package com.my2048.linzg.lin2048;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by asus on 2017/8/10.
 */

public class GameListener implements View.OnTouchListener {
    private static final String TAG = "GameListener";
    private final GameLogic logic;
    private float startX, startY;
    private boolean isDeal = false;
    private final Game2048View view;
    private int pushedButton = 0;
    private boolean isButton = false;

    public GameListener(Game2048View view,GameLogic gameLogic) {
        this.logic = gameLogic;
        this.view = view;
    }

    // TODO: 2017/9/5 触摸监听事件有待优化！！！
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouch: action_down");
                startX = event.getX();
                startY = event.getY();
                int temp = view.getPushButton(startX,startY);
                if (temp != 0){
                    pushedButton = temp;
                    isButton = true;
                    isDeal = true;
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (isButton && view.getPushButton(event.getX(),event.getY()) != pushedButton){
                    pushedButton = 0;
                    isButton = false;
                }
                if (!isDeal){
                    Log.d(TAG, "onTouch: 允许判断");
                    float moveX = event.getX() - startX;
                    float moveY = event.getY() - startY;
                    Log.d(TAG, "onTouch: "+moveX+","+moveY);
                    if (Math.sqrt(Math.pow(Math.abs(moveX), 2) + Math.pow(Math.abs(moveY), 2)) >= 200) {
                        if (moveX > 0 && Math.abs(moveY) < (Math.abs(moveX) * 0.7f)) {
                            Log.d(TAG, "onTouch: 右");
                            logic.move(GameLogic.SLIDE_RIGHT);
                            isDeal = true;
                            return false;
                        } else if (moveX < 0 && Math.abs(moveY) < (Math.abs(moveX) * 0.7f)) {
                            Log.d(TAG, "onTouch: 左");
                            logic.move(GameLogic.SLIDE_LEFT);
                            isDeal = true;
                            return false;
                        } else if (moveY > 0 && Math.abs(moveX) < (Math.abs(moveY) * 0.7f)) {
                            Log.d(TAG, "onTouch: 下");
                            logic.move(GameLogic.SLIDE_DOWN);
                            isDeal = true;
                            return false;
                        } else if (moveY < 0 && Math.abs(moveX) < (Math.abs(moveY) * 0.7f)) {
                            Log.d(TAG, "onTouch: 上");
                            logic.move(GameLogic.SLIDE_UP);
                            isDeal = true;
                            return false;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouch: action_up");
                if (isButton){
                    switch (pushedButton){
                        //undo
                        case 1: logic.undo();break;
                        //newgame
                        case 2: logic.newGame();break;
                        //about
                        case 3: logic.showInfo();
                                break;
                        case 4: if (Math.abs(event.getX() - startX) < view.baseGapWidth &&
                                Math.abs(event.getY() - startY) < view.baseGapWidth)
                                    logic.continueRun();
                                break;
                    }
                }
                isButton = false;
                isDeal = false;
                return true;

        }
        return false;
    }
}
