package com.sakura.reggieApi.module.address.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sakura.reggieApi.module.address.controller.AddressController;
import com.sakura.reggieApi.module.address.pojo.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author sakura
 * @className AddressMapper
 * @createTime 2023/2/14
 */
@Mapper
public interface AddressMapper extends BaseMapper<AddressBook> {
}
