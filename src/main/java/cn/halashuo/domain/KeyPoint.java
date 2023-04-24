package cn.halashuo.domain;

public class KeyPoint {
    private Integer id;
    private Float x;
    private Float y;
    private Float score;

    public KeyPoint(Integer id, Float x, Float y, Float score) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.score = score;
    }

    public Integer getId() {
        return id;
    }

    public Float getX() {
        return x;
    }

    public Float getY() {
        return y;
    }

    public Float getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "    第 " + (id+1) + " 个关键点: " +
                " x=" + x +
                " y=" + y +
                " c=" + score +
                "\n";
    }
}
