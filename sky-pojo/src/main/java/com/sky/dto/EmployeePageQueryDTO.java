package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/*
    分页查询时，前端传递的数据(DTO)
 */
@Data
public class EmployeePageQueryDTO implements Serializable {

    //员工姓名
    private String name;

    //页码
    private int page;

    //每页显示记录数
    private int pageSize;

}
