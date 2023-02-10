package com.sakura.reggieApi.module.sysuser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sakura.reggieApi.common.pojo.Role;
import com.sakura.reggieApi.module.sysuser.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author sakura
 * @className EmployeeMapper
 * @createTime 2023/2/8
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

    List<Role> selectByIdRoles(Integer uid);
}
