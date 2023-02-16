package com.sakura.reggieApi.module.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sakura.reggieApi.module.order.pojo.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author sakura
 * @className OrderDetailMapper
 * @createTime 2023/2/15
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

    Integer batchInsert(@Param("orderDetailList") List<OrderDetail> orderDetailList);
}
