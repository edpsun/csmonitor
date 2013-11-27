package com.hylps.image;

import java.awt.image.BufferedImage;

public class Test {
    public static void main(String[] args) {
        ImageAnalyzer ia = ImageAnalyzer.getInstance();

        BufferedImage image1 = ImageHelper.readImage(("/home/esun/Desktop/work_temp/image_idendtify/test_pic/1.jpg"));
        String fp1 = ia.produceFingerPrint(image1);
        System.out.println(fp1);


        BufferedImage image2 = ImageHelper.readImage(("/home/esun/Desktop/work_temp/image_idendtify/test_pic/3.jpg"));
        String fp2 = ia.produceFingerPrint(image2);
        System.out.println(fp2);

        System.out.println(ia.hammingDistance(fp1, fp2));
    }

}
