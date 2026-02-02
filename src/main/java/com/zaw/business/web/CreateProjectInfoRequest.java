package com.zaw.business.web;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 创建项目简介请求
 */
@Getter
@Setter
public class CreateProjectInfoRequest {

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目路径列表
     */
    private List<String> paths;

    /**
     * 项目简介
     */
    private String description;
}
