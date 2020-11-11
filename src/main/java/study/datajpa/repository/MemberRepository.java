package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    @Query("select m from Member m where m.username = :username and m.age = :age") //@Query, 리포지토리 메소드에 쿼리 정의
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m") //특정값 조회
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t") //DTO로 조회
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username = :name") //이름 기반 파라미터 바인딩
    List<Member> findMembersNamedParam(@Param("name") String username);

    @Query("select m from Member m where m.username = ?1 and m.age = ?2") //위치 기반 파라미터 바인딩
    List<Member> findMembersNumeratedParam(String username, int age);

    @Query("select m from Member m where m.username in :names") //컬렉션 파라미터 바인딩
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findByUsernameIn(List<String> names); //메소드 이름 쿼리로 IN절 조회

    List<Member> findListByUsername(String username); //컬렉션
    Member findMemberByUsername(String username); //단건
    Optional<Member> findOptionalMemberByUsername(String username); //단건 Optional

    /**
     * 스프링 데이터 JPA 페이징과 정렬
     * * 검색 조건: 나이가 10살
     * * 정렬 조건: 이름으로 내림차순(desc)
     * * 페이징 조건: 첫 번째 페이지, 페이지당 보여줄 데이터는 3건
     */
    @Query(value = "select m from Member m left join m.team t where m.age = :age", countQuery = "select count(m) from Member m where m.age = :age")
    Page<Member> findByAge(@Param("age") int age, Pageable pageable); //count 쿼리 사용

    Slice<Member> findSliceByAge(int age, Pageable pageable); //count 쿼리 사용 안함

    List<Member> findListByAge(int age, Pageable pageable); //count 쿼리 사용 안함

    //count 쿼리를 다음과 같이 분리할 수 있음
    @Query(value = "select m from Member m", countQuery = "select count(m.username) from Member m")
    Page<Member> findMemberAllCountBy(Pageable pageable);

    //count 쿼리 분리 시, 조회 성능 비교
    @Query(value = "select m from Member m") //1. count 쿼리 분리 X
    Page<Member> findMemberNoConfigCountQuery(Pageable pageable);
    @Query(value = "select m from Member m left join m.team t") //2. (left outer join) count 쿼리 분리 X
    Page<Member> findMemberNoConfigCountQuery2(Pageable pageable);
    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m) from Member m") //3. (left outer join) count 쿼리 분리 O
    Page<Member> findMemberConfigCountQuery(Pageable pageable);

    //Top, First 키워드를 사용하여 쿼리 메서드의 결과를 제한 할 수 있다
    List<Member> findFirstBy();
    List<Member> findTopBy();
    List<Member> findTop3By();
    Slice<Member> findSliceTop3ByAge(int age);
    Page<Member> findPageTop3ByAge(int age, Pageable pageable);

    //==기타 연습 과제==//
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
