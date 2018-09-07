package com.nowcoder.async;

/**
 * Created by nowcoder on 2018/7/14.
 EventType，获得活动的类型，可以有点赞，评论，登录等待
 表示刚刚发生了什么事件

 异步架构（优先队列，消费者线程，找到相应handler），若该部分异常，biz==eventProduceren仍然可以用

 优点：把复杂的业务处理的流程切割开，对及时反馈的数据进行反馈，对滞后的更新的数据全部通过事件的处理函数慢慢更新
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












