package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.Notice;
import com.admin.service.NoticeService;
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
 * 公告模块
 */
@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(@RequestParam(defaultValue = "1") long page,
                                            @RequestParam(defaultValue = "10") long pageSize,
                                            @RequestParam(required = false) String keyword) {
        return Result.ok(noticeService.list(page, pageSize, keyword));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Notice notice) {
        noticeService.add(notice);
        return Result.ok(null, "新增成功");
    }

    @PutMapping
    public Result<Void> update(@RequestBody Notice notice) {
        noticeService.update(notice);
        return Result.ok(null, "修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return Result.ok(null, "删除成功");
    }
}
