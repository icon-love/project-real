package com.admin.service;

import com.admin.common.BusinessException;
import com.admin.common.PageResult;
import com.admin.entity.GalleryCategory;
import com.admin.entity.GalleryImage;
import com.admin.mapper.GalleryCategoryMapper;
import com.admin.mapper.GalleryImageMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 图库服务：分类 + 图片（上传/删除/重命名）
 */
@Service
@RequiredArgsConstructor
public class GalleryService {

    private final GalleryCategoryMapper categoryMapper;
    private final GalleryImageMapper imageMapper;
    private final FileStorageService fileStorageService;

    // ================= 分类 =================

    public Map<String, Object> categoryList(long page, long pageSize, String name) {
        LambdaQueryWrapper<GalleryCategory> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(GalleryCategory::getName, name);
        }
        wrapper.orderByAsc(GalleryCategory::getSort).orderByAsc(GalleryCategory::getId);
        return PageResult.of(categoryMapper.selectPage(new Page<>(page, pageSize), wrapper));
    }

    public void addCategory(GalleryCategory category) {
        if (!StringUtils.hasText(category.getName())) {
            throw new BusinessException("请填写分类名称");
        }
        if (existsName(category.getName(), null)) {
            throw new BusinessException("分类名称已存在");
        }
        category.setId(null);
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getRemark() == null) {
            category.setRemark("");
        }
        category.setCreateTime(new Date());
        categoryMapper.insert(category);
    }

    public void updateCategory(GalleryCategory category) {
        if (category.getId() == null || categoryMapper.selectById(category.getId()) == null) {
            throw new BusinessException("分类不存在");
        }
        if (StringUtils.hasText(category.getName()) && existsName(category.getName(), category.getId())) {
            throw new BusinessException("分类名称已存在");
        }
        categoryMapper.updateById(category);
    }

    public void deleteCategory(Long id) {
        if (categoryMapper.selectById(id) == null) {
            throw new BusinessException("分类不存在");
        }
        if (imageMapper.selectCount(new LambdaQueryWrapper<GalleryImage>()
                .eq(GalleryImage::getCategoryId, id)) > 0) {
            throw new BusinessException("该分类下存在图片，无法删除");
        }
        categoryMapper.deleteById(id);
    }

    // ================= 图片 =================

    public Map<String, Object> imageList(long page, long pageSize, Long categoryId, String name) {
        LambdaQueryWrapper<GalleryImage> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(GalleryImage::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(name)) {
            wrapper.like(GalleryImage::getName, name);
        }
        wrapper.orderByDesc(GalleryImage::getCreateTime).orderByDesc(GalleryImage::getId);
        return PageResult.of(imageMapper.selectPage(new Page<>(page, pageSize), wrapper));
    }

    public List<GalleryImage> upload(MultipartFile[] files, Long categoryId) {
        if (files == null || files.length == 0) {
            throw new BusinessException("未选择文件");
        }
        Long cid = categoryId == null ? 1L : categoryId;
        if (categoryMapper.selectById(cid) == null) {
            throw new BusinessException("分类不存在");
        }
        List<GalleryImage> records = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            String url;
            try {
                url = fileStorageService.save(file);
            } catch (IOException e) {
                throw new BusinessException("文件保存失败");
            }
            String original = file.getOriginalFilename();
            String name = original != null && original.contains(".")
                    ? original.substring(0, original.lastIndexOf('.'))
                    : "未命名图片";
            GalleryImage image = new GalleryImage();
            image.setCategoryId(cid);
            image.setName(name);
            image.setUrl(url);
            image.setSize(Math.max(1, Math.round(file.getSize() / 1024f)));
            image.setCreateTime(new Date());
            imageMapper.insert(image);
            records.add(image);
        }
        return records;
    }

    public void deleteImage(Long id) {
        GalleryImage image = imageMapper.selectById(id);
        if (image == null) {
            throw new BusinessException("图片不存在");
        }
        imageMapper.deleteById(id);
        fileStorageService.deleteByUrl(image.getUrl());
    }

    public void renameImage(Long id, String name) {
        GalleryImage image = imageMapper.selectById(id);
        if (image == null) {
            throw new BusinessException("图片不存在");
        }
        if (!StringUtils.hasText(name)) {
            throw new BusinessException("请输入图片名称");
        }
        image.setName(name);
        imageMapper.updateById(image);
    }

    private boolean existsName(String name, Long excludeId) {
        LambdaQueryWrapper<GalleryCategory> wrapper = new LambdaQueryWrapper<GalleryCategory>()
                .eq(GalleryCategory::getName, name);
        if (excludeId != null) {
            wrapper.ne(GalleryCategory::getId, excludeId);
        }
        return categoryMapper.selectCount(wrapper) > 0;
    }
}
