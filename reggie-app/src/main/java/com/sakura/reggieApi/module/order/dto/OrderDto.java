package com.sakura.reggieApi.module.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author sakura
 * @className OrderDto
 * @createTime 2023/2/16
 */
public class OrderDto implements Serializable {
    private static final Long serialVersionUID = 1L;

    private String number;
    private String payMethod;
    private Integer status;
    //结账时间
    @JsonFormat(pattern = "yyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date checkoutTime;

    @Override
    public String toString() {
        return "OrderDto{" +
                "number='" + number + '\'' +
                ", payMethod='" + payMethod + '\'' +
                ", status=" + status +
                ", checkoutTime=" + checkoutTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDto orderDto = (OrderDto) o;
        return Objects.equals(number, orderDto.number) && Objects.equals(payMethod, orderDto.payMethod) && Objects.equals(status, orderDto.status) && Objects.equals(checkoutTime, orderDto.checkoutTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, payMethod, status, checkoutTime);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public Date getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(Date checkoutTime) {
        this.checkoutTime = checkoutTime;
    }
}
