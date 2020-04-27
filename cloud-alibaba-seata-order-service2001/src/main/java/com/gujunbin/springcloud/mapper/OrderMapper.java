package com.gujunbin.springcloud.mapper;

import com.gujunbin.springcloud.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/26 13:32
 * @Description:
 */
@Mapper
public interface OrderMapper {

    /**
     * 创建订单
     * @param order
     */
    void create(Order order);

    /**
     * 修改订单状态  默认0 改为 1
     * @param userId
     * @param status
     */
    void update(@Param("userId") Long userId, @Param("status") Integer status);
}
