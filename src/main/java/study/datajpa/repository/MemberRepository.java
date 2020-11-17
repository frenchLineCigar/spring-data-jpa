package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by frenchline707@gmail.com on 2020-11-08
 * Blog : http://frenchline707.tistory.com
 * Github : http://github.com/frenchLineCigar
 */

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member> { //JpaSpecificationExecutor 인터페이스 상속

    //Query Method : 메소드 이름을 분석해서 JPQL 쿼리 실행
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findByUsernameAndAgeGreaterThanEqual(String username, int age);

    List<Member> findHelloBy(); //findHelloBy 처럼 …에 식별하기 위한 내용(설명)이 들어가도 된다

    List<Member> findTop3HelloBy();

    //@Query(name = "Member.findByUsername") //스프링 데이터 JPA는 이 애노테이션의 생략이 가능하다. 관례상 엔티티.메서드명으로 먼저 NamedQuery를 찾게 된다
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
    Slice<Member> findTop3ByAge(int age);
    Page<Member> findTop3ByAge(int age, Pageable pageable);

    /**
     * 벌크 업데이트 (벌크성 수정 쿼리)
     */
    @Modifying(clearAutomatically = true) //벌크 연산 이후 영속성 컨텍스트를 비운다 (쿼리가 나간 이후 em.clear()을 자동으로 해준다)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Modifying(clearAutomatically = true) //벌크 연산 이후 영속성 컨텍스트를 비운다 (쿼리가 나간 이후 em.clear()을 자동으로 해준다)
    @Query("update Member m set m.age = m.age + :x where m.age >= :starting")
    int bulkAgePlusX(@Param("starting") int starting, @Param("x") int x);

    /**
     * style 1. fetch join으로 N + 1 문제 해결 (JPA 기본 제공)
     */
    @Query("select m from Member m left join fetch m.team") //fetch 키워드: Member 조회 시 연관된(객체 그래프) Team도 쿼리 한번에 다 끌고 온다
    List<Member> findMemberFetchJoin();

    /**
     * style 2. EntityGraph를 활용한 fetch join (Spring Data JPA 제공): 메서드 이름으로만 쿼리 해결 시 fetch join을 하고 싶은 경우
     */
    @Override
    @EntityGraph(attributePaths = {"team"}) //연관된(객체 그래프로 탐색되는) m.team도 fetch join으로 다 끌고 온다
    List<Member> findAll();

    /**
     * style 3. JPQL + EntityGraph : JPQL을 짰는데 fetch join만 살짝 추가하고 싶은 경우
     */
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    /**
     * style 4. 엔티티에 @NamedEntityGraph를 정의해서 사용할 수도 있다 (JPA 2.2+)
     */
    @EntityGraph(attributePaths = {"team"})
//    @EntityGraph("Member.team") //엔티티에 @NamedEntityGraph를 정의한 것을 사용할 수도 있다 (JPA 2.2+)
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    /**
     * JPA Hint & Lock
     */
    //JPA Hint
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true")) //JPA가 Hibernate한테 위임할 수 있게 구멍을 열어놓는다
    Member findReadOnlyByUsername(String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true")) //스냅샷 생성 X -> 변경 감지 체크 X -> 읽기 전용으로만 간주
    Member findReadOnlyById(Long id);

    //스프링 5.1 버전 이후 사용 시, @Transaction(readOnly=true)로 설정하면, 이 옵션이 엔티티 매니저까지 반영되어 @QueryHint의 readOnly까지 모두 동작
    //@QueryHints(value = {@QueryHint(name = "org.hibernate.readOnly", value = "true")}, forCounting = true)
    Page<Member> findReadOnlyByUsername(String username, Pageable pageable);

    //JPA Lock
    //비관적 락(PESSIMISTIC LOCK): select for update -> 방언(Dialect)에 따라서 동작 방식이 달라진다
    //동일한 데이터를 동시에 수정할 가능성이 높다(트랜잭션의 충돌이 발생한다고 가정하고 우선적으로 락을 걸고 본다)는 비관적인 전제로 잠금을 거는 방식
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    //인터페이스 기반의 Projection
    List<UsernameOnly> findProjectionsByUsername(@Param("username") String username);

    //클래스 기반의 Projection
    List<UsernameOnlyDto> findProjectionsDtoByUsername(@Param("username") String username);

    //동적 Projection : 제네릭 타입 지정 가능
    <T> List<T> findProjectionsByUsername(@Param("username") String username, Class<T> type);

    //스프링 데이터 JPA 네이티브 쿼리
    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    //스프링 데이터 JPA 네이티브 쿼리 + 인터페이스 기반 Projection 활용 : 페이징 처리 가능
    @Query(value = "select m.member_id as id, m.username, t.name as teamName " +
            "from member m left join team t",
            countQuery = "select count(*) as totalCount from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);

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
