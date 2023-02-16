package com.sakura.reggieApi.module.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sakura.reggieApi.common.utils.JsonResponseResult;
import com.sakura.reggieApi.common.utils.TokenUtils;
import com.sakura.reggieApi.module.cart.mapper.ShoppingCartMapper;
import com.sakura.reggieApi.module.cart.pojo.ShoppingCart;
import com.sakura.reggieApi.module.cart.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author sakura
 * @className ShoppingCartServiceImpl
 * @createTime 2023/2/15
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
        implements ShoppingCartService {

    @Resource
    TokenUtils tokenUtils;

    @Resource
    ShoppingCartMapper shoppingCartMapper;


    /**
     * 添加购物车
     * @param token 令牌
     * @param shoppingCart 添加购物车对象
     */
    @Override
    public String addCart(String token, ShoppingCart shoppingCart) {

        // 获取用户id
        Long uid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        shoppingCart.setCreateTime(new Date());
        shoppingCart.setUserId(uid);

        int i = shoppingCartMapper.insert(shoppingCart);
        if (i <= 0)
            throw new RuntimeException("添加失败 服务异常");

        return JsonResponseResult.defaultSuccess("添加成功");
    }

    /**
     * 查询购物车
     * @param token 令牌
     */
    @Override
    public String listCart(String token) {
        // 获取用户id
        Long uid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<ShoppingCart>()
                .eq("user_id", uid)
                .orderByAsc("create_time");

        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(queryWrapper);
        if (shoppingCarts.size() <= 0)
            throw new RuntimeException("未添加任何数据进入购物车");

        return JsonResponseResult.success(shoppingCarts);
    }

    /**
     * 更新用户的购物车信息
     * @param token 令牌
     * @param shoppingCart 更新对象
     */
    @Override
    public String updateCart(String token, ShoppingCart shoppingCart) {
        // 获取用户id
        Long uid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        // 为 0 时删除
        if (shoppingCart.getNumber() == 0) {
            QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<ShoppingCart>()
                    .eq("user_id", uid)
                    .eq("name", shoppingCart.getName());
            shoppingCartMapper.delete(queryWrapper);
        }

        UpdateWrapper<ShoppingCart> updateWrapper = new UpdateWrapper<ShoppingCart>()
                .eq("user_id", uid)
                .eq("name", shoppingCart.getName());

        int update = shoppingCartMapper.update(shoppingCart, updateWrapper);

        if (update <= 0)
            throw new RuntimeException("服务异常");

        return JsonResponseResult.defaultSuccess("修改成功");
    }

    /**
     * 删除购物车信息
     * @param token 令牌
     * @param cartIdList 删除列表
     */
    @Override
    public String delCart(String token, List<Long> cartIdList) {

        // 获取用户id
        Long uid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));


        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<ShoppingCart>()
                .eq("user_id", uid)
                .in("id", cartIdList);

        int delete = shoppingCartMapper.delete(queryWrapper);
        if (delete < cartIdList.size()) {
            throw new RuntimeException("删除失败, 服务异常");
        }

        return JsonResponseResult.defaultSuccess("删除成功");
    }
}
