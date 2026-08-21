package com.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易订单
 */
@Data
@TableName("trade_order")
public class TradeOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long storeId;

    private BigDecimal amount;

    /** 状态：1 待发货，2 售后，3 已完成 */
    private Integer status;

    private Date createTime;
}
