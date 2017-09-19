package com.my2048.linzg.lin2048;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

/**
 * 负责游戏界面的绘制
 * Created by lin on 2017/8/4.
 */

public class Game2048View extends View {
    private static final String TAG = "Game2048View";
    private int viewWidth = 0;
    private int viewHeight = 0;

    private int cardSize;       //每张卡片的宽度
    public int cardBaseSize;   //卡片底盘的宽度
    public int cardBaseX;       //卡片底盘的起始坐标X
    public int cardBaseY;       //卡片底盘的起始坐标Y
    public int baseGapWidth;
    public int gridNum = 4;     //卡片格子的行列数
    private int scoreBodyY;
    private int bestBodyY;
    private int levelBodyY;
    private int textHeight;
    private int textTitleX;
    private int scorePadding;
    private int topHeight;
    private int textTitleWidth;
    private int scoreGap;
    private float halfScoreH;
    private int logoWidth;
    private int logoPadding;
    public int bottomY;
    public int bottomIconH;
    public int bottomIconW;
    private int newGameButX;
    private int undoButX;
    public int aboutButX;
    private float winLosePaintSize;

    private int maxNum = 17;
    private Drawable buttonBase = ContextCompat.getDrawable(this.getContext(), R.drawable.cardbase_color);
    private Drawable undoUnableLogo = ContextCompat.getDrawable(this.getContext(), R.drawable.undo_unable_150px);
    private Drawable orangeBase = ContextCompat.getDrawable(this.getContext(), R.drawable.button_base_orange);
    private String scoreText = getResources().getString(R.string.scoreText);
    private String bestText = getResources().getString(R.string.bestText);
    private String levelText = getResources().getString(R.string.levelText);
    private Paint paint = new Paint();
    private Paint scorePaint = new Paint();
    private Paint winLosePaint = new Paint();
    private Bitmap backgroundBitmap = null;
    private Drawable[] cards = new Drawable[maxNum];
    private AnimationManager manager;    //动画管理器
    public GameLogic logic;    //游戏逻辑类
    public Info info;
    public Game2048View(Context context) {
        super(context);
        manager = AnimationManager.share(this);
        logic = new GameLogic(this);
        setOnTouchListener(new GameListener(this,logic));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long st = System.currentTimeMillis();
        Log.d(TAG, "onDraw: ");
        drawView(canvas);
        st = System.currentTimeMillis() - st;
        Log.d(TAG, "onDraw: st:" + st);
//        logCards();
//        logAnimations();
//        Log.d(TAG, "onDraw: count:" + manager.getAnimationCount());

    }
    private void drawView(Canvas canvas){
        Log.d(TAG, "drawView: 1");
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        Log.d(TAG, "drawView: 2");
        drawScore(canvas, logic.getScore(), scoreBodyY);
        drawScore(canvas, logic.getBest(), bestBodyY);
        drawScore(canvas, logic.getLevel(), levelBodyY);
        Log.d(TAG, "drawView: 3");
        if (!logic.isCanUndo)
            drawUndoLogo(canvas,undoUnableLogo);
        if (manager.animationIsActive()) {
            Log.d(TAG, "drawView: 4");
            drawAnimation(canvas);
            Log.d(TAG, "drawView: aaaaaab:");
        }
        else {
            drawCards(canvas);
            if (!logic.infoIsShow) {
                if (logic.gameState == GameLogic.GAME_STATE_WIN) {
                    drawWin(canvas);
                } else if (logic.gameState == GameLogic.GAME_STATE_LOSE) {
                    drawLose(canvas);
                    drawButtonBase(canvas, newGameButX, orangeBase);
                    drawNewGameLogo(canvas);
                    if (logic.isCanUndo) {
                        drawButtonBase(canvas, undoButX, orangeBase);
                        drawUndoLogo(canvas, ContextCompat.getDrawable(this.getContext(), R.drawable.undo_150px));
                    }
                }
            }
        }
        if (logic.infoIsShow){
            Log.d(TAG, "drawView: info.draw");
            info.draw(canvas);
            drawButtonBase(canvas, aboutButX, orangeBase);
            drawCrossLogo(canvas);
        }
    }
    private Drawable cross = null;
    private void drawCrossLogo(Canvas canvas){
        if (null == cross)
            cross = ContextCompat.getDrawable(getContext(),R.drawable.return_150px);
        int dsize = (int)(bottomIconH * 0.2f);
        cross.setBounds(aboutButX + dsize,bottomY + dsize,aboutButX + bottomIconW - dsize,
                bottomY + bottomIconH - dsize);
        cross.draw(canvas);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.viewHeight = h;
        this.viewWidth = w;
        Log.d(TAG, "onSizeChanged: ");
        initParams();
        initCards();
        createBackground();
        if (info != null)
            info.resetting(this);
//        if (!readBackgroundBitmap(isPortrait)){
//            createBackground();
//            saveBackgroundBitmap();
//        }else{
//            Log.d(TAG, "onSizeChanged: 图片已读取，不用绘制了");
//        }
    }

