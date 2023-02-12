package com.sakura.reggieApi.module.sysuser.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sakura.reggieApi.common.pojo.Employee;
import com.sakura.reggieApi.module.sysuser.vo.EmployeeVo;


/**
 * @author sakura
 * @className EmployeeService
 * @createTime 2023/2/8
 */
public interface EmployeeService extends IService<Employee> {


    String login(Employee employeeReq);

    String logout(String token);

    String updatePassword(String token, EmployeeVo employeeVo);

    String addEmployee(String token, Employee employee);

    String listPage(String token, Integer curPage, String empName);

    String updateStatus(String token, String username, Boolean status);

    String selectOneEmp(String token, String username);

    String updateEmployee(String token, Employee reqEmployee);
}
