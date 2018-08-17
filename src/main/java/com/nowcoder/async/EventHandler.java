package com.nowcoder.async;

import java.util.List;

/**
 * Created by nowcoder on 2018/7/14.
 抽象成接口处理，处理model，以及关注 getSupportEventTyp
 */
public interface EventHandler {
    void doHandle(EventModel model);
    List<EventType> getSupportEventTypes();
}
