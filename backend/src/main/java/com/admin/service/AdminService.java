package com.admin.service;

import com.admin.common.BusinessException;
import com.admin.common.PageResult;
import com.admin.entity.SysAdmin;
import com.admin.entity.SysRole;
import com.admin.mapper.SysAdminMapper;
import com.admin.mapper.SysRoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员服务
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final SysAdminMapper adminMapper;
    private final SysRoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    public Map<String, Object> list(long page, long pageSize, String keyword, Integer status, Long roleId) {
        LambdaQueryWrapper<SysAdmin> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysAdmin::getUsername, keyword).or().like(SysAdmin::getNickname, keyword));
        }
        if (status != null) {
            wrapper.eq(SysAdmin::getStatus, status);
        }
        if (roleId != null) {
            wrapper.eq(SysAdmin::getRoleId, roleId);
        }
        wrapper.orderByAsc(SysAdmin::getId);

        Page<SysAdmin> result = adminMapper.selectPage(new Page<>(page, pageSize), wrapper);
        Map<Long, String> roleNames = roleMapper.selectList(null).stream()
                .collect(Collectors.toMap(SysRole::getId, SysRole::getName, (a, b) -> a));
        result.getRecords().forEach(a -> {
            a.setRoleName(roleNames.get(a.getRoleId()));
            // 列表不返回密码
            a.setPassword(null);
        });
        return PageResult.of(result);
    }

    public void updateStatus(Long id, Integer status) {
        if (adminMapper.selectById(id) == null) {
            throw new BusinessException("管理员不存在");
        }
        SysAdmin admin = new SysAdmin();
        admin.setId(id);
        admin.setStatus(status != null && status == 0 ? 0 : 1);
        adminMapper.updateById(admin);
    }

    public void add(SysAdmin admin) {
        if (!StringUtils.hasText(admin.getUsername()) || !StringUtils.hasText(admin.getPassword())) {
            throw new BusinessException("请填写用户名和密码");
        }
        if (adminMapper.selectCount(new LambdaQueryWrapper<SysAdmin>()
                .eq(SysAdmin::getUsername, admin.getUsername())) > 0) {
            throw new BusinessException("用户名已存在");
        }
        admin.setId(null);
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        if (!StringUtils.hasText(admin.getNickname())) {
            admin.setNickname(admin.getUsername());
        }
        if (admin.getRoleId() == null) {
            admin.setRoleId(2L);
        }
        admin.setRoleName(roleName(admin.getRoleId()));
        if (admin.getStatus() == null) {
            admin.setStatus(1);
        }
        admin.setCreateTime(new Date());
        adminMapper.insert(admin);
    }

    public void update(SysAdmin admin) {
        if (admin.getId() == null || adminMapper.selectById(admin.getId()) == null) {
            throw new BusinessException("管理员不存在");
        }
        if (StringUtils.hasText(admin.getUsername())) {
            boolean exists = adminMapper.selectCount(new LambdaQueryWrapper<SysAdmin>()
                    .eq(SysAdmin::getUsername, admin.getUsername())
                    .ne(SysAdmin::getId, admin.getId())) > 0;
            if (exists) {
                throw new BusinessException("用户名已存在");
            }
        }
        if (StringUtils.hasText(admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        } else {
            // 未填写密码则不修改
            admin.setPassword(null);
        }
        if (admin.getRoleId() != null) {
            admin.setRoleName(roleName(admin.getRoleId()));
        }
        admin.setUpdateTime(new Date());
        adminMapper.updateById(admin);
    }

    public void delete(Long id) {
        if (id != null && id == 1L) {
            throw new BusinessException("超级管理员不可删除");
        }
        if (adminMapper.selectById(id) == null) {
            throw new BusinessException("管理员不存在");
        }
        adminMapper.deleteById(id);
    }

    private String roleName(Long roleId) {
        SysRole role = roleMapper.selectById(roleId);
        return role != null ? role.getName() : "未分配";
    }
}
