package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.SysRole;
import com.admin.service.RoleService;
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
 * 角色模块
 */
@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/list")
    public Result<List<SysRole>> list() {
        return Result.ok(roleService.list());
    }

    @PostMapping
    public Result<Void> add(@RequestBody SysRole role) {
        roleService.add(role);
        return Result.ok(null, "新增成功");
    }

    @PutMapping
    public Result<Void> update(@RequestBody SysRole role) {
        roleService.update(role);
        return Result.ok(null, "修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.ok(null, "删除成功");
    }

    @GetMapping("/{id}/permission-ids")
    public Result<List<Long>> getPermissionIds(@PathVariable Long id) {
        return Result.ok(roleService.getPermissionIds(id));
    }

    @PutMapping("/{id}/permission")
    public Result<Void> setPermissions(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        roleService.setPermissions(id, body.get("permissionIds"));
        return Result.ok(null, "权限配置成功");
    }
}
