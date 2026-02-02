package com.zaw.business.controller;

import com.zaw.business.entity.ProjectInfo;
import com.zaw.business.service.ProjectInfoService;
import com.zaw.business.web.CreateProjectInfoRequest;
import com.zaw.business.web.UpdateProjectInfoRequest;
import com.zaw.common.web.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 项目简介接口
 */
@RestController
@RequestMapping("/project-info")
@AllArgsConstructor
public class ProjectInfoController {

    private final ProjectInfoService projectInfoService;

    /**
     * 创建项目简介
     *
     * @param request 创建请求
     * @return 项目实体
     */
    @PostMapping
    public R<ProjectInfo> create(@RequestBody CreateProjectInfoRequest request) {
        return R.ok(projectInfoService.create(request));
    }

    /**
     * 更新项目简介
     *
     * @param id      项目ID
     * @param request 更新请求
     * @return 项目实体
     */
    @PutMapping("/{id}")
    public R<ProjectInfo> update(@PathVariable Long id, @RequestBody UpdateProjectInfoRequest request) {
        return R.ok(projectInfoService.update(id, request));
    }

    /**
     * 删除项目简介
     *
     * @param id 项目ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public R<ProjectInfo> delete(@PathVariable Long id) {
        return R.ok(projectInfoService.delete(id));
    }

    /**
     * 获取项目列表
     *
     * @return 项目列表
     */
    @GetMapping
    public R<List<ProjectInfo>> list() {
        return R.ok(projectInfoService.list());
    }

    /**
     * 获取项目详情
     *
     * @param id 项目ID
     * @return 项目实体
     */
    @GetMapping("/{id}")
    public R<ProjectInfo> detail(@PathVariable Long id) {
        return R.ok(projectInfoService.detail(id));
    }
}
