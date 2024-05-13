package com.imwj.big.market.domain.service.raffle;

import com.imwj.big.market.domain.model.valobj.RuleTreeVO;
import com.imwj.big.market.domain.model.valobj.StrategyAwardRuleModelVo;
import com.imwj.big.market.domain.repository.IStrategyRepository;
import com.imwj.big.market.domain.service.AbstractRaffleStrategy;
import com.imwj.big.market.domain.service.armory.IStrategyDispatch;
import com.imwj.big.market.domain.service.rule.chatin.ILogicChain;
import com.imwj.big.market.domain.service.rule.chatin.factory.DefaultChainFactory;
import com.imwj.big.market.domain.service.rule.tree.factory.DefaultTreeFactory;
import com.imwj.big.market.domain.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author wj
 * @create 2024-04-25 17:38
 * @description 默认的抽奖策略实现
 */
@Slf4j
@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy {


    public DefaultRaffleStrategy(IStrategyRepository strategyRepository, IStrategyDispatch strategyDispatch, DefaultChainFactory chainFactory, DefaultTreeFactory treeFactory) {
        super(strategyRepository, strategyDispatch, chainFactory, treeFactory);
    }

    @Override
    public DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId) {
        ILogicChain logicChain = defaultChainFactory.openLogicChain(strategyId);
        return logicChain.logic(userId, strategyId);
    }

    @Override
    public DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId) {
        // 1.查询strategy_award表中对应的rule_model规则
        StrategyAwardRuleModelVo strategyAwardRuleModelVO = strategyRepository.queryStrategyAwardRuleModel(strategyId, awardId);
        if(strategyAwardRuleModelVO == null){
            return DefaultTreeFactory.StrategyAwardVO.builder().awardId(awardId).build();
        }
        // 2.查询t_rule_tree表中配置的树规则数据
        RuleTreeVO ruleTreeVO = strategyRepository.queryRuleTreeVoByTreeId(strategyAwardRuleModelVO.getRuleModels());
        if(ruleTreeVO == null){
            throw new RuntimeException("存在抽奖策略配置的规则模型 Key，未在库表 rule_tree、rule_tree_node、rule_tree_line 配置对应的规则树信息 " + strategyAwardRuleModelVO.getRuleModels());
        }
        // 3.执行规则树
        IDecisionTreeEngine decisionTreeEngine = defaultTreeFactory.openLogicTree(ruleTreeVO);
        return decisionTreeEngine.process(userId, strategyId, awardId);
    }

}
