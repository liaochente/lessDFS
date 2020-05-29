package com.liaochente.lessdfs.cache;

import com.liaochente.lessdfs.constant.LessConfig;

/**
 * 缓存控制工厂
 */
public class CacheFactory {

    private static LRUFileCaches<String, FileCacheItem> fileCaches;

    private CacheFactory() {

    }

    /**
     * 初始化
     * 创建内部存储桶
     */
    public final static void init() {
        if (LessConfig.isCache()) {
            fileCaches = new LRUFileCaches<>(LessConfig.getCacheSize());
        }
    }

    /**
     * 添加文件缓存
     *
     * @param key
     * @param bytes
     * @param fileExt
     */
    public final static void addCache(String key, byte[] bytes, String fileExt) {
        if (LessConfig.isCache()) {
            fileCaches.put(key, new FileCacheItem(bytes, fileExt));
        }
    }

    /**
     * 删除文件缓存
     *
     * @param key
     */
    public final static void removeCache(String key) {
        if (LessConfig.isCache()) {
            fileCaches.remove(key);
        }
    }

    /**
     * 获取文件缓存
     *
     * @param key
     * @return
     */
    public final static byte[] getCacheBytes(String key) {
        byte[] bytes = null;
        if (LessConfig.isCache()) {
            FileCacheItem fileCacheItem = fileCaches.get(key);
            if(fileCacheItem != null) {
                bytes = fileCacheItem.getFileBytes();
            }
        }
        return bytes;
    }

    /**
     * 获取文件缓存
     *
     * @param key
     * @return
     */
    public final static FileCacheItem getCacheItem(String key) {
        FileCacheItem fileCacheItem = null;
        if (LessConfig.isCache()) {
            fileCacheItem = fileCaches.get(key);
        }
        return fileCacheItem;
    }
}
