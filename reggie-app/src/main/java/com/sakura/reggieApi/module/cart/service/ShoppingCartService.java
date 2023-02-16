package com.sakura.reggieApi.module.cart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sakura.reggieApi.module.cart.pojo.ShoppingCart;

import java.util.List;

/**
 * @author sakura
 * @className ShoppingCartService
 * @createTime 2023/2/15
 */
public interface ShoppingCartService extends IService<ShoppingCart> {
    String addCart(String token, ShoppingCart shoppingCart);

    String listCart(String token);

    String updateCart(String token, ShoppingCart shoppingCart);

    String delCart(String token, List<Long> cartIdList);
}
