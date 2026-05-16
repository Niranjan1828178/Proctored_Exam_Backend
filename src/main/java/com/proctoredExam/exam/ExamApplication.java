package com.proctoredExam.exam;

import com.proctoredExam.exam.entity.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExamApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExamApplication.class, args);

	}

}


//be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
//2026-04-24T21:19:38.097+05:30  WARN 22672 --- [exam] [           main] .s.a.UserDetailsServiceAutoConfiguration :
//
//Using generated security password: 92ffed60-24e9-4378-b4c1-a4333ce7d376
//
//This generated password is for development use only. Your security configuration must be updated before running your application in production.