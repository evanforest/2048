package com.my2048.linzg.lin2048;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by lin on 2017/8/9.
 */

/**
 * 游戏逻辑类，负责处理游戏的逻辑和数据
 *  Created by asus on 2017/8/4.
 */
public class GameLogic {
    private static final String TAG = "GameLogic";
    public static final int SLIDE_UP = 1;
    public static final int SLIDE_DOWN = 2;
    public static final int SLIDE_LEFT = 3;
    public static final int SLIDE_RIGHT = 4;

    public static final int GAME_STATE_RUN = 1;
    public static final int GAME_STATE_WIN = 2;
    public static final int GAME_STATE_LOSE = 3;
    private static final String scoreString ="score";
    private static final String bestString ="best";
    private static final String levelString ="level";
    private static final String lastLevelString ="lastLevel";
    private static final String lastScoreString ="lastScore";
    private static final String lastGameStateString ="lastGameState";
    private static final String gameStateString ="gameState";
    private static final String undoCountString ="undoCount";
    private static final String isCanUndoString ="isCanUndo";
    private static final String isWinString ="isWin";

    private final Game2048View view;
    private int score;
    private int best;
    private int level;
    private int nowLevel;
    private int lastLevel;
    private int lastScore;
    public int gameState;
    private int lastGameState;
    private int undoCount;
    public boolean isCanUndo;
    private boolean isWin;
    private int emptyCount;
    public boolean infoIsShow;


    private final AnimationManager manager;
    private Card[][] grid;
    private Card[][] lastGrid;

    public GameLogic(Game2048View view) {
        this.view = view;
        this.manager = AnimationManager.share(view);
        grid = new Card[view.gridNum][view.gridNum];
        lastGrid = new Card[view.gridNum][view.gridNum];
        infoIsShow = false;
        load(view.getContext().getSharedPreferences("gameData", Context.MODE_PRIVATE));
        if (this.best == 0){
            putCard(getRandomCard());
            putCard(getRandomCard());
            gameState = GAME_STATE_RUN;
            view.invalidate();
        }
    }

    public void newGame() {
        if (infoIsShow)
            return;
        clearGrids();
        manager.clearAll();
        manager.animationCount = 0;
        score = 0;
        nowLevel = 0;
        lastLevel = 0;
        isCanUndo = false;
        isWin = false;
        gameState = GAME_STATE_RUN;
        lastGameState = gameState;
        putCard(getRandomCard());
        putCard(getRandomCard());
        view.invalidate();
    }

    private void putCard(Card card) {
        if(card == null)
            return;
        grid[card.getx()][card.gety()] = card;
        emptyCount -- ;
        manager.addBloomAnimation(new Position(card.getx(),card.gety()));
        manager.recordTime();
    }

    private Card getRandomCard() {
        List<Position> emptyPositions = getEmptyPositions();
        emptyCount = emptyPositions.size();
        if (emptyPositions.size() == 0){
            return null;
        }
        Position p = emptyPositions.get(new Random().nextInt(emptyPositions.size()));
        return new Card(p, getRandomValue());
    }

    private int getRandomValue() {
        return new Random().nextInt(10) < 9 ? 2 : 4;
    }

    private List getEmptyPositions() {
        List<Position> emptyPositions = new ArrayList();
        for (int xx = 0; xx < view.gridNum; xx++) {
            for (int yy = 0; yy < view.gridNum; yy++) {
                if (grid[xx][yy] == null)
                    emptyPositions.add(new Position(xx, yy));
            }
        }
        Log.d(TAG, "getEmptyPositions: " + emptyPositions.size());
        return emptyPositions;
    }

    private void clearGrids() {
        for (int i=0;i<view.gridNum;i++){
            for (int j=0;j<view.gridNum;j++){
                grid[i][j]=null;
                lastGrid[i][j]=null;
            }
        }
    }

