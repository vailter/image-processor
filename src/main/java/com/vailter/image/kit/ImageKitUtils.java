package com.vailter.image.kit;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.vailter.image.ex.ImageParseException;
import com.vailter.image.type.ImageType;

/**
 * Image处理的辅助工具
 *
 * @author mdc
 * @date 2018年6月7日
 */
public class ImageKitUtils {

    /**
     * 获取图片的类型。如果是 gif、jpg、png、bmp
     *
     * @param input an Object to be used as an input source, such as a File, readable RandomAccessFile, or InputStream.
     * @return 图片类型
     */
    public static ImageAndFormat getImageAndFormat(Object input) throws ImageParseException {
        ImageInputStream imageInput = null;

        try {
            imageInput = ImageIO.createImageInputStream(input);
            ImageAndFormat andImage = new ImageAndFormat();
            andImage.format = getFormat((ImageInputStream) imageInput);
            andImage.image = ImageIO.read(imageInput);

            return andImage;
        } catch (IOException e) {
            IOKit.closeQuietly(imageInput);
            throw new ImageParseException(e.getMessage(), e);
        }
    }

    /**
     * 获取图片的格式
     *
     * @param input an Object to be used as an input source, such as a File, readable RandomAccessFile, or InputStream.
     * @return
     * @throws IOException
     * @author mdc
     * @date 2016年8月14日
     */
    public static ImageType getFormat(Object input) throws IOException {
        ImageInputStream imageInput = ImageIO.createImageInputStream(input);
        return getFormat((ImageInputStream) imageInput);
    }

    /**
     * 获取图片的格式
     *
     * @param imageInput
     * @return
     * @throws IOException
     * @author mdc
     * @date 2016年8月14日
     */
    public static ImageType getFormat(ImageInputStream imageInput) throws IOException {
        Iterator<ImageReader> iterator = ImageIO.getImageReaders(imageInput);
        String type = null;

        for (; iterator.hasNext(); ) {
            ImageReader reader = iterator.next();
            String format = reader.getFormatName();

            if (format != null && format.trim().length() > 0) {
                type = format.toLowerCase();
                break;
            }
        }

        if (type == null) {
            throw new ImageParseException("不支持的图片格式");
        }

        if ("jpg".equals(type)) return ImageType.JPG;

        if ("jpeg".equals(type)) return ImageType.JPEG;

        if ("bmp".equals(type)) return ImageType.BMP;

        if ("wbmp".equals(type)) return ImageType.WBMP;

        if ("gif".equals(type)) return ImageType.GIF;

        if ("png".equals(type)) return ImageType.PNG;

        return ImageType.JPEG;
    }

    /**
     * 图片和格式
     *
     * @author mdc
     * @date 2016年8月14日
     */
    public static class ImageAndFormat {
        public BufferedImage image;
        public ImageType format;
    }

    /**
     * 获取等比缩放后的宽度和高度
     *
     * @param width      缩放的宽度
     * @param height     缩放的高度
     * @param proportion 是否等比缩放
     * @return
     * @author mdc
     * @date 2016年5月14日
     */
    public static int[] getProportion(BufferedImage image, int width, int height, boolean proportion) {
        int newW;
        int newH;

        if (proportion) { // 判断是否是等比缩放
            newW = image.getWidth();
            newH = image.getHeight();

            // 为等比缩放计算输出的图片宽度及高度
            double rate1 = ((double) newW) / (double) width;
            double rate2 = ((double) newH) / (double) height;
            double rate = rate1 > rate2 ? rate1 : rate2;

            newW = (int) (((double) newW) / rate);
            newH = (int) (((double) newH) / rate);
        } else {
            newW = width;
            newH = height;
        }

        return new int[]{newW, newH};
    }

    /**
     * 把图片转换为png类型
     *
     * @param src 源图片
     * @return
     * @author mdc
     * @date 2016年5月14日
     */
    public static BufferedImage convertPng(BufferedImage src) {
        if (src.getTransparency() == Transparency.TRANSLUCENT) {
            return src;
        }

        Image image = src.getScaledInstance(src.getWidth(), src.getHeight(), Image.SCALE_DEFAULT);
        BufferedImage tag = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = tag.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        InputStream input = null;
        ByteArrayOutputStream out = null;

        try {
            out = new ByteArrayOutputStream();
            ImageIO.write(tag, ImageType.PNG.getType(), out);

            input = new ByteArrayInputStream(out.toByteArray());
            return ImageIO.read(input);

        } catch (IOException e) {
            IOKit.closeQuietly(input);
            throw new ImageParseException(e);

        } finally {
            IOKit.closeQuietly(out);
        }
    }

    /**
     * 对图片进行旋转
     *
     * @param src   被旋转图片
     * @param angel 旋转角度
     * @return 旋转后的图片
     */
    public static BufferedImage rotate(Image src, int angel) {
        int src_width = src.getWidth(null);
        int src_height = src.getHeight(null);
        // 计算旋转后图片的尺寸
        Rectangle rect_des = calcRotatedSize(new Rectangle(new Dimension(
                src_width, src_height)), angel);
        BufferedImage res = null;
        res = new BufferedImage(rect_des.width, rect_des.height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = res.createGraphics();
        // 进行转换
        g2.translate((rect_des.width - src_width) / 2,
                (rect_des.height - src_height) / 2);
        g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);
        g2.drawImage(src, null, null);
        return res;
    }

    /**
     * 计算旋转后的图片
     *
     * @param src   被旋转的图片
     * @param angel 旋转角度
     * @return 旋转后的图片
     */
    public static Rectangle calcRotatedSize(Rectangle src, int angel) {
        // 如果旋转的角度大于90度做相应的转换
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }

        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angel_dalta_width = Math.atan((double) src.height / src.width);
        double angel_dalta_height = Math.atan((double) src.width / src.height);

        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha
                - angel_dalta_width));
        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha
                - angel_dalta_height));
        int des_width = src.width + len_dalta_width * 2;
        int des_height = src.height + len_dalta_height * 2;
        return new Rectangle(new Dimension(des_width, des_height));
    }

}
