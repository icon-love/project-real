package com.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 商品评论
 */
@Data
@TableName("product_review")
public class ProductReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;

    private String content;

    private Date createTime;
}
