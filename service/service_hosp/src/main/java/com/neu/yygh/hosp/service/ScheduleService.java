package com.neu.yygh.hosp.service;

import com.neu.yygh.model.hosp.Schedule;
import com.neu.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    void save(Map<String, Object> paramMap);

    Page<Schedule> findPageDeaprtment(int page, int limit, ScheduleOrderVo scheduleOrderVo);

    void remove(String hoscode, String hosSchedileId);

    Map<String, Object> getScheduleRule(long page, long limit, String hoscode, String depcode);

    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);
}
