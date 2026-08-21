package com.admin.service;

import com.admin.common.BusinessException;
import com.admin.entity.SysAdmin;
import com.admin.entity.SysRole;
import com.admin.entity.SysRoleMenu;
import com.admin.mapper.SysAdminMapper;
import com.admin.mapper.SysRoleMapper;
import com.admin.mapper.SysRoleMenuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务
 */
@Service
@RequiredArgsConstructor
public class RoleService {

    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysAdminMapper adminMapper;

    public List<SysRole> list() {
        List<SysRole> roles = roleMapper.selectList(new LambdaQueryWrapper<SysRole>().orderByAsc(SysRole::getId));
        roles.forEach(r -> r.setPermissionIds(menuIds(r.getId())));
        return roles;
    }

    public void add(SysRole role) {
        if (!StringUtils.hasText(role.getName())) {
            throw new BusinessException("请填写角色名称");
        }
        if (existsName(role.getName(), null)) {
            throw new BusinessException("角色名称已存在");
        }
        role.setId(null);
        role.setCreateTime(new Date());
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        roleMapper.insert(role);
        savePermissions(role.getId(), role.getPermissionIds());
    }

    public void update(SysRole role) {
        if (role.getId() == null) {
            throw new BusinessException("角色不存在");
        }
        SysRole db = roleMapper.selectById(role.getId());
        if (db == null) {
            throw new BusinessException("角色不存在");
        }
        if (StringUtils.hasText(role.getName())) {
            if (existsName(role.getName(), role.getId())) {
                throw new BusinessException("角色名称已存在");
            }
            db.setName(role.getName());
        }
        if (role.getRemark() != null) {
            db.setRemark(role.getRemark());
        }
        if (role.getStatus() != null) {
            db.setStatus(role.getStatus() == 0 ? 0 : 1);
        }
        roleMapper.updateById(db);
    }

    public void delete(Long id) {
        if (id != null && id == 1L) {
            throw new BusinessException("超级管理员角色不可删除");
        }
        if (roleMapper.selectById(id) == null) {
            throw new BusinessException("角色不存在");
        }
        if (adminMapper.selectCount(new LambdaQueryWrapper<SysAdmin>().eq(SysAdmin::getRoleId, id)) > 0) {
            throw new BusinessException("该角色下存在管理员，无法删除");
        }
        roleMapper.deleteById(id);
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, id));
    }

    public List<Long> getPermissionIds(Long id) {
        if (roleMapper.selectById(id) == null) {
            throw new BusinessException("角色不存在");
        }
        return menuIds(id);
    }

    public void setPermissions(Long id, List<Long> permissionIds) {
        if (roleMapper.selectById(id) == null) {
            throw new BusinessException("角色不存在");
        }
        savePermissions(id, permissionIds);
    }

    private List<Long> menuIds(Long roleId) {
        return roleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getRoleId, roleId))
                .stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());
    }

    private void savePermissions(Long roleId, List<Long> permissionIds) {
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        if (permissionIds == null) {
            return;
        }
        for (Long menuId : permissionIds) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(menuId);
            roleMenuMapper.insert(rm);
        }
    }

    private boolean existsName(String name, Long excludeId) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>().eq(SysRole::getName, name);
        if (excludeId != null) {
            wrapper.ne(SysRole::getId, excludeId);
        }
        return roleMapper.selectCount(wrapper) > 0;
    }
}
