package com.zaw.workflow.entity;

import com.zaw.common.entity.BaseModel;
import com.zaw.workflow.enums.FlowStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 流程定义实体
 *
 * <p>
 * 表示一个「流程模板定义」，而不是流程实例。
 * <ul>
 *     <li>同一个 code 可以存在多个版本</li>
 *     <li>通过 flowVersion 区分</li>
 * </ul>
 */
@Entity
@Table(
        name = "flow",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_flow_code_version",
                        columnNames = {"code", "flow_version"}
                )
        }
)
@Getter
@Setter
public class Flow extends BaseModel {

    /**
     * 流程名称（展示用）
     *
     * <p>
     * 可修改，不作为唯一标识
     */
    @Column(nullable = false, length = 128)
    private String name;

    /**
     * 流程编码（业务唯一标识）
     *
     * <p>
     * 同一个流程在不同版本下保持不变
     */
    @Column(nullable = false, length = 64)
    private String code;

    /**
     * 流程描述
     */
    @Column(length = 512)
    private String description;

    /**
     * 流程定义版本号
     *
     * <p>
     * 从 1 开始递增，不允许修改历史版本
     */
    @Column(name = "flow_version", nullable = false)
    private Integer flowVersion;

    /**
     * 流程状态
     *
     * <p>
     * 控制流程是否可被实例化 / 使用
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private FlowStatus status;

    /**
     * 流程上下文变量名配置（JSON）
     *
     * 建议：
     * <ul>
     *     <li>JSON Schema 校验</li>
     * </ul>
     */
    @Column(name = "context_config", columnDefinition = "TEXT")
    private String contextConfig;

}
