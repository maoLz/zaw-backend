package com.zaw.workflow.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSONObject;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;

public class WorkflowExecutor {

    @Test
    public void autoDbComment(){
        String requestStr = "{\n" + //
                "  \"path\": \"/Users/zhanglizhong/rebell/ai/merchant-service/merchant-dao/rebell-dao-common/src/main/java/com/rebell/model/mysql\",\n"
                + //
                "  \"hostname\": \"http://127.0.0.1:7001\",\n" + //
                "  \"className\": \"__FILE_NAME__\"\n" + //
                "}";
List<String> files = List.of(
        "OrderAfterSaleRecord.java",
        "OrderCoupon.java",
        "OrderShop.java",
        "OrderSku.java",
        "OrderSkuProp.java",
        "OrderStatusChangeRecord.java",
        "OrderUser.java",
        "ShoppingOrder.java",
        "ShopMarketingTemplate.java",
        "ShopMarketingTemplateDraft.java",
        "ShopMarketingTemplateStats.java",
        "TemplateStore.java",
        "TemplateStoreDraft.java"
);
        for (String temp : files) {
            String rq = requestStr.replace("__FILE_NAME__", temp);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("contextJson", rq);
            jsonObject.put("businessKey", IdUtil.fastUUID());
            jsonObject.put("flowId", 19);
            HttpRequest httpRequest = HttpUtil.createPost("http://127.0.0.1:7001/flow/start");
            httpRequest.header("Content-type","application/json");
            httpRequest.body(jsonObject.toString());
            httpRequest.execute();
        }
    }

    @Test
    public void autoLogin(){
        String requestRq = "{\n" + //
                        "  \"number\": \"__number__\",\n" + //
                        "  \"account\": \"EO02@ZLZ.com\",\n" + //
                        "  \"hostname\": \"http://127.0.0.1:8080\"\n" + //
                        "}";
        for(int i =11;i<50;i++){
            HttpRequest httpRequest = HttpUtil.createPost("http://127.0.0.1:7001/flow/start");
            httpRequest.header("Content-type","application/json");
             JSONObject jsonObject = new JSONObject();
            jsonObject.put("contextJson", requestRq.replace("__number__", String.valueOf(i)));
            jsonObject.put("businessKey", IdUtil.fastUUID());
            jsonObject.put("flowId", 10);
            httpRequest.body(jsonObject.toString());
            httpRequest.execute();
        }
    }

}
