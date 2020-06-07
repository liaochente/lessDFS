package com.liaochente.lessdfs.http;

/**
 * 流光溢彩-处理类型枚举
 */
public enum AmbilightTypeEnum {
    SCALE_MODE_ER("ambilight/er/", "等比缩放"),
    SCALE_MODE_ER_W("ambilight/erw/", "宽度缩放，高度不变"),
    SCALE_MODE_ER_H("ambilight/erh/", "高度缩放，宽度不变"),
    SCALE_MODE_W("ambilight/w/", "指定宽度，高度等比缩放"),
    SCALE_MODE_H("ambilight/h/", "指定高度，宽度等比缩放"),
    SCALE_MODE_WH("ambilight/wh/", "指定宽度和高度");

    AmbilightTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public final static AmbilightTypeEnum convert(String type) {
        for (AmbilightTypeEnum typeEnum : AmbilightTypeEnum.values()) {
            if (typeEnum.type.equals(type)) {
                return typeEnum;
            }
        }
        return null;
    }

    private String type;

    private String name;

}
