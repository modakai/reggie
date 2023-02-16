package com.sakura.reggieApi.module.address.service.impl;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sakura.reggieApi.common.utils.JsonResponseResult;
import com.sakura.reggieApi.common.utils.TokenUtils;
import com.sakura.reggieApi.exception.AddressServiceException;
import com.sakura.reggieApi.module.address.mapper.AddressMapper;
import com.sakura.reggieApi.module.address.pojo.AddressBook;
import com.sakura.reggieApi.module.address.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author sakura
 * @className AddressServiceImpl
 * @createTime 2023/2/14
 */
@Service
@Transactional
public class AddressServiceImpl extends ServiceImpl<AddressMapper, AddressBook>
        implements AddressService {

    @Resource
    TokenUtils tokenUtils;
    @Resource
    AddressMapper addressMapper;

    /**
     * 添加地址
     * @param addressBook 添加地址的对象
     */
    @Override
    public String add(AddressBook addressBook, String token) {
        tokenUtils.checkToken(token);

        Long id = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        addressBook.setUserId(id);
        addressBook.setCreateTime(new Date());
        addressBook.setCreateUser(id);

        int insert = addressMapper.insert(addressBook);
        if (insert <= 0)
            throw new AddressServiceException("添加地址服务异常");

        return JsonResponseResult.defaultSuccess("添加成功");
    }

    @Override
    public String listAddress(String token) {
        tokenUtils.checkToken(token);

        Long id = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<AddressBook>()
                .eq("is_deleted", 0).eq("user_id", id)
                .orderByDesc("is_default");

        List<AddressBook> addressBooks = addressMapper.selectList(queryWrapper);

        if (addressBooks.size() <= 0)
            throw new AddressServiceException("未搜索到任何地址, 请先添加地址");

        return JsonResponseResult.success(addressBooks);
    }

    /**
     * 修改用户地址
     * @param token 令牌
     * @param addressBook 修改对象
     */
    @Override
    public String updateAddress(String token, AddressBook addressBook) {

        Long id = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        addressBook.setUpdateTime(new Date());
        addressBook.setUpdateUser(id);

        UpdateWrapper<AddressBook> updateWrapper = new UpdateWrapper<AddressBook>()
                .eq("id", addressBook.getId());

        int update = addressMapper.update(addressBook, updateWrapper);
        if (update <= 0)
            throw new AddressServiceException("服务异常");

        return JsonResponseResult.defaultSuccess("修改成功");
    }

    /**
     * 查询用户地址详情
     * @param token 令牌
     * @param id 地址id
     */
    @Override
    public String queryDetail(String token, Long id) {

        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<AddressBook>()
                .eq("id", id)
                .eq("user_id", tokenUtils.getMemberIdByJwtToken(token))
                .eq("is_deleted", 0);

        AddressBook addressBook = addressMapper.selectOne(queryWrapper);

        if (addressBook == null)
            throw new AddressServiceException("搜索不到相关数据");

        return JsonResponseResult.success(addressBook);
    }

    /**
     * 删除用户地址
     * @param token 令牌
     * @param id 地址id
     */
    @Override
    public String delete(String token, Long id) {

        Long uid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        UpdateWrapper<AddressBook> updateWrapper = new UpdateWrapper<AddressBook>()
                .eq("user_id", uid)
                .eq("id", id)
                .set("is_deleted", 1);

        int update = addressMapper.update(null, updateWrapper);
        if (update <= 0)
            throw new AddressServiceException("服务异常");


        return JsonResponseResult.defaultSuccess("删除成功");
    }

    /**
     * 获取默认的地址信息
     * @param token 令牌
     */
    @Override
    public String queryDefault(String token) {
        Long uid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<AddressBook>()
                .eq("user_id", uid)
                .eq("is_deleted", 0)
                .eq("is_default", 1);

        AddressBook defaultAddress = addressMapper.selectOne(queryWrapper);
        if (defaultAddress == null)
            throw new AddressServiceException("用户未设置默认地址");

        return JsonResponseResult.success(defaultAddress);
    }
}
