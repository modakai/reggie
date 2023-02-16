package com.sakura.reggieApi.module.setmealmanagement.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sakura.reggieApi.module.dishmanagement.pojo.Dish;
import com.sakura.reggieApi.module.dishmanagement.pojo.Flavor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author sakura
 * @className Setmeal
 * @createTime 2023/2/13
 */
@TableName("setmeal")
public class Setmeal implements Serializable {

    @TableField(exist = false)
    private static final Long serialVersionUID = 1L;
    @TableField(exist = false)
    private List<SetmealDish> setmealDishList;

    @TableId
    private Long id;

    //套餐名称
    private String name;

    //套餐分类id
    private Long categoryId;

    //套餐价格
    private BigDecimal price;

    //商品码
    private String code;

    //图片
    private String image;

    //描述信息
    private String description;

    //0 停售 1 起售
    private Integer status;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private Long createUser;

    private Long updateUser;

    //是否删除
    private Integer isDeleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Setmeal setmeal = (Setmeal) o;
        return Objects.equals(setmealDishList, setmeal.setmealDishList) && Objects.equals(id, setmeal.id) && Objects.equals(name, setmeal.name) && Objects.equals(categoryId, setmeal.categoryId) && Objects.equals(price, setmeal.price) && Objects.equals(code, setmeal.code) && Objects.equals(image, setmeal.image) && Objects.equals(description, setmeal.description) && Objects.equals(status, setmeal.status) && Objects.equals(createTime, setmeal.createTime) && Objects.equals(updateTime, setmeal.updateTime) && Objects.equals(createUser, setmeal.createUser) && Objects.equals(updateUser, setmeal.updateUser) && Objects.equals(isDeleted, setmeal.isDeleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(setmealDishList, id, name, categoryId, price, code, image, description, status, createTime, updateTime, createUser, updateUser, isDeleted);
    }

    @Override
    public String toString() {
        return "Setmeal{" +
                "setmealDishList=" + setmealDishList +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", categoryId=" + categoryId +
                ", price=" + price +
                ", code='" + code + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createUser=" + createUser +
                ", updateUser=" + updateUser +
                ", isDeleted=" + isDeleted +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public List<SetmealDish> getSetmealDishList() {
        return setmealDishList;
    }

    public void setSetmealDishList(List<SetmealDish> setmealDishList) {
        this.setmealDishList = setmealDishList;
    }
}
