package com.heycine.slash.auth.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * springBoot 启动类
 * ::优雅编程，此刻做起！
 * ::Elegant programming, start now!
 *
 * @author zhiji.zhou
 * @date 2022/8/25 下午9:58
 */
@Slf4j
@SpringBootApplication(scanBasePackages = {"com.heycine.slash"})
@MapperScan(basePackages = {"com.heycine.slash.auth.business.mapper"})
@EntityScan(basePackages = {"com.heycine.slash.auth.business.entity"})

@EnableSwagger2
@EnableFeignClients
@EnableDiscoveryClient
public class AuthServiceApplication {

	public static void main(String[] args) {
		TimeInterval timer = DateUtil.timer();

		SpringApplication.run(AuthServiceApplication.class, args);

		log.info("=========== auth-service服务启动成功 =========== 共耗时：{} ms", timer.interval());
	}

}
