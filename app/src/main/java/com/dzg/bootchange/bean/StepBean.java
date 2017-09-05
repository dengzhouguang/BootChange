package com.dzg.bootchange.bean;

/**
 * Created by Administrator on 2017/8/20 0020.
 */

public class StepBean {
    /**
     * biz : alipay
     * steps : 79
     * time : 1503170368246
     */

    private String biz;

    @Override
    public String toString() {
        return "StepBean{" +
                "biz='" + biz + '\'' +
                ", steps=" + steps +
                ", time=" + time +
                '}';
    }

    private int steps;
    private long time;

    public String getBiz() {
        return biz;
    }

    public void setBiz(String biz) {
        this.biz = biz;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
