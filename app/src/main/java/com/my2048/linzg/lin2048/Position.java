package com.my2048.linzg.lin2048;

/**
 * Created by asus on 2017/8/10.
 */

public class Position {
    private int mx,my;
    public Position(int x,int y){
        this.mx = x;
        this.my = y;
    }
    public Position(Position position){
        this.mx = position.getx();
        this.my = position.gety();
    }

    public int getx() {
        return mx;
    }

    public int gety() {
        return my;
    }

    public void set(int x, int y){
        this.mx = x;
        this.my = y;
    }
}
