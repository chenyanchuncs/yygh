package com.neu.yygh.hosp.service;


import com.neu.yygh.model.hosp.Department;
import com.neu.yygh.vo.hosp.DepartmentQueryVo;
import com.neu.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    void save(Map<String, Object> paramMap);

    Page<Department> findPageDeaprtment(int page, int limit, DepartmentQueryVo departmentQueryVo);

    Boolean remove(String hoscode, String depcode);

    List<DepartmentVo> findDeptTree(String hoscode);

    //查科室名称
    String getDepName(String hoscode, String depcode);
}
