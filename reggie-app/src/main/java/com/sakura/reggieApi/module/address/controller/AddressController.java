package com.sakura.reggieApi.module.address.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sakura.reggieApi.common.anno.LogAnnotation;
import com.sakura.reggieApi.common.utils.JsonResponseResult;
import com.sakura.reggieApi.common.utils.TokenUtils;
import com.sakura.reggieApi.module.address.mapper.AddressMapper;
import com.sakura.reggieApi.module.address.pojo.AddressBook;
import com.sakura.reggieApi.module.address.service.AddressService;
import jdk.security.jarsigner.JarSigner;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author sakura
 * @className AddressController
 * @createTime 2023/2/14
 */
@RestController
@RequestMapping("/app/address")
public class AddressController {

    private static final String HEADER_TOKEN_KEy = TokenUtils.REQUEST_HEADER_TOKEN_KEY;

    @Resource
    AddressService addressService;

    @LogAnnotation("获取默认的地址信息")
    @GetMapping("/default")
    public String doAddressDefault(@RequestHeader(HEADER_TOKEN_KEy) String token) {
        return addressService.queryDefault(token);
    }

    @LogAnnotation("删除地址")
    @DeleteMapping("/{id}")
    public String doDelete(@RequestHeader(value = HEADER_TOKEN_KEy) String token,
                           @PathVariable("id") Long id) {
        return addressService.delete(token, id);
    }


    @LogAnnotation("查询用户地址详情")
    @GetMapping("/detail/{id}")
    public String doDetail(@RequestHeader(value = HEADER_TOKEN_KEy) String token,
                           @PathVariable("id") Long id){
        return addressService.queryDetail(token, id);
    }

    @LogAnnotation("修改地址相关信息")
    @PutMapping("/update")
    public String doUpdate(@RequestHeader(value = HEADER_TOKEN_KEy) String token,
                           @RequestBody AddressBook addressBook) {

        return addressService.updateAddress(token, addressBook);
    }


    @LogAnnotation("获取APP端用户的收货地址")
    @GetMapping("/list")
    public String doList(@RequestHeader(value = HEADER_TOKEN_KEy) String token) {

      return addressService.listAddress(token);
    }

    @LogAnnotation("添加用户的收货地址")
    @PostMapping("/add")
    public String addAddress(@RequestBody AddressBook addressBook,
                             @RequestHeader(value = HEADER_TOKEN_KEy) String token) {
        return addressService.add(addressBook, token);
    }
}
