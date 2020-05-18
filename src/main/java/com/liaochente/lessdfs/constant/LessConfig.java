package com.liaochente.lessdfs.constant;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class LessConfig {
    public final static ThreadPoolExecutor GLOBAL_THREAD_POOL = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(64));

    public final static ScheduledThreadPoolExecutor GLOBAL_SCHEDULED_THREAD_POOL = new ScheduledThreadPoolExecutor(8);

    public final static int MAGIC_CODE = 0x294;

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

    private final static List<VirtualDirectory> VIRTUAL_DIRECTORIES = new ArrayList<>();

    public static class VirtualDirectory implements Serializable {
        private Path realPath;

        private String virtualPath;

        private volatile long weight;

        public VirtualDirectory(Path realPath, String virtualPath, long weight) {
            this.realPath = realPath;
            this.virtualPath = virtualPath;
            this.weight = weight;
        }

        public Path getRealPath() {
            return realPath;
        }

        public void setRealPath(Path realPath) {
            this.realPath = realPath;
        }

        public String getVirtualPath() {
            return virtualPath;
        }

        public void setVirtualPath(String virtualPath) {
            this.virtualPath = virtualPath;
        }

        public long getWeight() {
            return weight;
        }

        public void setWeight(long weight) {
            this.weight = weight;
        }
    }

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
        autowiredVirtualDirectory();
    }

    /**
     * 加载配置文件，解析成Key-Value形式返回
     *
     * @return
     * @throws IOException
     */
    private final static Map<String, String> loadConfig() throws IOException {
        String path = LessConfig.class.getClassLoader().getResource("less.conf").getPath();
        List<String> configLines = Files.readAllLines(Paths.get(path));
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
                        } else {
                            field.set(null, configValue);
                        }
                    }
                }
            }
        }
    }

    /**
     * 初始化虚拟目录信息
     * 1.生成虚拟目录到实际存储目录的映射关系
     * 2.启动定时任务轮询各个目录的可用空间大小，然后进行排序方便后续使用
     */
    private final static void autowiredVirtualDirectory() throws IOException {
        //初始化实际存储目录和虚拟目录之间的关系
        String[] storageRealPaths = storageDir.split(",");

        int index = 0;
        for (String storageRealPath : storageRealPaths) {
            Path path = Paths.get(storageRealPath);
            if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
                Files.createDirectory(path);
            }

            VirtualDirectory entry = new VirtualDirectory(path, "L" + index, path.toFile().getUsableSpace());
            VIRTUAL_DIRECTORIES.add(entry);
        }

        if (VIRTUAL_DIRECTORIES.size() > 1) {
            GLOBAL_SCHEDULED_THREAD_POOL.scheduleAtFixedRate(() -> {
                VIRTUAL_DIRECTORIES.forEach((e) -> {
                    Path path = e.getRealPath();
                    e.setWeight(path.toFile().getUsableSpace());
                });

                VIRTUAL_DIRECTORIES.sort((o1, o2) -> (int) (o1.getWeight() - o2.getWeight()));
            }, 60, 60, TimeUnit.SECONDS);
        }
    }

    /**
     * 获得一个可存储的虚拟目录
     *
     * @return
     */
    public static VirtualDirectory getVirtualDirectory() {
        return VIRTUAL_DIRECTORIES.get(0);
    }

    /**
     *
     *
     * @param path
     * @return
     */
    public static String getFileRealPath(String path) {
        String fileName = path.substring(path.indexOf("/") + 1);
        String virtualPath = path.substring(0, path.indexOf("/"));
        VirtualDirectory virtualDirectory = VIRTUAL_DIRECTORIES.stream().filter((e) -> virtualPath.equals(e.getVirtualPath())).collect(Collectors.toList()).get(0);
        return virtualDirectory.getRealPath().toString() + "/" + fileName;
    }

    public static String getDataDir() {
        return dataDir;
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


}
