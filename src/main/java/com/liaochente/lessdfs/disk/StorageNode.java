package com.liaochente.lessdfs.disk;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 存储节点
 * 记录存储节点的状态，每一个实例代表一个存储节点
 */
public class StorageNode implements Serializable {

    private String virtualDirectoryDrive;

    /**
     * 父级目录盘符
     */
    private String parentDrive;

    /**
     * 目录盘符
     */
    private String drive;

    /**
     * 目录绝对路径
     */
    private String absolutePath;

    /**
     *
     * @param virtualDirectoryPath 所属虚拟目录的绝对路径
     * @param parentDrive 上级盘符
     * @param drive 盘符
     * @throws IOException
     */
    public StorageNode(String virtualDirectoryPath, String virtualDirectoryDrive, String parentDrive, String drive) throws IOException {
        this.virtualDirectoryDrive = virtualDirectoryDrive;
        this.parentDrive = parentDrive;
        this.drive = drive;
        this.absolutePath = virtualDirectoryPath + "/" + parentDrive + "/" + drive;

        Path path = Paths.get(absolutePath);
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            Files.createDirectories(path);
        }
    }

    public String getVirtualDirectoryDrive() {
        return virtualDirectoryDrive;
    }

    public String getParentDrive() {
        return parentDrive;
    }

    public String getDrive() {
        return drive;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }
}
