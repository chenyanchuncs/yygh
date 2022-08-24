package com.neu.yygh.hosp.controller;

import com.neu.yygh.common.result.Result;
import com.neu.yygh.hosp.service.HospitalService;
import com.neu.yygh.model.hosp.Hospital;
import com.neu.yygh.vo.hosp.HospitalQueryVo;
import com.neu.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("admin/hosp/hospital")
@RestController
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    //更新医院的上线状态
    @ApiOperation("更新医院的上线状态")
    @GetMapping("updateHospStatus/{id}/{status}")
    public Result updateStatus(@PathVariable String id,
                               @PathVariable Integer status) {
        hospitalService.updateStatus(id, status);
        return Result.ok();
    }

    //查询全部医院数据
    @GetMapping("list/{page}/{limit}")
    public Result listHosp(@PathVariable Integer page,
                           @PathVariable Integer limit,
                           HospitalQueryVo hospitalQueryVo) {
        Page<Hospital> pageResult = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        return Result.ok(pageResult);
    }

    //医院详情信息
    @ApiOperation("医院详情信息")
    @GetMapping("showHospDetail/{id}")
    public Result showHospDetail(@PathVariable String id) {
        Map<String, Object> map = hospitalService.getHospById(id);
        return Result.ok(map);
    }
}
