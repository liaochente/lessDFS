package com.liaochente.lessdfs.disk;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 虚拟目录
 */
public class VirtualDirectory implements Serializable {

    /**
     * 子目录盘符
     */
    private final static String[] SUB_DIRECTORYS = new String[1];

    /**
     * 虚拟目录真实路径
     */
    private Path realPath;

    /**
     * 虚拟盘符
     */
    private String virtualPath;

    /**
     * 权重
     */
    private volatile long weight;

    public VirtualDirectory(String realPath, String virtualPath) throws IOException {
        this.realPath = Paths.get(realPath);
        this.virtualPath = virtualPath;
        this.weight = this.realPath.toFile().getUsableSpace();

        if (!Files.exists(this.realPath, LinkOption.NOFOLLOW_LINKS)) {
            Files.createDirectory(this.realPath);
        }

        //init subDirectorys
        for (int i = 0; i < SUB_DIRECTORYS.length; i++) {
            StringBuffer sb = new StringBuffer();
            String hexStr = Integer.toHexString(i);
            if (hexStr.length() < 2) {
                sb.append(0);
            }
            sb.append(hexStr);

            SUB_DIRECTORYS[i] = sb.toString().toUpperCase();
        }

        //create sub directorys
        for (String firstFID : SUB_DIRECTORYS) {
            for (String secondFID : SUB_DIRECTORYS) {
                Path secondFIDPath = Paths.get(this.realPath.toString() + "/" + firstFID + "/" + secondFID);
                if (!Files.exists(secondFIDPath, LinkOption.NOFOLLOW_LINKS)) {
                    Files.createDirectories(secondFIDPath);
                }
            }
        }
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
