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
 * <p>
 * 等比缩放，指定图片宽高为原来的scale%
 * 按宽缩放，指定图片宽度为原来的scale%，高度不变
 * 按高缩放，指定图片高度为原来的scale%，宽度不变
 * 指定目标图片宽度为 Width，高度等比压缩
 * 指定目标图片高度为 Height，宽度等比压缩
 * 限定缩略图的宽度和高度的最大值分别为 Width 和 Height，进行等比缩放
 * 限定缩略图的宽度和高度的最小值分别为 Width 和 Height，进行等比缩放
 * 忽略原图宽高比例，指定图片宽度为 Width，高度为 Height ，强行缩放图片，可能导致目标图片变形
 * 等比缩放图片，缩放后的图像，总像素数量不超过 Area
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

    private final static byte[] imageToBytes(Image image) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.drawImage(image, null, null);
        return imageToBytes(bufferedImage);
    }

    private final static byte[] imageToBytes(BufferedImage image) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "JPEG", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("image to bytes error", e);
        }
    }
}
