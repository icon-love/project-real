package com.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 菜单权限
 */
@Data
@TableName("sys_menu")
public class SysMenu {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId;

    private String title;

    /** 路由 name */
    private String name;

    /** 路由 path */
    private String path;

    /** 组件路径 */
    private String component;

    private String icon;

    private Integer sort;

    /** 是否显示 */
    private Boolean visible;

    /** 状态：0 禁用，1 启用 */
    private Integer status;

    /** 按钮权限标识 */
    private String perms;

    private Date createTime;

    /** 子菜单（非表字段） */
    @TableField(exist = false)
    private List<SysMenu> children;
}
