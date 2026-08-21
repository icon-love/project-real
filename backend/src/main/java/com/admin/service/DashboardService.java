package com.admin.service;

import com.admin.entity.Product;
import com.admin.entity.ProductReview;
import com.admin.entity.Store;
import com.admin.entity.TradeOrder;
import com.admin.mapper.MemberMapper;
import com.admin.mapper.ProductMapper;
import com.admin.mapper.ProductReviewMapper;
import com.admin.mapper.StoreMapper;
import com.admin.mapper.TradeOrderMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘服务：统计数据全部来自真实数据表（订单/商品/会员/店铺/评论）
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    private final TradeOrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final MemberMapper memberMapper;
    private final StoreMapper storeMapper;
    private final ProductReviewMapper reviewMapper;

    /** 统计面板 */
    public List<Map<String, Object>> stats() {
        LocalDate today = LocalDate.now(ZONE);
        Date todayStart = startOfDay(today);
        Date todayEnd = startOfDay(today.plusDays(1));
        Date yesterdayStart = startOfDay(today.minusDays(1));

        BigDecimal salesToday = sumAmount(todayStart, todayEnd);
        BigDecimal salesYesterday = sumAmount(yesterdayStart, todayStart);
        long ordersToday = orderCount(todayStart, todayEnd);
        long ordersYesterday = orderCount(yesterdayStart, todayStart);

        long productTotal = productMapper.selectCount(null);
        long productThisMonth = productCountSince(monthStart(today));
        long productLastMonth = productCountBetween(monthStart(today.minusMonths(1)), monthStart(today));

        long memberTotal = memberMapper.selectCount(null);
        long memberThisMonth = memberCountSince(monthStart(today));
        long memberLastMonth = memberCountBetween(monthStart(today.minusMonths(1)), monthStart(today));

        List<Map<String, Object>> list = new ArrayList<>();
        list.add(stat("sales", "今日销售额", salesToday.longValue(), "元", "Money", "#409EFF",
                trend(salesToday, salesYesterday), "较昨日"));
        list.add(stat("orders", "今日订单数", ordersToday, "单", "ShoppingCart", "#67C23A",
                trend(ordersToday, ordersYesterday), "较昨日"));
        list.add(stat("products", "商品总数", productTotal, "件", "Goods", "#E6A23C",
                trend(productThisMonth, productLastMonth), "本月新增"));
        list.add(stat("users", "会员总数", memberTotal, "人", "User", "#F56C6C",
                trend(memberThisMonth, memberLastMonth), "本月新增"));
        return list;
    }

    /** 销售趋势（最近 12 个月） */
    public Map<String, Object> salesTrend() {
        LocalDate today = LocalDate.now(ZONE);
        LocalDate startMonth = today.minusMonths(11).withDayOfMonth(1);

        List<Map<String, Object>> rows = orderMapper.selectMaps(new QueryWrapper<TradeOrder>()
                .select("DATE_FORMAT(create_time, '%Y-%m') AS ym",
                        "IFNULL(SUM(amount), 0) AS sales",
                        "COUNT(*) AS orders")
                .ge("create_time", Date.from(startMonth.atStartOfDay(ZONE).toInstant()))
                .groupBy("ym")
                .orderByAsc("ym"));

        Map<String, Map<String, Object>> byMonth = new HashMap<>();
        for (Map<String, Object> row : rows) {
            byMonth.put(String.valueOf(row.get("ym")), row);
        }

        List<String> months = new ArrayList<>();
        List<Integer> sales = new ArrayList<>();
        List<Integer> orders = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            String key = YearMonth.from(startMonth).plusMonths(i).toString();
            months.add(key);
            Map<String, Object> row = byMonth.get(key);
            if (row == null) {
                sales.add(0);
                orders.add(0);
            } else {
                sales.add(new BigDecimal(String.valueOf(row.get("sales"))).intValue());
                orders.add(((Number) row.get("orders")).intValue());
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("months", months);
        result.put("sales", sales);
        result.put("orders", orders);
        return result;
    }

    /** 商品分类占比 */
    public List<Map<String, Object>> categoryRatio() {
        List<Map<String, Object>> rows = productMapper.selectMaps(new QueryWrapper<Product>()
                .select("category AS name", "COUNT(*) AS value")
                .groupBy("category")
                .orderByDesc("value"));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", row.get("name"));
            item.put("value", ((Number) row.get("value")).intValue());
            result.add(item);
        }
        return result;
    }

    /** 店铺与交易提示 */
    public Map<String, Object> storeTrade() {
        List<Store> stores = storeMapper.selectList(new LambdaQueryWrapper<Store>().orderByDesc(Store::getSales));

        Date todayStart = startOfDay(LocalDate.now(ZONE));
        long pendingShip = orderCountByStatus(1);
        long afterSale = orderCountByStatus(2);
        long stockWarn = productMapper.selectCount(new LambdaQueryWrapper<Product>().lt(Product::getStock, 50));
        long todayComments = reviewMapper.selectCount(new LambdaQueryWrapper<ProductReview>()
                .ge(ProductReview::getCreateTime, todayStart));

        List<Map<String, Object>> tips = new ArrayList<>();
        tips.add(tip("订单待发货", pendingShip, "warning"));
        tips.add(tip("售后待处理", afterSale, "danger"));
        tips.add(tip("商品库存预警", stockWarn, "warning"));
        tips.add(tip("今日新增评论", todayComments, "success"));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("stores", stores);
        result.put("tips", tips);
        return result;
    }

    // ================= 私有辅助 =================

    private Map<String, Object> stat(String key, String name, long value, String unit,
                                     String icon, String color, double trend, String desc) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("key", key);
        map.put("name", name);
        map.put("value", value);
        map.put("unit", unit);
        map.put("icon", icon);
        map.put("color", color);
        map.put("trend", trend);
        map.put("desc", desc);
        return map;
    }

    private Map<String, Object> tip(String title, long value, String type) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("title", title);
        map.put("value", value);
        map.put("type", type);
        return map;
    }

    /** 环比（百分比，保留 1 位小数） */
    private double trend(long current, long previous) {
        if (previous == 0) {
            return 0;
        }
        return BigDecimal.valueOf((current - previous) * 100.0 / previous)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private Date startOfDay(LocalDate date) {
        return Date.from(date.atStartOfDay(ZONE).toInstant());
    }

    private Date monthStart(LocalDate date) {
        return Date.from(date.withDayOfMonth(1).atStartOfDay(ZONE).toInstant());
    }

    private BigDecimal sumAmount(Date start, Date end) {
        List<Map<String, Object>> rows = orderMapper.selectMaps(new QueryWrapper<TradeOrder>()
                .select("IFNULL(SUM(amount), 0) AS total")
                .ge("create_time", start)
                .lt("create_time", end));
        if (rows.isEmpty() || rows.get(0) == null || rows.get(0).get("total") == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(String.valueOf(rows.get(0).get("total")));
    }

    private long orderCount(Date start, Date end) {
        return orderMapper.selectCount(new LambdaQueryWrapper<TradeOrder>()
                .ge(TradeOrder::getCreateTime, start)
                .lt(TradeOrder::getCreateTime, end));
    }

    private long orderCountByStatus(int status) {
        return orderMapper.selectCount(new LambdaQueryWrapper<TradeOrder>().eq(TradeOrder::getStatus, status));
    }

    private long productCountSince(Date start) {
        return productMapper.selectCount(new LambdaQueryWrapper<Product>().ge(Product::getCreateTime, start));
    }

    private long productCountBetween(Date start, Date end) {
        return productMapper.selectCount(new LambdaQueryWrapper<Product>()
                .ge(Product::getCreateTime, start)
                .lt(Product::getCreateTime, end));
    }

    private long memberCountSince(Date start) {
        return memberMapper.selectCount(new LambdaQueryWrapper<com.admin.entity.Member>().ge(com.admin.entity.Member::getCreateTime, start));
    }

    private long memberCountBetween(Date start, Date end) {
        return memberMapper.selectCount(new LambdaQueryWrapper<com.admin.entity.Member>()
                .ge(com.admin.entity.Member::getCreateTime, start)
                .lt(com.admin.entity.Member::getCreateTime, end));
    }
}
