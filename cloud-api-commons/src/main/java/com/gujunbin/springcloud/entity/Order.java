package com.gujunbin.springcloud.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/26 13:25
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long id;

    private Long userId;

    private Long productId;

    private Integer count;

    private BigDecimal money;

    private Integer status;
}
