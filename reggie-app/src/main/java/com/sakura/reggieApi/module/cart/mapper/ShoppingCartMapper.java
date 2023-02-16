package com.sakura.reggieApi.module.cart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sakura.reggieApi.module.cart.pojo.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author sakura
 * @className ShoppingCartMapper
 * @createTime 2023/2/15
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