    private void logCards() {
        Log.d(TAG, "logCards: " + gCV(0, 0) + "," + gCV(0, 1) + "," + gCV(0, 2) + "," + gCV(0, 3));
        Log.d(TAG, "logCards: " + gCV(1, 0) + "," + gCV(1, 1) + "," + gCV(1, 2) + "," + gCV(1, 3));
        Log.d(TAG, "logCards: " + gCV(2, 0) + "," + gCV(2, 1) + "," + gCV(2, 2) + "," + gCV(2, 3));
        Log.d(TAG, "logCards: " + gCV(3, 0) + "," + gCV(3, 1) + "," + gCV(3, 2) + "," + gCV(3, 3));
    }

    private void logAnimations() {
        Log.d(TAG, "logAnimations: " + gAS(0, 0) + "," + gAS(0, 1) + "," + gAS(0, 2) + "," + gAS(0, 3));
        Log.d(TAG, "logAnimations: " + gAS(1, 0) + "," + gAS(1, 1) + "," + gAS(1, 2) + "," + gAS(1, 3));
        Log.d(TAG, "logAnimations: " + gAS(2, 0) + "," + gAS(2, 1) + "," + gAS(2, 2) + "," + gAS(2, 3));
        Log.d(TAG, "logAnimations: " + gAS(3, 0) + "," + gAS(3, 1) + "," + gAS(3, 2) + "," + gAS(3, 3));
    }

    private int gCV(int x, int y) {
        if (logic.getNowCardAt(x, y) != null)
            return logic.getNowCardAt(x, y).getValue();
        return 0;
    }

    private int gAS(int x, int y) {
        return manager.animations(x, y).size();
    }

