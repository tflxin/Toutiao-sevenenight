package com.nowcoder.async;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Created by nowcoder on 2018/7/14.
 用来发送事件中的数据（EnventModel），把数据序列化后放到某个序列里面，然后放到Redis的某个 队列中
 */
@Service
public class EventProducer {

    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel) {
        try {
            //先序列化
            String json = JSONObject.toJSONString(eventModel);
            //使得队列有一个单独的名字
            String key = RedisKeyUtil.getEventQueueKey();
           //发布到队列中
            jedisAdapter.lpush(key, json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}























