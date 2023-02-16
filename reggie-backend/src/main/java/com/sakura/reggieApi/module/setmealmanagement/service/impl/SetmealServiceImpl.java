package com.sakura.reggieApi.module.setmealmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sakura.reggieApi.common.utils.JsonResponseResult;
import com.sakura.reggieApi.common.utils.TokenUtils;
import com.sakura.reggieApi.exception.SetmealServiceException;
import com.sakura.reggieApi.module.dishmanagement.pojo.Dish;
import com.sakura.reggieApi.module.setmealmanagement.mapper.SetmealDishMapper;
import com.sakura.reggieApi.module.setmealmanagement.mapper.SetmealMapper;
import com.sakura.reggieApi.module.setmealmanagement.pojo.Setmeal;
import com.sakura.reggieApi.module.setmealmanagement.pojo.SetmealDish;
import com.sakura.reggieApi.module.setmealmanagement.service.SetmealService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author sakura
 * @className SetmealServiceImpl
 * @createTime 2023/2/13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
        implements SetmealService {

    @Value("${page.pageSize}")
    private Integer pageSize;

    @Resource
    SetmealMapper setmealMapper;
    @Resource
    SetmealDishMapper setmealDishMapper;

    @Resource
    TokenUtils tokenUtils;

    /**
     * 分页查询套餐列表
     * @param token 令牌
     * @param curPage 当前页码
     */
    @Override
    public String listPage(String token, Integer curPage, String name) {
        tokenUtils.checkToken(token);

        Long eid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<Setmeal>()
                .eq("is_deleted", 0);

        if (!"default".equals(name))
            queryWrapper.like("name", name);


        Page<Setmeal> setmealPage = new Page<>(curPage, pageSize);

        Page<Setmeal> page = setmealMapper.selectPage(setmealPage, queryWrapper);

        if (page.getTotal() <= 0)
            throw new SetmealServiceException("未搜索到套餐, 请先添加套餐");

        return JsonResponseResult.success(page);
    }

    /**
     * 修改 套餐的启售 停售 删除功能
     * @param token 令牌
     * @param setmeal 套餐对象
     */
    @Override
    public String simpleUpdate(String token, Setmeal setmeal) {

        updateSermeal(token, setmeal);

        return JsonResponseResult.defaultSuccess("修改成功");
    }

    /**
     * 批量 修改 售卖状态 或者 删除套餐
     * @param token 令牌
     * @param setmealList 套餐列表
     */
    @Override
    public String batchUpdate(String token, List<Setmeal> setmealList) {

        for (Setmeal setmeal : setmealList) {
            updateSermeal(token, setmeal);
        }

        return JsonResponseResult.defaultSuccess("修改成功");
    }

    /**
     * 添加套餐
     * @param token 令牌
     * @param setmealReq 要添加的套餐对象
     */
    @Override
    public String addSetmeal(String token, Setmeal setmealReq) {
        tokenUtils.checkToken(token);

        Long eid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));
        setmealReq.setCreateTime(new Date());
        setmealReq.setCreateUser(eid);

        // 1, 查看是否重复添加套餐
        QueryWrapper<Setmeal> repeatQueryWrapper = new QueryWrapper<Setmeal>()
                .eq("name", setmealReq.getName())
                .eq("is_deleted", 0);

        Setmeal repeatSermeal = setmealMapper.selectOne(repeatQueryWrapper);
        if (repeatSermeal != null)
            throw new SetmealServiceException("无法重复的套餐, 如需要变更, 请进行对对应的套餐修改");

        // 2 添加数据 setmeal表
        int count = setmealMapper.insert(setmealReq);

        // 3, 给对应的 setmeal_dish 添加数据
        List<SetmealDish> setmealDishList = setmealReq.getSetmealDishList();
        for (SetmealDish setmealDish : setmealDishList) {
            setmealDish.setCreateTime(new Date());
            setmealDish.setCreateUser(eid);
            setmealDish.setSetmealId(setmealReq.getId());

            count += setmealDishMapper.insert(setmealDish);
        }

        if (count < setmealDishList.size() + 1)
            throw new SetmealServiceException("添加失败, 未知原因");

        return JsonResponseResult.defaultSuccess("添加成功");
    }

    /**
     * 查询 套餐的详情
     * @param token 令牌
     * @param name 套餐的名称
     */
    @Override
    public String queryDetail(String token, String name) {
        tokenUtils.checkToken(token);

        if ("".equals(name.trim()))
            throw new SetmealServiceException("套餐名称输入不合法");

        QueryWrapper<Setmeal> setmealQueryWrapper = new QueryWrapper<Setmeal>()
                .eq("name", name)
                .eq("is_deleted", 0);

        Setmeal setmealDB = setmealMapper.selectOne(setmealQueryWrapper);
        if (setmealDB == null)
            throw new SetmealServiceException("未查询到对应名称的套餐, 请确认是否已经删除");

        QueryWrapper<SetmealDish> setmealDishQueryWrapper = new QueryWrapper<SetmealDish>()
                .eq("setmeal_id", setmealDB.getId())
                .eq("is_deleted", 0);

        List<SetmealDish> setmealDishList = setmealDishMapper.selectList(setmealDishQueryWrapper);

        if (setmealDishList.size() <= 0)
            throw new SetmealServiceException("未查询到套餐对应的菜品");

        setmealDB.setSetmealDishList(setmealDishList);

        return JsonResponseResult.success(setmealDB);
    }

    /**
     * 修改套餐信息
     * @param token 令牌
     * @param setmealReq 修改对象
     */
    @Override
    public String updateSermealDetail(String token, Setmeal setmealReq) {
        tokenUtils.checkToken(token);

        Long eid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        // 修改 默认必须修改的信息
        setmealReq.setUpdateUser(eid);
        setmealReq.setUpdateTime(new Date());

        UpdateWrapper<Setmeal> setmealUpdateWrapper = new UpdateWrapper<Setmeal>()
                .eq("id", setmealReq.getId());


        int count = setmealMapper.update(setmealReq, setmealUpdateWrapper);

        List<SetmealDish> setmealDishReqList = setmealReq.getSetmealDishList();

        for (SetmealDish setmealDish : setmealDishReqList) {
            Long id = setmealDish.getId();
            // 判断 id 是否为 null 如果为 null 则说明是新添加的菜品
            if (id == null) {

                setmealDish.setSetmealId(setmealReq.getId());
                setmealDish.setCreateUser(eid);
                setmealDish.setCreateTime(new Date());
                count += setmealDishMapper.insert(setmealDish);
            } else {
                UpdateWrapper<SetmealDish> updateWrapper = new UpdateWrapper<SetmealDish>()
                        .eq("id", setmealDish.getId());

                setmealDish.setUpdateUser(eid);
                setmealDish.setUpdateTime(new Date());

                count += setmealDishMapper.update(setmealDish, updateWrapper);
            }
        }

        if (count < setmealDishReqList.size() + 1) {
            throw new SetmealServiceException("修改失败, 具体原因未知");
        }

        return JsonResponseResult.defaultSuccess("修改成功");
    }

    /**
     * 根据 套餐的分类id 查询对应套餐列表
     * @param token 令牌
     * @param categoryId 分类id
     */
    @Override
    public String listDishByCId(String token, Long categoryId) {
        QueryWrapper<Setmeal> setmealQueryWrapper = new QueryWrapper<Setmeal>()
                .eq("category_id", categoryId)
                .eq("status", 1)
                .eq("is_deleted", 0);


        List<Setmeal> setmealList = setmealMapper.selectList(setmealQueryWrapper);

        if (setmealList.size() <= 0)
            throw new SetmealServiceException("未搜索到对应套餐");

        return JsonResponseResult.success(setmealList);
    }


    /**
     * 修改 套餐的启售 停售 删除功能
    */
    private void updateSermeal(String token, Setmeal setmeal) {
        tokenUtils.checkToken(token);

        Long eid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        // 根据 套餐的名称进行查询
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<Setmeal>()
                .eq("name", setmeal.getName());

        Setmeal setmealDB = setmealMapper.selectOne(queryWrapper);

        // 删除部分
        // 判断 传入的对象 是否 未要删除
        if (setmeal.getIsDeleted() == 1) {
            // 判断数据库中的 套餐的 售卖状态 是否处于 停售
            if (setmealDB.getStatus() == 1){
                // 如果等于 1 则说明正在启售 无法进行删除操作
                throw new SetmealServiceException("当前套餐还处于启售, 无法进行删除");
            }
        }

        // 修改启售状态
        UpdateWrapper<Setmeal> updateWrapper = new UpdateWrapper<Setmeal>()
                .eq("name", setmeal.getName())
                .set("is_deleted", setmeal.getIsDeleted())
                .set("status", setmeal.getStatus())
                .set("update_time", new Date())
                .set("update_user", eid);

        int i = setmealMapper.update(null, updateWrapper);

        if (i <= 0)
            throw new SetmealServiceException("修改失败, 服务器异常");

    }
}