    private void initParams() {
        cardSize = (int) Math.min(viewHeight / Math.ceil((gridNum + 1) * 7.0 / 5), viewWidth / (gridNum + 1));
        baseGapWidth = cardSize / (gridNum + 3);
        cardBaseSize = cardSize * gridNum + baseGapWidth * (gridNum + 1);
        cardBaseY = (int) Math.floor(viewHeight / 7.0 * 2);
        cardBaseX = viewWidth / 2 - cardBaseSize / 2;
        textTitleX = viewWidth / 2 + cardBaseSize / 2 - baseGapWidth - cardBaseSize / 20 * 3;
        topHeight = cardBaseY - 2 * baseGapWidth;
        textHeight = (topHeight - 4 * baseGapWidth) / 3;
        scoreBodyY = 2 * baseGapWidth;
        bestBodyY = scoreBodyY + textHeight + baseGapWidth;
        levelBodyY = bestBodyY + textHeight + baseGapWidth;
        textTitleWidth = cardBaseSize / 20 * 3;
        scorePadding = baseGapWidth / 2;
        scoreGap = baseGapWidth / 3;
        logoWidth = topHeight - 2 * baseGapWidth;
        logoPadding = baseGapWidth / 2;
        buttonBase.setAlpha(200);
        //底部图标参数
        bottomY = cardBaseY + cardBaseSize + baseGapWidth * 3 / 2;
        bottomIconH = cardBaseSize * 3 / 25 ;
        bottomIconW = bottomIconH;
        aboutButX = cardBaseX + cardBaseSize - bottomIconH - baseGapWidth;
        undoButX = cardBaseX + baseGapWidth;
        newGameButX = undoButX + bottomIconH + baseGapWidth * 3 / 2;
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "ClearSans-Bold.ttf");
        //分数类型画笔初始化
        paint.setTypeface(font);
        paint.setTextSize(textTitleWidth);
        float textSize = textTitleWidth * (textTitleWidth - 2 * baseGapWidth / 4) / (paint.measureText(scoreText));
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.textDarkColor));
        paint.setTextAlign(Paint.Align.CENTER);
        //分数画笔初始化
        scorePaint.setTypeface(font);
        scorePaint.setAntiAlias(true);
        scorePaint.setColor(Color.WHITE);
        scorePaint.setFakeBoldText(true);
        scorePaint.setTextSize(textSize * 2);
        scorePaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fm1 = scorePaint.getFontMetrics();
        halfScoreH = -fm1.descent + (fm1.descent - fm1.ascent) / 2;
        //
        winLosePaint.setTypeface(font);
        winLosePaint.setAntiAlias(true);
        winLosePaint.setTextAlign(Paint.Align.CENTER);
        winLosePaint.setTextSize(cardBaseSize);
        winLosePaintSize = cardBaseSize * cardBaseSize / (winLosePaint.measureText("啊啊啊啊啊啊啊"));
        winLosePaint.setTextSize(winLosePaintSize);
        winLosePaint.setFakeBoldText(true);
        winLosePaint.setColor(ContextCompat.getColor(getContext(),R.color.textDarkColor));        winLosePaint.setAlpha(255);
    }

    private void initCards() {
        int[] drawableId = new int[maxNum];
        drawableId[0] = R.drawable.card_view_color65537;
        drawableId[1] = R.drawable.card_view_color2;
        drawableId[2] = R.drawable.card_view_color4;
        drawableId[3] = R.drawable.card_view_color8;
        drawableId[4] = R.drawable.card_view_color16;
        drawableId[5] = R.drawable.card_view_color32;
        drawableId[6] = R.drawable.card_view_color64;
        drawableId[7] = R.drawable.card_view_color128;
        drawableId[8] = R.drawable.card_view_color256;
        drawableId[9] = R.drawable.card_view_color512;
        drawableId[10] = R.drawable.card_view_color1024;
        drawableId[11] = R.drawable.card_view_color2048;
        drawableId[12] = R.drawable.card_view_color4096;
        drawableId[13] = R.drawable.card_view_color8192;
        drawableId[14] = R.drawable.card_view_color16384;
        drawableId[15] = R.drawable.card_view_color32768;
        drawableId[16] = R.drawable.card_view_color65536;
        //cardTextPaint初始化
        Paint cardTextPaint = new Paint();
        cardTextPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "ClearSans-Bold.ttf"));
        cardTextPaint.setAntiAlias(true);
        cardTextPaint.setTextAlign(Paint.Align.CENTER);
        cardTextPaint.setFakeBoldText(true);
        cardTextPaint.setTextSize(cardSize);
        float cardTextSize = cardSize * cardSize / cardTextPaint.measureText("0000");
        Canvas c;
        Drawable d;
        for (int i = 0; i < maxNum; i++) {
            int value = (int) Math.pow(2, i);
            Bitmap b = Bitmap.createBitmap(cardSize, cardSize, Bitmap.Config.ARGB_8888);
            c = new Canvas(b);
            d = ContextCompat.getDrawable(getContext(), drawableId[i]);
            d.setBounds(0, 0, cardSize, cardSize);
            d.draw(c);
            if (i != 0) {
                cardTextPaint.setTextSize(cardTextSize);
                float temp = cardTextSize * cardSize * 0.9f /
                        (Math.max(cardSize * 0.9f, cardTextPaint.measureText(value+"")));
                cardTextPaint.setTextSize(temp);
                if (i <= 2)
                    cardTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.textDarkColor));
                else
                    cardTextPaint.setColor(Color.WHITE);
                drawCardText(c, "" + value,cardTextPaint);
            }
            cards[i] = new BitmapDrawable(getResources(),b);
        }

    }

    private void drawCards(Canvas canvas) {
        for (int x = 0; x < gridNum; x++) {
            for (int y = 0; y < gridNum; y++) {
                if (logic.getNowCardAt(x, y) != null) {
                    Card card = logic.getNowCardAt(x, y);
                    drawCard(canvas,card);
                }
            }
        }
    }

    private void drawCard(Canvas canvas, Card c) {
        if (c == null)
            return;
        cards[getIndex(c.getValue())].setBounds(getCardXByY(c.gety()),getCardYByX(c.getx()),
                getCardXByY(c.gety()) + cardSize,getCardYByX(c.getx())+cardSize);
        cards[getIndex(c.getValue())].draw(canvas);
    }

    private void drawCard(Canvas canvas, Card c, float[] shifting, float scale) {
        if (c == null)
            return;
        int dsize = (int)(cardSize * (1 - scale) / 2);
        int cardX = (int)(getCardXByY(c.gety()) + shifting[0] + dsize);
        int cardY = (int)(getCardYByX(c.getx()) + shifting[1] + dsize);
        cards[getIndex(c.getValue())].setBounds(cardX,cardY, (int)(cardX + cardSize * scale),(int) (cardY + cardSize * scale));
        cards[getIndex(c.getValue())].draw(canvas);
    }

    private void drawAnimation(Canvas canvas) {
        List<Animation> doneAnimations = new ArrayList<>();
        Log.d(TAG, "drawView: 5");
        long elapseTime = System.currentTimeMillis() - manager.lastRecordTime; //记录当前时间
        Log.d(TAG, "drawAnimation: elapseTime:" + elapseTime);
        for (int x = 0; x < gridNum; x++) {
            for (int y = 0; y < gridNum; y++) {
                //******************
                if (manager.animations(x, y).size() == 0)
                    drawCard(canvas, logic.getNowCardAt(x, y));
                for (Animation anim : manager.animations(x, y)){
                    anim.addElapseTime(elapseTime);
                    if (anim.isActive()) {
                        if (anim.getType() == Animation.TYPE_BLOOM || anim.getType() == Animation.TYPE_FUSE) {
                            drawCard(canvas, logic.getNowCardAt(x, y),
                                        toPixel(anim.getShifting()), anim.getScale());
                        } else if (anim.getType() == Animation.TYPE_MOVE) {
                            drawCard(canvas, logic.getLastCardAt(anim.startPos.getx(), anim.startPos.gety()),
                                        toPixel(anim.getShifting()), anim.getScale());
                            if (manager.animations(x, y).size() == 2)
                                drawCard(canvas, logic.getLastCardAt(x, y));
                        }
                    }else if (anim.isDone()){
                        if (!(manager.animations(x, y).size() >= 2 && anim.getType() == Animation.TYPE_MOVE)) {
                            drawCard(canvas, logic.getNowCardAt(x, y));
                        }
                        doneAnimations.add(anim);
                    }
                }
            }
        }
        Log.d(TAG, "drawAnimation: doneAnimation:" + doneAnimations.size());
        for (Animation a : doneAnimations)
            manager.removeAnimation(a);

        if (manager.animationIsActive() || manager.getAnimationCount() <= 0) {
            manager.recordTime();
            Log.d(TAG, "drawAnimation: lzg");
            invalidate();
        }

    }

    private int getCardXByY(int y) {
        return cardBaseX + baseGapWidth + y * (cardSize + baseGapWidth);
    }

    private float[] toPixel(float[] shifting) {
        shifting[0] *= (cardSize + baseGapWidth);
        shifting[1] *= (cardSize + baseGapWidth);
        return shifting;
    }

    private int getCardYByX(int x) {
        return cardBaseY + baseGapWidth + x * (cardSize + baseGapWidth);
    }

    private int getIndex(int value) {
        int index = (int) (Math.log(value) / Math.log(2));
        return index < 17?index:0;
    }

    private void drawCardText(Canvas canvas, String text,Paint paint) {
        Paint.FontMetrics fm1 = paint.getFontMetrics();
//        float halfTextH = -fm1.descent + (fm1.bottom - fm1.top) / 2;
        float halfTextH = -fm1.descent + (fm1.descent - fm1.ascent) / 2;
        canvas.drawText(text, cardSize / 2, cardSize / 2 + halfTextH, paint);
    }

    private void createBackground() {
        Log.d(TAG, "createBackground: ");
        backgroundBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
        Canvas backgroundCanvas = new Canvas(this.backgroundBitmap);
        int backColor = ContextCompat.getColor(this.getContext(), R.color.gameBackground);
        backgroundCanvas.drawColor(backColor);
        drawHeader(backgroundCanvas);
        drawbottomBase(backgroundCanvas);
        drawGrid(backgroundCanvas);
        drawTopbase(backgroundCanvas);
        drawScoreBase(backgroundCanvas);
        //drawLogo(backgroundCanvas);
        drawLogoText(backgroundCanvas);
        drawAboutButton(backgroundCanvas);
        drawNewGameButton(backgroundCanvas);
        drawUndoButton(backgroundCanvas);
        //saveBackgroundBitmap();
        Log.d(TAG, "drawView: 10");
        Log.d(TAG, "drawView: 20");
    }

    private void drawHeader(Canvas canvas) {
        Drawable header = ContextCompat.getDrawable(this.getContext(), R.drawable.headerbase);
        header.setBounds(0, 0, viewWidth, viewHeight / 4);
        header.draw(canvas);
    }

    private void drawbottomBase(Canvas canvas) {
        Drawable buttom = ContextCompat.getDrawable(this.getContext(), R.drawable.bottombase);
        buttom.setBounds(0, viewHeight * 4/5, viewWidth, viewHeight);
        buttom.draw(canvas);
    }

    private void drawGrid(Canvas canvas) {
        Drawable gridBase = ContextCompat.getDrawable(this.getContext(),R.drawable.cardbase_color);
        gridBase.setBounds(cardBaseX, cardBaseY, cardBaseX + cardBaseSize, cardBaseY + cardBaseSize);
        gridBase.draw(canvas);
        Drawable cardbase = ContextCompat.getDrawable(this.getContext(), R.drawable.card_view_color0);
        for (int row = 0; row < gridNum; row++) {
            for (int column = 0; column < gridNum; column++) {
                cardbase.setBounds(getCardXByY(column), getCardYByX(row),
                        getCardXByY(column) + cardSize, getCardYByX(row) + cardSize);
                cardbase.draw(canvas);
            }
        }
    }

    private void drawScoreBase(Canvas canvas) {
        Drawable scorebase = ContextCompat.getDrawable(this.getContext(), R.drawable.scorebase);
        scorebase.setAlpha(150);
        scorebase.setBounds(textTitleX, scoreBodyY, textTitleX + textTitleWidth, scoreBodyY + textHeight);
        scorebase.draw(canvas);
        scorebase.setBounds(textTitleX, bestBodyY, textTitleX + textTitleWidth, bestBodyY + textHeight);
        scorebase.draw(canvas);
        scorebase.setBounds(textTitleX, levelBodyY, textTitleX + textTitleWidth, levelBodyY + textHeight);
        scorebase.draw(canvas);
        Paint.FontMetrics fm = paint.getFontMetrics();
        float halfTextHeight = -fm.descent + (fm.bottom - fm.top) / 2;
        canvas.drawText(scoreText, textTitleX + textTitleWidth / 2, scoreBodyY + textHeight / 2 + halfTextHeight, paint);
        canvas.drawText(bestText, textTitleX + textTitleWidth / 2, bestBodyY + textHeight / 2 + halfTextHeight, paint);
        canvas.drawText(levelText, textTitleX + textTitleWidth / 2, levelBodyY + textHeight / 2 + halfTextHeight, paint);
    }

    private void drawTopbase(Canvas canvas) {
        Drawable drawable = ContextCompat.getDrawable(this.getContext(), R.drawable.cardbase_color);
        drawable.setAlpha(150);
        drawable.setBounds(viewWidth / 2 - cardBaseSize / 2, baseGapWidth, viewWidth / 2 + cardBaseSize / 2, baseGapWidth + topHeight);
        drawable.draw(canvas);
    }

    private void drawLogo(Canvas canvas) {
        Drawable logoBack = ContextCompat.getDrawable(this.getContext(), R.drawable.card_view_color0);
        logoBack.setBounds(viewWidth / 2 - cardBaseSize / 2 + baseGapWidth,
                2 * baseGapWidth, viewWidth / 2 - cardBaseSize / 2 + baseGapWidth + logoWidth,
                2 * baseGapWidth + logoWidth);
        logoBack.draw(canvas);
    }

    private void drawLogoText(Canvas canvas) {
        Paint logoPaint = new Paint();
        logoPaint.setTextAlign(Paint.Align.CENTER);
        logoPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "ClearSans-Bold.ttf"));
        logoPaint.setColor(Color.WHITE);
        logoPaint.setFakeBoldText(true);
        logoPaint.setTextSize(logoWidth);
        logoPaint.setTextSize(logoWidth * (logoWidth - 2 * logoPadding) / (int) (logoPaint.measureText("2048")));
        Paint.FontMetrics fm2 = logoPaint.getFontMetrics();
        float halfLogoTextH = -fm2.descent + (fm2.bottom - fm2.top) / 2;
        canvas.drawText("2048", viewWidth / 2 - cardBaseSize / 2 + baseGapWidth + logoWidth / 2,
                2 * baseGapWidth + logoWidth / 2 + halfLogoTextH, logoPaint);
    }

    private void drawScore(Canvas canvas, int score, int y) {
        Drawable scoreBase = ContextCompat.getDrawable(this.getContext(), R.drawable.scorebodybase);
        float w = scorePaint.measureText("" + score) + scorePadding * 2;
        int mx = (int) (textTitleX - w - scoreGap);
        scoreBase.setAlpha(150);
        scoreBase.setBounds(mx, y, (int) (mx + w), y + textHeight);
        scoreBase.draw(canvas);
        canvas.drawText(String.valueOf("" + score), mx + w / 2, y + textHeight / 2 + halfScoreH, scorePaint);

    }
    private void drawWin(Canvas canvas){
        Drawable base = ContextCompat.getDrawable(this.getContext(),R.drawable.win_lose_draw);
        base.setAlpha(100);
        base.setBounds(0,0,cardBaseSize,cardBaseSize);
        Bitmap bitmap = Bitmap.createBitmap(cardBaseSize,cardBaseSize,Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        base.draw(c);
        winLosePaint.setTextSize(winLosePaintSize/2);
        winLosePaint.setColor(Color.WHITE);
        c.drawText("点击继续",cardBaseSize / 2,cardBaseSize / 2 + cardSize/2, winLosePaint);
        winLosePaint.setTextSize(winLosePaintSize);
        winLosePaint.setColor(ContextCompat.getColor(getContext(),R.color.textDarkColor));
        c.drawText("太棒了!2048",cardBaseSize / 2,cardBaseSize / 2 - baseGapWidth, winLosePaint);
        Drawable winDrawale = new BitmapDrawable(this.getResources(),bitmap);
        winDrawale.setBounds(cardBaseX,cardBaseY,cardBaseX + cardBaseSize,cardBaseY + cardBaseSize);
        winDrawale.draw(canvas);
    }
    private void drawLose(Canvas canvas){
        Drawable base = ContextCompat.getDrawable(this.getContext(),R.drawable.win_lose_draw);
        base.setAlpha(100);
        base.setBounds(0,0,cardBaseSize,cardBaseSize);
        Bitmap bitmap2 = Bitmap.createBitmap(cardBaseSize,cardBaseSize,Bitmap.Config.ARGB_8888);
        Canvas c2 = new Canvas(bitmap2);
        base.draw(c2);
        c2.drawText("游戏结束",cardBaseSize / 2,cardBaseSize / 2, winLosePaint);
        Drawable loseDrawale = new BitmapDrawable(this.getResources(),bitmap2);
        loseDrawale.setBounds(cardBaseX,cardBaseY,cardBaseX + cardBaseSize,cardBaseY + cardBaseSize);
        loseDrawale.draw(canvas);
    }


    private void drawButtonBase(Canvas canvas,int baseX,Drawable drawable){
        drawable.setBounds(baseX,bottomY,baseX + bottomIconW,
                bottomY + bottomIconH);
        drawable.draw(canvas);
    }
    private void drawUndoLogo(Canvas canvas,Drawable logo){
        int dsize = (int)(bottomIconH * 0.2f);
        logo.setBounds(undoButX + dsize,bottomY + dsize,undoButX + bottomIconW - dsize,
                bottomY + bottomIconH - dsize);
        logo.draw(canvas);
    }
    private void drawUndoButton(Canvas canvas) {
        drawButtonBase(canvas,undoButX,buttonBase);
        drawUndoLogo(canvas,ContextCompat.getDrawable(this.getContext(),R.drawable.undo_150px));
    }
    private void drawNewGameLogo(Canvas canvas){
        Drawable logo = ContextCompat.getDrawable(this.getContext(),R.drawable.newgame_150px);
        int dsize = (int)(bottomIconH * 0.2f);
        logo.setBounds(newGameButX + dsize,bottomY + dsize,newGameButX + bottomIconW - dsize,
                bottomY + bottomIconH - dsize);
        logo.draw(canvas);
    }
    private void drawNewGameButton(Canvas canvas) {
        drawButtonBase(canvas,newGameButX,buttonBase);
        drawNewGameLogo(canvas);
    }

    private void drawAboutButton(Canvas canvas) {
        drawButtonBase(canvas,aboutButX,buttonBase);
        Drawable aboutDraw = ContextCompat.getDrawable(this.getContext(),R.drawable.about_150px);
        int dsize = (int)(bottomIconH * 0.2f);
        aboutDraw.setBounds(aboutButX + dsize,bottomY + dsize,aboutButX + bottomIconW - dsize,
                bottomY + bottomIconH - dsize);
        aboutDraw.draw(canvas);
    }
    public int getPushButton(float x,float y){
        if (y > bottomY && y < bottomY + bottomIconH) {
            if (x > undoButX && x < undoButX + bottomIconW){
                return 1;
            }else if (x > newGameButX && x < newGameButX + bottomIconW){
                return 2;
            }else if (x > aboutButX && x < aboutButX + bottomIconW){
                return 3;
            }
        }
        if (logic.gameState == GameLogic.GAME_STATE_WIN) {
            if (x > cardBaseX && x < cardBaseX + cardBaseSize && y > cardBaseY && y < cardBaseY + cardBaseSize)
                return 4;
        }
        return 0;
    }
//    private void saveBackgroundBitmap() {
//        StringBuffer path = new StringBuffer("");
//        if (isPortrait)
//            path.append(dir + "/bgP.lzg");
//        else
//            path.append(dir + "/bgL.lzg");
//        File f = new File(path.toString());
//        if (!f.exists())
//            try {
//                f.createNewFile();
//                Log.d(TAG, "saveBackgroundBitmap: 背景缓存成功");
//            } catch (IOException e) {
//                Log.d(TAG, "saveBackgroundBitmap: " + path.toString());
//                Log.d(TAG, "saveBackgroundBitmap: 背景缓存失败");
//                e.printStackTrace();
//            }
//        SDcard.saveBitmap(this.backgroundBitmap, f, Bitmap.CompressFormat.PNG, 100);
//    }

//    private boolean readBackgroundBitmap(boolean isPortrait) {
//        File f = null;
//        if (isPortrait)
//            f = new File(dir + "/bgP.lzg");
//        else
//            f = new File(dir + "/bgL.lzg");
//        if (f.exists() && f.isFile()) {
//            backgroundBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
//            return true;
//        } else
//            return false;
//    }

    private boolean isScreenOriatationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }
    public void save(SharedPreferences preferences){
        logic.save(preferences);
    }
    public void load(SharedPreferences preferences){
        logic.load(preferences);
    }
}
