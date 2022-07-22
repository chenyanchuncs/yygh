package com.neu.yygh.hosp.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.neu.yygh.hosp.mapper")
public class HospConfig {
}
