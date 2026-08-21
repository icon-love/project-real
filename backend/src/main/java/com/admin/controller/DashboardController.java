package com.admin.controller;

import com.admin.common.Result;
import com.admin.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 仪表盘模块
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public Result<List<Map<String, Object>>> stats() {
        return Result.ok(dashboardService.stats());
    }

    @GetMapping("/sales-trend")
    public Result<Map<String, Object>> salesTrend() {
        return Result.ok(dashboardService.salesTrend());
    }

    @GetMapping("/category-ratio")
    public Result<List<Map<String, Object>>> categoryRatio() {
        return Result.ok(dashboardService.categoryRatio());
    }

    @GetMapping("/store-trade")
    public Result<Map<String, Object>> storeTrade() {
        return Result.ok(dashboardService.storeTrade());
    }
}
