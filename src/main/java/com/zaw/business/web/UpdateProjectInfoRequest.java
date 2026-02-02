package com.zaw.business.web;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 更新项目简介请求
 */
@Getter
@Setter
public class UpdateProjectInfoRequest {

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
