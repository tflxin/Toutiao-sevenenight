package com.nowcoder.async;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nowcoder on 2018/7/14.
 EnventModel表示当时发生的事件的一些数据信息，都打包在enventModel里面
 */
public class EventModel {
   //发生了什么事情
    private EventType type;
    private int actorId;
    private int entityId;
    private int entityType;
    //对象拥有者
    private int entityOwnerId;
   //通过map表示带带触发的线程的东西
    private Map<String, String> exts = new HashMap<>();

    public Map<String, String> getExts() {
        return exts;
    }
    public EventModel() {

    }
    public EventModel(EventType type) {
      //默认的构造函数
        this.type = type;
    }

    public String getExt(String name) {

        return exts.get(name);
    }
//所有的set中return this 为了方便
    public EventModel setExt(String name, String value) {
        exts.put(name, value);
        return this;
    }

    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }
}






















