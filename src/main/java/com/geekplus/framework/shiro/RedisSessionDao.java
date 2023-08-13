/**
 * author     : geekplus
 * email      : geekcjj@gmail.com
 * date       : 2022/4/30 4:32 下午
 * description: 做什么的？
 */
package com.geekplus.framework.shiro;

import com.geekplus.common.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisSessionDao extends AbstractSessionDAO {

    @Value("${token.expireTime}")
    private long expireTime;

    @Resource
    private RedisTemplate redisTemplate;

//    private RedisCache cache;
//
//    @Autowired
//    private RedisCacheManager redisCacheManager;
//
//    public RedisSessionDao(RedisCacheManager redisCacheManager) {
//        this.cache = (RedisCache)redisCacheManager.getCache("session-cache-manager");
//    }

    public RedisSessionDao() {
        super();
    }

    public RedisSessionDao(long expireTime, RedisTemplate redisTemplate) {
        super();
        this.expireTime = expireTime;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        //this.saveSession(session);
        redisTemplate.opsForValue().set(Constant.LOGIN_USER_KEY+session.getId(), session, expireTime, TimeUnit.SECONDS);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        return sessionId == null ? null : (Session) redisTemplate.opsForValue().get(Constant.LOGIN_USER_KEY+sessionId);
//        if(sessionId == null){
//            log.error("session id is null");
//            return null;
//        }
//        return (Session)cache.get(sessionId.toString());
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        //this.saveSession(session);
        if (session != null && session.getId() != null) {
            session.setTimeout(expireTime * 1000);
            redisTemplate.opsForValue().set(Constant.LOGIN_USER_KEY+session.getId(), session, expireTime, TimeUnit.SECONDS);
        }
    }

    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            log.error("session or session id is null");
            return;
        }
        redisTemplate.opsForValue().getOperations().delete(Constant.LOGIN_USER_KEY+session.getId());
        //cache.evict(session.getId().toString());
    }

    @Override
    public Collection<Session> getActiveSessions() {
        //Set<Session> sessions = new HashSet<>();

        /** 这里最好别用keys，可能会阻塞
         Set<String> keys = cache.keys();
         if(keys != null && keys.size()>0){
         for(String key : keys){
         Session s = (Session)cache.get(key);
         sessions.add(s);
         }
         }
         **/
        return redisTemplate.keys(Constant.LOGIN_USER_KEY+"*");
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private void saveSession(Session session) throws UnknownSessionException{
        if (session == null || session.getId() == null) {
            log.error("session or session id is null");
            return;
        }
//        //设置过期时间
//        long expireTime = 1800000l;
//        session.setTimeout(expireTime);
        //cache.put(session.getId().toString(), session);
    }
}
