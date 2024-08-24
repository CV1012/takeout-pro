package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 前后端：用于分页查询的数据封装
 */

@Data
public class DishPageQueryDTO implements Serializable {

    // 页码
    private int page;

    // 展示的内容数
    private int pageSize;

    // 菜品名称
    private String name;

    //分类id 菜品分类
    private Integer categoryId;

    //状态 0表示禁用 1表示启用
    private Integer status;

}
