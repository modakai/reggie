package com.sakura.reggieApi.module.sysuser.controller;

import com.sakura.reggieApi.common.anno.LogAnnotation;
import com.sakura.reggieApi.common.pojo.Employee;
import com.sakura.reggieApi.common.utils.TokenUtils;
import com.sakura.reggieApi.module.sysuser.service.EmployeeService;
import com.sakura.reggieApi.module.sysuser.vo.EmployeeVo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
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

    private static final String HERDER_TOKEN_KEY = TokenUtils.REQUEST_HEADER_TOKEN_KEY;

    @Resource
    private EmployeeService employeeService;


    @LogAnnotation("修改系统用户信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN')")
    @PutMapping("/updateEmp")
    public String doUpdateEmp(@RequestHeader(HERDER_TOKEN_KEY) String token,
                              @RequestBody Employee reqEmployee) {

        return employeeService.updateEmployee(token, reqEmployee);
    }

    @LogAnnotation("查询单个系统用户信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN')")
    @GetMapping("/queryOne/{username}")
    public String doQueryEmployee(@RequestHeader(HERDER_TOKEN_KEY) String token,
                                  @PathVariable("username") String username) {
        return employeeService.selectOneEmp(token, username);
    }

    @LogAnnotation("超级管理员修改用户的状态")
    @PreAuthorize("hasRole('SUPADMIN')")
    @PutMapping("/forbid/{username}/{status}")
    public String doEmployeeStatusForbid(@RequestHeader(HERDER_TOKEN_KEY) String token,
                                         @PathVariable("username") String username,
                                         @PathVariable("status") Boolean status) {
        return employeeService.updateStatus(token, username, status);
    }

    @LogAnnotation("查询系统用户列表")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/list/{curPage}/{empName}")
    public String doEmployeeList(@RequestHeader(HERDER_TOKEN_KEY) String token,
                                 @PathVariable("curPage") Integer curPage,
                                 @PathVariable(value = "empName", required = false) String empName) {

        return employeeService.listPage(token, curPage, empName);
    }

    @PostMapping("/addEmployee")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @LogAnnotation("新增系统用户")
    public String doAddEmployee(@RequestHeader(HERDER_TOKEN_KEY) String token, @RequestBody Employee employee) {
        return employeeService.addEmployee(token, employee);
    }


    @LogAnnotation("系统用户修改密码")
    @PutMapping("/updatePassword")
    public String doUpdatePassword(@RequestHeader(HERDER_TOKEN_KEY) String token,
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
    public String doLogout(@RequestHeader(HERDER_TOKEN_KEY) String token) {
        return employeeService.logout(token);
    }

}
