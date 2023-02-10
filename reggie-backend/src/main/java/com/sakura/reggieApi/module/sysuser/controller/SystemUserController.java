package com.sakura.reggieApi.module.sysuser.controller;

import com.sakura.reggieApi.common.anno.LogAnnotation;
import com.sakura.reggieApi.module.sysuser.pojo.Employee;
import com.sakura.reggieApi.module.sysuser.service.EmployeeService;
import com.sakura.reggieApi.module.sysuser.vo.EmployeeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author sakura
 * @className SystemUserController
 * @createTime 2023/2/8
 */
@RestController
@RequestMapping("/backend/user")
public class SystemUserController {

    @Resource
    private EmployeeService employeeService;

    @PostMapping("/addEmployee")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String doAddEmployee(@RequestHeader("token") String token, @RequestBody Employee employee) {
        return employeeService.addEmployee(token, employee);
    }


    @LogAnnotation("系统用户修改密码")
    @PutMapping("/updatePassword")
    public String doUpdatePassword(@RequestHeader("token") String token,
                                   @RequestBody EmployeeVo employeeVo) {
       return employeeService.updatePassword(token, employeeVo);
    }

    @LogAnnotation("瑞吉外卖系统用户登入")
    @PostMapping("/login")
    public String doIndex(@RequestBody Employee employee){
        return employeeService.login(employee);
    }

    @LogAnnotation("瑞吉外卖系统用户退出登入")
    @PostMapping("/logout")
    public String doLogout(@RequestHeader("token") String token) {
        return employeeService.logout(token);
    }

}
