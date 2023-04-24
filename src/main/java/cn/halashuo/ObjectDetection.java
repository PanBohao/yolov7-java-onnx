package cn.halashuo;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import cn.halashuo.domain.ODResult;
import cn.halashuo.utils.Lable;
import cn.halashuo.utils.Letterbox;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;

public class ObjectDetection {

    static
    {
        //在使用OpenCV前必须加载Core.NATIVE_LIBRARY_NAME类,否则会报错
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) throws OrtException {

        // 加载ONNX模型
        OrtEnvironment environment = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions sessionOptions = new OrtSession.SessionOptions();
        OrtSession session = environment.createSession("src\\main\\resources\\model\\yolov7-d6.onnx", sessionOptions);
        // 输出基本信息
        session.getInputInfo().keySet().forEach(x-> {
            try {
                System.out.println("input name = " + x);
                System.out.println(session.getInputInfo().get(x).getInfo().toString());
            } catch (OrtException e) {
                throw new RuntimeException(e);
            }
        });

        // 加载标签及颜色
        Lable lable = new Lable();

        // 读取 image
        Mat img = Imgcodecs.imread("src\\main\\resources\\image\\test.jpg");
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2RGB);
        Mat image = img.clone();

        // 在这里先定义下框的粗细、字的大小、字的类型、字的颜色(按比例设置大小粗细比较好一些)
        int minDwDh = Math.min(img.width(), img.height());
        int thickness = minDwDh/333;
        double fontSize = minDwDh/1145.14;
        int fontFace = Imgproc.FONT_HERSHEY_SIMPLEX;
        Scalar fontColor = new Scalar(255, 255, 255);

        // 更改 image 尺寸
        Letterbox letterbox = new Letterbox();
        image = letterbox.letterbox(image);
        double ratio  = letterbox.getRatio();
        double dw = letterbox.getDw();
        double dh = letterbox.getDh();
        int rows  = letterbox.getHeight();
        int cols  = letterbox.getWidth();
        int channels = image.channels();

        // 将Mat对象的像素值赋值给Float[]对象
        float[] pixels = new float[channels * rows * cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double[] pixel = image.get(j,i);
                for (int k = 0; k < channels; k++) {
                    // 这样设置相当于同时做了image.transpose((2, 0, 1))操作
                    pixels[rows*cols*k+j*cols+i] = (float) pixel[k]/255.0f;
                }
            }
        }

        // 创建OnnxTensor对象
        long[] shape = { 1L, (long)channels, (long)rows, (long)cols };
        OnnxTensor tensor = OnnxTensor.createTensor(environment, FloatBuffer.wrap(pixels), shape);
        HashMap<String, OnnxTensor> stringOnnxTensorHashMap = new HashMap<>();
        stringOnnxTensorHashMap.put(session.getInputInfo().keySet().iterator().next(), tensor);

        // 运行模型
        OrtSession.Result output = session.run(stringOnnxTensorHashMap);

        // 得到结果
        float[][] outputData = (float[][]) output.get(0).getValue();
        Arrays.stream(outputData).iterator().forEachRemaining(x->{
            ODResult odResult = new ODResult(x);
            System.out.println(odResult);
            // 画框
            Point topLeft = new Point((odResult.getX0()-dw)/ratio, (odResult.getY0()-dh)/ratio);
            Point bottomRight = new Point((odResult.getX1()-dw)/ratio, (odResult.getY1()-dh)/ratio);
            Scalar color = new Scalar(lable.getColor(odResult.getClsId()));
            Imgproc.rectangle(img, topLeft, bottomRight, color, thickness);
            // 框上写文字
            String boxName = lable.getName(odResult.getClsId()) + ": " + odResult.getScore();
            Point boxNameLoc = new Point((odResult.getX0()-dw)/ratio, (odResult.getY0()-dh)/ratio-3);
            Imgproc.putText(img, boxName, boxNameLoc, fontFace, fontSize, fontColor, thickness);
        });

        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2BGR);

        // 保存图像
        // Imgcodecs.imwrite("C:\\Users\\pbh0612\\Desktop\\image.jpg", img);
        HighGui.imshow("Display Image", img);
        // 等待按下任意键继续执行程序
        HighGui.waitKey();
    }
}
