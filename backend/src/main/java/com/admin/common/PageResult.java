package com.admin.common;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页结果构造助手：返回 { list, total }
 */
public class PageResult {

    public static Map<String, Object> of(List<?> list, long total) {
        Map<String, Object> map = new HashMap<>(4);
        map.put("list", list);
        map.put("total", total);
        return map;
    }

    public static Map<String, Object> of(IPage<?> page) {
        return of(page.getRecords(), page.getTotal());
    }
}
