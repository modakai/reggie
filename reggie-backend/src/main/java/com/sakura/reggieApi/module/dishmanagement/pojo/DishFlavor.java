package com.sakura.reggieApi.module.dishmanagement.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author sakura
 * @className DishFlavor
 * @createTime 2023/2/12
 */
@TableName("dish_flavor")
public class DishFlavor implements Serializable {

    private static final Long serialVersionUID = 1L;

    @TableId
    private Long id;
    private Long dishId;
    private String name;
    private String value;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private Long createUser;
    private Long updateUser;
    private Boolean isDeleted;


    public DishFlavor() {
    }

    public DishFlavor(Long dishId, String name, String value, Date createTime, Long createUser, Boolean isDeleted) {
        this.dishId = dishId;
        this.name = name;
        this.value = value;
        this.createTime = createTime;
        this.createUser = createUser;
        this.isDeleted = isDeleted;
    }

    public DishFlavor(Long id, Long dishId, String name, String value, Date updateTime, Long updateUser, Boolean isDeleted) {
        this.id = id;
        this.dishId = dishId;
        this.name = name;
        this.value = value;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "DishFlavor{" +
                "id=" + id +
                ", dishId=" + dishId +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createUser=" + createUser +
                ", updateUser=" + updateUser +
                ", is_deleted=" + isDeleted +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DishFlavor that = (DishFlavor) o;
        return Objects.equals(id, that.id) && Objects.equals(dishId, that.dishId) && Objects.equals(name, that.name) && Objects.equals(value, that.value) && Objects.equals(createTime, that.createTime) && Objects.equals(updateTime, that.updateTime) && Objects.equals(createUser, that.createUser) && Objects.equals(updateUser, that.updateUser) && Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dishId, name, value, createTime, updateTime, createUser, updateUser, isDeleted);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    public Long getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
