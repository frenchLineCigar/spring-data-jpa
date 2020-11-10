package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

/**
 * Created by frenchline707@gmail.com on 2020-11-08
 * Blog : http://frenchline707.tistory.com
 * Github : http://github.com/frenchLineCigar
 */

public interface MemberRepository extends JpaRepository<Member, Long> { //엔티티 타입, 식별자(PK) 타입

    //Query Method : 메소드 이름을 분석해서 JPQL 쿼리 실행
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findByUsernameAndAgeGreaterThanEqual(String username, int age);

    List<Member> findHelloBy(); //findHelloBy 처럼 …에 식별하기 위한 내용(설명)이 들어가도 된다

    List<Member> findTop3HelloBy();

    @Query(name = "Member.findByUsername") //스프링 데이터 JPA는 이 애노테이션의 생략이 가능하다. 관례상 엔티티.메서드명으로 먼저 NamedQuery를 찾게 된다
    List<Member> findByUsername(@Param("username") String username); //Spring Data JPA NamedQuery

    long countMemberByUsernameStartingWith(String startingWith); //parameter bound with appended %
    long countMemberByUsernameEndingWith(String endingWith); //parameter bound with prepended %
    long countMemberByUsernameContaining(String containing); //parameter bound wrapped in %
    long countMemberByUsernameLike(String like); //parameter bound without %

    boolean existsMemberByAge(int age);
    long deleteMemberByAge(int age);
    long removeMemberByAge(int age);

    Member findFirstByOrderByUsernameAsc();
    Member findTopByOrderByAgeDesc();

    List<Member> findAllByUsername(String username, Pageable pageable);
    List<Member> findFirst5ByUsername(String username, Sort sort);
    List<Member> findTop10ByUsername(String username, Pageable pageable);

    //Paging 다룰때 해보기
    Page<Member> queryByUsername(String username, Pageable pageable);
    Page<Member> findByUsername(String username, Pageable pageable);
    Page<Member> getByUsername(String username, Pageable pageable);
    Page<Member> queryFirst10ByUsername(String username, Pageable pageable);
    Slice<Member> findTop3ByUsername(String username, Pageable pageable);

    //Join 다룰때 해보기
    List<Member> findDistinctMemberByUsernameOrAge(String username, int age);
    List<Member> findMemberDistinctByUsernameOrAge(String username, int age);
    List<Member> findDistinctByUsernameAndAge(String username, int age);

}
