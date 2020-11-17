package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

/**
 * 인터페이스 기반의 Projection : 프록시를 가지고 동작
 */

public interface UsernameOnly {

    /**
     * 인터페이스 기반의 Closed Projection 사용: select 절에 필요한 필드만 가져옴
     */
    String getUsername();

    /**
     * 인터페이스 기반의 Open Projection 사용: select 절에서 일단 모든 필드를 다 퍼올리고 PL로 파싱함
     */
    @Value("#{target.username + ' ' + target.age}") //PL 문법을 그대로 사용할 수 있다. 스프링이 PL 문법을 지원
    String getUsernameAndAge();

}
