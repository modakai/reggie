package com.sakura.reggieApi.module.cart.controller;

import com.sakura.reggieApi.common.anno.LogAnnotation;
import com.sakura.reggieApi.common.utils.TokenUtils;
import com.sakura.reggieApi.module.cart.pojo.ShoppingCart;
import com.sakura.reggieApi.module.cart.service.ShoppingCartService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sakura
 * @className ShoppingCartController
 * @createTime 2023/2/15
 */
@RestController
@RequestMapping("/app/cart")
public class ShoppingCartController {

    private static final String HERDER_TOKEN_KEY = TokenUtils.REQUEST_HEADER_TOKEN_KEY;

    @Resource
    ShoppingCartService shoppingCartService;

    @LogAnnotation("删除购物车信息")
    @DeleteMapping
    public String doDelete(@RequestHeader(HERDER_TOKEN_KEY) String token,
                           @RequestBody List<Long> cartIdList) {
        return shoppingCartService.delCart(token, cartIdList);
    }

    @LogAnnotation("修改购物车信息")
    @PutMapping
    public String doUpdate(@RequestHeader(HERDER_TOKEN_KEY) String token,
                           @RequestBody ShoppingCart shoppingCart) {
        return shoppingCartService.updateCart(token, shoppingCart);
    }

    @LogAnnotation("查询购物车")
    @GetMapping
    public String doListCart(@RequestHeader(HERDER_TOKEN_KEY) String token) {
        return shoppingCartService.listCart(token);
    }

    @LogAnnotation("添加购物车")
    @PostMapping
    public String doAdd(@RequestHeader(HERDER_TOKEN_KEY) String token,
                        @RequestBody ShoppingCart shoppingCart) {
        return shoppingCartService.addCart(token, shoppingCart);
    }
}
