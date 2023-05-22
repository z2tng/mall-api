package org.csu.api.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mystore_category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField(value = "parent_id")
    private Integer parentId;

    private String name;
    private Integer status;

    @TableField(value = "sort_order")
    private Integer sortOrder;

    @TableField(value = "create_time")
    private LocalDateTime creatTime;
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
