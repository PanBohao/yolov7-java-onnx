package cn.halashuo;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import cn.halashuo.domain.KeyPoint;
import cn.halashuo.domain.PEResult;
import cn.halashuo.utils.Letterbox;
import cn.halashuo.utils.NMS;
import cn.halashuo.config.PEPlotConfig;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PoseEstimation {
    static
    {
        //在使用OpenCV前必须加载Core.NATIVE_LIBRARY_NAME类,否则会报错
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) throws OrtException {

        // 加载ONNX模型
        OrtEnvironment environment = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions sessionOptions = new OrtSession.SessionOptions();
        OrtSession session = environment.createSession("src\\main\\resources\\model\\yolov7-w6-pose.onnx", sessionOptions);
        // 输出基本信息
        session.getInputInfo().keySet().forEach(x -> {
            try {
                System.out.println("input name = " + x);
                System.out.println(session.getInputInfo().get(x).getInfo().toString());
            } catch (OrtException e) {
                throw new RuntimeException(e);
            }
        });

        // 读取 image
        Mat img = Imgcodecs.imread("src\\main\\resources\\image\\bus.jpg");
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2RGB);
        Mat image = img.clone();

        // 在这里先定义下线的粗细、关键的半径(按比例设置大小粗细比较好一些)
        int minDwDh = Math.min(img.width(), img.height());
        int thickness = minDwDh / 333;
        int radius = minDwDh / 168;

        // 更改 image 尺寸
        Letterbox letterbox = new Letterbox();
        letterbox.setNewShape(new Size(960, 960));
        letterbox.setStride(64);
        image = letterbox.letterbox(image);
        double ratio = letterbox.getRatio();
        double dw = letterbox.getDw();
        double dh = letterbox.getDh();
        int rows = letterbox.getHeight();
        int cols = letterbox.getWidth();
        int channels = image.channels();

        // 将Mat对象的像素值赋值给Float[]对象
        float[] pixels = new float[channels * rows * cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double[] pixel = image.get(j, i);
                for (int k = 0; k < channels; k++) {
                    // 这样设置相当于同时做了image.transpose((2, 0, 1))操作
                    pixels[rows * cols * k + j * cols + i] = (float) pixel[k] / 255.0f;
                }
            }
        }

        // 创建OnnxTensor对象
        long[] shape = {1L, (long) channels, (long) rows, (long) cols};
        OnnxTensor tensor = OnnxTensor.createTensor(environment, FloatBuffer.wrap(pixels), shape);
        HashMap<String, OnnxTensor> stringOnnxTensorHashMap = new HashMap<>();
        stringOnnxTensorHashMap.put(session.getInputInfo().keySet().iterator().next(), tensor);

        // 运行模型
        OrtSession.Result output = session.run(stringOnnxTensorHashMap);

        // 得到结果
        float[][] outputData = ((float[][][]) output.get(0).getValue())[0];

        List<PEResult> peResults = new ArrayList<>();
        for (int i=0;i<outputData.length;i++){
            PEResult result = new PEResult(outputData[i]);
            if (result.getScore()>0.25f) {
                peResults.add(result);
            }
        }

        // 对结果进行非极大值抑制
        peResults = NMS.nms(peResults, 0.65f);

        for (PEResult peResult: peResults) {
            System.out.println(peResult);
            // 画框
            Point topLeft = new Point((peResult.getX0()-dw)/ratio, (peResult.getY0()-dh)/ratio);
            Point bottomRight = new Point((peResult.getX1()-dw)/ratio, (peResult.getY1()-dh)/ratio);
            Imgproc.rectangle(img, topLeft, bottomRight, new Scalar(255,0,0), thickness);
            List<KeyPoint> keyPoints = peResult.getKeyPointList();
            // 画点
            keyPoints.forEach(keyPoint->{
                if (keyPoint.getScore()>0.45f) {
                    Point center = new Point((keyPoint.getX()-dw)/ratio, (keyPoint.getY()-dh)/ratio);
                    Scalar color = PEPlotConfig.poseKptColor.get(keyPoint.getId());
                    Imgproc.circle(img, center, radius, color, -1); //-1表示实心
                }
            });
            // 画线
            for (int i=0;i<PEPlotConfig.skeleton.length;i++){
                int indexPoint1 = PEPlotConfig.skeleton[i][0]-1;
                int indexPoint2 = PEPlotConfig.skeleton[i][1]-1;
                if ( keyPoints.get(indexPoint1).getScore()>0.45f && keyPoints.get(indexPoint2).getScore()>0.45f ) {
                    Scalar coler = PEPlotConfig.poseLimbColor.get(i);
                    Point point1 = new Point(
                            (keyPoints.get(indexPoint1).getX()-dw)/ratio,
                            (keyPoints.get(indexPoint1).getY()-dh)/ratio
                    );
                    Point point2 = new Point(
                            (keyPoints.get(indexPoint2).getX()-dw)/ratio,
                            (keyPoints.get(indexPoint2).getY()-dh)/ratio
                    );
                    Imgproc.line(img, point1, point2, coler, thickness);
                }
            }
        }
        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2BGR);
        // 保存图像
        // Imgcodecs.imwrite("image.jpg", img);
        HighGui.imshow("Display Image", img);
        // 等待按下任意键继续执行程序
        HighGui.waitKey();

    }
}