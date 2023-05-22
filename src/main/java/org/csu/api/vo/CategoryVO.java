package org.csu.api.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class CategoryVO {
    private Integer id;
    private Integer parentId;
    private String name;
    private Integer status;
    private Integer sortOrder;
}
