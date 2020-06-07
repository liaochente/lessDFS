package com.liaochente.lessdfs.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * 图片处理工具
 */
public class ImageUtils {

    private final static Logger LOG = LoggerFactory.getLogger(ImageUtils.class);

    /**
     * 等比缩放，指定图片宽高为原来的scale%
     *
     * @param data
     * @param scale
     * @return
     */
    public final static byte[] imageScale(byte[] data, int scale) {
        Image image = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data)) {
            BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
            int imageWidth = bufferedImage.getWidth();
            int imageHeight = bufferedImage.getHeight();

            BigDecimal scaleDecimal = new BigDecimal(scale);

            imageWidth = scaleDecimal.divide(new BigDecimal(100)).multiply(new BigDecimal(imageWidth)).intValue();
            imageHeight = scaleDecimal.divide(new BigDecimal(100)).multiply(new BigDecimal(imageHeight)).intValue();

            image = bufferedImage.getScaledInstance(imageWidth, imageHeight, bufferedImage.SCALE_SMOOTH);
        } catch (IOException e) {
            throw new RuntimeException("图片I/O读取失败", e);
        }

        return imageToBytes(image);
    }

    /**
     * 按宽缩放，指定图片宽度为原来的scale%，高度不变
     *
     * @param data
     * @param scale
     * @return
     */
    public final static byte[] imageScaleWidth(byte[] data, int scale) {
        Image image = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data)) {
            BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
            int imageWidth = bufferedImage.getWidth();
            int imageHeight = bufferedImage.getHeight();

            BigDecimal scaleDecimal = new BigDecimal(scale);

            imageWidth = scaleDecimal.divide(new BigDecimal(100)).multiply(new BigDecimal(imageWidth)).intValue();
//            imageHeight = scaleDecimal.divide(new BigDecimal(100)).multiply(new BigDecimal(imageHeight)).intValue();

            image = bufferedImage.getScaledInstance(imageWidth, imageHeight, bufferedImage.SCALE_SMOOTH);
        } catch (IOException e) {
            throw new RuntimeException("图片I/O读取失败", e);
        }

        return imageToBytes(image);
    }

    /**
     * 按高缩放，指定图片高度为原来的scale%，宽度不变
     *
     * @param data
     * @param scale
     * @return
     */
    public final static byte[] imageScaleHeight(byte[] data, int scale) {
        Image image = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data)) {
            BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
            int imageWidth = bufferedImage.getWidth();
            int imageHeight = bufferedImage.getHeight();

            BigDecimal scaleDecimal = new BigDecimal(scale);

//            imageWidth = scaleDecimal.divide(new BigDecimal(100)).multiply(new BigDecimal(imageWidth)).intValue();
            imageHeight = scaleDecimal.divide(new BigDecimal(100)).multiply(new BigDecimal(imageHeight)).intValue();

            image = bufferedImage.getScaledInstance(imageWidth, imageHeight, bufferedImage.SCALE_SMOOTH);
        } catch (IOException e) {
            throw new RuntimeException("图片I/O读取失败", e);
        }

        return imageToBytes(image);
    }

    /**
     * 指定目标图片宽度为 Width，高度等比压缩
     *
     * @param data
     * @param width
     * @return
     */
    public final static byte[] imageScaleWidth2(byte[] data, int width) {
        Image image = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data)) {
            BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
            int imageWidth = bufferedImage.getWidth();
            int imageHeight = bufferedImage.getHeight();

            //求出缩放比例
            BigDecimal scaleDecimal = new BigDecimal(width);
            scaleDecimal = scaleDecimal.divide(new BigDecimal(imageWidth));

            imageWidth = width;
            imageHeight = scaleDecimal.multiply(new BigDecimal(imageHeight)).intValue();

            image = bufferedImage.getScaledInstance(imageWidth, imageHeight, bufferedImage.SCALE_SMOOTH);

            LOG.debug("指定目标图片宽度为 Width，高度等比压缩: imageWidth={},imageHeight={}, scaleWidth={}, scaleHeight={}",
                    bufferedImage.getWidth(), bufferedImage.getHeight(), imageWidth, imageHeight);
        } catch (IOException e) {
            throw new RuntimeException("图片I/O读取失败", e);
        }

        return imageToBytes(image);
    }

    /**
     * 指定目标图片高度为 Height，宽度等比压缩
     *
     * @param data
     * @param height
     * @return
     */
    public final static byte[] imageScaleHeight2(byte[] data, int height) {
        Image image = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data)) {
            BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
            int imageWidth = bufferedImage.getWidth();
            int imageHeight = bufferedImage.getHeight();

            //求出缩放比例
            BigDecimal scaleDecimal = new BigDecimal(height);
            scaleDecimal = scaleDecimal.divide(new BigDecimal(imageHeight));

            imageWidth = scaleDecimal.multiply(new BigDecimal(imageWidth)).intValue();
            imageHeight = height;

            image = bufferedImage.getScaledInstance(imageWidth, imageHeight, bufferedImage.SCALE_SMOOTH);

            LOG.debug("指定目标图片宽度为 Width，高度等比压缩: imageWidth={},imageHeight={}, scaleWidth={}, scaleHeight={}",
                    bufferedImage.getWidth(), bufferedImage.getHeight(), imageWidth, imageHeight);
        } catch (IOException e) {
            throw new RuntimeException("图片I/O读取失败", e);
        }

        return imageToBytes(image);
    }

    /**
     * 忽略原图宽高比例，指定图片宽度为 Width，高度为 Height ，强行缩放图片，可能导致目标图片变形
     *
     * @param data
     * @param width
     * @param height
     * @return
     */
    public final static byte[] imageScale(byte[] data, int width, int height) {
        Image image = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data)) {
            BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);

            image = bufferedImage.getScaledInstance(width, height, bufferedImage.SCALE_SMOOTH);

            LOG.debug("指定目标图片宽度为 Width，高度等比压缩: imageWidth={},imageHeight={}, scaleWidth={}, scaleHeight={}",
                    bufferedImage.getWidth(), bufferedImage.getHeight(), width, height);
        } catch (IOException e) {
            throw new RuntimeException("图片I/O读取失败", e);
        }

        return imageToBytes(image);
    }

    /**
     * Image to Bytes
     *
     * @param image
     * @return
     */
    private final static byte[] imageToBytes(Image image) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.drawImage(image, null, null);
        return imageToBytes(bufferedImage);
    }

    /**
     * BufferedImage to Bytes
     *
     * @param image
     * @return
     */
    private final static byte[] imageToBytes(BufferedImage image) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("image to bytes error", e);
        }
    }
}
