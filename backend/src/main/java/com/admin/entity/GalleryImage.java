package com.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 图片
 */
@Data
@TableName("gallery_image")
public class GalleryImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId;

    private String name;

    /** 图片地址 */
    private String url;

    /** 大小（KB） */
    private Integer size;

    private Date createTime;
}
