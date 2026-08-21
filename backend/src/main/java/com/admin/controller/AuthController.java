package com.admin.controller;

import com.admin.common.Result;
import com.admin.dto.ChangePasswordDTO;
import com.admin.dto.LoginDTO;
import com.admin.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 认证模块：登录 / 退出 / 用户信息 / 修改密码
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Valid LoginDTO dto) {
        return Result.ok(authService.login(dto), "登录成功");
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.ok(null, "退出成功");
    }

    @GetMapping("/user/info")
    public Result<Map<String, Object>> userInfo() {
        return Result.ok(authService.userInfo());
    }

    @PutMapping("/user/password")
    public Result<Void> changePassword(@RequestBody @Valid ChangePasswordDTO dto) {
        authService.changePassword(dto);
        return Result.ok(null, "密码修改成功");
    }
}
