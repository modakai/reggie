package com.sakura.reggieApi.module.appuser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sakura.reggieApi.common.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author sakura
 * @className UserMapper
 * @createTime 2023/2/14
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    Integer insertRoleUser(Long id);
}
