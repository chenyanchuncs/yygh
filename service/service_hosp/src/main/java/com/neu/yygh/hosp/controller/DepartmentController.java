package com.neu.yygh.hosp.controller;

import com.neu.yygh.common.result.Result;
import com.neu.yygh.hosp.service.DepartmentService;
import com.neu.yygh.vo.hosp.DepartmentVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/hosp/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    //根据医院编号，查询所有科室
    @ApiOperation("根据医院编号，查询所有科室")
    @GetMapping("getDeptList/{hoscode}")
    public Result getDeptList(@PathVariable String hoscode) {
        List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
        return Result.ok(list);
    }
}
