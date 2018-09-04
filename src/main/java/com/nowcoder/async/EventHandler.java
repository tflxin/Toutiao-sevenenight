package com.nowcoder.async;

import java.util.List;

/**
 * Created by nowcoder on 2018/7/14.
 EventHandler接口：对于活动定义他的行为
 抽象成接口处理，处理model，以及关注 getSupportEventTyp
 */
public interface EventHandler {
    void doHandle(EventModel model);//定义接口，针对活动要执行的动作
    List<EventType> getSupportEventTypes();//关注的活动类型有哪些
}
