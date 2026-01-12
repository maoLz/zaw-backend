package com.zaw.workflow.entity;

import com.zaw.common.entity.BaseModel;
import com.zaw.workflow.enums.ExecutorType;
import com.zaw.workflow.enums.NodeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 流程节点定义
 *
 * <p>
 * 表示流程定义中的一个节点（非运行实例）
 * <ul>
 *     <li>隶属于某个流程定义（Flow）</li>
 *     <li>节点结构在流程发布后应保持不可变</li>
 * </ul>
 */
@Entity
@Table(
        name = "flow_node",
        uniqueConstraints = {
                /**
                 * 同一个流程定义下，节点 key 必须唯一
                 */
                @UniqueConstraint(
                        name = "uk_flow_node_flow_key",
                        columnNames = {"flow_id", "node_key"}
                )
        }
)
@Getter
@Setter
public class FlowNode extends BaseModel {

    /**
     * 所属流程定义 ID
     *
     * <p>
     * 指向 Flow 表的主键
     */
    @Column(name = "flow_id", nullable = false)
    private Long flowId;

    /**
     * 节点唯一标识
     *
     * <p>
     * 在同一个流程定义中唯一，不可修改
     * 用于节点跳转、条件判断、连线引用
     */
    @Column(name = "node_key", nullable = false, length = 64)
    private String nodeKey;

    /**
     * 节点名称（展示用）
     */
    @Column(name = "node_name", length = 128)
    private String nodeName;

    /**
     * 节点类型
     *
     * <p>
     * 如：START / TASK / GATEWAY / END
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "node_type", nullable = false, length = 50)
    private NodeType nodeType;

    /**
     * 节点执行人类型
     *
     * <p>
     * 如：USER / ROLE / SYSTEM
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "executor_type", length = 50)
    private ExecutorType executorType;

    /**
     * 节点配置（JSON）
     *
     * <p>
     * 存储节点参数、条件、执行配置等
     * <p>
     * 建议：
     * <ul>
     *     <li>按 nodeType 约定 JSON 结构</li>
     *     <li>发布后不允许修改</li>
     * </ul>
     */
    @Column(name = "config", columnDefinition = "json")
    private String config;

    /**
     * 节点在流程设计器中的 X 坐标
     */
    @Column(name = "position_x")
    private Integer positionX;

    /**
     * 节点在流程设计器中的 Y 坐标
     */
    @Column(name = "position_y")
    private Integer positionY;
}
