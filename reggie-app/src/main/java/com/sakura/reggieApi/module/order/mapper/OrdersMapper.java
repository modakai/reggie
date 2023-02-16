package com.sakura.reggieApi.module.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sakura.reggieApi.module.order.pojo.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author sakura
 * @className OrdersMapper
 * @createTime 2023/2/15
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

}
