package cn.licoy.dbs.controller;

import cn.licoy.dbs.entity.Order;
import cn.licoy.dbs.repository.OrderRepository;
import cn.licoy.dbs.util.SerializeUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author licoy.cn
 * @version 2017/9/30
 */
@RestController
@RequestMapping(value = "/")
@Log4j
public class CrudController {

    @Resource
    private OrderRepository repository;
    @Resource
    private JedisPool jedisPool;

    @RequestMapping(value = "add",method = RequestMethod.GET)
    public String add(){
        Long s = System.currentTimeMillis();
        for (long i=10000;i<10020;i++){
            Order order = new Order();
            order.setOrderId(i);
            order.setUserId(i);
            repository.save(order);
        }

        /*for (long i=2000;i<4000;i++){
            Order order = new Order();
            order.setOrderId(i);
            order.setUserId(i);
            repository.save(order);
        }*/
        Long e = System.currentTimeMillis();
        System.out.println("add 共耗时 : "+(e-s)+"ms");
        return "success";
    }

    @RequestMapping(value = "/del",method = RequestMethod.GET)
    public Long del(){
        Jedis jedis = jedisPool.getResource();
        Long len = jedis.del("queryAll");
        jedis.close();
        return len;
    }

    @RequestMapping(value = "queryAll",method = RequestMethod.GET)
    public Object queryAll(){
        Long s = System.currentTimeMillis();
        Jedis jedis = jedisPool.getResource();
        if(jedis.exists("queryAll")){
            Long e = System.currentTimeMillis();
            log.info("queryAll - redis缓存已返回！共耗时 : "+(e-s)+"ms");
            Object obj = SerializeUtils.unSerialize(jedis.get("queryAll".getBytes()));
            jedis.close();
            return obj;
        }
        Iterable<Order> o  = repository.findAll();
        Long e = System.currentTimeMillis();
        jedis.set("queryAll".getBytes(),SerializeUtils.serialize(o));
        jedis.close();
        log.info("query all 共耗时 : "+(e-s)+"ms");
        return o;
    }

}
