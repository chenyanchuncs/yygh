package com.neu.yygh.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neu.yygh.model.cmn.Dict;
import com.neu.yygh.model.hosp.HospitalSet;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {
    //根据数据id查询子数据列表
    List<Dict> findChlidData(Long id);


    /**
     * 导出
     * @param response
     */
    void exportData(HttpServletResponse response);

    //导入数据字典
    void importDictData(MultipartFile file);

    String getDictName(String dictCode, String value);

    List<Dict> findByDictCode(String dictCode);
}
