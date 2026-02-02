package com.zaw.business.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaw.business.entity.ProjectInfo;
import com.zaw.business.web.CreateProjectInfoRequest;
import com.zaw.business.web.UpdateProjectInfoRequest;
import com.zaw.common.exception.BizException;

import java.util.List;

/**
 * 项目简介转换工具
 */
public class ProjectInfoMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 创建请求转实体
     *
     * @param request 创建请求
     * @return 项目实体
     */
    public static ProjectInfo toEntity(CreateProjectInfoRequest request) {
        ProjectInfo entity = new ProjectInfo();
        entity.setName(request.getName());
        entity.setPaths(toJsonPaths(request.getPaths()));
        entity.setDescription(request.getDescription());
        return entity;
    }

    /**
     * 更新请求写入实体
     *
     * @param entity  项目实体
     * @param request 更新请求
     */
    public static void updateEntity(ProjectInfo entity, UpdateProjectInfoRequest request) {
        entity.setName(request.getName());
        entity.setPaths(toJsonPaths(request.getPaths()));
        entity.setDescription(request.getDescription());
    }

    /**
     * 路径列表转 JSON
     *
     * @param paths 路径列表
     * @return JSON 字符串
     */
    public static String toJsonPaths(List<String> paths) {
        if (paths == null) {
            return "[]";
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(paths);
        } catch (JsonProcessingException e) {
            throw new BizException("项目路径格式错误");
        }
    }
}
