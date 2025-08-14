package com.example.book;
import com.example.book.role.Role;
import com.example.book.role.RoleRepository;
import com.example.book.user.User;
import com.example.book.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
@EnableAsync
@EnableJpaAuditing(auditorAwareRef = "auditor")
public class BookNetworkApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookNetworkApiApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(RoleRepository repository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (repository.findByName("USER").isEmpty() && repository.findByName("ADMIN").isEmpty()) {
				var roleUser = Role.builder()
						.name("USER")
						.build();
				var roleAdmin = Role.builder().name("ADMIN")
						.build();
				repository.save(roleUser);
				repository.save(roleAdmin);
			}
			if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
				var roleAdmin = repository.findByName("ADMIN")
						.orElseThrow(() -> new RuntimeException("No admin found"));
				var user = User.builder()
						.email("admin@gmail.com")
						.password(passwordEncoder.encode("password"))
						.roles(List.of(roleAdmin))
						.enabled(true)
						.build();
				userRepository.save(user);
			}
		};
	}
}
