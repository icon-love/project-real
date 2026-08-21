package com.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 管理员
 */
@Data
@TableName("sys_admin")
public class SysAdmin {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    /** 密码（BCrypt 加密，列表不返回） */
    private String password;

    private String nickname;

    private Long roleId;

    private String phone;

    private String email;

    private String avatar;

    /** 状态：0 禁用，1 启用 */
    private Integer status;

    private Date createTime;

    private Date updateTime;

    /** 角色名称（非表字段） */
    @TableField(exist = false)
    private String roleName;
}
