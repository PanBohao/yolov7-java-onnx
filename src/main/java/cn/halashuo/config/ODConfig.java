package cn.halashuo.config;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public final class ODConfig {

    public static final String modelPath = "src\\main\\resources\\model\\yolov7-d6.onnx";
    public static final String picPath = "images\\test.jpg";// 要预测图片位置
    public static final String savePicPath = "images\\OD-test.jpg";// 预测结果保存位置
    public static final Integer lineThicknessRatio = 333;
    public static final Double fontSizeRatio = 1145.14;
    private static final List<String> names = new ArrayList<>(Arrays.asList(
            "person", "bicycle", "car", "motorcycle", "airplane", "bus", "train",
            "truck", "boat", "traffic light", "fire hydrant", "stop sign", "parking meter",
            "bench", "bird", "cat", "dog", "horse", "sheep", "cow", "elephant", "bear",
            "zebra", "giraffe", "backpack", "umbrella", "handbag", "tie", "suitcase",
            "frisbee", "skis", "snowboard", "sports ball", "kite", "baseball bat",
            "baseball glove", "skateboard", "surfboard", "tennis racket", "bottle",
            "wine glass", "cup", "fork", "knife", "spoon", "bowl", "banana", "apple",
            "sandwich", "orange", "broccoli", "carrot", "hot dog", "pizza", "donut",
            "cake", "chair", "couch", "potted plant", "bed", "dining table", "toilet",
            "tv", "laptop", "mouse", "remote", "keyboard", "cell phone", "microwave",
            "oven", "toaster", "sink", "refrigerator", "book", "clock", "vase", "scissors",
            "teddy bear", "hair drier", "toothbrush"));

    private final Map<String, double[]> colors;

    public ODConfig() {
        this.colors = new HashMap<>();
        names.forEach(name->{
            Random random = new Random();
            double[] color = {random.nextDouble()*256, random.nextDouble()*256, random.nextDouble()*256};
            colors.put(name, color);
        });
    }

    public String getName(int clsId) {
        return names.get(clsId);
    }

    public double[] getColor(int clsId) {
        return colors.get(getName(clsId));
    }
}