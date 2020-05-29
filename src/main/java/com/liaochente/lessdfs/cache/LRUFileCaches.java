package com.liaochente.lessdfs.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUFileCaches<K, V extends FileCacheItem> extends LinkedHashMap<K, V> implements IFileCaches {

    private final static Logger LOG = LoggerFactory.getLogger(LRUFileCaches.class);

    //default size is 1024M
    private long cacheSize = 1024 * 1024;

    public LRUFileCaches(Long cacheSize) {
        super(16, (float) 0.75, true);
        if (cacheSize != null) {
            this.cacheSize = cacheSize;
        }
        LOG.debug("成功创建缓存存储容器 cacheSize = {}", cacheSize);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        long currentSize = super.values().stream().mapToLong(FileCacheItem::getFileSize).sum();
        LOG.debug("缓存存储容器空间统计 currentSize = {}, cacheSize = {}，clear = {}", currentSize, cacheSize, currentSize > cacheSize);
        return currentSize > cacheSize;
    }
}
