package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

/**
 * Created by frenchline707@gmail.com on 2020-11-08
 * Blog : http://frenchline707.tistory.com
 * Github : http://github.com/frenchLineCigar
 */

public interface MemberRepository extends JpaRepository<Member, Long> { //엔티티 타입, 엔티티의 PK(@Id)에 맵핑된 타입
}
