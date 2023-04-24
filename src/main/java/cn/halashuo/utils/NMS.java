package cn.halashuo.utils;

import cn.halashuo.domain.PEResult;

import java.util.ArrayList;
import java.util.List;

public class NMS {

    public static List<PEResult> nms(List<PEResult> boxes, float iouThreshold) {
        // 根据score从大到小对List进行排序
        boxes.sort((b1, b2) -> Float.compare(b2.getScore(), b1.getScore()));
        List<PEResult> resultList = new ArrayList<>();
        for (int i = 0; i < boxes.size(); i++) {
            PEResult box = boxes.get(i);
            boolean keep = true;
            // 从i+1开始，遍历之后的所有boxes，移除与box的IOU大于阈值的元素
            for (int j = i + 1; j < boxes.size(); j++) {
                PEResult otherBox = boxes.get(j);
                float iou = getIntersectionOverUnion(box, otherBox);
                if (iou > iouThreshold) {
                    keep = false;
                    break;
                }
            }
            if (keep) {
                resultList.add(box);
            }
        }
        return resultList;
    }
    private static float getIntersectionOverUnion(PEResult box1, PEResult box2) {
        float x1 = Math.max(box1.getX0(), box2.getX0());
        float y1 = Math.max(box1.getY0(), box2.getY0());
        float x2 = Math.min(box1.getX1(), box2.getX1());
        float y2 = Math.min(box1.getY1(), box2.getY1());
        float intersectionArea = Math.max(0, x2 - x1) * Math.max(0, y2 - y1);
        float box1Area = (box1.getX1() - box1.getX0()) * (box1.getY1() - box1.getY0());
        float box2Area = (box2.getX1() - box2.getX0()) * (box2.getY1() - box2.getY0());
        float unionArea = box1Area + box2Area - intersectionArea;
        return intersectionArea / unionArea;
    }
}