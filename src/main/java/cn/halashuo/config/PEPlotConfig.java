package cn.halashuo.config;

import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PEPlotConfig {

    public static final List<Scalar> palette= new ArrayList<>(Arrays.asList(
            new Scalar( 255, 128, 0 ),
            new Scalar( 255, 153, 51 ),
            new Scalar( 255, 178, 102 ),
            new Scalar( 230, 230, 0 ),
            new Scalar( 255, 153, 255 ),
            new Scalar( 153, 204, 255 ),
            new Scalar( 255, 102, 255 ),
            new Scalar( 255, 51, 255 ),
            new Scalar( 102, 178, 255 ),
            new Scalar( 51, 153, 255 ),
            new Scalar( 255, 153, 153 ),
            new Scalar( 255, 102, 102 ),
            new Scalar( 255, 51, 51 ),
            new Scalar( 153, 255, 153 ),
            new Scalar( 102, 255, 102 ),
            new Scalar( 51, 255, 51 ),
            new Scalar( 0, 255, 0 ),
            new Scalar( 0, 0, 255 ),
            new Scalar( 255, 0, 0 ),
            new Scalar( 255, 255, 255 )
    ));

    public static final int[][] skeleton = {
            {16, 14}, {14, 12}, {17, 15}, {15, 13}, {12, 13}, {6, 12},
            {7, 13}, {6, 7}, {6, 8}, {7, 9}, {8, 10}, {9, 11}, {2, 3},
            {1, 2}, {1, 3}, {2, 4}, {3, 5}, {4, 6}, {5, 7}
    };

    public static final List<Scalar> poseLimbColor = new ArrayList<>(Arrays.asList(
            palette.get(9), palette.get(9), palette.get(9), palette.get(9), palette.get(7),
            palette.get(7), palette.get(7), palette.get(0), palette.get(0), palette.get(0),
            palette.get(0), palette.get(0), palette.get(16), palette.get(16), palette.get(16),
            palette.get(16), palette.get(16), palette.get(16), palette.get(16)));

    public static final List<Scalar> poseKptColor = new ArrayList<>(Arrays.asList(
            palette.get(16), palette.get(16), palette.get(16), palette.get(16), palette.get(16),
            palette.get(0), palette.get(0), palette.get(0), palette.get(0), palette.get(0),
            palette.get(0), palette.get(9), palette.get(9), palette.get(9), palette.get(9),
            palette.get(9), palette.get(9)));

}