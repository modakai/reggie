package com.sakura.reggieApi.module.dishmanagement.controller;

import com.sakura.reggieApi.common.anno.LogAnnotation;
import com.sakura.reggieApi.common.utils.TokenUtils;
import com.sakura.reggieApi.module.dishmanagement.pojo.Dish;
import com.sakura.reggieApi.module.dishmanagement.service.DishService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sakura
 * @className DishController
 * @createTime 2023/2/12
 */
@RestController
@RequestMapping("/backend/dish")
public class DishController {

    private static final String HERDER_TOKEN_KEY = TokenUtils.REQUEST_HEADER_TOKEN_KEY;

    @Resource
    DishService dishService;

    @LogAnnotation("根据菜品分类的Id 查询对应的菜品列表")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN')")
    @GetMapping("/listByCategoryId/{categoryId}")
    public String doListDishByCategoryId(@RequestHeader(HERDER_TOKEN_KEY) String token,
                                         @PathVariable("categoryId") Long categoryId) {
        return dishService.listDishByCId(token, categoryId);
    }


    @LogAnnotation("批量修改菜品信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN')")
    @PutMapping("/batchUpdate")
    public String doBatchSimpleUpdate(@RequestHeader(HERDER_TOKEN_KEY) String token,
                                      @RequestBody List<Dish> dishList) {
        return dishService.batchUpateDish(token, dishList);
    }


    @LogAnnotation("修改菜品的具体信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN')")
    @PutMapping("/update")
    public String doUpdateDish(@RequestHeader(HERDER_TOKEN_KEY) String token,
                               @RequestBody Dish dish) {
        return dishService.updateDish(token, dish);
    }

    @LogAnnotation("查看菜品详情")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN', 'USER')")
    @GetMapping("/detail/{name}")
    public String doDishDetail(@RequestHeader(HERDER_TOKEN_KEY) String token,
                               @PathVariable("name") String name) {
        return dishService.queryDetail(token, name);
    }

    @LogAnnotation("修改售卖状态,删除菜品")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN')")
    @PutMapping("/simpleUpdate")
    public String doSimpleUpdateDish(@RequestHeader(HERDER_TOKEN_KEY) String token,
                                     @RequestBody Dish reqDish) {
        return dishService.simpleUpdateDish(token, reqDish);
    }


    @LogAnnotation("分页查询菜品列表")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN')")
    @GetMapping("/list/{curPage}/{dishName}")
    public String doListPage(@RequestHeader(HERDER_TOKEN_KEY) String token,
                             @PathVariable("curPage") Integer curPage,
                             @PathVariable("dishName") String dishName) {
        return dishService.listPage(token, curPage, dishName);
    }

    @LogAnnotation("添加菜品")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN')")
    @PostMapping("/save")
    public String doAddDish(@RequestHeader(HERDER_TOKEN_KEY) String token,
                            @RequestBody Dish dish) {

       return dishService.saveDishAndDishFlavor(token, dish);

    }

    @LogAnnotation("查询菜品的口味列表")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN')")
    @GetMapping("/listFlavor")
    public String doListFlavor(@RequestHeader(HERDER_TOKEN_KEY) String token) {
        return dishService.listFlavor(token);
    }

}
