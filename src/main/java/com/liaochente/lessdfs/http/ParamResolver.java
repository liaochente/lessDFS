package com.liaochente.lessdfs.http;

/**
 * http请求参数解析器
 */
public class ParamResolver {
    private String httpUri;

    private String fileUri;

    /**
     * 是否开启附件模式
     */
    private boolean attachment;

    /**
     * 是否开启流光溢彩功能
     */
    private boolean ambilight;

    /**
     * 流光溢彩-处理类型
     */
    private AmbilightTypeEnum ambilightTypeEnum;

    /**
     * 缩放百分比scale%
     */
    private int scale;

    /**
     * 指定宽度
     */
    private int width;

    /**
     * 指定高度
     */
    private int height;

    public ParamResolver(String httpUri) {
        this.httpUri = httpUri;

        if (httpUri.indexOf("/") == 0) {
            httpUri = httpUri.substring(1);
        }
        String[] httpUris = httpUri.split("\\?");

        this.fileUri = httpUris[0];

        if (httpUris.length > 1) {
            String command = httpUris[1];
            ambilight = command.indexOf("ambilight") > -1;

            if (ambilight) {
                String[] params = httpUris[1].split("\\/");

                ambilightTypeEnum = AmbilightTypeEnum.convert(params[0] + "/" + params[1] + "/");

                if (params.length < 4) {
                    scale = Integer.parseInt(params[2]);
                } else {
                    width = Integer.parseInt(params[2]);
                    height = Integer.parseInt(params[3]);
                }
            } else {
                attachment = command.indexOf("attachment") > -1;
            }
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        sb.append("httpUri=");
        sb.append(httpUri);
        sb.append(",");
        sb.append("ambilight=");
        sb.append(ambilight);
        sb.append(",");
        sb.append("attachment=");
        sb.append(attachment);
        sb.append(",");
        sb.append("scale=");
        sb.append(scale);
        sb.append(",");
        sb.append("width=");
        sb.append(width);
        sb.append(",");
        sb.append("height=");
        sb.append(height);
        sb.append("]");
        return sb.toString();
    }

    public String getHttpUri() {
        return httpUri;
    }

    public String getFileUri() {
        return fileUri;
    }

    public boolean isAttachment() {
        return attachment;
    }

    public boolean isAmbilight() {
        return ambilight;
    }

    public AmbilightTypeEnum getAmbilightTypeEnum() {
        return ambilightTypeEnum;
    }

    public int getScale() {
        return scale;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
