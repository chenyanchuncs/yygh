package com.neu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.neu.yygh.cmn.client.DictFeignClient;
import com.neu.yygh.hosp.repository.HospitalRepository;
import com.neu.yygh.hosp.service.HospitalService;
import com.neu.yygh.model.hosp.Hospital;
import com.neu.yygh.vo.hosp.HospitalQueryVo;
import com.neu.yygh.vo.hosp.HospitalSetQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;


    @Override
    public void save(Map<String, Object> paramMap) {
        //将数据集合转换为对象集合
        String jsonString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(jsonString, Hospital.class);

        //判断是否存在相同的数据，若不存在，直接添加；存在，进行更新
        String hoscode = hospital.getHoscode();
        Hospital hospitalExist = hospitalRepository.getHospitalByHoscode(hoscode);

        if (hospitalExist != null) {
            hospital.setStatus(hospitalExist.getStatus());
            hospital.setCreateTime(hospitalExist.getCreateTime());

        } else {
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
        }
        hospital.setUpdateTime(new Date());
        hospital.setIsDeleted(0);
        hospitalRepository.save(hospital);
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode);
    }

    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        //创建pageable对象
        Pageable pageable = PageRequest.of(page - 1, limit);

        //构建条件匹配器
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        //对象转换
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);
        //创建
        Example<Hospital> example = Example.of(hospital, matcher);

        //调用方法实现查询
        Page<Hospital> all = hospitalRepository.findAll(example, pageable);

        all.getContent().stream().forEach(iteam -> {
            this.setHospitalHosType(iteam);
        });

        return all;
    }
//更新医院状态
    @Override
    public void updateStatus(String id, Integer status) {
        Hospital hospital = hospitalRepository.findById(id).get();
        //设置修改的值
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }

    @Override
    public Map<String, Object> getHospById(String id) {
        Map<String, Object> result = new HashMap<>();
        Hospital hospital = this.setHospitalHosType(hospitalRepository.findById(id).get());
        result.put("hospital", hospital);
        result.put("bookingRule", hospital.getBookingRule());

        return result;
    }

    //获得医院名称
    @Override
    public String getHospName(String hoscode) {
        Hospital hospitalByHoscode = hospitalRepository.getHospitalByHoscode(hoscode);
        if (hospitalByHoscode != null) {
            return hospitalByHoscode.getHosname();
        }
        return null;
    }

    @Override
    public List<Hospital> findByHosname(String hosname) {
        return hospitalRepository.findHospitalByHosnameLike(hosname);
    }

    //进行医院等级设置
    private Hospital setHospitalHosType(Hospital item) {
        String hostypeString = dictFeignClient.getName("Hostype", item.getHostype());

        //查询省市地区
        String provinceString = dictFeignClient.getName(item.getProvinceCode());
        String cityString = dictFeignClient.getName(item.getCityCode());
        String districtString = dictFeignClient.getName(item.getDistrictCode());

        item.getParam().put("hostypeString", hostypeString);
        item.getParam().put("fullAddress", provinceString + cityString + districtString);

        return item;
    }
}
