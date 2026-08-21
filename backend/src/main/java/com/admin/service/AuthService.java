package com.admin.service;

import com.admin.common.BusinessException;
import com.admin.common.JwtUtil;
import com.admin.common.ResultCode;
import com.admin.common.UserContext;
import com.admin.dto.ChangePasswordDTO;
import com.admin.dto.LoginDTO;
import com.admin.entity.SysAdmin;
import com.admin.entity.SysMenu;
import com.admin.entity.SysRole;
import com.admin.entity.SysRoleMenu;
import com.admin.mapper.SysAdminMapper;
import com.admin.mapper.SysMenuMapper;
import com.admin.mapper.SysRoleMapper;
import com.admin.mapper.SysRoleMenuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 认证服务：登录、退出、用户信息、修改密码
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysAdminMapper adminMapper;
    private final SysRoleMapper roleMapper;
    private final SysMenuMapper menuMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final MenuService menuService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /** 登录，返回 token */
    public Map<String, Object> login(LoginDTO dto) {
        SysAdmin admin = adminMapper.selectOne(new LambdaQueryWrapper<SysAdmin>()
                .eq(SysAdmin::getUsername, dto.getUsername()));
        if (admin == null || !passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (admin.getStatus() == null || admin.getStatus() == 0) {
            throw new BusinessException("该账号已被禁用，请联系管理员");
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("token", jwtUtil.createToken(admin.getId()));
        return data;
    }

    /** 当前用户信息（含菜单树与按钮权限） */
    public Map<String, Object> userInfo() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "未登录");
        }
        SysAdmin admin = adminMapper.selectById(userId);
        if (admin == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "用户不存在");
        }
        SysRole role = roleMapper.selectById(admin.getRoleId());

        List<SysMenu> allMenus = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(SysMenu::getSort)
                .orderByAsc(SysMenu::getId));
        List<Long> menuIds = roleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getRoleId, admin.getRoleId()))
                .stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());
        List<SysMenu> menus = menuService.filterMenus(allMenus, menuIds);

        List<String> perms = new ArrayList<>();
        collectPerms(menus, perms);
        if (admin.getRoleId() != null && admin.getRoleId() == 1L) {
            perms.add("*");
        }

        Map<String, Object> user = new LinkedHashMap<>();
        user.put("id", admin.getId());
        user.put("username", admin.getUsername());
        user.put("nickname", admin.getNickname());
        user.put("avatar", admin.getAvatar());
        user.put("roleId", admin.getRoleId());
        user.put("roleName", role != null ? role.getName() : admin.getRoleName());
        user.put("email", admin.getEmail());
        user.put("phone", admin.getPhone());
        user.put("perms", perms);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("user", user);
        result.put("menus", menus);
        return result;
    }

    /** 修改密码 */
    public void changePassword(ChangePasswordDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "未登录");
        }
        SysAdmin admin = adminMapper.selectById(userId);
        if (admin == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), admin.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new BusinessException("新密码不能与原密码相同");
        }
        admin.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        adminMapper.updateById(admin);
    }

    /** 收集按钮级权限标识 */
    private void collectPerms(List<SysMenu> nodes, List<String> acc) {
        for (SysMenu node : nodes) {
            if (node.getPerms() != null && !node.getPerms().isBlank()) {
                acc.add(node.getPerms());
            }
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                collectPerms(node.getChildren(), acc);
            }
        }
    }
}
