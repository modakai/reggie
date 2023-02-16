package com.sakura.reggieApi.module.setmealmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sakura.reggieApi.module.setmealmanagement.pojo.Setmeal;

import java.util.List;

/**
 * @author sakura
 * @className SetmealService
 * @createTime 2023/2/13
 */
public interface SetmealService extends IService<Setmeal> {
    String listPage(String token, Integer curPage, String name);

    String simpleUpdate(String token, Setmeal setmeal);

    String batchUpdate(String token, List<Setmeal> setmealList);

    String addSetmeal(String token, Setmeal setmealReq);

    String queryDetail(String token, String name);

    String updateSermealDetail(String token, Setmeal setmealReq);
}
