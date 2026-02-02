# 项目背景

java项目,springboot 3.4.10,jdk21.使用jpa，现在需要修复jpa entity类中的注解/注释。

# 需求

## entity表类名上增加以下注解:
1. 增加@Table注解(jakarta.persistence.Table)
2. 增加@Comment注解(org.hibernate.annotations.Comment)

## entity表类中的字段增加以下注解:
1.除Join字段外(oneToOne,ManyToOne,OneToMany)，增加@Column注解，并且添加 columnDefinition 说明 以及name属性
2.(OneToOne,OneToMany中)在joinColumn中补全columnDefinition说明

## 其他需求
1.以上需求，需要保留原有字段含义。
2.检查并且保留原有import代码

## demo代码

@Entity
@Getter
@Setter
@Table(name = "bo_suspend_record")
@Comment("BO商户停用记录表") 
public class BoSuspendRecord extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint COMMENT '主键ID'")
    private Long id;

    @Column(name = "suspend_reason", length = 1000, columnDefinition = "varchar(1000) COMMENT '停用原因'")
    private String suspendReason;

    @Column(name = "template_uuid", columnDefinition = "varchar(50) COMMENT 'marketingTemplate.UUID'")
    private String templateUuid;

    @Column(name = "operator_uuid", columnDefinition = "varchar(50) COMMENT '操作员UUID'")
    private String operatorUuid;

}

# 需处理代码

${context.code}

# 输出格式

{ \"patches\": [
    {
      \"path\": \"文件相对路径\",
        \"isNew\":\"默认为true\",    \"edits\": [
        {
          \"type\": \"i默认为 new_file\",
          \"content\": \"包含新代码（保留原有缩进和换行）\"
        }
      ]
    }
]
}