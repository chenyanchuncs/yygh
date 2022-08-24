package com.neu.yygh.cmn.controller;

import com.neu.yygh.cmn.service.DictService;
import com.neu.yygh.common.result.Result;
import com.neu.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.Get;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api("数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Autowired
    private DictService dictService;


    //根据dictcode查询下级节点
    @ApiOperation("根据dictcode查询下级节点")
    @GetMapping("/findByDictCode/{dictCode}")
    public Result findByDictCode(@PathVariable String dictCode) {
        List<Dict> list = dictService.findByDictCode(dictCode);
        return Result.ok(list);
    }

    /**
     * 根据dictcode与value进行查询
     * @param dictCode
     * @param value
     * @return
     */
    @GetMapping("getName/{dictCode}/{value}")
    public String getName (@PathVariable String dictCode,
                           @PathVariable String value) {
        //System.out.println("dictCode: " + dictCode + ", value:" + value );
        String dictName = dictService.getDictName(dictCode, value);
        return dictName;
    }

    /**
     * 根据value进行查询
     * @param value
     * @return
     */
    @GetMapping("getName/{value}")
    public String getName (@PathVariable String value) {
        String dictName = dictService.getDictName("", value);
        return dictName;
    }


    /**
     * 导入数据字典
     * @param file
     * @return
     */
    @ApiOperation(value = "导入")
    @PostMapping("importData")
    public Result importData(MultipartFile file) {
        dictService.importDictData(file);
        return Result.ok();
    }


    /**
     * Excel导出方法
     * @param response
     */
    @ApiOperation(value="导出")
    @GetMapping(value = "/exportData")
    public void exportData(HttpServletResponse response) {
        dictService.exportData(response);
    }


    //根据数据id查询子数据列表
    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id) {
        List<Dict> list = dictService.findChlidData(id);
        return Result.ok(list);
    }



}
