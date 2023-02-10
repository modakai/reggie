package com.sakura.reggieApi.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sakura.reggieApi.common.pojo.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author sakura
 * @className RoleMapper
 * @createTime 2023/2/8
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    List<Role> selectByeIdRoles(Long eid);

}
