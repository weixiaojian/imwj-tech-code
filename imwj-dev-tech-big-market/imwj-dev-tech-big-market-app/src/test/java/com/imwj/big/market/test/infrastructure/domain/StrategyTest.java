package com.imwj.big.market.test.infrastructure.domain;

import com.imwj.big.market.domain.service.armory.IStrategyArmory;
import com.imwj.big.market.infrastructure.persistent.redis.RedissonService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RMap;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author wj
 * @create 2024-04-24 16:41
 * @description 领域测试
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyTest {

    @Resource
    private RedissonService redisService;
    @Resource
    private IStrategyArmory strategyArmory;

    /**
     * redis测试
     */
    @Test
    public void test_map() {
        RMap<Integer, Integer> map = redisService.getMap("strategy_id_100001");
        map.put(1, 101);
        map.put(2, 102);
        map.put(3, 103);
        map.put(4, 104);
        map.put(5, 105);
        map.put(6, 106);
        map.put(7, 107);
        map.put(8, 108);
        map.put(9, 109);
        map.put(10, 1010);

        log.info("测试结果：{}", redisService.getMap("strategy_id_100001").get(3));
    }

    /**
     * 数据库strategy_award表装配到redis测试
     */
    @Test
    public void test_StrategyAward() {
        strategyArmory.assembleLotteryStrategy(100001L);
        strategyArmory.assembleLotteryStrategy(100002L);
    }

    /**
     * 数据库strategy_award表装配成功后抽奖测试
     */
    @Test
    public void test_getRandomAwardId() {
        Integer randomAwardId = strategyArmory.getRandomAwardId(100001L);
        log.info("奖品id：{}", randomAwardId);
    }
}
