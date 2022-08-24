package com.neu.yygh.hosp.service;

import com.neu.yygh.model.hosp.Hospital;
import com.neu.yygh.vo.hosp.HospitalQueryVo;
import com.neu.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.models.auth.In;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {
    void save(Map<String, Object> paramMap);

    Hospital getByHoscode(String hoscode);

    Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);

    Map<String, Object> getHospById(String id);

    String getHospName(String hoscode);

    List<Hospital> findByHosname(String hosname);
}
