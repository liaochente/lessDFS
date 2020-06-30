package com.liaochente.lessdfs.cache;

import com.liaochente.lessdfs.constant.LessConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 文件缓存工具类
 * 实现Least Recently Used算法
 */
public class LRUFileCaches implements IFileCaches {

    private final static Logger LOG = LoggerFactory.getLogger(LRUFileCaches.class);

    private static long cacheSize;

    private static LinkedHashMap<String, FileCacheItem> caches;

    static {
        if (LessConfig.isCache()) {
            cacheSize = LessConfig.getCacheSize();
            caches = new LinkedHashMap<String, FileCacheItem>(16, (float) 0.75, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, FileCacheItem> eldest) {
                    long currentSize = super.values().stream().mapToLong(FileCacheItem::getFileSize).sum();
                    LOG.debug("缓存存储容器空间统计 currentSize = {}, cacheSize = {}，clear = {}", currentSize, cacheSize, currentSize > cacheSize);
                    return currentSize > cacheSize;
                }
            };
        }
    }

    private LRUFileCaches() {

    }

    /**
     * 添加文件缓存
     *
     * @param key
     * @param bytes
     * @param fileExt
     */
    public final static void addCache(String key, byte[] bytes, String fileExt) {
        if (isCache()) {
            caches.put(key, new FileCacheItem(bytes, fileExt));
        }
    }

    /**
     * 删除文件缓存
     *
     * @param key
     */
    public final static void removeCache(String key) {
        if (isCache()) {
            caches.remove(key);
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
        FileCacheItem fileCacheItem = getCacheItem(key);
        if (fileCacheItem != null) {
            bytes = fileCacheItem.getFileBytes();
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
        if (isCache()) {
            fileCacheItem = caches.get(key);
        }
        return fileCacheItem;
    }

    /**
     * 判断是否启用了缓存
     *
     * @return
     */
    private final static boolean isCache() {
        return LessConfig.isCache() && cacheSize > 0;
    }
}
