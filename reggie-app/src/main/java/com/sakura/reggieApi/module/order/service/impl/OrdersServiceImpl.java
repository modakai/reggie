package com.sakura.reggieApi.module.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sakura.reggieApi.common.pojo.User;
import com.sakura.reggieApi.common.utils.JsonResponseResult;
import com.sakura.reggieApi.common.utils.TokenUtils;
import com.sakura.reggieApi.exception.OrdersServiceException;
import com.sakura.reggieApi.module.address.mapper.AddressMapper;
import com.sakura.reggieApi.module.address.pojo.AddressBook;
import com.sakura.reggieApi.module.appuser.mapper.UserMapper;
import com.sakura.reggieApi.module.cart.mapper.ShoppingCartMapper;
import com.sakura.reggieApi.module.cart.pojo.ShoppingCart;
import com.sakura.reggieApi.module.order.dto.OrderDto;
import com.sakura.reggieApi.module.order.mapper.OrderDetailMapper;
import com.sakura.reggieApi.module.order.mapper.OrdersMapper;
import com.sakura.reggieApi.module.order.pojo.OrderDetail;
import com.sakura.reggieApi.module.order.pojo.Orders;
import com.sakura.reggieApi.module.order.service.OrdersService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author sakura
 * @className OrdersServiceImpl
 * @createTime 2023/2/15
 */
