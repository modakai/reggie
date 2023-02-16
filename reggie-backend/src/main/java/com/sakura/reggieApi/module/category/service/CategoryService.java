package com.sakura.reggieApi.module.category.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sakura.reggieApi.module.category.pojo.Category;

/**
 * @author sakura
 * @className CategoryService
 * @createTime 2023/2/12
 */
public interface CategoryService extends IService<Category> {
    String saveCategory(String token, Integer type, Category category);

    String pageListCategory(String token, Integer curPage);

    String removeCategory(String token, Integer type, Long id);

    String queryCategoryDetail(String token, String name);

    String upateCategory(String token, Category category);

    String listByType(String token, Integer type);

    String queryName(String token, Long id);

    String listCategoryAndDish(String token);
}
