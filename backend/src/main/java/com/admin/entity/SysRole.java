package com.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 角色
 */
@Data
@TableName("sys_role")
public class SysRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String remark;

    /** 状态：0 禁用，1 启用 */
    private Integer status;

    private Date createTime;

    /** 已分配的菜单权限 id（非表字段） */
    @TableField(exist = false)
    private List<Long> permissionIds;
}
