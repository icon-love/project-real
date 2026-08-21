package com.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 图库分类
 */
@Data
@TableName("gallery_category")
public class GalleryCategory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Integer sort;

    private String remark;

    private Date createTime;
}
