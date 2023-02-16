package com.sakura.reggieApi.module.category.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sakura.reggieApi.common.utils.JsonResponseResult;
import com.sakura.reggieApi.common.utils.TokenUtils;
import com.sakura.reggieApi.exception.CategoryException;
import com.sakura.reggieApi.module.category.mapper.CategoryMapper;
import com.sakura.reggieApi.module.category.pojo.Category;
import com.sakura.reggieApi.module.category.service.CategoryService;
import com.sakura.reggieApi.module.dishmanagement.pojo.Dish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author sakura
 * @className CategoryServiceImpl
 * @createTime 2023/2/12
 */
@Service
@Transactional
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {

    @Resource
    TokenUtils tokenUtils;

    @Resource
    CategoryMapper categoryMapper;

    @Value("${page.pageSize}")
    private Integer pageSize;

    /**
     * 添加 商品分类
     * @param token 令牌
     * @param type 类别  1 菜品   2 套餐
     * @param category 分类实体
     */
    @Override
    public String saveCategory(String token, Integer type, Category category) {
        // 1 校验token
        tokenUtils.checkToken(token);

        // 2, 判断 是否添加重复的 或者 套餐
        QueryWrapper<Category> queryWrapper = new QueryWrapper<Category>()
                .eq("name", category.getName());
        Category one = categoryMapper.selectOne(queryWrapper);
        if(one != null)
            throw new CategoryException("无法添加重复的商品");

        // 3, 校验 type 是否 等于 1 | 2 其中一个
        if (type != 1L && type != 2L)
            throw new CategoryException("添加的菜品类型错误");

        Long id = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        category.setCreateUser(id);
        category.setType(type);
        category.setCreateTime(new Date());
        int insert = categoryMapper.insert(category);

        if (insert <= 0)
            throw new RuntimeException("服务异常");

        String msg = type == 1 ? "菜品" : "套餐";

        return JsonResponseResult.defaultSuccess("添加" +msg + "成功");
    }

    /**
     * 分页查询 分类信息
     * @param token 令牌
     * @param curPage 当前页码
     */
    @Override
    public String pageListCategory(String token, Integer curPage) {
        tokenUtils.checkToken(token);

        Page<Category> categoryPage = new Page<>(curPage, pageSize);

        QueryWrapper<Category> queryWrapper = new QueryWrapper<Category>()
                .orderByAsc("sort");

        Page<Category> page = categoryMapper.selectPage(categoryPage, queryWrapper);

        if (page.getTotal() <= 0)
            throw new CategoryException("未搜索到任何商品");

        return JsonResponseResult.success(page);
    }

    /**
     * 删除分类信息
     * @param token 令牌
     * @param id 分类id
     */
    @Override
    public String removeCategory(String token, Integer type, Long id) {
        // 1, 校验token
        tokenUtils.checkToken(token);

        // 2, 判断该 分类id 是属于 菜品分类还是套餐分类
        Long count = 0L;
        if (type == 1) {
            // 2.1 如果为 菜品分类 查询 id 对应的 菜品表 中是否 存在菜品
            count = categoryMapper.selectCountDishByCategoryId(id);
        } else if (type == 2L){
            // 2.1 如果为  套餐分类 则查询 id 对应的 套餐表 是否存在套餐
            count = categoryMapper.selectCountSetMealByCategoryId(id);
        } else {
            throw new CategoryException("要删除的分类的类型有误, 请类型管理员!");
        }

        // 2.1 不存在则删除
        // 2.2 存在则进行 提示: 该菜品分类存在关联的菜品, 无法进行删除
        if (count > 0 )
            throw new CategoryException("该菜品分类存在关联的菜品, 无法进行删除");

        int i = categoryMapper.deleteById(id);
        if (i <= 0)
            throw new CategoryException("服务异常");

        return JsonResponseResult.defaultSuccess("删除成功");
    }

    /**
     * 查询菜品分类的详情
     * @param token 令牌
     * @param name 菜品分类的名称
     */
    @Override
    public String queryCategoryDetail(String token, String name) {
        tokenUtils.checkToken(token);

        QueryWrapper<Category> queryWrapper = new QueryWrapper<Category>()
                .eq("name", name);

        Category category = categoryMapper.selectOne(queryWrapper);

        if (category == null)
            throw new CategoryException("未查询到该菜品分类");


        return JsonResponseResult.success(category);
    }

    /**
     * 修改分类信息
     * @param token 令牌
     * @param category 实体类
     */
    @Override
    public String upateCategory(String token, Category category) {
        tokenUtils.checkToken(token);

        UpdateWrapper<Category> updateWrapper = new UpdateWrapper<Category>()
                .eq("id", category.getId())
                .set("name", category.getName())
                .set("sort", category.getSort())
                .set("update_time", new Date())
                .set("update_user", Long.valueOf(tokenUtils.getMemberIdByJwtToken(token)));

        int i = categoryMapper.update(null, updateWrapper);
        if (i <= 0)
            throw new CategoryException("服务异常");

        return JsonResponseResult.defaultSuccess("修改成功");
    }

    /**
     * 根据类型 来查询分类的 列表
     * @param token 令牌
     * @param type 类型
     */
    @Override
    public String listByType(String token, Integer type) {
        tokenUtils.checkToken(token);

        if (type != 1 && type != 2)
            throw new CategoryException("类型传输错误");

        String typeName;
        if (type == 1) {
           typeName = "菜品分类";
        } else  {
            typeName = "套餐分类";
        }

        QueryWrapper<Category> queryWrapper = new QueryWrapper<Category>()
                .eq("type", type);

        List<Category> list = categoryMapper.selectList(queryWrapper);

        if (list.size() <= 0)
            throw new CategoryException("系统中没有" + typeName + "数据, 请先进行添加!");

        return JsonResponseResult.success(list);
    }

    /**
     * 根据 id 获取分类的名称
     * @param token 令牌
     * @param id 分类id
     */
    @Override
    public String queryName(String token, Long id) {
        tokenUtils.checkToken(token);

        QueryWrapper<Category> queryWrapper = new QueryWrapper<Category>()
                .eq("id", id);

        Category category = categoryMapper.selectOne(queryWrapper);
        if (category == null)
            throw new CategoryException("没有该类型分类");

        return JsonResponseResult.success(category.getName());
    }

    /**
     * 查询 分类 以及分类相关菜品
     * @return
     */
    @Override
    public String listCategoryAndDish(String token) {
        tokenUtils.checkToken(token);

        QueryWrapper<Category> queryWrapper = new QueryWrapper<Category>()
                .orderByAsc("sort");

        List<Category> categoryList = categoryMapper.selectList(queryWrapper);

        for (Category category : categoryList) {
            List<Dish> dishes = categoryMapper.selectCorrelationDish(category.getId());

            category.setDishList(dishes);
        }

        return JsonResponseResult.success(categoryList);
    }


}
