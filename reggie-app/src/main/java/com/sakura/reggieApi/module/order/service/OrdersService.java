package com.sakura.reggieApi.module.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sakura.reggieApi.module.order.dto.OrderDto;
import com.sakura.reggieApi.module.order.pojo.Orders;

/**
 * @author sakura
 * @className OrdersService
 * @createTime 2023/2/15
 */
public interface OrdersService extends IService<Orders> {
    String submitOrders(String token, Orders orders);

    String listLatelyOrders(String token);

    String listAll(String token);

    String listPage(String token, Integer curPage);

    String conditionalListPage(String token, OrderDto orderDto);

    String queryDetail(String token, OrderDto orderDto);

    String updateStatus(String token, OrderDto orderDto);
}
