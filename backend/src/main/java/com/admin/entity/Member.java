package com.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 会员
 */
@Data
@TableName("member")
public class Member {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String nickname;

    private String phone;

    private Date createTime;
}
