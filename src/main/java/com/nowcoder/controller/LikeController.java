package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.News;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.NewsService;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by nowcoder on 2018/7/13.
 点赞：给资讯关联一个like的集合，把userId放进去
 踩：加一disLike的集合，把userId放进去；
 采用redis的set类型的类型，不需要DAO层，不对数据库进行操作，
 like无模式，直接从JedisAdaptor中读取：封装好的包装类工具
 afterPropertiesSet()：set之前建一个新的线程池pool = new JedisPool("localhost", 6379);
 //获取Jedis getJedis()， getJedis().get(key)；jedis.set(key, value);
 //包装，把集合放进去jedis.sadd(key, value);
 jedis.srem(key, value);jedis.sismember(key, value);
 jedis.scard(key);//集合中人数 ，验证码设置setex(key, 10, value);
 jedis.lpush(key, value);jedis.brpop(timeout, key);
 //用一个json串保存
 set(key, JSON.toJSONString(obj));

 前台的入口，把数据读取出来并加入一些入口
 news里面的count需要更新
 likeCount的形式反馈到前端
 更改数量：主页：HomeController： 更改getNews():vo.set(like,''')
        资讯详情页newsController：newsDetail也需要做相应的更改：
 int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
 */
@Controller
public class LikeController {
    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    NewsService newsService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String like(@Param("newId") int newsId) {
        //JedisAdapter.scard(0)
        long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_NEWS, newsId);
        // 更新喜欢数
        News news = newsService.getById(newsId);
        newsService.updateLikeCount(newsId, (int)likeCount);
        //发送一个事件过来
        eventProducer.fireEvent(new EventModel(EventType.LIKE)
                .setEntityOwnerId(news.getUserId())
                .setActorId(hostHolder.getUser().getId()).setEntityId(newsId));
        return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String dislike(@Param("newId") int newsId) {
        //视频里面 int userId = hostHolder.getUser().getId();
        long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_NEWS, newsId);
        // 更新喜欢数
        newsService.updateLikeCount(newsId, (int) likeCount);
        return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
    }
}
