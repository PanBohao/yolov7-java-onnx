package cn.halashuo.domain;

import java.util.ArrayList;
import java.util.List;

public class PEResult {

    private Float x0;
    private Float y0;
    private Float x1;
    private Float y1;
    private Float score;
    private Integer clsId;
    private List<KeyPoint> keyPointList;

    public PEResult(float[] peResult) {
        float x = peResult[0];
        float y = peResult[1];
        float w = peResult[2]/2.0f;
        float h = peResult[3]/2.0f;
        this.x0 = x-w;
        this.y0 = y-h;
        this.x1 = x+w;
        this.y1 = y+h;
        this.score = peResult[4];
        this.clsId = (int) peResult[5];
        this.keyPointList = new ArrayList<>();
        int keyPointNum = (peResult.length-6)/3;
        for (int i=0;i<keyPointNum;i++) {
            this.keyPointList.add(new KeyPoint(i, peResult[6+3*i], peResult[6+3*i+1], peResult[6+3*i+2]));
        }
    }

    public Float getX0() {
        return x0;
    }

    public Float getY0() {
        return y0;
    }

    public Float getX1() {
        return x1;
    }

    public Float getY1() {
        return y1;
    }

    public Float getScore() {
        return score;
    }

    public Integer getClsId() {
        return clsId;
    }

    public List<KeyPoint> getKeyPointList() {
        return keyPointList;
    }

    @Override
    public String toString() {
        String result = "PEResult:" +
                "  x0=" + x0 +
                ", y0=" + y0 +
                ", x1=" + x1 +
                ", y1=" + y1 +
                ", score=" + score +
                ", clsId=" + clsId +
                "\n";
        for (KeyPoint x : keyPointList) {
            result = result + x.toString();
        }
        return result;
    }
}

