package com.neu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.neu.yygh.hosp.repository.ScheduleRepository;
import com.neu.yygh.hosp.service.DepartmentService;
import com.neu.yygh.hosp.service.HospitalService;
import com.neu.yygh.hosp.service.ScheduleService;
import com.neu.yygh.model.hosp.Department;
import com.neu.yygh.model.hosp.Schedule;
import com.neu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.neu.yygh.vo.hosp.ScheduleOrderVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    @Override
    public void save(Map<String, Object> paramMap) {
        //将数据集合转换为对象
        Schedule schedule = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Schedule.class);


        Schedule exist = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());

        //System.out.println("exit：" + exist);

        //判断
        if (exist != null) {
            exist.setUpdateTime(new Date());
            exist.setIsDeleted(0);
            exist.setStatus(1);
            scheduleRepository.save(exist);
        } else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> findPageDeaprtment(int page, int limit, ScheduleOrderVo scheduleOrderVo) {
        //通过pageable对象，设置当前页与每页记录数
        Pageable pageable = PageRequest.of(page - 1, limit);
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleOrderVo, schedule);
        schedule.setIsDeleted(0);
        schedule.setStatus(1);
        //创建Example对象
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Schedule> example = Example.of(schedule, matcher);
        Page<Schedule> all = scheduleRepository.findAll(example, pageable);
        return all;
    }

    @Override
    public void remove(String hoscode, String hosScheduleId) {
        //查询科室信息
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (schedule != null) {
            scheduleRepository.deleteById(schedule.getId());
        }
    }

    @Override
    public Map<String, Object> getScheduleRule(long page, long limit, String hoscode, String depcode) {
        //根据医院编号与科室编号进行查询
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);



        //根据工作日期进行排序
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),    //匹配条件
                Aggregation.group("workDate")  //分组字段
                        .first("workDate").as("workDate")
                        //统计每个医生的挂号数量
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                //排序
                Aggregation.sort(Sort.Direction.DESC, "workDate"),
                //分页
                Aggregation.skip((page - 1) * limit),
                Aggregation.limit(limit)
        );

        //调用方法，最终进行查询
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVo = aggregate.getMappedResults();


        //分组查询的总记录数
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalAggResult =
                mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggResult.getMappedResults().size();


        //把日期转化为星期
        for (BookingScheduleRuleVo bookingScheduleRuleVo1 : bookingScheduleRuleVo) {
            Date workDate = bookingScheduleRuleVo1.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo1.setDayOfWeek(dayOfWeek);
        }

        //设置最终的数据结构进行返回
        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList", bookingScheduleRuleVo);
        result.put("total", total);

        //获取医院名称
        String hosName = hospitalService.getHospName(hoscode);
        //其它基础数据使用另一个map存放
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname", hoscode);
        result.put("baseMap", baseMap);
        return result;
    }

    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        //根据参数设置MongoDB
        List<Schedule> scheduleList = scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, new DateTime(workDate).toDate());
        //查询其它数值并设置其它数值
        scheduleList.stream().forEach(iteam ->{
            this.packageSchedule(iteam);
        });


        return scheduleList;
    }

    //封装排班详情中的部分数值
    private void packageSchedule(Schedule iteam) {
        //设置医院名称
        iteam.getParam().put("hosname", hospitalService.getHospName(iteam.getHoscode()));
        //设置科室名称
        iteam.getParam().put("depname", departmentService.getDepName(iteam.getHoscode(), iteam.getDepcode()));
        //设置日期对应的星期
        iteam.getParam().put("dayOfWeek", this.getDayOfWeek(new DateTime(iteam.getWorkDate())));
    }

    /**
     * 根据日期获取周几数据
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }

}
