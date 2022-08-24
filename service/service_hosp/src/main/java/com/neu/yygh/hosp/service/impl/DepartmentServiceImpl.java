package com.neu.yygh.hosp.service.impl;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.neu.yygh.hosp.repository.DepartmentRepositiory;
import com.neu.yygh.hosp.service.DepartmentService;
import com.neu.yygh.model.hosp.Department;
import com.neu.yygh.vo.hosp.DepartmentQueryVo;
import com.neu.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepositiory departmentRepositiory;

    @Override
    public void save(Map<String, Object> paramMap) {
        //将数据集合转换为对象
        Department department = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Department.class);


        Department exist = departmentRepositiory.getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());

        //System.out.println("exit：" + exist);

        //判断
        if (exist != null) {
            exist.setUpdateTime(new Date());
            exist.setIsDeleted(0);
            departmentRepositiory.save(exist);
        } else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepositiory.save(department);
        }


    }

    @Override
    public Page<Department> findPageDeaprtment(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        //通过pageable对象，设置当前页与每页记录数
        Pageable pageable = PageRequest.of(page - 1, limit);
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo, department);
        department.setIsDeleted(0);
        //创建Example对象
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                        .withIgnoreCase(true);
        Example<Department> example = Example.of(department, matcher);
        Page<Department> all = departmentRepositiory.findAll(example, pageable);
        return all;
    }

    @Override
    public Boolean remove(String hoscode, String depcode) {
        //查询科室信息
        Department department = departmentRepositiory.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            departmentRepositiory.deleteById(department.getId());
        }
        return true;
    }

    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //
        List<DepartmentVo> result = new ArrayList<>();

        //根据医院编号查询医院的所有科室信息
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        Example of = Example.of(departmentQuery);
        List<Department> all = departmentRepositiory.findAll(of);

        //根据大科室编号进行分组，然后获取每个大科室里面的下级科室
        Map<String, List<Department>> collect =
                all.stream().collect(Collectors.groupingBy(Department::getBigcode));

        //遍历map集合
        for (Map.Entry<String, List<Department>> entry : collect.entrySet()) {
            //大科室编号
            String bigCode = entry.getKey();
            List<Department> departmentsList = entry.getValue();

            //封装大科室
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepcode(bigCode);
            departmentVo.setDepname(departmentsList.get(0).getBigname());

            //封装小科室
            List<DepartmentVo> chil = new ArrayList<>();
            for (Department department : departmentsList) {
                DepartmentVo departmentVo1 = new DepartmentVo();
                departmentVo1.setDepcode(department.getDepcode());
                departmentVo1.setDepname(department.getDepname());
                chil.add(departmentVo1);
            }
            departmentVo.setChildren(chil);
            result.add(departmentVo);
        }
        return result;
    }

    //查科室名称
    @Override
    public String getDepName(String hoscode, String depcode) {
        Department departmentByHoscodeAndDepcode = departmentRepositiory.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
    if (departmentByHoscodeAndDepcode != null) {
        return departmentByHoscodeAndDepcode.getDepname();
    }
        return null;
    }
}
