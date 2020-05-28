package com.liaochente.lessdfs.disk;

import com.liaochente.lessdfs.constant.LessConfig;
import com.liaochente.lessdfs.util.SystemUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 虚拟目录工厂
 * 负责初始化虚拟目录相关信息、提供计算文件与虚拟目录属性相关的能力
 */
public class VirtualDirectoryFactory {

    /**
     * 定时任务
     */
    private final static ScheduledThreadPoolExecutor GLOBAL_SCHEDULED_THREAD_POOL = new ScheduledThreadPoolExecutor(8);

    /**
     * 当前所有可用存储节点
     */
    private final static List<VirtualDirectory> VIRTUAL_DIRECTORIES = new ArrayList<>();

    /**
     * 初始化虚拟目录工厂
     * 1.生成虚拟目录到实际存储目录的映射关系
     * 2.启动定时任务轮询各个目录的可用空间大小，然后进行排序方便后续使用
     */
    public final static void init() throws IOException {
        //初始化实际存储目录和虚拟目录之间的关系
        String[] storageRealPaths = LessConfig.getStorageDir().split(",");

        int index = 0;
        for (String storageRealPath : storageRealPaths) {
            VirtualDirectory virtualDirectory = new VirtualDirectory(storageRealPath, "L" + index);
            VirtualDirectoryFactory.addVirtualDirectory(virtualDirectory);
        }

        if (VIRTUAL_DIRECTORIES.size() > 1) {
            GLOBAL_SCHEDULED_THREAD_POOL.scheduleAtFixedRate(() -> {
                VIRTUAL_DIRECTORIES.forEach((e) -> {
                    Path path = Paths.get(e.getAbsolutePath());
                    e.setWeight(path.toFile().getUsableSpace());
                });

                VIRTUAL_DIRECTORIES.sort((o1, o2) -> (int) (o1.getWeight() - o2.getWeight()));
            }, 60, 60, TimeUnit.SECONDS);
        }
    }

    /**
     * 获取文件存储的真实路径
     * file key example: L0/00/00/abcdasdadsad
     *
     * @param fileKey
     * @return
     */
    public static String searchFile(String fileKey) {
        int flag = fileKey.indexOf("/");
        String relativePath = fileKey.substring(flag + 1);
        String virtualDrive = fileKey.substring(0, flag);

        VirtualDirectory virtualDirectory = VIRTUAL_DIRECTORIES.stream().filter((e) -> virtualDrive.equals(e.getDrive())).collect(Collectors.toList()).get(0);
        return virtualDirectory.getAbsolutePath() + "/" + relativePath;
    }

    /**
     * 添加存储节点
     *
     * @param virtualDirectory
     */
    public final static void addVirtualDirectory(VirtualDirectory virtualDirectory) {
        VIRTUAL_DIRECTORIES.add(virtualDirectory);
    }

    /**
     * 获取最佳存储节点
     *
     * @return
     */
    public static StorageNode getBestStorageNode(byte[] fileBytes, String fileExt) {
        String fileMD5 = SystemUtils.md5String(fileBytes);
        VirtualDirectory virtualDirectory = getVirtualDirectory();
        List<StorageNode> storageNodes = virtualDirectory.getNodes();
        return storageNodes.get(fileMD5.hashCode() % storageNodes.size());
    }

    /**
     * 获得一个可用的存储节点
     *
     * @return
     */
    private static VirtualDirectory getVirtualDirectory() {
        return VIRTUAL_DIRECTORIES.get(0);
    }
}
