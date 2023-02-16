package com.sakura.reggieApi.module.sysuser.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sakura.reggieApi.common.utils.JsonResponseResult;
import com.sakura.reggieApi.common.utils.RedisUtils;
import com.sakura.reggieApi.common.utils.TokenUtils;
import com.sakura.reggieApi.exception.*;
import com.sakura.reggieApi.module.sysuser.mapper.EmployeeMapper;
import com.sakura.reggieApi.common.pojo.Employee;
import com.sakura.reggieApi.module.sysuser.service.EmployeeService;

import com.sakura.reggieApi.module.sysuser.vo.EmployeeVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author sakura
 * @className EmployeeServiceImpl
 * @createTime 2023/2/8
 */
@Service
@Transactional
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
        implements EmployeeService {

    private static final BCryptPasswordEncoder PASSWORD_ENCODE = new BCryptPasswordEncoder();
    private static final String PASSWORD_COMPOSE = "{bcrypt}";

    @Value("${page.pageSize}")
    private Integer pageSize;


    @Resource
    AuthenticationManager authenticationManager;
    @Resource
    EmployeeMapper employeeMapper;

    @Resource
    RedisUtils redisUtils;
    @Resource
    TokenUtils tokenUtils;

    /**
     * 系统用户登入
     * @param employeeReq 登入的用户
     * @return 登入成功的 token 并且将用户的信息保存到 redis 中
     *          不成功 为 则抛出异常
     */
    @Override
    public String login(Employee employeeReq) {
        // 登入逻辑

        // 1, 创建 认证的用户
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(employeeReq.getUsername(), employeeReq.getPassword());

        // 2, 进行用户认证
        // 返回一个认证
        Authentication authentication = authenticationManager.authenticate(authRequest);

        // 2.1 判断是否认证成功
        if (ObjectUtils.isEmpty(authentication))
            throw new AuthenticationServiceException("认证失败, 用户名或者密码错误");

        // 如果认证成功 生成 token 并且保存用户信息
        Employee employee = (Employee) authentication.getPrincipal();
        String employeeId = employee.getId().toString();
        String employeeName = employee.getUsername();

        // 3. 生成 token
        String token = tokenUtils.getJwtToken(employeeId, employeeName);

        // 4 保存用户信息
        try {
            redisUtils.set("login:" + employeeId, new ObjectMapper().writeValueAsString(employee));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return JsonResponseResult.successToken(token, employeeName);
    }

    /**
     * 用户退出登入请求
     * @param token 用户令牌
     * @return 结果信息
     */
    @Override
    public String logout(String token) {
        // 1, 校验 token 是否合法
        // 不合法的话 会抛出异常
        tokenUtils.checkToken(token);

        // 2, 从token 中获取出 信息
        String id = tokenUtils.getMemberIdByJwtToken(token);


        // 3, 从redis 中删除 用户的信息
        Boolean delete = redisUtils.delete("login:" + id);

        // 4, 将令牌存储到 黑名单中
        redisUtils.sAdd("token:black", token);

        // 删除成功则为 true
        if (!delete) {
            throw new RuntimeException("token 非法");
        }

        return JsonResponseResult.defaultSuccess("注销成功");
    }

    /** 用户更新密码
     */
    @Override
    public String updatePassword(String token, EmployeeVo employeeVo) {
        // 1, 校验token
        tokenUtils.checkToken(token);

        // 2, 查询用户  校验 旧密码是否 与 输入的旧密码是否相等
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<Employee>()
                .eq("id", tokenUtils.getMemberIdByJwtToken(token));
        Employee employee = employeeMapper.selectOne(queryWrapper);

        if (ObjectUtils.isEmpty(employee))
            throw new NotSysUserException("没有该用户");
        // 2.1 如果相等 则进行修改用户密码, 并且将 redis 中的令牌 删除,让用户重新登入
        // 2.1.1 加密 输入的oldPassword
        String oldPassword = employeeVo.getOldPassword();
        // 2.1.2 取出 数据库中的 用户密码
        String password = employee.getPassword().substring(employee.getPassword().indexOf("}") + 1);
        // 2.1.3 比较
        if (!PASSWORD_ENCODE.matches(oldPassword, password))
            throw new UpdatePasswordException("旧密码输入错误!");

        // 3, 校验 输入的两次密码是否正确
        String newPassword = employeeVo.getNewPassword();
        String repeatPassword = employeeVo.getRepeatPassword();
        if (!newPassword.matches(repeatPassword))
            throw new UpdatePasswordException("两次密码输入不一致");

        // 2.1.4 删除redis 中的数据 并将 当前的 token 提交进 redis 的黑名单中
        redisUtils.delete("login:" + employee.getId());
        redisUtils.sAdd("token:black", token);

        // 4, 正确则 修改用户的 密码
        // 4.1 加密 密码 (拼写前缀)
        String encryptNewPassword = PASSWORD_COMPOSE + PASSWORD_ENCODE.encode(newPassword);
        UpdateWrapper<Employee> updateWrapper = new UpdateWrapper<Employee>()
                .eq("id", employee.getId())
                .set("password", encryptNewPassword);

        int update = employeeMapper.update(null, updateWrapper);
        if (update <= 0) {
            throw new RuntimeException("修改异常");
        }

        return JsonResponseResult.defaultSuccess("修改密码成功");
    }

    /**
     * 添加用户
     * @param token 当前登入用户的令牌
     * @param employee 新增的用户
     */
    @Override
    public String addEmployee(String token, Employee employee) {
        // 1, 校验 token
        tokenUtils.checkToken(token);

        // 2, 校验新增的账户是否合法
        // 2.1 账号 必须为 3-20 字  必须为 字母 或者阿拉伯数字
        String username = employee.getUsername();
        if (! (username.length() >=3 && username.length() <= 20))
            throw new EmployeeUsernameNotLegalException("账号输入不合法");
        if (!username.matches("[\\dA-Za-z]{3,20}"))
            throw new EmployeeUsernameNotLegalException("账号输入不合法");

        // 2.2 验证唯一性
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<Employee>()
                .eq("username", username);
        Employee dbEmp = employeeMapper.selectOne(queryWrapper);
        if (dbEmp != null)
            throw new EmployeeUsernameRepeatException("账号重复 请重新输入");
        // 2.3 默认密码 123456
        employee.setPassword("{noop}123456");
        // 2.4 默认性别男性
        if (employee.getSex() == null) {
            employee.setSex("男");
        }
        // 2.5 身份证号必须符合身份证规则
        if (!employee.getIdNumber().matches("(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)"))
            throw new EmployeeIdNumberException("身份证号输入错误");
        // 3, 向数据库中添加用户
        // 3.1 添加前做处理
        Long curEmpId = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));
        employee.setCreateTime(new Date());
        employee.setStatus(true);
        employee.setCreateUser(curEmpId);

        // 3.2 添加用户
        int insert = employeeMapper.insert(employee);
        // 3.3 添加用户与 角色表的关系数据 自行实现
        insert += employeeMapper.insertByIdRoleUser(employee.getId());

        if (insert <= 1)
            throw new RuntimeException("服务器异常");

        return JsonResponseResult.defaultSuccess("添加成功");
    }

    /**
     * 系统用户的分页查询
     * @param token 令牌
     * @param curPage 当前页面
     * @param empName 用户名
     */
    @Override
    public String listPage(String token, Integer curPage, String empName) {
        // 1, 校验token
        tokenUtils.checkToken(token);
        // 2, 查询 empName 是否为 default
        Page<Employee> employeePage = new Page<>(curPage, pageSize);
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        if (!"default".equals(empName)) {
            queryWrapper.like("name", empName);
        }

        // 2.1 不为 default 则是 非用户名的分页查询
        Page<Employee> page = employeeMapper.selectPage(employeePage, queryWrapper);

        if (page.getTotal() <= 0)
            throw new EmployeeServiceException("未搜索到任何用户数据");

        // 3, 返回数据
        return JsonResponseResult.success(page);
    }


    /**
     * 更新用户的 状态
     * @param token 令牌
     * @param username 用户账号
     * @param status 状态 0 为 false   1 为 true
     */
    @Override
    public String updateStatus(String token, String username, Boolean status) {
        // 1, 校验token
        tokenUtils.checkToken(token);

        // 2, 编写条件
        Long eid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));
        UpdateWrapper<Employee> updateWrapper = new UpdateWrapper<Employee>()
                .eq("username", username)
                .set("status", status)
                .set("update_time", new Date())
                .set("update_user", eid);

        int update = employeeMapper.update(null, updateWrapper);
        if (update <= 0) {
            throw new EmployeeServiceException("服务异常");
        }

        return JsonResponseResult.defaultSuccess("修改成功");
    }

    /**
     * 根据 username 查询单个用户
     * @param token 令牌
     * @param username 要查询的用户
     */
    @Override
    public String selectOneEmp(String token, String username) {
        // 1, 校验token
        tokenUtils.checkToken(token);

        QueryWrapper<Employee> queryWrapper = new QueryWrapper<Employee>()
                .eq("username", username);

        Employee employee = employeeMapper.selectOne(queryWrapper);
        if (employee == null)
            throw new NotSysUserException("对不起, 未查询到相应的用户信息");

        return JsonResponseResult.success(employee);
    }

    /**
     * 修改用户信息
     * @param token 令牌
     * @param reqEmployee 要修改的用户信息
     */
    @Override
    public String updateEmployee(String token, Employee reqEmployee) {
        // 1, 校验token
        tokenUtils.checkToken(token);
        // 2, 取出 token 里面的 用户id
        Long eid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        reqEmployee.setUpdateTime(new Date());
        reqEmployee.setUpdateUser(eid);

        int i = employeeMapper.updateById(reqEmployee);
        if (i <= 0)
            throw new EmployeeServiceException("服务异常");

        return JsonResponseResult.defaultSuccess("编辑成功");
    }


}
