package com.sakura.reggieApi.module.sysuser.vo;

/**
 * @author sakura
 * @className EmployeeVo
 * @createTime 2023/2/10
 */
public class EmployeeVo {

    private String oldPassword;
    private String newPassword;
    private String repeatPassword;

    @Override
    public String toString() {
        return "EmployeeVo{" +
                "oldPassword='" + oldPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                ", repeatPassword='" + repeatPassword + '\'' +
                '}';
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
