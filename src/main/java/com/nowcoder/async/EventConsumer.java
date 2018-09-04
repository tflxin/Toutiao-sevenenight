package com.nowcoder.async;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.async.handler.LoginExceptionHandler;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nowcoder on 2018/7/14.
 EventConsumer：消费活动，得到某个活动实体，如何将活动分发下去给相关的所有handle实现。
     package org.springframework.context;ApplicationContext
   专门的消费者，取出队列中的数据，取出来以后，反序列化为当时的线程。
   找到对应的handler，处理掉把各种handler管理好
 初始化 logger日志  把event做成一个路由表（找到实现接口的可以记录下ApplicationContext）
 当一个类实现了这个接口（ApplicationContextAware）之后，这个类就可以方便获得ApplicationContext中的所有bean。
 换句话说，就是这个类可以直接获取spring配置文件中，所有有引用到的bean对象

 消费活动，在初始化前，先得到Handler接口所有的实现类，遍历实现类。
 通过getSupportEventType得到每个实现类对应处理的活动类型。反过来记录在config哈希表中，
 config中的key是活动的类型，比如说是LIKE，COMMENT，是枚举里的成员，value是一个ArrayList的数组，
 里面存放的是各种实现方法。见代码中的。当从队列中获得一个活动时，这里用的是从右向外pop()一个活动实体。
 进行解析。这里的config.get(eventModel.getType())是一个数组，
 里面存放着所有关于这个活动要执行的实现类。遍历这个数组，开始执行实现类里的方法。
 */

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
     //可以找到对应的eventHandler
    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;
    @Autowired
    private JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        //所有的enventHandler类的（实现EH接口的）
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                //找到EnventHandler，所支持的getSupportEventTypes，然后把它注册到config中
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
                for (EventType type : eventTypes) {
                   //判断有么偶有这个类型
                    if (!config.containsKey(type)) {
                        config.put(type, new ArrayList<EventHandler>());
                    }

                    // 注册每个事件的处理函数
                    config.get(type).add(entry.getValue());
                }
            }
        }

        // 启动线程去消费事件
        //使用一个匿名类
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 从队列一直消费
                while (true) {
                    //取数据
                    String key = RedisKeyUtil.getEventQueueKey();
                    //把所有的envent  从jedisAdapter队列的右边去处理啊（0，key）0比搜地一直等待
                    List<String> messages = jedisAdapter.brpop(0, key);
                    // 第一个元素是队列名字
                    for (String message : messages) {
                        if (message.equals(key)) {
                            continue;
                        }
                        //EventModel：lpush进来的，现取出俩
                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
                        // 找到这个事件的处理handler列表
                       //先检查是否是异常的事件
                        if (!config.containsKey(eventModel.getType())) {
                            logger.error("不能识别的事件");
                            continue;
                        }
                        //找到注册好的所有的handler 然他们去处理
                        for (EventHandler handler : config.get(eventModel.getType())) {
                            handler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

/**
 * 总结：
 * 一、在controller中执行like喜欢动作的时候，会有一个EventModel模型，记录当下事件的所有信息，json序列化，存入redis缓存中。EventModel相关的属性有，活动类型EventType，触发者，出发对象id，触发对象类型，触发对象拥有者，触发现场有哪些信息要保存map。
 * 二、后台一直有一个线程在消费给队列。如何实现呢，考虑到拓展性，首先会定义一个service层的接口，EventHandler，定义其中的方法doHandler要做的事情和当前event涉及到的所有活动类型EventType。
 * 三、实现EventHandler接口，如有点赞活动，我点赞后，需要执行什么，有LikeHandler实现EventHandler接口，在doHandler中写具体要执行的方法，比如点赞后在后台可以发消息给用户。点赞相关的活动只有点赞。复杂一点的活动会涉及好几个活动类型。
 *
 * 四、最重要的是EventConsumer，实现了InitializingBean, ApplicationContextAware接口，ApplicationContextAware可以获得当前的applicationContext，之后是InitializingBean接口中的afterPropertiesSet方法，在里面配置好config。
 * 首先拿到所有实现EventHandler接口的所有类。下面起一个线程从队列中消费。
 */


























