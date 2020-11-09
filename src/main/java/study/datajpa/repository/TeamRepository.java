package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Team;

/**
 * Created by frenchline707@gmail.com on 2020-11-09
 * Blog : http://frenchline707.tistory.com
 * Github : http://github.com/frenchLineCigar
 */

public interface TeamRepository extends JpaRepository<Team, Long> { //엔티티 타입, 식별자(PK) 타입
}
