package com.admin.service;

import com.admin.common.BusinessException;
import com.admin.entity.SysMenu;
import com.admin.entity.SysRoleMenu;
import com.admin.mapper.SysMenuMapper;
import com.admin.mapper.SysRoleMenuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单权限服务
 */
@Service
@RequiredArgsConstructor
public class MenuService {

    private final SysMenuMapper menuMapper;
    private final SysRoleMenuMapper roleMenuMapper;

    /** 完整菜单树 */
    public List<SysMenu> list() {
        List<SysMenu> all = selectAll();
        return buildTree(all);
    }

    /** 根据角色可见菜单 id 过滤出菜单树（与前端 mock 的 filterMenus 逻辑一致） */
    public List<SysMenu> filterMenus(List<SysMenu> all, List<Long> ids) {
        List<SysMenu> tree = buildTree(all);
        return filterNodes(tree, ids);
    }

    public void add(SysMenu menu) {
        if (!StringUtils.hasText(menu.getTitle())) {
            throw new BusinessException("请填写菜单名称");
        }
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (menu.getParentId() != 0L && menuMapper.selectById(menu.getParentId()) == null) {
            throw new BusinessException("父级菜单不存在");
        }
        menu.setId(null);
        if (!StringUtils.hasText(menu.getName())) menu.setName("Menu" + System.currentTimeMillis());
        if (menu.getSort() == null) menu.setSort(0);
        if (menu.getVisible() == null) menu.setVisible(true);
        if (menu.getStatus() == null) menu.setStatus(1);
        if (menu.getPerms() == null) menu.setPerms("");
        menu.setCreateTime(new Date());
        menuMapper.insert(menu);
    }

    public void update(SysMenu menu) {
        if (menu.getId() == null || menuMapper.selectById(menu.getId()) == null) {
            throw new BusinessException("菜单不存在");
        }
        if (StringUtils.hasText(menu.getTitle()) && menu.getTitle().isBlank()) {
            throw new BusinessException("请填写菜单名称");
        }
        // updateById 默认只更新非空字段，实现局部更新
        menuMapper.updateById(menu);
    }

    public void delete(Long id) {
        if (menuMapper.selectById(id) == null) {
            throw new BusinessException("菜单不存在");
        }
        if (menuMapper.selectCount(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, id)) > 0) {
            throw new BusinessException("请先删除该菜单下的子菜单");
        }
        menuMapper.deleteById(id);
        // 同步清理角色-菜单关联
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getMenuId, id));
    }

    public void updateStatus(Long id, Integer status) {
        if (menuMapper.selectById(id) == null) {
            throw new BusinessException("菜单不存在");
        }
        SysMenu menu = new SysMenu();
        menu.setId(id);
        menu.setStatus(status != null && status == 0 ? 0 : 1);
        menuMapper.updateById(menu);
    }

    private List<SysMenu> selectAll() {
        return menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(SysMenu::getSort)
                .orderByAsc(SysMenu::getId));
    }

    /** 由扁平列表构建树 */
    private List<SysMenu> buildTree(List<SysMenu> all) {
        Map<Long, List<SysMenu>> byParent = all.stream()
                .filter(m -> m.getParentId() != null)
                .collect(Collectors.groupingBy(SysMenu::getParentId, LinkedHashMap::new, Collectors.toList()));
        for (SysMenu m : all) {
            m.setChildren(byParent.getOrDefault(m.getId(), new ArrayList<>()));
        }
        return all.stream()
                .filter(m -> m.getParentId() == null || m.getParentId() == 0L)
                .collect(Collectors.toList());
    }

    /** 递归过滤：命中 id 保留整棵子树；否则仅保留命中的后代 */
    private List<SysMenu> filterNodes(List<SysMenu> nodes, List<Long> ids) {
        List<SysMenu> result = new ArrayList<>();
        for (SysMenu node : nodes) {
            List<SysMenu> children = filterNodes(node.getChildren(), ids);
            boolean matched = ids.contains(node.getId());
            if (matched || !children.isEmpty()) {
                if (!matched) {
                    node.setChildren(children);
                }
                result.add(node);
            }
        }
        return result;
    }
}
