package com.zaw.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

/**
 * 基础实体抽象类
 *
 * <p>
 * 所有 JPA Entity 的父类，统一提供：
 * <ul>
 *     <li>主键 ID</li>
 *     <li>业务唯一标识 UUID</li>
 *     <li>创建时间 / 修改时间</li>
 *     <li>乐观锁版本号</li>
 * </ul>
 *
 * <p>
 * 设计原则：
 * <ul>
 *     <li>仅用于 JPA 实体继承，不单独映射成表</li>
 *     <li>不参与 equals / hashCode</li>
 *     <li>不作为 DTO / VO 使用</li>
 * </ul>
 */
@MappedSuperclass
@Getter
@Setter
@FieldNameConstants
public abstract class BaseModel {

    /**
     * 数据库主键
     *
     * <p>
     * 使用数据库自增 ID：
     * <ul>
     *     <li>简单稳定</li>
     *     <li>适合 MySQL</li>
     * </ul>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 业务唯一标识
     *
     * <p>
     * 用于：
     * <ul>
     *     <li>对外暴露</li>
     *     <li>日志 / 链路追踪</li>
     *     <li>避免直接暴露数据库主键</li>
     * </ul>
     *
     * <p>
     * 注意：不作为主键、不参与 JPA 关系映射
     */
    @Column(name = "uuid", unique = true, nullable = false, length = 64)
    private String uuid;

    /**
     * 创建时间
     *
     * <p>
     * 由 Hibernate 在 insert 时自动填充
     *
     * <p>
     * ⚠ 推荐升级为 LocalDateTime / OffsetDateTime
     */
    @CreationTimestamp
    @Column(name = "create_date", updatable = false)
    private Date createDate;

    /**
     * 修改时间
     *
     * <p>
     * 每次 update 时由 Hibernate 自动更新
     *
     * <p>
     * ⚠ 推荐升级为 LocalDateTime / OffsetDateTime
     */
    @UpdateTimestamp
    @Column(name = "modify_date")
    private Date modifyDate;

    /**
     * 乐观锁版本号
     *
     * <p>
     * 用于防止并发更新覆盖：
     * <ul>
     *     <li>update 时会自动校验 version</li>
     *     <li>版本不一致抛 OptimisticLockException</li>
     * </ul>
     */
    @Version
    @Column(name = "version", nullable = false)
    private long version;

    /**
     * 构造方法
     *
     * <p>
     * 在实体创建时生成 UUID
     */
    protected BaseModel() {
        this.uuid = UUID.randomUUID().toString();
    }
}
