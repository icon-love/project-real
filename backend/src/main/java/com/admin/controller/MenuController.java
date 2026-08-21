package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.SysMenu;
import com.admin.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 菜单权限模块
 */
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/list")
    public Result<List<SysMenu>> list() {
        return Result.ok(menuService.list());
    }

    @PostMapping
    public Result<Void> add(@RequestBody SysMenu menu) {
        menuService.add(menu);
        return Result.ok(null, "新增成功");
    }

    @PutMapping
    public Result<Void> update(@RequestBody SysMenu menu) {
        menuService.update(menu);
        return Result.ok(null, "修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return Result.ok(null, "删除成功");
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        menuService.updateStatus(id, body.get("status"));
        return Result.ok(null, "修改成功");
    }
}
