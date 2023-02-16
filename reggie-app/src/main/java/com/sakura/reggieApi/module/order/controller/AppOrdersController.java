package com.sakura.reggieApi.module.order.controller;

import com.sakura.reggieApi.common.anno.LogAnnotation;
import com.sakura.reggieApi.common.utils.TokenUtils;
import com.sakura.reggieApi.module.order.pojo.Orders;
import com.sakura.reggieApi.module.order.service.OrdersService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author sakura
 * @className AppOrdersController
 * @createTime 2023/2/15
 */
@RestController
@RequestMapping("/app/order")
public class AppOrdersController {

    private static final String HERDER_TOKEN_KEY = TokenUtils.REQUEST_HEADER_TOKEN_KEY;

    @Resource
    OrdersService ordersService;

    @LogAnnotation("查询历史订单信息")
    @GetMapping("/listAll")
    public String doListAll(@RequestHeader(HERDER_TOKEN_KEY) String token) {
        return ordersService.listAll(token);
    }

    @LogAnnotation("查询最近订单列表")
    @GetMapping("/lately")
    public String doLatelyOrder(@RequestHeader(HERDER_TOKEN_KEY) String token) {
        return ordersService.listLatelyOrders(token);
    }

    @LogAnnotation("用户提交订单")
    @PostMapping("/submit")
    public String doSubmitOrder(@RequestHeader(HERDER_TOKEN_KEY) String token,
                                @RequestBody Orders orders) {
        return ordersService.submitOrders(token, orders);
    }
}
