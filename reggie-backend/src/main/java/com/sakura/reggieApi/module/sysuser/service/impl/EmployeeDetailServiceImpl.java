package com.sakura.reggieApi.module.sysuser.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sakura.reggieApi.common.pojo.Role;
import com.sakura.reggieApi.module.sysuser.mapper.EmployeeMapper;
import com.sakura.reggieApi.common.mapper.RoleMapper;
import com.sakura.reggieApi.common.pojo.Employee;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author sakura
 * @className EmplogyeeDetailServiceImpl
 * @createTime 2023/2/9
 */
@Service
@Transactional
public class EmployeeDetailServiceImpl implements UserDetailsService, UserDetailsPasswordService {
    @Resource
    EmployeeMapper employeeMapper;
    @Resource
    RoleMapper roleMapper;

    /*
        校验用户名
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据 用户名去查询 数据库中的用户
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<Employee>()
                .eq("username", username);

        Employee employee = employeeMapper.selectOne(queryWrapper);
        if (ObjectUtils.isEmpty(employee))
            throw new UsernameNotFoundException("用户名不存在");

        // 查询用户在 数据库中的角色
        List<Role> roles = roleMapper.selectByeIdRoles(employee.getId());
        employee.setRoles(roles);

        return employee;
    }

    /*
        更新密码
     */
    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {

        UpdateWrapper<Employee> updateWrapper = new UpdateWrapper<Employee>()
                .eq("username", user.getUsername())
                .set("password", newPassword)
                .set("update_time", new Date());

        int update = employeeMapper.update(null, updateWrapper);

        if (update != 1) {
           throw new ArithmeticException("服务器异常");
        }

        ((Employee) user).setPassword(newPassword);
        return user;
    }
}
