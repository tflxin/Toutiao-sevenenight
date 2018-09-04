package com.nowcoder.async;

/**
 * Created by nowcoder on 2018/7/14.
 EventType，获得活动的类型，可以有点赞，评论，登录等待
 表示刚刚发生了什么事件
 */
public enum EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3);

    private int value;
    EventType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}