@Service
@Transactional
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
        implements OrdersService {

    @Value("${page.pageSize}")
    private Integer pageSize;

    @Resource
    TokenUtils tokenUtils;


    @Resource
    ShoppingCartMapper shoppingCartMapper;
    @Resource
    AddressMapper addressMapper;
    @Resource
    UserMapper userMapper;
    @Resource
    OrdersMapper ordersMapper;
    @Resource
    OrderDetailMapper orderDetailMapper;

    /**
     * ???????????? ???????????????
     * @param token ??????
     * @param orders ????????????
     */
    @Override
    public String submitOrders(String token, Orders orders) {
        // 1  ??????????????? id
        Long uid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        // ???????????????
        Long orderId = IdWorker.getId();

        // 2  ?????? ??????????????? ???????????? ????????????????????? ??????
        List<ShoppingCart> cartList = shoppingCartMapper.selectList(new QueryWrapper<ShoppingCart>()
                .eq("user_id", uid));
        if (cartList.size() <= 0)
            throw new OrdersServiceException("?????????????????????, ???????????? ?????????????????????????????????!");

        // ?????? ?????????id
        List<Long> cartIdList = new ArrayList<>();

        // 2.1 ??????????????????
        AtomicInteger atomic = new AtomicInteger(0);
        List<OrderDetail> orderDetails = cartList.stream().map((item) -> {
            cartIdList.add(item.getId());

            // ?????? ????????????
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setId(IdWorker.getId());
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());

            atomic.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        // 3  ??? ????????????????????????
        // 3.1  ?????????????????? ???????????????????????? ???????????????
        AddressBook addressBook = addressMapper.selectOne(new QueryWrapper<AddressBook>()
                .eq("id", orders.getAddressBookId()));
        if (addressBook == null)
            throw new OrdersServiceException("?????????????????????  ????????????");

        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());

        // 3.2 ??????????????????
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("id", uid));
        if (user == null)
            throw new OrdersServiceException("????????????????????? ????????????");
        orders.setUserId(user.getId());
        orders.setPhone(user.getPhone());
        orders.setUserName(user.getUsername());
        orders.setAmount(new BigDecimal(atomic.get()));

        orders.setOrderTime(new Date());
        orders.setId(orderId);
        orders.setNumber(String.valueOf(orderId));
        orders.setStatus(2);
        orders.setCheckoutTime(new Date());
        int count = ordersMapper.insert(orders);

        // 4  ?????????????????????????????????
        count += orderDetailMapper.batchInsert(orderDetails);

        // 5  ???????????????????????????
        count += shoppingCartMapper.delete(new UpdateWrapper<ShoppingCart>().in("id", cartIdList));

        if (count < 1 + cartList.size() + orderDetails.size())
            throw new OrdersServiceException("?????????????????? ????????????");

        return JsonResponseResult.defaultSuccess("??????????????????");
    }

    /**
     * ??????????????? ????????????
     * @param token ??????
     */
    @Override
    public String listLatelyOrders(String token) {

        Long uid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        LocalDateTime nowTime = LocalDateTime.now();

        QueryWrapper<Orders> queryWrapper = new QueryWrapper<Orders>()
                .eq("user_id", uid)
                .between("order_time", nowTime.minusDays(7), nowTime);

        List<Orders> ordersList = ordersMapper.selectList(queryWrapper);

        if (ordersList.size() <= 0) {
            throw new OrdersServiceException("??????????????????");
        }

        for (Orders orders : ordersList) {
            QueryWrapper<OrderDetail> detailQueryWrapper = new QueryWrapper<OrderDetail>()
                    .eq("order_id", orders.getId());

            orders.setOrderDetail(orderDetailMapper.selectList(detailQueryWrapper));
        }

        return JsonResponseResult.success(ordersList);
    }

    /**
     * ??????????????????
     * @param token ??????
     */
    @Override
    public String listAll(String token) {
        Long uid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));


        QueryWrapper<Orders> queryWrapper = new QueryWrapper<Orders>()
                .eq("user_id", uid);

        List<Orders> ordersList = ordersMapper.selectList(queryWrapper);
        if (ordersList.size() <= 0) {
            throw new OrdersServiceException("????????????");
        }

        for (Orders orders : ordersList) {
            QueryWrapper<OrderDetail> detailQueryWrapper = new QueryWrapper<OrderDetail>()
                    .eq("order_id", orders.getId());

            orders.setOrderDetail(orderDetailMapper.selectList(detailQueryWrapper));
        }

        return JsonResponseResult.success(ordersList);
    }

    /**
     * ????????? ???????????? ????????????
     * @param token ??????
     * @param curPage ????????????
     */
    @Override
    public String listPage(String token, Integer curPage) {

        return queryPage(curPage, null);
    }

    /**
     * ?????? ????????????
     * @param token ??????
     * @param orderDto ????????????
     */
    @Override
    public String conditionalListPage(String token, OrderDto orderDto) {

        // ?????????
        String number = orderDto.getNumber();
        // ????????????
        Date checkoutTime = orderDto.getCheckoutTime();
        // ????????????
        String payMethod = orderDto.getPayMethod();
        // ????????????
        Integer status = orderDto.getStatus();

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        // ?????? ?????????????????????
        queryWrapper.like(number != null, Orders::getNumber, number);
        // ????????????????????????
        queryWrapper.eq(checkoutTime != null, Orders::getCheckoutTime, checkoutTime);
        // ??????????????????
        queryWrapper.eq(payMethod != null, Orders::getPayMethod, payMethod);
        // ????????????
        queryWrapper.eq(status != null, Orders::getStatus, status);

        List<Orders> ordersList = ordersMapper.selectList(queryWrapper);
        if (ordersList.size() <= 0)
            throw new OrdersServiceException("????????????????????????");

        return JsonResponseResult.success(ordersList);
    }

    /**
     * ??????????????? ???????????????????????????
     * @param token ??????
     * @param orderDto  ??????????????????
     */
    @Override
    public String queryDetail(String token, OrderDto orderDto) {
        String number = orderDto.getNumber();
        if (number == null)
            throw new OrdersServiceException("?????????????????????");

        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getNumber, number);
        Orders orders = ordersMapper.selectOne(ordersLambdaQueryWrapper);

        if (orders == null)
            throw new OrdersServiceException("???????????????????????????????????????");

        // ?????????????????? order_detail
        LambdaQueryWrapper<OrderDetail> detailLambdaQueryWrapper = new LambdaQueryWrapper<OrderDetail>()
                .eq(OrderDetail::getOrderId, orders.getNumber());

        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(detailLambdaQueryWrapper);
        orders.setOrderDetail(orderDetailList);

        return JsonResponseResult.success(orders);
    }

    /**
     * ??????????????????
     * @param token ??????
     * @param orderDto ??????????????????
     */
    @Override
    public String updateStatus(String token, OrderDto orderDto) {
        Integer status = orderDto.getStatus();
        String number = orderDto.getNumber();

        if (status == null)
            throw new OrdersServiceException("??????????????????");

        if (number == null)
            throw new OrdersServiceException("?????????????????????");

        UpdateWrapper<Orders> updateWrapper = new UpdateWrapper<Orders>()
                .eq("number", number)
                .set("status", status);

        int update = ordersMapper.update(null, updateWrapper);
        if (update <= 0)
            throw new OrdersServiceException("????????????");

        String msg = "";
        if (status == 3) {
            msg = "????????????";
        } else if (status == 4) {
            msg = "???????????????";
        } else if (status == 5) {
            msg = "???????????????";
        }

        return JsonResponseResult.defaultSuccess(msg);
    }

    private String queryPage(Integer curPage, QueryWrapper<Orders> queryWrapper) {

        Page<Orders> ordersPage = new Page<>(curPage, pageSize);

        Page<Orders> page = ordersMapper.selectPage(ordersPage, queryWrapper);

        if (page.getTotal() <= 0)
            throw new OrdersServiceException("????????????????????????");

        return JsonResponseResult.success(page);
    }
}
