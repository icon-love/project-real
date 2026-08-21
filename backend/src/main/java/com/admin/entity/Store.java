package com.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 店铺
 */
@Data
@TableName("store")
public class Store {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    /** 累计销售额 */
    private BigDecimal sales;

    /** 累计交易笔数 */
    private BigDecimal tips;
}
