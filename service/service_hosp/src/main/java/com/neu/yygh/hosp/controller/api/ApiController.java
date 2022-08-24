package com.neu.yygh.hosp.controller.api;


import com.neu.yygh.common.exception.YyghException;
import com.neu.yygh.common.helper.HttpRequestHelper;
import com.neu.yygh.common.result.Result;
import com.neu.yygh.common.result.ResultCodeEnum;
import com.neu.yygh.common.utils.MD5;
import com.neu.yygh.hosp.service.DepartmentService;
import com.neu.yygh.hosp.service.HospitalService;
import com.neu.yygh.hosp.service.HospitalSetService;
import com.neu.yygh.hosp.service.ScheduleService;
import com.neu.yygh.model.hosp.Department;
import com.neu.yygh.model.hosp.Hospital;
import com.neu.yygh.model.hosp.HospitalSet;
import com.neu.yygh.model.hosp.Schedule;
import com.neu.yygh.vo.hosp.DepartmentQueryVo;
import com.neu.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.service.ApiListing;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    //删除排班接口
    @PostMapping("schedule/remove")
    public Result remove(HttpServletRequest request) {
        //获取传递过来的科室信息
        Map<String, String[]> reuqestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(reuqestMap);


        //获得医院编号
        //签名验证

        //获得医院传过来的签名
        String sign = (String) paramMap.get("sign");

        //获得本地表中医院的签名
        String hoscode = (String) paramMap.get("hoscode");

        HospitalSet byHoscode = hospitalSetService.getByHoscode(hoscode);
        if (byHoscode == null) {
            return Result.fail();
        }
        String signKey = byHoscode.getSignKey();

        //通过MD5加密之后在进行判断
        String encrypt = MD5.encrypt(signKey);
        //System.out.println("MD5: " + encrypt);

        if (!encrypt.equals(sign)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }


        //科室编号获取
        String depcode = (String) paramMap.get("depcode");
        //排班编号获取
        String hosScheduleId = (String) paramMap.get("hosScheduleId");


        scheduleService.remove(hoscode, hosScheduleId);
        return Result.ok();
    }

    //查询排班接口
    @PostMapping("schedule/list")
    public Result findSchedule(HttpServletRequest request) {
        //获取传递过来的科室信息
        Map<String, String[]> reuqestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(reuqestMap);


        //获得医院编号
        //签名验证

        //获得医院传过来的签名
        String sign = (String) paramMap.get("sign");

        //获得本地表中医院的签名
        String hoscode = (String) paramMap.get("hoscode");

        HospitalSet byHoscode = hospitalSetService.getByHoscode(hoscode);
        if (byHoscode == null) {
            return Result.fail();
        }
        String signKey = byHoscode.getSignKey();

        //通过MD5加密之后在进行判断
        String encrypt = MD5.encrypt(signKey);
        //System.out.println("MD5: " + encrypt);

        if (!encrypt.equals(sign)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }


        //科室编号获取
        String depcode = (String) paramMap.get("depcode");

        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 1 : Integer.parseInt((String) paramMap.get("limit"));


        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        scheduleOrderVo.setHoscode(hoscode);
        scheduleOrderVo.setDepcode(depcode);

        //调用service方法
        Page<Schedule> pageModel = scheduleService.findPageDeaprtment(page, limit, scheduleOrderVo);
        //System.out.println("controller结果: "+pageModel);
        return Result.ok(pageModel);


    }

    //上传排班接口
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {
        //获取传递过来的科室信息
        Map<String, String[]> reuqestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(reuqestMap);


        //获得医院编号
        //签名验证

        //获得医院传过来的签名
        String sign = (String) paramMap.get("sign");

        //获得本地表中医院的签名
        String hoscode = (String) paramMap.get("hoscode");

        HospitalSet byHoscode = hospitalSetService.getByHoscode(hoscode);
        if (byHoscode == null) {
            return Result.fail();
        }
        String signKey = byHoscode.getSignKey();

        //通过MD5加密之后在进行判断
        String encrypt = MD5.encrypt(signKey);
        //System.out.println("MD5: " + encrypt);

        if (!encrypt.equals(sign)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }


        //科室编号获取
        String depcode = (String) paramMap.get("depcode");
        scheduleService.save(paramMap);


        return Result.ok();
    }

    //删除科室接口
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request) {
        //获取传递过来的科室信息
        Map<String, String[]> reuqestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(reuqestMap);


        //获得医院编号
        //签名验证

        //获得医院传过来的签名
        String sign = (String) paramMap.get("sign");

        //获得本地表中医院的签名
        String hoscode = (String) paramMap.get("hoscode");

        HospitalSet byHoscode = hospitalSetService.getByHoscode(hoscode);
        if (byHoscode == null) {
            return Result.fail();
        }
        String signKey = byHoscode.getSignKey();

        //通过MD5加密之后在进行判断
        String encrypt = MD5.encrypt(signKey);
        //System.out.println("MD5: " + encrypt);

        if (!encrypt.equals(sign)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }


        //科室编号获取
        String depcode = (String) paramMap.get("depcode");

        Boolean mark = departmentService.remove(hoscode, depcode);
        if (mark) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    //查询科室接口
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request) {
        //获取传递过来的科室信息
        Map<String, String[]> reuqestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(reuqestMap);


        //获得医院编号
        //签名验证

        //获得医院传过来的签名
        String sign = (String) paramMap.get("sign");

        //获得本地表中医院的签名
        String hoscode = (String) paramMap.get("hoscode");

        HospitalSet byHoscode = hospitalSetService.getByHoscode(hoscode);
        if (byHoscode == null) {
            return Result.fail();
        }
        String signKey = byHoscode.getSignKey();

        //通过MD5加密之后在进行判断
        String encrypt = MD5.encrypt(signKey);
        //System.out.println("查询科室信息");
        //System.out.println("MD51: " + sign);
        //System.out.println("MD52: " + encrypt);
        //System.out.println("MD53: " + MD5.encrypt(encrypt));


        if (!encrypt.equals(sign)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 1 : Integer.parseInt((String) paramMap.get("limit"));


        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        //System.out.println("陈");
        //System.out.println("page:" + page + ", limit: " + limit);
        //调用service方法
        Page<Department> pageModel = departmentService.findPageDeaprtment(page, limit, departmentQueryVo);
        //System.out.println("controller结果: "+pageModel);
        return Result.ok(pageModel);
    }



    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request) {
        //获取传递过来的意义信息
        Map<String, String[]> reuqestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(reuqestMap);

        //获得医院编号
        //签名验证

        //获得医院传过来的签名
        String sign = (String) paramMap.get("sign");

        //获得本地表中医院的签名
        String hoscode = (String) paramMap.get("hoscode");

        HospitalSet byHoscode = hospitalSetService.getByHoscode(hoscode);
        if (byHoscode == null) {
            return Result.fail();
        }
        String signKey = byHoscode.getSignKey();



        //通过MD5加密之后在进行判断
        String encrypt = MD5.encrypt(signKey);
        //System.out.println("展示医院信息");
        //System.out.println("MD51: " + sign);
        //System.out.println("MD52: " + encrypt);
        //System.out.println("MD5: " + encrypt);

        if (!encrypt.equals(sign)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        //调用service方法实现医院编号查询
        Hospital hospital = hospitalService.getByHoscode(hoscode);

        return Result.ok(hospital);
    }


    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        //获取传递过来的科室信息
        Map<String, String[]> reuqestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(reuqestMap);
        //签名验证

        //获得医院传过来的签名
        String sign = (String) paramMap.get("sign");

        //获得本地表中医院的签名
        String hoscode = (String) paramMap.get("hoscode");

        HospitalSet byHoscode = hospitalSetService.getByHoscode(hoscode);
        if (byHoscode == null) {
            return Result.fail();
        }
        String signKey = byHoscode.getSignKey();

        //通过MD5加密之后在进行判断
        String encrypt = MD5.encrypt(signKey);
        //System.out.println("MD5: " + encrypt);

        if (!encrypt.equals(sign)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        departmentService.save(paramMap);
        return Result.ok();
    }

    /**
     * 上传医院数据接口
     * @param request
     * @return
     */
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request) {
        //获取传递过来的意义信息
        Map<String, String[]> reuqestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(reuqestMap);

        //签名验证

        //获得医院传过来的签名
        String sign = (String) paramMap.get("sign");

        //获得本地表中医院的签名
        String hoscode = (String) paramMap.get("hoscode");

        HospitalSet byHoscode = hospitalSetService.getByHoscode(hoscode);
        if (byHoscode == null) {
            return Result.fail();
        }
        String signKey = byHoscode.getSignKey();

        //通过MD5加密之后在进行判断
        String encrypt = MD5.encrypt(signKey);
        //System.out.println("MD5: " + encrypt);

        if (!encrypt.equals(sign)) {
           throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        //传输过程中“+”转换为了“ ”，因此我们要转换回来
        String logoDataString = (String)paramMap.get("logoData");
        if(!StringUtils.isEmpty(logoDataString)) {
            String logoData = logoDataString.replaceAll(" ", "+");
            paramMap.put("logoData", logoData);
        }

        //调用service
        hospitalService.save(paramMap);
        return Result.ok();
    }



}
