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
 EventProducer：活动生产者，相当于生产消费者设计模式中的生产者，在controller层执行一个动作之后，
                用这个类把需要异步的信息（用来发送事件中的数据（EnventModel））打包好，放入到redis队里中。
 利用json进行对象序列化和反序列化，然后json存放到Redis中，相当于存储在redis的某个 队列中
 同步的话容易受到io影响造成程序吞吐量降低，采用异步方式比较好，将活动打包，放入队列，
 后台有一个消费线程一直在拆包，执行
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























