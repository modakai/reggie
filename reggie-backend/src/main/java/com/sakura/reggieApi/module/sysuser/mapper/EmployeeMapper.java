package com.sakura.reggieApi.module.sysuser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sakura.reggieApi.common.pojo.Role;
import com.sakura.reggieApi.common.pojo.Employee;
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

    /**
     * 向关系表中插入数据
     * @param eid
     * @return
     */
    Integer insertByIdRoleUser(Long eid);
}
