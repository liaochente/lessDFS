package com.liaochente.lessdfs.constant;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class LessConfig {

    @LessValue("less.server.data_path")
    private static String dataDir = "";

    @LessValue("less.server.storage_path")
    private static String storageDir = "";

    @LessValue("less.server.group")
    private static String group = "group0";

    @LessValue("less.server.password")
    private static String password = "123456";

    @LessValue("less.server.port")
    private static int port = 8888;

    @LessValue("less.server.bossgroup")
    private static int bossgroup = 0;

    @LessValue("less.server.workgroup")
    private static int workgroup = 0;

    @LessValue("less.server.so_backlog")
    private static int soBacklog = 1024;

    @LessValue("less.server.max_frame_length")
    private static int maxFrameLength = 102400;

    @LessValue("less.server.cache")
    private static boolean cache;

    @LessValue("less.server.cache.size")
    private static long cacheSize;

    @LessValue("less.server.http")
    private static boolean http;

    @LessValue("less.server.http.port")
    private static int httpPort;

    private LessConfig() {

    }

    /**
     * 初始化配置文件
     *
     * @throws IOException
     */
    public final static void init() throws IOException, IllegalAccessException {
        Map<String, String> configMap = loadConfig();
        autowiredConfig(configMap);
    }

    /**
     * 加载配置文件，解析成Key-Value形式返回
     *
     * @return
     * @throws IOException
     */
    private final static Map<String, String> loadConfig() throws IOException {
        List<String> configLines = new ArrayList<>();
        try (
                InputStream inputStream = LessConfig.class.getClassLoader().getResourceAsStream("less.conf");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                configLines.add(line);
            }
        }

        Map<String, String> configMap = configLines.stream()
                .filter((configLine) -> configLine.trim().indexOf("#") == -1)
                .map((configLine) -> configLine.split("="))
                .filter((kvs) -> kvs.length > 1)
                .collect(Collectors.toMap((kv) -> kv[0], kv -> kv[1]));
        return configMap;
    }

    /**
     * 自动填充配置属性
     *
     * @param configMap
     * @throws IllegalAccessException
     */
    private final static void autowiredConfig(Map<String, String> configMap) throws IllegalAccessException {
        Field[] fields = LessConfig.class.getDeclaredFields();
        if (fields != null) {
            for (Field field : fields) {
                LessValue lessValue = field.getAnnotation(LessValue.class);
                if (lessValue != null) {
                    String configValue = configMap.get(lessValue.value());
                    if (configValue != null && !"".equals(configValue)) {
                        field.setAccessible(true);
                        if ("int".equals(field.getType().getName())) {
                            field.set(null, Integer.parseInt(configValue));
                        } else if ("boolean".equals(field.getType().getName())) {
                            field.set(null, Boolean.parseBoolean(configValue));
                        }  else if ("long".equals(field.getType().getName())) {
                            field.set(null, Long.parseLong(configValue));
                        } else {
                            field.set(null, configValue);
                        }
                    }
                }
            }
        }
    }

    public static boolean isHttp() {
        return http;
    }

    public static int getHttpPort() {
        return httpPort;
    }

    public static String getStorageDir() {
        return storageDir;
    }

    public static String getGroup() {
        return group;
    }

    public static String getPassword() {
        return password;
    }

    public static int getPort() {
        return port;
    }

    public static int getBossgroup() {
        return bossgroup;
    }

    public static int getWorkgroup() {
        return workgroup;
    }

    public static int getSoBacklog() {
        return soBacklog;
    }

    public static int getMaxFrameLength() {
        return maxFrameLength;
    }

    public static boolean isCache() {
        return cache;
    }

    public static long getCacheSize() {
        return cacheSize;
    }
}
