package edu.eci.cvds.ecireserves;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import lombok.Generated;

@SpringBootApplication
@EnableMongoRepositories
@EnableScheduling
@Generated
public class EciReservesApplication {

	public static void main(String[] args) {
		SpringApplication.run(EciReservesApplication.class, args);
	}

}
