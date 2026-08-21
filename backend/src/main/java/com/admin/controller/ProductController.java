package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.Product;
import com.admin.service.ProductService;
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

import java.util.List;
import java.util.Map;

/**
 * 商品模块
 */
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/category/list")
    public Result<List<String>> categoryList() {
        return Result.ok(productService.categoryList());
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> list(@RequestParam(defaultValue = "1") long page,
                                            @RequestParam(defaultValue = "10") long pageSize,
                                            @RequestParam(required = false) String name,
                                            @RequestParam(required = false) String category,
                                            @RequestParam(required = false) Integer status) {
        return Result.ok(productService.list(page, pageSize, name, category, status));
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        productService.updateStatus(id, body.get("status"));
        return Result.ok(null, "状态修改成功");
    }

    @PostMapping
    public Result<Void> add(@RequestBody Product product) {
        productService.add(product);
        return Result.ok(null, "新增成功");
    }

    @PutMapping
    public Result<Void> update(@RequestBody Product product) {
        productService.update(product);
        return Result.ok(null, "修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return Result.ok(null, "删除成功");
    }
}
