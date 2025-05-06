package com.bounteous.bug_bounty_backend;

import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.BugClaimRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.DeveloperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BugBountyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BugBountyBackendApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(@Autowired DeveloperRepository repository) {
		return args -> {
			//			Developer dev = Developer.builder()
			//					.username("Imad")
			//					.rating(5.f)
			//					.id(1l)
			//					.build()
			//					;
			//			System.out.println(dev);
		};
	}
}
