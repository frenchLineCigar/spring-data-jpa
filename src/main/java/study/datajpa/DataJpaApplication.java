package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing
@SpringBootApplication
//@EnableJpaRepositories(basePackages = "study.datajpa.repository") //부트 사용 시 생략 가능, 메인 앱 하위가 아닐 경우 지정
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	/* @CreatedBy, @LastModifiedBy 가 사용하는 빈 */
	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> Optional.of(UUID.randomUUID().toString()); //AuditorAware.getCurrentAuditor() 구현
		//실제로는 UUID.randomUUID().toString() 부분에 로그인 유저 ID를 넣어줘야 된다
		//스프링 시큐리티 컨텍스트, 홀더 등에서 세션 정보를 가져와서 해당 로그인 ID를 꺼내서 넣는 처리를 하면 된다
	}

}
