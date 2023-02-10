package com.sakura.reggieApi.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色 实体类
 * @author sakura
 * @className Role
 * @createTime 2023/2/8
 */
@TableName("role")
@Data
public class Role implements Serializable {
    @TableField(exist = false)
    private static final Long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private String nameDetail;
}
