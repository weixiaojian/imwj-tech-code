package com.imwj.big.market.domain.strategy.service.rule.tree.factory.engine.impl;

import com.imwj.big.market.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.imwj.big.market.domain.strategy.model.valobj.RuleTreeNodeLineVO;
import com.imwj.big.market.domain.strategy.model.valobj.RuleTreeNodeVO;
import com.imwj.big.market.domain.strategy.model.valobj.RuleTreeVO;
import com.imwj.big.market.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.imwj.big.market.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.imwj.big.market.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author wj
 * @create 2024-05-09 17:48
 * @description 决策树引擎实现
 */
@Slf4j
public class DescisionTreeEngine implements IDecisionTreeEngine {

    private final Map<String, ILogicTreeNode> logicTreeNodeMap;

    private final RuleTreeVO ruleTreeVO;

    public DescisionTreeEngine(Map<String, ILogicTreeNode> logicTreeNodeMap, RuleTreeVO ruleTreeVO) {
        this.logicTreeNodeMap = logicTreeNodeMap;
        this.ruleTreeVO = ruleTreeVO;
    }

    @Override
    public DefaultTreeFactory.StrategyAwardVO process(String userId, Long strategyId, Integer awardId) {
        DefaultTreeFactory.StrategyAwardVO strategyAwardVO = null;
        // 获取基础信息（根节点、节点集合）
        String nextNode = ruleTreeVO.getTreeRootRuleNode();
        Map<String, RuleTreeNodeVO> treeNodeMap = ruleTreeVO.getTreeNodeMap();

        // 获取当前操作节点（根节点）
        RuleTreeNodeVO ruleTreeNode = treeNodeMap.get(nextNode);

        // 循环决策树节点
        while (nextNode != null){
            // 获取决策节点
            ILogicTreeNode logicTreeNode = logicTreeNodeMap.get(ruleTreeNode.getRuleKey());
            String ruleValue = ruleTreeNode.getRuleValue();

            // 决策节点计算
            DefaultTreeFactory.TreeActionEntity logicEntity = logicTreeNode.logic(userId, strategyId, awardId, ruleValue);
            RuleLogicCheckTypeVO ruleLogicCheckTypeVo = logicEntity.getRuleLogicCheckType();
            strategyAwardVO = logicEntity.getStrategyAwardVO();
            log.info("决策树引擎【{}】treeId:{} node:{} code:{}", ruleTreeVO.getTreeName(), ruleTreeVO.getTreeId(), nextNode, ruleLogicCheckTypeVo.getCode());

            nextNode = nextNode(ruleLogicCheckTypeVo.getCode(), ruleTreeNode.getTreeNodeLineVOList());
            ruleTreeNode = treeNodeMap.get(nextNode);
        }
        // 返回最终结果
        return strategyAwardVO;
    }

    /**
     * 获取下一个节点key
     * @param matterValue
     * @param ruleTreeNodeLineVOList
     * @return
     */
    private String nextNode(String matterValue, List<RuleTreeNodeLineVO> ruleTreeNodeLineVOList){
        if(ruleTreeNodeLineVOList == null || ruleTreeNodeLineVOList.isEmpty()){
            return null;
        }
        for(RuleTreeNodeLineVO nodeLine : ruleTreeNodeLineVOList){
            // 核心：遍历所有的子节点，找到符合条件的下一节点（当前节点RuleLockLogicTreeNode是放行状态 则找子节点中放行的节点ruleLimitValue，当前节点是拦截状态则找子节点中的拦截的节点）
            if(decisionLogic(matterValue, nodeLine)){
                return nodeLine.getRuleNodeTo();
            }
        }
        return null;
    }

    /**
     * 判断节点规则值是否满足条件
     * @param matterValue 拦截/放行的code
     * @param nodeLine 子节点
     * @return
     */
    private boolean decisionLogic(String matterValue, RuleTreeNodeLineVO nodeLine){
        switch (nodeLine.getRuleLimitType()){
            case EQUAL:
                return matterValue.equals(nodeLine.getRuleLimitValue().getCode());
            case GT:
            case LT:
            case GE:
            case LE:
            default:
                return false;
        }
    }
}
