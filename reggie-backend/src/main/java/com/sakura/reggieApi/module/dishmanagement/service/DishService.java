package com.sakura.reggieApi.module.dishmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sakura.reggieApi.module.dishmanagement.pojo.Dish;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author sakura
 * @className DishService
 * @createTime 2023/2/12
 */
public interface DishService extends IService<Dish> {
    String uploadFile(String token, MultipartFile dishFile);

    String listFlavor(String token);

    String saveDishAndDishFlavor(String token, Dish dish);

    String listPage(String token, Integer curPage, String dishName);

    String simpleUpdateDish(String token, Dish reqDish);

    String queryDetail(String token, String name);


    String updateDish(String token, Dish dish);

    String batchUpateDish(String token, List<Dish> dishList);

    String listDishByCId(String token, Long categoryId);
}
