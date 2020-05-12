package com.liaochente.lessdfs.constant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LessConfig {

    /**
     * 文件存储根目录
     */
    public final static String FILE_ROOT_PATH = "/Users/liaochente/lessdfs_data/";

    /**
     * 分组
     */
    public final static String GROUP = "group0/";

    /**
     * 通讯密码
     */
    public final static String PASSWORD = "123456";

    public final static Map<String, Map<String, String>> FILE_INDEX_MAP = new ConcurrentHashMap<>();

}
