package com.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

/**
 * 图片（内容以二进制形式存于数据库）
 */
@Data
@TableName("gallery_image")
public class GalleryImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId;

    private String name;

    /** 图片地址（内容接口 /api/gallery/image/{id}/content） */
    private String url;

    /** 大小（KB） */
    private Integer size;

    /** 图片内容（二进制，存数据库；列表响应不序列化，避免数据量过大） */
    @JsonIgnore
    private byte[] data;

    /** MIME 类型，如 image/png、image/svg+xml */
    private String contentType;

    private Date createTime;
}
