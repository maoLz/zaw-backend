package com.zaw.business.service;

import com.zaw.business.entity.ProjectInfo;
import com.zaw.business.mapper.ProjectInfoMapper;
import com.zaw.business.repository.ProjectInfoRepository;
import com.zaw.business.web.CreateProjectInfoRequest;
import com.zaw.business.web.UpdateProjectInfoRequest;
import com.zaw.common.exception.BizException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 项目简介服务
 */
@Service
@AllArgsConstructor
public class ProjectInfoService {

    private final ProjectInfoRepository projectInfoRepository;

    /**
     * 创建项目简介
     *
     * @param request 创建请求
     * @return 项目实体
     */
    public ProjectInfo create(CreateProjectInfoRequest request) {
        // 第一步：构建项目实体
        ProjectInfo entity = ProjectInfoMapper.toEntity(request);
        // 第二步：保存项目实体
        return projectInfoRepository.save(entity);
    }

    /**
     * 更新项目简介
     *
     * @param id      项目ID
     * @param request 更新请求
     * @return 项目实体
     */
    public ProjectInfo update(Long id, UpdateProjectInfoRequest request) {
        // 第一步：查询项目实体
        ProjectInfo entity = projectInfoRepository.findById(id).orElse(null);
        if (entity == null) {
            // 项目不存在时抛出业务异常
            throw new BizException("项目不存在");
        } else {
            // 项目存在时更新实体
            ProjectInfoMapper.updateEntity(entity, request);
        }
        // 第二步：保存更新结果
        return projectInfoRepository.save(entity);
    }

    /**
     * 删除项目简介
     *
     * @param id 项目ID
     * @return 删除结果
     */
    public ProjectInfo delete(Long id) {
        // 第一步：查询项目实体
        ProjectInfo entity = projectInfoRepository.findById(id).orElse(null);
        if (entity == null) {
            // 项目不存在时抛出业务异常
            throw new BizException("项目不存在");
        } else {
            // 项目存在时继续删除
            projectInfoRepository.delete(entity);
        }
        return entity;
    }

    /**
     * 获取项目列表
     *
     * @return 项目列表
     */
    public List<ProjectInfo> list() {
        // 第一步：查询全部项目
        return projectInfoRepository.findAll();
    }

    /**
     * 获取项目详情
     *
     * @param id 项目ID
     * @return 项目实体
     */
    public ProjectInfo detail(Long id) {
        // 第一步：查询项目实体
        ProjectInfo entity = projectInfoRepository.findById(id).orElse(null);
        if (entity == null) {
            // 项目不存在时抛出业务异常
            throw new BizException("项目不存在");
        } else {
            // 项目存在时返回实体
            return entity;
        }
    }
}
