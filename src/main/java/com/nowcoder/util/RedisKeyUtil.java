package com.nowcoder.util;

/**
 * Created by nowcoder on 2018/7/13.
 *RedisKeyUtil： BIZ：消费者模式触发事件的人儿
 *  getLikeKey
 *  getDisLikeKey
 */
public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENT = "EVENT";

    public static String getEventQueueKey() {
        return BIZ_EVENT;
    }
    //如果喜欢1,2 将两个封装为一个集合
    public static String getLikeKey(int entityId, int entityType) {
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityId, int entityType) {
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }
}











