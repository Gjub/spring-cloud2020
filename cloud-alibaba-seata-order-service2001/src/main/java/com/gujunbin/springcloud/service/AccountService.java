package com.gujunbin.springcloud.service;

import com.gujunbin.springcloud.entity.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/26 13:31
 * @Description:
 */
@FeignClient(value = "seata-account-service")
public interface AccountService {

    /**
     * 减账户的钱
     * @param userId
     * @param money
     * @return
     */
    @PostMapping("/account/decrease")
    CommonResult decrease(@RequestParam("userId") Long userId, @RequestParam("money") BigDecimal money);
}
