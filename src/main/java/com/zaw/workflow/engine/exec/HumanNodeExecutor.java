package com.zaw.workflow.engine.exec;

import com.zaw.workflow.entity.*;
import com.zaw.workflow.enums.ActionType;
import com.zaw.workflow.enums.ExecutorStatus;

import lombok.extern.slf4j.Slf4j;

import com.zaw.workflow.dto.FlowExecContext;
import com.alibaba.fastjson2.JSON;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class HumanNodeExecutor implements NodeExecutor {


    @Override
    public String execute(FlowExecContext context) throws Exception {
        FlowNode node = context.getCurrentNode();
        FlowInstance instance = context.getFlowInstance();
        log.info("[humanNode][execute]node:{},instance:{}",node.getNodeName(),instance.getBusinessKey());
        NodeExecutorRecord record = context.getCurrentRecord();
        if(record.getStatus() == ExecutorStatus.RUNNING){
            record.setStatus(ExecutorStatus.WAITING);
        }else if(record.getStatus() == ExecutorStatus.WAITING){
            Map<String, Object> humanParam = context.getRequest().getHumanParam();
            String action = (String) humanParam.get("action");
            ActionType actionType = ActionType.valueOf(action);
            record.setAction(actionType);
            if(actionType == ActionType.GOTO){
                record.setNextNodeId((Long) humanParam.get("nextNodeId"));
            }
            record.setStatus(ExecutorStatus.SUCCESS);
            return JSON.toJSONString(context.getRequest().getHumanParam());

        }
        return "";
    }



}
