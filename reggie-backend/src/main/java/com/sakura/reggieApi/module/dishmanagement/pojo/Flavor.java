package com.sakura.reggieApi.module.dishmanagement.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author sakura
 * @className Flavor
 * @createTime 2023/2/12
 */
@TableName("flavor")
public class Flavor implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    private String name;

    /**
     * JSON 格式的属性数组
     */
    private String value;

    private Boolean isDel;

    public Flavor() {
    }

    public Flavor(Long id, String name, String value, Boolean isDel) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.isDel = isDel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flavor flavor = (Flavor) o;
        return Objects.equals(id, flavor.id) && Objects.equals(name, flavor.name) && Objects.equals(value, flavor.value) && Objects.equals(isDel, flavor.isDel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, value, isDel);
    }

    @Override
    public String toString() {
        return "Flavor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", isDel=" + isDel +
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getDel() {
        return isDel;
    }

    public void setDel(Boolean del) {
        isDel = del;
    }
}