    /**
     * 移动函数
     * 计算出移动后的grid
     * 添加所需要的动画
     * @param direction 移动的方向
     */
    public void move(int direction) {
        if (gameState != GAME_STATE_RUN){
            Log.d(TAG, "move: 不为run状态，返回");
            return;
        }

        manager.clearAll();
        manager.animationCount = 0;
        manager.lastRecordTime = System.currentTimeMillis();
        //用于记录是否产生了移动
        Log.d(TAG, "move: " + direction);
        boolean isMove = false;
        //保存上一次的数组
        Card[][] tempGrid = copyAarryFrom(grid);
        int tempScore = score;
        int tempLevel = nowLevel;
        //逐行或逐列进行操作，用于存放提取的某一行（或列）
        Position[] bufferPos;
        //要移动到的目标位置
        Position targetPosition;
        //进行4行（列）的操作
        for (int i = 0; i < view.gridNum; i++) {
      //      Log.d(TAG, "move: 第" +i+"行(列)操作");
            //依次提取出每一行（列），从上到下(从左到右)提取
                //提取出来的数组会按照与手指滑动相反方向的顺序排列
            bufferPos = getOrder(i, direction);
            //对一行（列）中的每一个Card进行操作
            int[] isFusedFlag = new int[view.gridNum];
            for (int flag = 0;flag < view.gridNum; flag++)
                isFusedFlag[flag] = 0;
            for (int j = 1; j < view.gridNum; j++) {
  //              Log.d(TAG, "move: 第" + j + "个操作");
                //正在操作的Card
                Position nowPosition = bufferPos[j];
                Card nowCard = grid[nowPosition.getx()][nowPosition.gety()];
                if (nowCard != null) {
     //               Log.d(TAG, "move: " + bufferPos[j].getx()+","+bufferPos[j].gety()+"不为空");
                    //通过getTagetPosition()计算出将要移动到的位置
                    int target = getTagetPosition(bufferPos,isFusedFlag, j);
     //               Log.d(TAG, "move: 目标位置："+ target);
                    //目标位置就是本身，即没有移动，直接退出对下一个进行操作

                    //发生了移动
                    if (target != j){
                        //得到目标位置
                        targetPosition = bufferPos[target];
                        //目标位置的Card
                        Card targetCard = grid[targetPosition.getx()][targetPosition.gety()];
                        //添加移动动画，起始位置为操作的Card
                        manager.addMoveAnimation(targetPosition,nowPosition);
                        //如果目标位置的Card不为null，即该卡片的value与操作的Card的value相同
                            // 即发生了卡片融合
                        if (targetCard != null) {
                            //添加融合动画，位置在目标位置
                            manager.addFuseAnimation(targetPosition);
                            //将卡片的value融合，即*2
                            int newScore = nowCard.getValue() * 2;
                            Log.d(TAG, "move: newScore:" + newScore);
                            nowCard.setValue(newScore);
                            if (newScore > nowLevel)
                                nowLevel = newScore;
                            if (nowLevel > level)
                                level = nowLevel;
                            score += newScore;
                        }
                        //移动卡片到目标位置
                        nowCard.moveTo(targetPosition, grid);
                        //发生了移动
                        isMove = true;
                    }
                //如果正在操作的Card为空
                }
            }
        }
        //如果发生了移动
        if (isMove){
            Log.d(TAG, "move: 发生了移动");
            if (score > best)
                best = score;
            lastGrid = tempGrid;
            lastScore = tempScore;
            lastLevel = tempLevel;
            isCanUndo = true;
            lastGameState = gameState;
            putCard(getRandomCard());
            if (emptyCount == 0) {
                if (isLose())
                    gameState = GAME_STATE_LOSE;
            }else if (!isWin && nowLevel >= 2048) {
                gameState = GAME_STATE_WIN;
                isWin = true;
            }else
                gameState = GAME_STATE_RUN;
            view.invalidate();
            Log.d(TAG, "move: gameState:" + gameState);
        }
    }
    public void continueRun(){
        gameState = GAME_STATE_RUN;
        view.invalidate();
    }
    public void showInfo(){
        if (view.info == null){
            view.info = new Info(view);
        }
        if (!infoIsShow){
            infoIsShow = true;
            view.info.isBigger = true;

        } else {
            view.info.isBigger = false;
        }
        view.info.lastRcordTime = System.currentTimeMillis();
        view.info.isDone = false;
        view.invalidate();
    }
    public void undo(){
        if (infoIsShow)
            return;
        if (isCanUndo) {
            if (gameState == GAME_STATE_WIN)
                isWin = false;
            manager.clearAll();
            manager.animationCount = 0;
            score = lastScore;
            nowLevel = lastLevel;
            lastScore = 0;
            gameState = lastGameState;
            grid = lastGrid;
            lastGrid = new Card[view.gridNum][view.gridNum];
            isCanUndo = false;
            view.invalidate();
        }
    }

    private boolean isLose(){
        for (int x=0;x<view.gridNum-1;x++){
            for (int y=0;y<view.gridNum-1;y++){
                if (grid[x][y] == null){
                    return false;
                }else{
                    if (grid[x][y].getValue() == grid[x][y+1].getValue()
                            || grid[x][y].getValue() == grid[x+1][y].getValue()){
                        return false;
                    }
                }
            }
            if (grid[x][view.gridNum-1] == null || grid[view.gridNum-1][x] == null)
                return false;
            if (grid[x][view.gridNum-1].getValue() == grid[x+1][view.gridNum-1].getValue())
                return false;
            if (grid[view.gridNum-1][x].getValue() == grid[view.gridNum-1][x+1].getValue())
                return false;
        }
        return true;
    }
    private int getTagetPosition(Position[] buffer,int[] isFused, int j) {
        int pos = j;
        for (int count = j - 1; count >= 0; count--) {
            if (grid[buffer[count].getx()][buffer[count].gety()] == null)
                pos = count;
            else if (grid[buffer[count].getx()][buffer[count].gety()].getValue()
                    == grid[buffer[j].getx()][buffer[j].gety()].getValue()) {
                if (isFused[count] != 1) {
                    pos = count;
                    isFused[count] = 1;
                }
                break;
            }else
                break;
        }
        return pos;
    }

