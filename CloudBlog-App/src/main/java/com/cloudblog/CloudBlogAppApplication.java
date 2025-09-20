package com.cloudblog;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.cloudblog.*.mapper")
public class CloudBlogAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudBlogAppApplication.class, args);
    }

}
