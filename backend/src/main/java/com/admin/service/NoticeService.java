package com.admin.service;

import com.admin.common.BusinessException;
import com.admin.common.PageResult;
import com.admin.entity.Notice;
import com.admin.mapper.NoticeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;

/**
 * 公告服务
 */
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeMapper noticeMapper;

    public Map<String, Object> list(long page, long pageSize, String keyword) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Notice::getTitle, keyword).or().like(Notice::getAuthor, keyword));
        }
        wrapper.orderByDesc(Notice::getCreateTime).orderByDesc(Notice::getId);
        return PageResult.of(noticeMapper.selectPage(new Page<>(page, pageSize), wrapper));
    }

    public void add(Notice notice) {
        if (!StringUtils.hasText(notice.getTitle())) {
            throw new BusinessException("请填写公告标题");
        }
        notice.setId(null);
        if (notice.getContent() == null) {
            notice.setContent("");
        }
        if (!StringUtils.hasText(notice.getAuthor())) {
            notice.setAuthor("超级管理员");
        }
        notice.setStatus(1);
        notice.setCreateTime(new Date());
        noticeMapper.insert(notice);
    }

    public void update(Notice notice) {
        if (notice.getId() == null || noticeMapper.selectById(notice.getId()) == null) {
            throw new BusinessException("公告不存在");
        }
        if (StringUtils.hasText(notice.getTitle()) && notice.getTitle().isBlank()) {
            throw new BusinessException("请填写公告标题");
        }
        noticeMapper.updateById(notice);
    }

    public void delete(Long id) {
        if (noticeMapper.selectById(id) == null) {
            throw new BusinessException("公告不存在");
        }
        noticeMapper.deleteById(id);
    }
}
