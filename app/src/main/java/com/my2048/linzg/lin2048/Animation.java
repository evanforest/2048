package com.my2048.linzg.lin2048;

/**
 * Created by lin on 2017/8/10.
 */

public class Animation extends Position{
    public static final int TYPE_MOVE = 1;  //卡片移动动画
    public static final int TYPE_BLOOM = 2; //卡片生成动画
    public static final int TYPE_FUSE = 3;  //卡片融合动画
    private final int animationType;  //动画类型
    private final int durationTime;   //动画持续时间
    private final int delayTime;      //动画启动延时
    private int elapseTime;     //动画开始过去的时间
//    public Position coordinatePos = null;
    public Position startPos = null;

    /**
     * Animation构造函数
     * @param animationType 动画类型
     * @param durationTime  动画持续时间
     * @param delayTime 延时时间
     * @param coordinate     所在位置
     * @param startPos    目标位置（只用TYPE_MOVE才用）
     */
    public Animation(int animationType,int durationTime,int delayTime,Position coordinate,Position startPos){
        super(coordinate);
        this.animationType = animationType;
        this.durationTime = durationTime;
        this.delayTime = delayTime;
        this.elapseTime = 0;
        this.startPos = startPos;
    }
    public boolean isActive(){
        return elapseTime > delayTime && elapseTime <= delayTime + durationTime;
    }
    public boolean isDone(){
        return elapseTime > delayTime + durationTime;
    }
    public void addElapseTime(long newElapseTime){
        this.elapseTime += newElapseTime;
    }
    private float percent(){
        if(!isActive())
            return 0.0f;
        if (isDone())
            return 1.0f;
        return ((elapseTime * 1.0f - delayTime) / durationTime);
    }
    public int getType(){
        return this.animationType;
    }
    public float[] getShifting(){
        float[] shifting = new float[2];

        if (animationType == TYPE_MOVE) {
            //shifting[0]存放x轴的位移格数，所以得用y轴来计算
                //shifting[1]同理
            shifting[0] =  startPos.gety() - super.gety();
            shifting[1] =  startPos.getx() - super.getx();
        }else{
            shifting[0] = 0;
            shifting[1] = 0;
            return shifting;
        }
        shifting[0] *= (-percent());
        shifting[1] *= (-percent());
        return shifting;
    }
    public float getScale(){
        float scale = 1.0f;
        if (animationType == TYPE_FUSE){
            if (percent() < 0.5f){
                scale = 1.0f + (percent() /0.5f) * 0.1f;
            }else{
                scale = 1.1f - (percent() - 0.5f) /0.5f * 0.1f;
            }
        }else if (animationType == TYPE_BLOOM){
            scale = percent() * 1.0f;
        }
        return scale;
    }
    public int getAnimationX(){
        return super.getx();
    }
    public int getAnimationY(){
        return super.gety();
    }
}
