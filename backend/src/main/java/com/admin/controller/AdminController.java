package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.SysAdmin;
import com.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 管理员模块
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(@RequestParam(defaultValue = "1") long page,
                                            @RequestParam(defaultValue = "10") long pageSize,
                                            @RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) Integer status,
                                            @RequestParam(required = false) Long roleId) {
        return Result.ok(adminService.list(page, pageSize, keyword, status, roleId));
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        adminService.updateStatus(id, body.get("status"));
        return Result.ok(null, "状态修改成功");
    }

    @PostMapping
    public Result<Void> add(@RequestBody SysAdmin admin) {
        adminService.add(admin);
        return Result.ok(null, "新增成功");
    }

    @PutMapping
    public Result<Void> update(@RequestBody SysAdmin admin) {
        adminService.update(admin);
        return Result.ok(null, "修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        adminService.delete(id);
        return Result.ok(null, "删除成功");
    }
}
