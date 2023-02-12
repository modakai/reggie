package com.sakura.reggieApi.module.category.controller;

import com.sakura.reggieApi.common.anno.LogAnnotation;
import com.sakura.reggieApi.common.utils.TokenUtils;
import com.sakura.reggieApi.module.category.pojo.Category;
import com.sakura.reggieApi.module.category.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

/**
 * @author sakura
 * @className CategoryController
 * @createTime 2023/2/12
 */
@RestController
@RequestMapping("/backend/category")
public class CategoryController {

    private static final String HERDER_TOKEN_KEY = TokenUtils.REQUEST_HEADER_TOKEN_KEY;

    @Autowired
    CategoryService categoryService;


    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN')")
    @PutMapping("/update")
    public String doCategoryUpdate(@RequestHeader(HERDER_TOKEN_KEY) String token,
                                   @RequestBody Category category) {
        return categoryService.upateCategory(token, category);
    }

    @LogAnnotation("查询分类详情")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN')")
    @GetMapping("/queryDetail/{name}")
    public String doCategoryDetail(@RequestHeader(HERDER_TOKEN_KEY) String token,
                                   @PathVariable("name") String name) {
        return categoryService.queryCategoryDetail(token, name);
    }

    @LogAnnotation("删除分类信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN')")
    @DeleteMapping("/remove/{type}/{categoryId}")
    public String doRemoveCategory(@RequestHeader(HERDER_TOKEN_KEY) String token,
                                   @PathVariable("type") Integer type,
                                   @PathVariable("categoryId") Long id) {
        return categoryService.removeCategory(token, type, id);
    }

    @LogAnnotation("查询分类管理")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN')")
    @GetMapping("/list/{curPage}")
    public String doListCategory(@RequestHeader(HERDER_TOKEN_KEY) String token,
                                 @PathVariable("curPage") Integer curPage) {
        return categoryService.pageListCategory(token, curPage);
    }

    @LogAnnotation("添加分类信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN')")
    @PostMapping("/add/{type}")
    public String doAddCategory(@RequestHeader(HERDER_TOKEN_KEY) String token,
                                @PathVariable("type") Integer type,
                                @RequestBody Category category) {
        return categoryService.saveCategory(token, type, category);
    }
}
