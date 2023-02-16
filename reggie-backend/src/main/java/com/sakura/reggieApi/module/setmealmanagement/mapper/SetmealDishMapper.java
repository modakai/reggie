package com.sakura.reggieApi.module.setmealmanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sakura.reggieApi.module.setmealmanagement.pojo.Setmeal;
import com.sakura.reggieApi.module.setmealmanagement.pojo.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author sakura
 * @className SetmealDishMapper
 * @createTime 2023/2/13
 */
@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {
}
