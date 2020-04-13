package com.gujunbin.springcloud.mapper;

import com.gujunbin.springcloud.entity.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * (Payment)表数据库访问层
 *
 * @author GuJunBin
 * @since 2020-04-08 17:43:22
 */
@Mapper
public interface PaymentMapper {

    /**
     * 新建
     * @param payment
     * @return
     */
    int create(Payment payment);

    /**
     * 通过id查找
     * @param id
     * @return
     */
    Payment getPaymentById(@Param("id") Long id);
}
