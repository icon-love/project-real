package com.admin.service;

import com.admin.common.BusinessException;
import com.admin.common.PageResult;
import com.admin.entity.Product;
import com.admin.mapper.ProductMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品服务
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    /** 商品分类列表（去重，来自商品表真实数据） */
    public List<String> categoryList() {
        List<Object> objs = productMapper.selectObjs(new QueryWrapper<Product>()
                .select("DISTINCT category")
                .orderByAsc("category"));
        return objs.stream().map(String::valueOf).collect(Collectors.toList());
    }

    public Map<String, Object> list(long page, long pageSize, String name, String category, Integer status) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Product::getName, name);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(Product::getCategory, category);
        }
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        wrapper.orderByDesc(Product::getId);
        return PageResult.of(productMapper.selectPage(new Page<>(page, pageSize), wrapper));
    }

    public void updateStatus(Long id, Integer status) {
        if (productMapper.selectById(id) == null) {
            throw new BusinessException("商品不存在");
        }
        Product product = new Product();
        product.setId(id);
        product.setStatus(status != null && status == 0 ? 0 : 1);
        productMapper.updateById(product);
    }

    public void add(Product product) {
        if (!StringUtils.hasText(product.getName())) {
            throw new BusinessException("请输入商品名称");
        }
        if (existsName(product.getName(), null)) {
            throw new BusinessException("商品名称已存在");
        }
        product.setId(null);
        if (!StringUtils.hasText(product.getCategory())) {
            product.setCategory("其他");
        }
        if (product.getPrice() == null) {
            product.setPrice(java.math.BigDecimal.ZERO);
        }
        if (product.getStock() == null) {
            product.setStock(0);
        }
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        if (product.getImage() == null) {
            product.setImage("");
        }
        if (product.getDescription() == null) {
            product.setDescription("");
        }
        product.setCreateTime(new Date());
        productMapper.insert(product);
    }

    public void update(Product product) {
        if (product.getId() == null || productMapper.selectById(product.getId()) == null) {
            throw new BusinessException("商品不存在");
        }
        if (StringUtils.hasText(product.getName()) && existsName(product.getName(), product.getId())) {
            throw new BusinessException("商品名称已存在");
        }
        productMapper.updateById(product);
    }

    public void delete(Long id) {
        if (productMapper.selectById(id) == null) {
            throw new BusinessException("商品不存在");
        }
        productMapper.deleteById(id);
    }

    private boolean existsName(String name, Long excludeId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>().eq(Product::getName, name);
        if (excludeId != null) {
            wrapper.ne(Product::getId, excludeId);
        }
        return productMapper.selectCount(wrapper) > 0;
    }
}
