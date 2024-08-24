package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品，id非必须
     * @param dishDTO
     * @return
     */
    public Result save(DishDTO dishDTO){
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品删除：
     *  - 单个删除、批量删除  -- 一个接口即可
     *  - 在销售中的菜品不可删除
     *  - 被套餐关联的菜品不可删除
     *  - 删除菜品后，关联的口味数据也需要被删除
     *  数据库的关联：
     *  dish_flavor.dish_id --- dish.id  --- setmeal_dish.dish_id
     */

    /**
     * 菜品删除
     * 传入的参数： 1,2,3 -- String
     * @return
     */
    @DeleteMapping()
    @ApiOperation("菜品删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品删除：{}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据id查询菜品
     *      - 查询菜品信息 dish
     *      - 查询与菜品关联的口味信息 dish_flavor
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品:{}", id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }



    /**
     * 更新菜品， id必须传入
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("更新菜品信息")
    public Result update(DishDTO dishDTO){
        log.info("更新菜品：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }
}
