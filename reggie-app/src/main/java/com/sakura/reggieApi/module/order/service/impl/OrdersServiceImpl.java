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
     * 提交用户 支付的订单
     * @param token 令牌
     * @param orders 订单对象
     */
    @Override
    public String submitOrders(String token, Orders orders) {
        // 1  获取用户的 id
        Long uid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        // 生成订单号
        Long orderId = IdWorker.getId();

        // 2  根据 用户去查询 用户当前 购物车所拥有的 菜品
        List<ShoppingCart> cartList = shoppingCartMapper.selectList(new QueryWrapper<ShoppingCart>()
                .eq("user_id", uid));
        if (cartList.size() <= 0)
            throw new OrdersServiceException("购物车列表为空, 无法下单 请先提交商品进入购物车!");

        // 保存 购物车id
        List<Long> cartIdList = new ArrayList<>();

        // 2.1 计算订单金额
        AtomicInteger atomic = new AtomicInteger(0);
        List<OrderDetail> orderDetails = cartList.stream().map((item) -> {
            cartIdList.add(item.getId());

            // 封装 订单详情
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

        // 3  向 订单表中插入数据
        // 3.1  查询地址信息 将地址信息封装进 订单对象中
        AddressBook addressBook = addressMapper.selectOne(new QueryWrapper<AddressBook>()
                .eq("id", orders.getAddressBookId()));
        if (addressBook == null)
            throw new OrdersServiceException("传入的地址有误  无法下单");

        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());

        // 3.2 查询用户对象
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("id", uid));
        if (user == null)
            throw new OrdersServiceException("用户信息不正确 无法下单");
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

        // 4  向订单明细表中插入数据
        count += orderDetailMapper.batchInsert(orderDetails);

        // 5  删除购物车中的商品
        count += shoppingCartMapper.delete(new UpdateWrapper<ShoppingCart>().in("id", cartIdList));

        if (count < 1 + cartList.size() + orderDetails.size())
            throw new OrdersServiceException("订单生成失败 服务异常");

        return JsonResponseResult.defaultSuccess("订单生成成功");
    }

    /**
     * 获取最近的 订单信息
     * @param token 令牌
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
            throw new OrdersServiceException("暂无最近订单");
        }

        for (Orders orders : ordersList) {
            QueryWrapper<OrderDetail> detailQueryWrapper = new QueryWrapper<OrderDetail>()
                    .eq("order_id", orders.getId());

            orders.setOrderDetail(orderDetailMapper.selectList(detailQueryWrapper));
        }

        return JsonResponseResult.success(ordersList);
    }

    /**
     * 查询历史信息
     * @param token 令牌
     */
    @Override
    public String listAll(String token) {
        Long uid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));


        QueryWrapper<Orders> queryWrapper = new QueryWrapper<Orders>()
                .eq("user_id", uid);

        List<Orders> ordersList = ordersMapper.selectList(queryWrapper);
        if (ordersList.size() <= 0) {
            throw new OrdersServiceException("暂无订单");
        }

        for (Orders orders : ordersList) {
            QueryWrapper<OrderDetail> detailQueryWrapper = new QueryWrapper<OrderDetail>()
                    .eq("order_id", orders.getId());

            orders.setOrderDetail(orderDetailMapper.selectList(detailQueryWrapper));
        }

        return JsonResponseResult.success(ordersList);
    }

    /**
     * 管理员 分页获取 订单列表
     * @param token 令牌
     * @param curPage 当前页码
     */
    @Override
    public String listPage(String token, Integer curPage) {

        return queryPage(curPage, null);
    }

    /**
     * 根据 条件查询
     * @param token 令牌
     * @param orderDto 条件对象
     */
    @Override
    public String conditionalListPage(String token, OrderDto orderDto) {

        // 订单号
        String number = orderDto.getNumber();
        // 支付时间
        Date checkoutTime = orderDto.getCheckoutTime();
        // 支付方式
        String payMethod = orderDto.getPayMethod();
        // 订单状态
        Integer status = orderDto.getStatus();

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        // 根据 订单号模糊查询
        queryWrapper.like(number != null, Orders::getNumber, number);
        // 订单支付时间筛选
        queryWrapper.eq(checkoutTime != null, Orders::getCheckoutTime, checkoutTime);
        // 订单支付方式
        queryWrapper.eq(payMethod != null, Orders::getPayMethod, payMethod);
        // 订单状态
        queryWrapper.eq(status != null, Orders::getStatus, status);

        List<Orders> ordersList = ordersMapper.selectList(queryWrapper);
        if (ordersList.size() <= 0)
            throw new OrdersServiceException("未搜索到相关数据");

        return JsonResponseResult.success(ordersList);
    }

    /**
     * 根据订单号 查询对应的订单详情
     * @param token 令牌
     * @param orderDto  请求参数对象
     */
    @Override
    public String queryDetail(String token, OrderDto orderDto) {
        String number = orderDto.getNumber();
        if (number == null)
            throw new OrdersServiceException("订单号不能为空");

        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getNumber, number);
        Orders orders = ordersMapper.selectOne(ordersLambdaQueryWrapper);

        if (orders == null)
            throw new OrdersServiceException("未查询到该订单号的订单信息");

        // 查询订单详情 order_detail
        LambdaQueryWrapper<OrderDetail> detailLambdaQueryWrapper = new LambdaQueryWrapper<OrderDetail>()
                .eq(OrderDetail::getOrderId, orders.getNumber());

        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(detailLambdaQueryWrapper);
        orders.setOrderDetail(orderDetailList);

        return JsonResponseResult.success(orders);
    }

    /**
     * 修改订单状态
     * @param token 令牌
     * @param orderDto 请求参数对象
     */
    @Override
    public String updateStatus(String token, OrderDto orderDto) {
        Integer status = orderDto.getStatus();
        String number = orderDto.getNumber();

        if (status == null)
            throw new OrdersServiceException("状态不能为空");

        if (number == null)
            throw new OrdersServiceException("订单号不能为空");

        UpdateWrapper<Orders> updateWrapper = new UpdateWrapper<Orders>()
                .eq("number", number)
                .set("status", status);

        int update = ordersMapper.update(null, updateWrapper);
        if (update <= 0)
            throw new OrdersServiceException("服务异常");

        String msg = "";
        if (status == 3) {
            msg = "派送成功";
        } else if (status == 4) {
            msg = "订单已完成";
        } else if (status == 5) {
            msg = "订单已取消";
        }

        return JsonResponseResult.defaultSuccess(msg);
    }

    private String queryPage(Integer curPage, QueryWrapper<Orders> queryWrapper) {

        Page<Orders> ordersPage = new Page<>(curPage, pageSize);

        Page<Orders> page = ordersMapper.selectPage(ordersPage, queryWrapper);

        if (page.getTotal() <= 0)
            throw new OrdersServiceException("未有任何订单信息");

        return JsonResponseResult.success(page);
    }
}
