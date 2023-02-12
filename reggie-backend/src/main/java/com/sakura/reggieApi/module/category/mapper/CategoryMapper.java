package com.sakura.reggieApi.module.category.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sakura.reggieApi.module.category.pojo.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author sakura
 * @className CategoryMapper
 * @createTime 2023/2/12
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 根据 分类id 查询 是否存在关联的菜品
     * @param id 分类id
     * @return 记录数
     */
    @Select("select count(*) from dish where category_id = #{id}")
    Long selectCountDishByCategoryId(Long id);


    /**
     * 根据分类 id 查询 是否存在相关的套餐
     * @param id 分类id
     * @return 记录数
     */
    @Select("select count(*) from setmeal where category_id = #{id};")
    Long selectCountSetMealByCategoryId(Long id);
}
