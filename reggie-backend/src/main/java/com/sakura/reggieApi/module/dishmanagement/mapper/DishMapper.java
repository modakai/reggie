package com.sakura.reggieApi.module.dishmanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sakura.reggieApi.module.dishmanagement.pojo.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author sakura
 * @className DishMapper
 * @createTime 2023/2/12
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
