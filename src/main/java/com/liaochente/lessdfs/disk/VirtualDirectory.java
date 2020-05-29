package com.liaochente.lessdfs.disk;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 虚拟目录
 * 记录虚拟目录的状态
 */
public class VirtualDirectory implements Serializable {

    /**
     * 子目录盘符
     */
    private List<StorageNode> nodes = new ArrayList<>(65536);

    /**
     * 目录绝对路径
     */
    private String absolutePath;

    /**
     * 虚拟盘符
     */
    private String drive;

    /**
     * 权重
     */
    private volatile long weight;

    /**
     * 构造函数
     * 初始化虚拟目录的真实磁盘路径
     * 初始化虚拟目录的盘符
     * 初始化虚拟目录的权重
     * 创建虚拟目录以及其二级子目录，子目录使用十六进制命名，每级256个，共65536个
     *
     * @param absolutePath
     * @param drive
     * @throws IOException
     */
    public VirtualDirectory(String absolutePath, String drive) throws IOException {
        this.absolutePath = absolutePath;
        this.drive = drive;

        Path path = Paths.get(absolutePath);
        this.weight = path.toFile().getUsableSpace();

        //create directory
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            Files.createDirectory(path);
        }

        //create storage node
        String[] drives = createDrives(1);
        for (String fid1 : drives) {
            for (String fid2 : drives) {
                StorageNode storageNode = new StorageNode(this.absolutePath, this.drive, fid1, fid2);
                nodes.add(storageNode);
            }
        }
    }

    /**
     * 创建指定数量的盘符
     *
     * @param num
     * @return
     */
    private String[] createDrives(int num) {
        String[] drives = new String[num];
        for (int i = 0; i < drives.length; i++) {
            StringBuffer sb = new StringBuffer();
            String hexStr = Integer.toHexString(i);
            if (hexStr.length() < 2) {
                sb.append(0);
            }
            sb.append(hexStr);

            drives[i] = sb.toString().toUpperCase();
        }
        return drives;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public String getDrive() {
        return drive;
    }

    public List<StorageNode> getNodes() {
        return nodes;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }
}
