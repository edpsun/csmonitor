package com.hylps.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

public class ImageHelper {

    public static BufferedImage readJPEGImage(String filename) {
        try {
            InputStream imageIn = new FileInputStream(new File(filename));
            JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(imageIn);
            BufferedImage sourceImage = decoder.decodeAsBufferedImage();

            return sourceImage;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ImageFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedImage readImage(String filename) {
        try {
            File inputFile = new File(filename);
            BufferedImage sourceImage = ImageIO.read(inputFile);
            return sourceImage;
        } catch (Exception e) {
            throw new RuntimeException("[Error] Can not read image.", e);
        }
    }

    public static BufferedImage readImage(InputStream in) {
        try {
            BufferedImage sourceImage = ImageIO.read(in);
            return sourceImage;
        } catch (Exception e) {
            throw new RuntimeException("[Error] Can not read image.", e);
        }
    }

    public static BufferedImage readImageByUrl(String url) {
        HttpURLConnection httpConnection = null;

        InputStream in = null;
        try {
            URL _url = new URL(url);
            httpConnection = (HttpURLConnection) _url.openConnection();
            in = httpConnection.getInputStream();
            return readImage(in);
        } catch (Exception ex) {
            throw new RuntimeException("Getting data error. URL: " + url, ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
    }

   

    public static BufferedImage thumb(BufferedImage source, int width, int height, boolean b) {
        int type = source.getType();
        BufferedImage target = null;
        double sx = (double) width / source.getWidth();
        double sy = (double) height / source.getHeight();

        if (b) {
            if (sx > sy) {
                sx = sy;
                width = (int) (sx * source.getWidth());
            } else {
                sy = sx;
                height = (int) (sy * source.getHeight());
            }
        }

        if (type == BufferedImage.TYPE_CUSTOM) { // handmade
            ColorModel cm = source.getColorModel();
            WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
            boolean alphaPremultiplied = cm.isAlphaPremultiplied();
            target = new BufferedImage(cm, raster, alphaPremultiplied, null);
        } else
            target = new BufferedImage(width, height, type);
        Graphics2D g = target.createGraphics();
        // smoother than exlax:
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
        g.dispose();
        return target;
    }

   

}