    private Position[] getOrder(int index, int direction) {
        Position[] temp = new Position[view.gridNum];
        if (direction == SLIDE_RIGHT) {
            for (int i = 0; i < view.gridNum; i++) {
                temp[i] = new Position(index, view.gridNum - 1 - i);
            }
        } else if (direction == SLIDE_LEFT) {
            for (int i = 0; i < view.gridNum; i++) {
                temp[i] = new Position(index, i);
            }
        } else if (direction == SLIDE_UP) {
            for (int i = 0; i < view.gridNum; i++) {
                temp[i] = new Position(i, index);
            }
        } else if (direction == SLIDE_DOWN) {
            for (int i = 0; i < view.gridNum; i++) {
                temp[i] = new Position(view.gridNum - 1 - i, index);
            }
        }
        return temp;

    }
    private Card[][] copyAarryFrom(Card[][] src){
        Card[][] des = new Card[view.gridNum][view.gridNum];
        for (int x=0;x<view.gridNum;x++){
            for (int y=0;y<view.gridNum;y++){
                if (src[x][y] == null)
                    des[x][y] = null;
                else
                    des[x][y] = new Card(src[x][y]);
            }
        }
        return des;

    }
    public int getScore(){
        return this.score;
    }
    public int getBest(){
        return this.best;
    }
    public int getLevel(){
        return this.level;
    }
    public Card getLastCardAt(int x,int y){
        return lastGrid[x][y];
    }
    public Card getNowCardAt(int x,int y){
        return grid[x][y];
    }
    public void load(SharedPreferences preferences){
        for (int i=0;i<view.gridNum;i++){
            for (int j=0;j<view.gridNum;j++){
                int value = preferences.getInt("grid"+i+","+j,0);
                if (value != 0)
                    grid[i][j] = new Card(new Position(i,j),value);
                else
                    grid[i][j] = null;
            }
        }
        for (int i=0;i<view.gridNum;i++){
            for (int j=0;j<view.gridNum;j++){
                int value = preferences.getInt("lastGrid"+i+","+j,0);
                if (value != 0)
                    lastGrid[i][j] = new Card(new Position(i,j),value);
                else
                    lastGrid[i][j] = null;
            }
        }
        this.score = preferences.getInt(scoreString,0);
        this.best = preferences.getInt(bestString,0);
        this.level = preferences.getInt(levelString,0);
        this.lastScore = preferences.getInt(lastScoreString,0);
        this.undoCount = preferences.getInt(undoCountString,0);
        this.isCanUndo =  preferences.getBoolean(isCanUndoString,false);
        this.gameState =  preferences.getInt(gameStateString,GAME_STATE_RUN);
        this.lastGameState =  preferences.getInt(lastGameStateString,GAME_STATE_RUN);
        this.lastLevel =  preferences.getInt(lastLevelString,0);
        this.isWin =  preferences.getBoolean(isWinString,false);
    }
    public void save(SharedPreferences preferences){
        SharedPreferences.Editor editor = preferences.edit();
        for (int i=0;i<view.gridNum;i++){
            for (int j=0;j<view.gridNum;j++){
                if (grid[i][j] !=null)
                    editor.putInt("grid"+i+","+j,grid[i][j].getValue());
                else
                    editor.putInt("grid"+i+","+j,0);
            }
        }
        for (int i=0;i<view.gridNum;i++){
            for (int j=0;j<view.gridNum;j++){
                if (lastGrid[i][j] !=null)
                    editor.putInt("lastGrid"+i+","+j,lastGrid[i][j].getValue());
                else
                    editor.putInt("lastGrid"+i+","+j,0);
            }
        }
        editor.putBoolean(isCanUndoString,isCanUndo);
        editor.putInt(scoreString,score);
        editor.putInt(bestString,best);
        editor.putInt(levelString,level);
        editor.putInt(lastScoreString,lastScore);
        editor.putInt(undoCountString,undoCount);
        editor.putInt(gameStateString,gameState);
        editor.putInt(lastGameStateString,lastGameState);
        editor.putInt(lastLevelString,lastLevel);
        editor.putBoolean(isWinString,isWin);
        editor.apply();
    }

}
