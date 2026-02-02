package com.zaw.business.entity;

import com.zaw.common.entity.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 项目简介实体
 */
@Entity
@Table(name = "project_info")
@Getter
@Setter
public class ProjectInfo extends BaseModel {

    /**
     * 项目名称
     */
    @Column(nullable = false, length = 128)
    private String name;

    /**
     * 项目路径列表（JSON）
     */
    @Column(name = "project_paths", nullable = false, columnDefinition = "TEXT")
    private String paths;

    /**
     * 项目简介
     */
    @Column(columnDefinition = "TEXT")
    private String description;
}
