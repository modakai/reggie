package com.sakura.reggieApi.module.address.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sakura.reggieApi.module.address.pojo.AddressBook;

/**
 * @author sakura
 * @className AddressService
 * @createTime 2023/2/14
 */
public interface AddressService extends IService<AddressBook> {
    String add(AddressBook addressBook, String token);

    String listAddress(String token);

    String updateAddress(String token, AddressBook addressBook);

    String queryDetail(String token, Long id);

    String delete(String token, Long id);

    String queryDefault(String token);
}
