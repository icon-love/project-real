package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.GalleryCategory;
import com.admin.entity.GalleryImage;
import com.admin.service.GalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 图库模块：分类 + 图片
 */
@RestController
@RequestMapping("/api/gallery")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;

    // ================= 分类 =================

    @GetMapping("/category/list")
    public Result<Map<String, Object>> categoryList(@RequestParam(defaultValue = "1") long page,
                                                    @RequestParam(defaultValue = "10") long pageSize,
                                                    @RequestParam(required = false) String name) {
        return Result.ok(galleryService.categoryList(page, pageSize, name));
    }

    @PostMapping("/category")
    public Result<Void> addCategory(@RequestBody GalleryCategory category) {
        galleryService.addCategory(category);
        return Result.ok(null, "新增成功");
    }

    @PutMapping("/category")
    public Result<Void> updateCategory(@RequestBody GalleryCategory category) {
        galleryService.updateCategory(category);
        return Result.ok(null, "修改成功");
    }

    @DeleteMapping("/category/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        galleryService.deleteCategory(id);
        return Result.ok(null, "删除成功");
    }

    // ================= 图片 =================

    @GetMapping("/list")
    public Result<Map<String, Object>> imageList(@RequestParam(defaultValue = "1") long page,
                                                 @RequestParam(defaultValue = "10") long pageSize,
                                                 @RequestParam(required = false) Long categoryId,
                                                 @RequestParam(required = false) String name) {
        return Result.ok(galleryService.imageList(page, pageSize, categoryId, name));
    }

    @PostMapping("/upload")
    public Result<List<GalleryImage>> upload(@RequestParam("files") MultipartFile[] files,
                                             @RequestParam(value = "categoryId", required = false) Long categoryId) {
        List<GalleryImage> records = galleryService.upload(files, categoryId);
        return Result.ok(records, "成功上传 " + records.size() + " 张图片");
    }

    @DeleteMapping("/image/{id}")
    public Result<Void> deleteImage(@PathVariable Long id) {
        galleryService.deleteImage(id);
        return Result.ok(null, "删除成功");
    }

    /** 图片内容接口：从数据库读取二进制并返回（img 标签访问，无需鉴权） */
    @GetMapping("/image/{id}/content")
    public ResponseEntity<byte[]> imageContent(@PathVariable Long id) {
        GalleryImage image = galleryService.getImage(id);
        if (image == null || image.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        String contentType = image.getContentType() != null && !image.getContentType().isBlank()
                ? image.getContentType()
                : "application/octet-stream";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(image.getData());
    }

    @PutMapping("/image/{id}")
    public Result<Void> renameImage(@PathVariable Long id, @RequestBody Map<String, String> body) {
        galleryService.renameImage(id, body.get("name"));
        return Result.ok(null, "重命名成功");
    }
}
