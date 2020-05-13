package com.liaochente.lessdfs.constant;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LessConfig {

    public final static Map<String, Map<String, String>> FILE_INDEX_MAP = new ConcurrentHashMap<>();

    /**
     * 初始化配置文件
     *
     * @throws IOException
     */
    public final static void init() throws IOException {
        String path = LessConfig.class.getClassLoader().getResource("less.conf").getPath();
        List<String> configLines = Files.readAllLines(Paths.get(path));
        Map<String, String> configMap = configLines.stream()
                .filter((configLine) -> configLine.trim().indexOf("#") == -1)
                .map((configLine) -> configLine.split("="))
                .filter((kvs) -> kvs.length > 1)
                .collect(Collectors.toMap((kv) -> kv[0], kv -> kv[1]));

        if (configLines != null && configLines.size() > 0) {
            Field[] fields = LessConfig.class.getDeclaredFields();
            if (fields != null) {
                for (Field field : fields) {
                    LessValue lessValue = field.getAnnotation(LessValue.class);
                    if (lessValue != null) {
                        String configValue = configMap.get(lessValue.value());
                        if (configValue != null && !"".equals(configValue)) {
                            field.setAccessible(true);
                            try {
                                if ("int".equals(field.getType().getName())) {
                                    field.set(null, Integer.parseInt(configValue));
                                } else {
                                    field.set(null, configValue);
                                }

                                if ("dataDir".equals(field.getName())) {
                                    LessConfig.storageDir = "/" + LessConfig.dataDir + "/storege/";
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    @LessValue("less.server.datadir")
    public static String dataDir = "";

    public static String storageDir = "/" + dataDir + "/storege/";

    @LessValue("less.server.group")
    public static String group = "group0";

    @LessValue("less.server.password")
    public static String password = "123456";

    @LessValue("less.server.port")
    public static int port = 8888;

    @LessValue("less.server.bossgroup")
    public static int bossgroup = 0;

    @LessValue("less.server.workgroup")
    public static int workgroup = 0;

    @LessValue("less.server.so_backlog")
    public static int soBacklog = 1024;

    @LessValue("less.server.max_frame_length")
    public static int maxFrameLength = 102400;

}
