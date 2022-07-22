package com.neu.yygh.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neu.yygh.common.result.Result;
import com.neu.yygh.common.utils.MD5;
import com.neu.yygh.hosp.service.HospitalSetService;
import com.neu.yygh.model.hosp.HospitalSet;
import com.neu.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {
    //注入service

    @Autowired
    private HospitalSetService hospitalSetService;

    //查询医院设置表中的所有信息
    @ApiOperation(value = "获得所有医院设置信息")
    @GetMapping("findAll")
    public Result finfAll() {
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    //2 逻辑删除医院设置数据
    @ApiOperation(value = "逻辑删除医院设置信息")
    @ApiParam(name = "id", value = "根据ID删除")
    @DeleteMapping("{id}")
    public Result removeHospSet(@PathVariable Long id) {
        boolean flag = hospitalSetService.removeById(id);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    //3 条件查询待分页
    @ApiOperation(value = "带分页的条件查询")
    @PostMapping("findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable Long current,
                                  @PathVariable Long limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {
        //创建page对象
        Page<HospitalSet> page = new Page<>(current, limit);
        //构造条件
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        String hosname = hospitalSetQueryVo.getHosname();
        String hoscode = hospitalSetQueryVo.getHoscode();
        if (!StringUtils.isEmpty(hosname)) {
            queryWrapper.like("hosname", hospitalSetQueryVo.getHosname());
        }
        if (!StringUtils.isEmpty(hoscode)) {
            queryWrapper.eq("hoscode", hospitalSetQueryVo.getHoscode());
        }


        //调用方法实现分页
        Page<HospitalSet>  hospitalSetPage = hospitalSetService.page(page, queryWrapper);

        return Result.ok(hospitalSetPage);

    }


    //4 添加医院设置
    @ApiOperation(value = "添加医院设置")
    @PostMapping("saveHospitialSet")
    public Result saveHospitialSet(@RequestBody HospitalSet hospitalSet) {
        //设置状态
        hospitalSet.setStatus(1);
        //签名秘钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));

        //调用service
        boolean save = hospitalSetService.save(hospitalSet);
        if (save) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }


    //5 根据ID获取医院设置
    @ApiOperation(value = "根据ID获取医院设置")
    @GetMapping("getHospSet/{id}")
    public Result getHospSet(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    //6 修改医院设置
    @ApiOperation(value = "修改医院设置")
    @PostMapping("updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet) {
        boolean b = hospitalSetService.updateById(hospitalSet);
        if (b) {
            return Result.ok();
        } else {
            return Result.fail();
        }

    }

    //7 批量删除医院设置
    @ApiOperation(value = "批量删除医院设置")
    @DeleteMapping("batchRemoveHospitalSet")
    public Result batchRemoveHospitalSet(@RequestBody List<Long> idList) {
        boolean b = hospitalSetService.removeByIds(idList);
        if (b) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    //8 医院设置的锁定与解锁设置，通过一个标志位实现
    @PutMapping("lockHospital/{id}/{status}")
    public Result lockHospital(@PathVariable Long id, @PathVariable Integer status) {
        //根据ID,先把初始值查出来
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        if (StringUtils.isEmpty(hospitalSet.toString())) {
            return Result.fail("该ID对应信息不存在");
        }
        //设置状态
        hospitalSet.setStatus(status);
        //调用方法进行更新
        boolean b = hospitalSetService.updateById(hospitalSet);
        if (b) {
            return Result.ok();
        } else {
            return Result.fail();
        }

    }

    //9 发送签名的秘钥
    @PutMapping("sendKey/{id}")
    public Result sendKey(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        //todo 发送短信
        return Result.ok(hospitalSet);
    }

}
