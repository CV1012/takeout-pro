package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Transactional // 开启事务管理，涉及到两张表的操作
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 向菜品表中插入一条数据：
        dishMapper.insert(dish);

        // 由于在插入菜品表之前，是不知道其id是多少
        //      所以需要设置主键返回，并将其设置到口味表中的dish_id中
        Long id = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){

            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(id);
            });
            // 向口味表中插入n条数据：
            dishFlavorMapper.insertBatch(flavors);
        }
    }


    /**
     * 分页查询
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 菜品批量删除
     * @param ids
     */
    public void deleteBatch(List<Long> ids) {
        // 在销售中的菜品不可删除
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            // 在售：
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        // 被套餐关联的菜品不可删除  setmeal_dish
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds != null && setmealIds.size() >0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 删除菜品数据
        ids.forEach(dishId->{
            // 删除菜品数据：
            dishMapper.deleteByDishId(dishId);
            // 删除口味表数据 -- 根据dish_id
            dishFlavorMapper.deleteByDishId(dishId);

        });
    }

    /**
     * 查询菜品信息
     * @param id
     * @return
     */
    public DishVO getById(Long id) {
        // 查询菜品信息：
        Dish dish = dishMapper.getById(id);
        // 查询口味信息：
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

        // VO封装：
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 更新菜品数据
     * 事务管理
     * @param dishDTO
     */
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        // 菜品id
        Long dishId = dishDTO.getId();
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 修改菜品数据
        dishMapper.update(dish);

        // 修改口味数据：先删除之前的口味，再插入传入的新的口味
        dishFlavorMapper.deleteByDishId(dishId);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() >0 ){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
        }
        dishFlavorMapper.insertBatch(flavors);
    }
}
