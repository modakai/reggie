package com.sakura.reggieApi.module.dishmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sakura.reggieApi.common.utils.JsonResponseResult;
import com.sakura.reggieApi.common.utils.RedisUtils;
import com.sakura.reggieApi.common.utils.TokenUtils;
import com.sakura.reggieApi.common.utils.UploadFileUtil;
import com.sakura.reggieApi.exception.DishManagementException;
import com.sakura.reggieApi.module.dishmanagement.mapper.DishFlavorMapper;
import com.sakura.reggieApi.module.dishmanagement.mapper.DishMapper;
import com.sakura.reggieApi.module.dishmanagement.mapper.FlavorMapper;
import com.sakura.reggieApi.module.dishmanagement.pojo.Dish;
import com.sakura.reggieApi.module.dishmanagement.pojo.DishFlavor;
import com.sakura.reggieApi.module.dishmanagement.pojo.Flavor;
import com.sakura.reggieApi.module.dishmanagement.service.DishService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author sakura
 * @className DishServiceImpl
 * @createTime 2023/2/12
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
        implements DishService {

    @Value("${page.pageSize}")
    private Integer pageSize;

    @Resource
    UploadFileUtil uploadFileUtil;
    @Resource
    RedisUtils redisUtils;
    @Resource
    TokenUtils tokenUtils;
    @Resource
    DishMapper dishMapper;
    @Resource
    FlavorMapper flavorMapper;
    @Resource
    DishFlavorMapper dishFlavorMapper;

    /**
     * ??????????????? ????????????
     *
     * @param token    ??????
     * @param dishFile ??????
     */
    @Override
    public String uploadFile(String token, MultipartFile dishFile) {

        tokenUtils.checkToken(token);

        return uploadFileUtil.uploadFile(dishFile);
    }

    /**
     * ??????????????????
     *
     * @param token ??????
     */
    @Override
    public String listFlavor(String token) {
        tokenUtils.checkToken(token);

        List<Flavor> list = flavorMapper.selectList(new QueryWrapper<Flavor>().eq("is_del", 0));

        if (list.size() <= 0)
            throw new DishManagementException("????????????????????????????????????, ????????????????????????");

        return JsonResponseResult.success(list);
    }

    /**
     * ????????????
     *
     * @param token ??????
     * @param dish  ??????
     */
    @Override
    public String saveDishAndDishFlavor(String token, Dish dish) {
        tokenUtils.checkToken(token);

        Long eid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));
        dish.setCreateTime(new Date());
        dish.setCreateUser(eid);


        // 1, ?????? dish ??? name ?????????????????????????????????
        QueryWrapper<Dish> dishQueryWrapper = new QueryWrapper<Dish>()
                .eq("name", dish.getName())
                .eq("is_deleted", 0);

        Dish dishDb = dishMapper.selectOne(dishQueryWrapper);
        if (dishDb != null)
            throw new DishManagementException("?????????????????????????????????, ??????????????????");

        // 2, ????????????
        int count = dishMapper.insert(dish);

        // 3, ??????  dish ??? ????????????????????? ??? dish_flavor ????????????
        List<Flavor> flavorList = dish.getFlavorList();
        for (Flavor flavor : flavorList) {
            String flavorName = flavor.getName();
            String flavorValue = flavor.getValue();

            DishFlavor dishFlavor = new DishFlavor(dish.getId(), flavorName, flavorValue, new Date(), eid, false);
            count += dishFlavorMapper.insert(dishFlavor);
        }

        if (count < flavorList.size() + 1)
            throw new DishManagementException("???????????? ??????????????????, ??????????????????");

        // ????????????
        redisUtils.delete("dish_" + dish.getCategoryId());

        return JsonResponseResult.defaultSuccess("????????????");
    }

    /**
     * ????????????????????????
     *
     * @param token    ??????
     * @param curPage  ????????????
     * @param dishName ????????????
     */
    @Override
    public String listPage(String token, Integer curPage, String dishName) {
        tokenUtils.checkToken(token);

        QueryWrapper<Dish> queryWrapper = new QueryWrapper<Dish>()
                .eq("is_deleted", 0);

        if (!"default".equals(dishName)) {
            queryWrapper.like("name", dishName);
        }

        queryWrapper.orderByDesc("create_time");
        Page<Dish> dishPage = new Page<Dish>(curPage, pageSize);

        Page<Dish> page = dishMapper.selectPage(dishPage, queryWrapper);

        if (page.getTotal() <= 0)
            throw new DishManagementException("???????????????????????????");

        return JsonResponseResult.success(page);
    }

    /**
     * ??????????????? ????????????
     * ?????? / ??????
     * ??????
     *
     * @param token   ??????
     * @param reqDish ?????????????????????
     */
    @Override
    public String simpleUpdateDish(String token, Dish reqDish) {
        tokenUtils.checkToken(token);

        Integer i = simpleUpdate(reqDish, token);

        if (i <= 0)
            throw new DishManagementException("????????????, ??????????????? ??????????????????");

        return JsonResponseResult.defaultSuccess("????????????");
    }

    /**
     * ??????????????????
     *
     * @param token ??????
     * @param name  ????????????
     */
    @Override
    public String queryDetail(String token, String name) {
        tokenUtils.checkToken(token);

        if ("".equalsIgnoreCase(name.trim()))
            throw new DishManagementException("??????????????????????????????");

        QueryWrapper<Dish> queryWrapper = new QueryWrapper<Dish>()
                .eq("name", name);

        Dish dish = dishMapper.selectOne(queryWrapper);

        if (dish == null)
            throw new DishManagementException("?????????????????????, ?????????????????? ??????????????????????????????");

        QueryWrapper<DishFlavor> flavorQueryWrapper = new QueryWrapper<DishFlavor>()
                .in("dish_id", dish.getId());

        List<DishFlavor> dishFlavorList = dishFlavorMapper.selectList(flavorQueryWrapper);

        List<Flavor> flavorList = new ArrayList<>();
        for (DishFlavor dishFlavor : dishFlavorList) {
            Long id = dishFlavor.getId();
            String flavorName = dishFlavor.getName();
            String value = dishFlavor.getValue();
            Boolean isDeleted = dishFlavor.getIsDeleted();
            Flavor flavor = new Flavor(id, flavorName, value, isDeleted);
            flavorList.add(flavor);
        }

        dish.setFlavorList(flavorList);

        return JsonResponseResult.success(dish);
    }

    /**
     * ??????????????????
     *
     * @param token ??????
     * @param dish  ??????
     */
    @Override
    public String updateDish(String token, Dish dish) {
        tokenUtils.checkToken(token);

        Long eid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        dish.setUpdateTime(new Date());
        dish.setUpdateUser(eid);
        UpdateWrapper<Dish> updateWrapper = new UpdateWrapper<Dish>()
                .eq("id", dish.getId())
                .set("name", dish.getName())
                .set("category_id", dish.getCategoryId())
                .set("price", dish.getPrice())
                .set("image", dish.getImage())
                .set("description", dish.getDescription())
                .set("update_time", new Date())
                .set("update_user", eid);

        int count = dishMapper.update(null, updateWrapper);

        // 2, ?????? dish_flavor ????????????
        List<Flavor> flavorList = dish.getFlavorList();
        // 2.2 ?????? ?????? id ??? flavor
        List<DishFlavor> dishFlavorList = new ArrayList<>();
        for (Flavor flavor : flavorList) {
            // 2.1 ?????????  dishFlavor ??????
            Long id = flavor.getId();
            String name = flavor.getName();
            String flavorValue = flavor.getValue();
            Boolean del = flavor.getDel();

            if (id == null) {
                DishFlavor dishFlavor = new DishFlavor(dish.getId(), name, flavorValue, new Date(), eid, del);
                count += dishFlavorMapper.insert(dishFlavor);
            } else {
                // 2.3 id ?????? null ???????????????
                DishFlavor dishFlavor = new DishFlavor(id, dish.getId(), name, flavorValue, new Date(), eid, del);
                UpdateWrapper<DishFlavor> wrapper = new UpdateWrapper<DishFlavor>()
                        .eq("name", name);
                count += dishFlavorMapper.update(dishFlavor, wrapper);
            }
        }

        if (count < flavorList.size() + 1) {
            throw new DishManagementException("????????????");
        }

        // ????????????
        redisUtils.delete("dish_" + dish.getCategoryId());

        return JsonResponseResult.defaultSuccess("????????????");
    }

    /**
     * ????????????
     *
     * @param token    ??????
     * @param dishList ????????????
     */
    @Override
    public String batchUpateDish(String token, List<Dish> dishList) {

        tokenUtils.checkToken(token);

        int count = 0;

        for (Dish dish : dishList) {
            count += simpleUpdate(dish, token);
        }

        if (count <= 0) {
            throw new DishManagementException("???????????????");
        }

        return JsonResponseResult.defaultSuccess("??????????????????");
    }

    /**
     * ?????? ??????id ???????????????????????????
     *
     * @param token      ??????
     * @param categoryId ??????id
     */
    @Override
    public String listDishByCId(String token, Long categoryId) {
        // ??????????????????????????? ??????id ???????????????
        String redisDishKey = "dish_" + categoryId;
        String jsonDishList = redisUtils.get(redisDishKey);
        if (jsonDishList != null) {
            try {
                List<Dish> dishList = new ObjectMapper().readValue(jsonDishList, List.class);
                return JsonResponseResult.success(dishList);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        QueryWrapper<Dish> dishQueryWrapper = new QueryWrapper<Dish>()
                .eq("category_id", categoryId)
                .eq("status", 1)
                .eq("is_deleted", 0)
                .orderByAsc("sort");

        List<Dish> dishList = dishMapper.selectList(dishQueryWrapper);

        if (dishList.size() <= 0)
            throw new DishManagementException("???????????????????????????");

        try {
            redisUtils.set(redisDishKey, new ObjectMapper().writeValueAsString(dishList));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return JsonResponseResult.success(dishList);
    }

    private Integer simpleUpdate(Dish dish, String token) {
        // 1, ???????????????????????????
        if (dish.getIsDeleted() == 1) {
            QueryWrapper<Dish> statusQueryWrapper = new QueryWrapper<Dish>()
                    .eq("name", dish.getName());
            Dish dishDb = dishMapper.selectOne(statusQueryWrapper);
            if (dishDb.getStatus() == 1)
                throw new DishManagementException("??????????????????????????????, ????????????");
        }

        // 2, ??????????????? ???????????????
        UpdateWrapper<Dish> updateWrapper = new UpdateWrapper<Dish>()
                .eq("name", dish.getName())
                .set("status", dish.getStatus())
                .set("is_deleted", dish.getIsDeleted())
                .set("update_user", Long.valueOf(tokenUtils.getMemberIdByJwtToken(token)))
                .set("update_time", new Date());

        int update = dishMapper.update(null, updateWrapper);

        // ????????????
        redisUtils.delete("dish_" + dish.getCategoryId());

        return update;
    }

}
