package com.sakura.reggieApi.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sakura.reggieApi.common.mapper.RoleMapper;
import com.sakura.reggieApi.common.pojo.Employee;
import com.sakura.reggieApi.common.pojo.Role;
import com.sakura.reggieApi.common.utils.RedisUtils;
import com.sakura.reggieApi.module.appuser.mapper.UserMapper;
import com.sakura.reggieApi.common.pojo.User;
import com.sakura.reggieApi.module.sysuser.mapper.EmployeeMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author sakura
 * @className UserDetailServiceImpl
 * @createTime 2023/2/14
 */
@Service
@Transactional
public class UserDetailServiceImpl implements UserDetailsService, UserDetailsPasswordService {
    @Resource
    UserMapper userMapper;
    @Resource
    EmployeeMapper employeeMapper;
    @Resource
    RoleMapper roleMapper;

    @Resource
    RedisUtils redisUtils;

    /*
        校验用户名
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据 用户名去查询 数据库中的用户
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<Employee>()
                .eq("username", username);

        Employee employee = employeeMapper.selectOne(queryWrapper);

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>()
                .eq("phone", username);

        User user = userMapper.selectOne(userQueryWrapper);

       if (employee != null) {
           // 查询用户在 数据库中的角色
           List<Role> empRoles = roleMapper.selectByeIdRoles(employee.getId(), null);
           employee.setRoles(empRoles);
           return employee;
       } else if (user != null){
           List<Role> userRoles = roleMapper.selectByeIdRoles(null, user.getId());
           user.setRoles(userRoles);
            user.setPassword(redisUtils.get("captcha:" + user.getPhone()));
           return user;
       } else {
          throw new UsernameNotFoundException("用户不存在");
       }

    }

    /*
        更新密码
     */
    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {

        if (user instanceof User){
            return user;
        }

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
