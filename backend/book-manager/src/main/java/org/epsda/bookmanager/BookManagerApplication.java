package org.epsda.bookmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 开启定时任务
public class BookManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookManagerApplication.class, args);
	}

}
