package com.gujunbin.springcloud.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description (Payment)表实体类
 *
 * @author GuJunBin
 * @date 2020-04-08 17:43:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
    * ID
    */    
    private Long id;
        
    private String serial;
}