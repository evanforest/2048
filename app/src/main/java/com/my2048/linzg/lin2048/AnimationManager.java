package com.my2048.linzg.lin2048;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2017/8/10.
 */

public class AnimationManager {
    private static final int TIME_MOVE_DURATION = 100;
    private static final int TIME_FUSE_DURATION = 100;
    private static final int TIME_BLOOM_DURATION = 100;
    private static final int TIME_BLOOM_DELAY = TIME_MOVE_DURATION;
    private static final int TIME_FUSE_DELAY = TIME_MOVE_DURATION;
    private final ArrayList<Animation>[][] animations;
    public int animationCount;
    public long lastRecordTime;
    private final Game2048View mView;
    private static AnimationManager mManager;
    public static AnimationManager share(Game2048View view){
        if (mManager == null){
            synchronized (AnimationManager.class){
                if (mManager == null)
                    mManager = new AnimationManager(view);
            }
        }
        return mManager;
    }

    private AnimationManager(Game2048View view) {
        this.mView = view;
        animations = new ArrayList[mView.gridNum][mView.gridNum];
        for (int i = 0; i < mView.gridNum; i++) {
            for (int j = 0; j < mView.gridNum; j++) {
                animations[i][j] = new ArrayList<>();
            }
        }
    }

    private void addAnimation(Animation anim) {
        animations[anim.getAnimationX()][anim.getAnimationY()].add(anim);
        animationCount += 1;
    }

    public void addMoveAnimation(Position coordinate, Position startPos) {
        Animation anim = new Animation(
                Animation.TYPE_MOVE, TIME_MOVE_DURATION, 0, new Position(coordinate),
                new Position(startPos));
        addAnimation(anim);
    }

    public void addFuseAnimation(Position coordinate) {
        Animation anim = new Animation(
                Animation.TYPE_FUSE, TIME_FUSE_DURATION, TIME_FUSE_DELAY,
                new Position(coordinate), null
        );
        addAnimation(anim);
    }

    public void addBloomAnimation(Position coordinate) {
        Animation anim = new Animation(
                Animation.TYPE_BLOOM, TIME_BLOOM_DURATION, TIME_BLOOM_DELAY,
                new Position(coordinate), null
        );
        addAnimation(anim);
    }

    public void removeAnimation(Animation anim) {
        animations[anim.getAnimationX()][anim.getAnimationY()].remove(anim);
        animationCount -= 1;
    }

    public boolean animationIsActive() {
        return animationCount>0;
    }


    public List<Animation> animations(int x,int y){
        return animations[x][y];
    }
    public void clearAll(){
        for (int i = 0; i < mView.gridNum; i++) {
            for (int j = 0; j < mView.gridNum; j++) {
                animations[i][j].clear();
            }
        }
    }
    public void recordTime(){
        this.lastRecordTime = System.currentTimeMillis();
    }
    public int getAnimationCount(){
        return animationCount;
    }

}
