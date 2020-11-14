package study.datajpa.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Created by frenchline707@gmail.com on 2020-11-08
 * Blog : http://frenchline707.tistory.com
 * Github : http://github.com/frenchLineCigar
 */

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

    @Test
    public void testMember() {
        System.out.println("memberRepository = " + memberRepository.getClass()); //class com.sun.proxy.$ProxyXXX
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
        assertThat(findMember).isSameAs(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

//        findMember1.setUsername("member!!!!!"); //변경 감지(dirty checking)

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
    }

    @Test
    public void findByUsernameAndAgeGreaterThanEqual() {
        Member m1 = new Member("AAA", 15);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThanEqual("AAA", 15);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(15);
        assertThat(result.get(1).getUsername()).isEqualTo("AAA");
        assertThat(result.get(1).getAge()).isEqualTo(20);
    }

    @Test
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findHelloBy();
    }

    @Test
    public void findTop3HelloBy() {
        //limit 3
        List<Member> top3HelloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        
        List<Member> result = memberRepository.findByUsername("AAA"); //Spring Data JPA NamedQuery
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10); //@Query, 리포지토리 메소드에 쿼리 정의
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        memberRepository.save(m1);
        m1.setTeam(team);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findMembersNamedParam() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findMembersNamedParam("AAA"); //이름 기반 파라미터 바인딩
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void findMembersNumeratedParam() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findMembersNumeratedParam("AAA", 10); //위치 기반 파라미터 바인딩
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        //where member0_.username in ('AAA' , 'BBB')
        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB")); //컬렉션 파라미터 바인딩
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void findByUsernameIn() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        //where member0_.username in ('AAA' , 'BBB')
        List<Member> result = memberRepository.findByUsernameIn(Arrays.asList("AAA", "BBB")); //메소드 이름 쿼리로 IN절 조회
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

//        List<Member> aaa = memberRepository.findListByUsername("AAA"); //컬렉션
//        Member aaa = memberRepository.findMemberByUsername("AAA"); //단건
        Optional<Member> aaa = memberRepository.findOptionalMemberByUsername("AAA");//단건 Optional
        System.out.println("aaa = " + aaa.orElseThrow(IllegalStateException::new));
//        System.out.println("aaa = " + aaa.orElseThrow(() -> new NoSuchElementException("No value present")));
    }

    @Test
    public void returnTypeCollection() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        
        // 주의: 컬렉션 조회 시, 파라미터를 이상하게 넣어서 데이터가 없을 수 있는 경우가 있는데, 이때 result는 null이 아니다.
        // 비어 있는 컬렉션(empty collection)을 반환한다.
        // List는 무조건 그냥 받으면 된다. 절대 null이 아닌 것이 보장된다.
        List<Member> result = memberRepository.findListByUsername("asdfasdf"); //컬렉션
        System.out.println("result = " + result); //result = []
        System.out.println("result.size() = " + result.size()); //result.size() = 0
    }

    @Test
    public void returnTypeSingle() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // 주의: 단건 조회 시, 결과가 없는 경우 findMember는 null이다.
        Member findMember = memberRepository.findMemberByUsername("asdfasdf");//단건
        System.out.println("findMember = " + findMember);
    }

    @Test
    public void returnTypeSingleOptional() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // Java 8 이후, 조회 데이터가 있을 지 없을 지 모르면 Optional을 써라
        // null이면 Optional.empty 로 반환
        Optional<Member> findMember = memberRepository.findOptionalMemberByUsername("asdfasdf");//단건 Optional
        System.out.println("findMember = " + findMember); //findMember = Optional.empty
    }

    @Test
    public void returnTypeSingleDuplicate() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        Assertions.assertThrows(IncorrectResultSizeDataAccessException.class, () -> {
            Optional<Member> findMember = memberRepository.findOptionalMemberByUsername("AAA"); //NonUniqueResultException(JPA) -> IncorrectResultSizeDataAccessException(Spring)
            System.out.println("findMember = " + findMember);
        });

        /**
         * 단건 조회 인데 결과가 2개 이상인 경우, 예외가 발생한다. 이때 발생한 JPA 예외는 Spring 예외로 변환돼 반환된다.
         * ````
         * Caused by: javax.persistence.NonUniqueResultException: query did not return a unique result: 2
         * -> org.springframework.dao.IncorrectResultSizeDataAccessException: query did not return a unique result: 2;
         * ````
         * 참고: NonUniqueResultException이 터지면 Spring Data JPA는 이 jpa 예외를
         * IncorrectResultSizeDataAccessException 라는 springframework 예외로 변환 후 반환해준다.
         * 왜냐하면 Repository의 기술은 JPA가 될 수도 있고, MongoDB가 될 수도 있고, 다른 기술이 될 수도 있을 것이다.
         * 이를 사용하는 Service 계층의 클라이언트 코드들은 JPA에 의존하는 것이 아니라, 스프링이 추상화한 예외에 의존하면
         * 하부의 Repository 기술을 JPA에서 MongoDB나 아니면 다른 JDBC 기술로 바꿔도, 스프링은 동일한 스펙의 예외를 내려준다.
         * 따라서 이를 사용하는 클라이언트 코드들을 바꿀 필요가 없다. 그래서 스프링이 예외를 한번 변환을 해서 반환을 해준다.
         */
    }

    @Test
    public void returnTypeException() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        Optional<Member> findMember = memberRepository.findOptionalMemberByUsername("CCC");//단건 Optional
        System.out.println("findMember = " + findMember); //findMember = Optional.empty

        Assertions.assertThrows(IllegalStateException.class, () -> {
            System.out.println("findMember = " + findMember.orElseThrow(() -> new IllegalStateException("입력하신 이름의 회원은 존재하지 않습니다.")));
//            System.out.println("findMember = " + findMember.orElseThrow(IllegalStateException::new));
//            System.out.println("findMember = " + findMember.orElseThrow(() -> new NoSuchElementException("No value present")));
        });
    }

    @Test
    public void paging_ReturnTypePage() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username")); //페이징 조건과 정렬 조건 설정

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest); //count 쿼리 사용

        //then
        List<Member> content = page.getContent(); //조회된 데이티

        assertThat(content.size()).isEqualTo(3); //조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(0); //현재 페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
        assertThat(page.isFirst()).isTrue(); //현재 페이지가 첫번째 페이지인가?
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
        assertThat(page.getNumberOfElements()).isEqualTo(3); //현재 페이지 데이터 수 (= 조회된 데이터 수)
        assertThat(page.getSize()).isEqualTo(page.getPageable().getPageSize()).isEqualTo(3); //한번에 가져올 데이터 수
    }

    @Test
    public void paging2_ReturnTypePage() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(1, 3, Sort.by(Sort.Direction.DESC, "username")); //페이징 조건과 정렬 조건 설정
        //PageRequest pageRequest = PageRequest.of(1, 4, Sort.by(Sort.Direction.DESC, "username")); //페이징 조건과 정렬 조건 설정

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest); //count 쿼리 사용

        //then
        List<Member> content = page.getContent(); //조회된 데이티

        assertThat(content.size()).isEqualTo(2); //조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(1); //현재 페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
        assertThat(page.isFirst()).isFalse(); //현재 페이지가 첫번째 페이지인가?
        assertThat(page.hasNext()).isFalse(); //다음 페이지가 있는가? -> False면 count 쿼리 안나감
        assertThat(page.getNumberOfElements()).isEqualTo(2); //현재 페이지 데이터 수 (= 조회된 데이터 수)
        assertThat(page.getSize()).isEqualTo(page.getPageable().getPageSize()).isEqualTo(3); //한번에 가져올 데이터 수
//        assertThat(content.size()).isEqualTo(1); //조회된 데이터 수
//        assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 수
//        assertThat(page.getNumber()).isEqualTo(1); //현재 페이지 번호
//        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
//        assertThat(page.isFirst()).isFalse(); //현재 페이지가 첫번째 페이지인가?
//        assertThat(page.hasNext()).isFalse(); //다음 페이지가 있는가? -> False면 count 쿼리 안나감
//        assertThat(page.getNumberOfElements()).isEqualTo(1); //현재 페이지 데이터 수 (= 조회된 데이터 수)
//        assertThat(page.getSize()).isEqualTo(page.getPageable().getPageSize()).isEqualTo(4); //한번에 가져올 데이터 수
    }

    @Test
    public void pagingDto() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest); //count 쿼리 사용

        Page<MemberDto> dtoPage = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
//        Page<MemberDto> dtoPage = page.map(member -> new MemberDto(member.getId(), member.getUsername(), member.getTeam().getName()));

        //then
        List<MemberDto> content = dtoPage.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(dtoPage.getTotalElements()).isEqualTo(5); //전체 row 수 (= totalCount)
        assertThat(dtoPage.getNumber()).isEqualTo(0); //현재 페이지 번호
        assertThat(dtoPage.getTotalPages()).isEqualTo(2); //전체 페이지 수
        assertThat(dtoPage.isFirst()).isTrue(); //첫번째 페이지 여부
        assertThat(dtoPage.hasNext()).isTrue(); //다음 페이지 유무
        assertThat(dtoPage.getNumberOfElements()).isEqualTo(3); //현재 페이지 row 수
        assertThat(dtoPage.getSize()).isEqualTo(dtoPage.getPageable().getPageSize()).isEqualTo(3); //한번에 가져올 row 수
    }

    @Test
    public void paging_ReturnTypeSlice() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest); //count 쿼리 사용 안함

        //then
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
//        assertThat(page.getTotalElements()).isEqualTo(5); //슬라이스는 전체 row 수 (= totalCount)를 가져오지 X
        assertThat(page.getNumber()).isEqualTo(0);
//        assertThat(page.getTotalPages()).isEqualTo(2); //따라서 전체 페이지 수도 계산 X
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
        assertThat(page.getNumberOfElements()).isEqualTo(3);
        assertThat(page.getSize()).isEqualTo(page.getPageable().getPageSize()).isEqualTo(3);
    }

    @Test
    public void paging_ReturnTypeList() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        List<Member> page = memberRepository.findListByAge(age, pageRequest);//count 쿼리 사용 안함

        //then
//        List<Member> content = page.getContent();

//        assertThat(content.size()).isEqualTo(3);
//        assertThat(page.getTotalElements()).isEqualTo(5);
//        assertThat(page.getNumber()).isEqualTo(0);
//        assertThat(page.getTotalPages()).isEqualTo(2);
//        assertThat(page.isFirst()).isTrue();
//        assertThat(page.hasNext()).isTrue();
//        assertThat(page.getNumberOfElements()).isEqualTo(3);
//        assertThat(page.getSize()).isEqualTo(page.getPageable().getPageSize()).isEqualTo(3);
    }

    @Test
    public void pagingTop3_ReturnTypeList() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;

        //when
        List<Member> content = memberRepository.findTop3By();
//        List<Member> content = memberRepository.findTopBy();
//        List<Member> content = memberRepository.findFirstBy();
        for (Member member : content) {
            System.out.println("member = " + member);
        }

        //then
        assertThat(content.size()).isEqualTo(3);
        assertThat(content.get(0).getUsername()).isEqualTo("member1");
        assertThat(content.get(1).getUsername()).isEqualTo("member2");
        assertThat(content.get(2).getUsername()).isEqualTo("member3");
    }

    @Test
    public void pagingTop3_ReturnTypeSlice() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Slice<Member> page = memberRepository.findTop3ByAge(age);
        for (Member member : page) {
            System.out.println("member <- slice = " + member);
        }
        //then
        List<Member> content = page.getContent();
        for (Member member : content) {
            System.out.println("member <- list = " + member);
        }

        assertThat(content.size()).isEqualTo(3);
//        assertThat(page.getTotalElements()).isEqualTo(5); //전체 row 수 (= totalCount)
        assertThat(page.getNumber()).isEqualTo(0); //현재 페이지 번호
//        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 수
        assertThat(page.isFirst()).isTrue(); //첫번째 페이지 여부
        assertThat(page.isLast()).isTrue(); //마지막 페이지 여부
        assertThat(page.hasNext()).isFalse(); //다음 페이지 유무
        assertThat(page.getNumberOfElements()).isEqualTo(3); //현재 페이지 row 수
        assertThat(page.getSize()).isEqualTo(3); //한번에 가져올 row 수
    }

    @Test
    public void pagingTop3_ReturnTypePage() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findTop3ByAge(age, pageRequest);
        for (Member member : page) {
            System.out.println("member <- page = " + member);
        }
        //then
        List<Member> content = page.getContent();
        for (Member member : content) {
            System.out.println("member <- list = " + member);
        }

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5); //전체 row 수 (= totalCount)
        assertThat(page.getNumber()).isEqualTo(0); //현재 페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 수
        assertThat(page.isFirst()).isTrue(); //첫번째 페이지 여부
        assertThat(page.isLast()).isFalse(); //마지막 페이지 여부
        assertThat(page.hasNext()).isTrue(); //다음 페이지 유무
        assertThat(page.getNumberOfElements()).isEqualTo(3); //현재 페이지 row 수
        assertThat(page.getSize()).isEqualTo(3); //한번에 가져올 row 수
    }

    @Test
    public void pagingFindMemberAllCountBy() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findMemberAllCountBy(pageRequest); //count 쿼리 사용

        Page<MemberDto> dtoPage = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        //then
        List<MemberDto> content = dtoPage.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(dtoPage.getTotalElements()).isEqualTo(5); //전체 row 수 (= totalCount)
        assertThat(dtoPage.getNumber()).isEqualTo(0); //현재 페이지 번호
        assertThat(dtoPage.getTotalPages()).isEqualTo(2); //전체 페이지 수
        assertThat(dtoPage.isFirst()).isTrue(); //첫번째 페이지 여부
        assertThat(dtoPage.hasNext()).isTrue(); //다음 페이지 유무
        assertThat(dtoPage.getNumberOfElements()).isEqualTo(3); //현재 페이지 row 수
        assertThat(dtoPage.getSize()).isEqualTo(dtoPage.getPageable().getPageSize()).isEqualTo(3); //한번에 가져올 row 수
    }

    @Test
    public void bulkUpdate() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20); //벌크 연산은 영속성 컨텍스트를 무시하고, 다이렉트로 DB에 처리한다
//        int resultCount = memberRepository.bulkAgePlusX(20, 100);
//        em.clear(); //벌크 연산 이후 영속성 컨텍스트를 비운다 (-> 스프링 데이터 JPA : @Modifying(clearAutomatically = true)로 걸어줄 수 있다)

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5); //40살일까? 41살일까?

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    //지연 로딩 시, N + 1 문제
    @Test
    public void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when N + 1
        //select Member 1
        List<Member> members = memberRepository.findAll();

        //then
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass()); //프록시 객체 : class study.datajpa.entity.Team$HibernateProxy$q1dcqgDo
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    //style 1. fetch join으로 N + 1 문제 해결 (JPA 기본 제공)
    @Test
    public void findMemberFetchJoin() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when FetchJoin
        //select Member left join Team 1
        List<Member> members = memberRepository.findMemberFetchJoin();

        //then
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass()); //진짜 객체 : class study.datajpa.entity.Team
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    //style 2. @Override findAll() + @EntityGraph를 활용한 fetch join (Spring Data JPA 제공)
    @Test
    public void overrideFindAllEntityGraph() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when EntityGraph
        //select Member left join Team 1
        List<Member> members = memberRepository.findAll(); //-> memberRepository에 재정의한 findAll()의 주석 해제 후 확인

        //then
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass()); //진짜 객체 : class study.datajpa.entity.Team
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    //style 3. JPQL + EntityGraph 혼용
    @Test
    public void findMemberEntityGraph() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when EntityGraph
        //select Member left join Team 1
        List<Member> members = memberRepository.findMemberEntityGraph();

        //then
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass()); //진짜 객체 : class study.datajpa.entity.Team
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    //style 4. 엔티티에 @NamedEntityGraph를 정의해서 사용 (JPA 2.2+)
    @Test
    public void findEntityGraphByUsername() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member1", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when EntityGraph
        //select Member left join Team 1
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        //then
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass()); //진짜 객체 : class study.datajpa.entity.Team
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    /**
     * JPA QueryHint
     */
    @Test
    public void queryHint() {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
//        Member findMember = memberRepository.findById(member1.getId()).get();
//        Member findMember = memberRepository.findReadOnlyByUsername("member1"); //Update Query 실행 X
        Member findMember = memberRepository.findReadOnlyById(member1.getId()); //Update Query 실행 X
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    @Transactional(readOnly = true) //스프링 5.1 버전 이후 사용 시, @Transaction(readOnly=true)로 설정하면, 이 옵션이 엔티티 매니저까지 반영되어 @QueryHint의 readOnly까지 모두 동작
    public void QueryHintPaging() {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        Member member2 = memberRepository.save(new Member("member2", 10));
        Member member3 = memberRepository.save(new Member("member3", 10));
        Member member4 = memberRepository.save(new Member("member4", 10));
        Member member5 = memberRepository.save(new Member("member5", 10));
        em.flush();
        em.clear();

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> page = memberRepository.findReadOnlyByUsername("member1", pageRequest);
        List<Member> content = page.getContent();
        content.get(0).setUsername("aaaaaaaaa");

        em.flush();
    }

    /**
     * JPA Lock
     */
    @Test
    public void lock() {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        //SELECT m.member_id, m.age, m.team_id, m.username FROM member m WHERE m.username='member1' FOR UPDATE;
        List<Member> result = memberRepository.findLockByUsername("member1");//Update Query 실행 X
        result.get(0).setUsername("member2");
        em.flush();
    }



    @Test
    public void countMemberByUsernameStartingWith() {
        Member m1 = new Member("김대리", 10);
        Member m2 = new Member("김사원", 20);
        Member m3 = new Member("김대리", 30);
        Member m4 = new Member("김과장", 40);
        Member m5 = new Member("이부장", 40);
        Member m6 = new Member("박차장", 40);
        Member m7 = new Member("동네세차장사장님", 50);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);
        memberRepository.save(m5);
        memberRepository.save(m6);
        memberRepository.save(m7);

        //select count(member0_.member_id) as col_0_0_ from member member0_ where member0_.username like '김대%' escape '\';
        long result = memberRepository.countMemberByUsernameStartingWith("김대");
        assertThat(result).isEqualTo(2);

        //select count(member0_.member_id) as col_0_0_ from member member0_ where member0_.username like '%장' escape '\';
        long result2 = memberRepository.countMemberByUsernameEndingWith("장");
        assertThat(result2).isEqualTo(3);

        //select count(member0_.member_id) as col_0_0_ from member member0_ where member0_.username like '%차장%' escape '\';
        Long result3 = memberRepository.countMemberByUsernameContaining("차장");
        assertThat(result3).isEqualTo(2);

        long result4 = memberRepository.countMemberByUsernameLike("김%");
        System.out.println("result4 = " + result4);
    }

    @Test
    public void existsMemberByAge() {
        Member m1 = new Member("AAA", 15);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        //select member0_.member_id as col_0_0_ from member member0_ where member0_.age=15 limit 1;
        boolean result = memberRepository.existsMemberByAge(15);
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void deleteMemberByAge() {
        Member m1 = new Member("AAA", 20);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long result = memberRepository.deleteMemberByAge(20);
        assertThat(result).isEqualTo(2);

        List<Member> all2 = memberRepository.findAll();
        assertThat(all2.size()).isEqualTo(0);
    }

    @Test
    public void removeMemberByAge() {
        Member m1 = new Member("AAA", 20);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long result = memberRepository.removeMemberByAge(20);
        assertThat(result).isEqualTo(2);

        List<Member> all2 = memberRepository.findAll();
        assertThat(all2.size()).isEqualTo(0);
    }

    @Test
    public void findFirstByOrderByUsernameAsc() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        Member m3 = new Member("AAA", 20);
        Member m4 = new Member("BBB", 30);
        Member m5 = new Member("BBB", 30);
        Member m6 = new Member("CCC", 30);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);
        memberRepository.save(m5);
        memberRepository.save(m6);

        //order by member0_.username ASC LIMIT 1;
        Member result = memberRepository.findFirstByOrderByUsernameAsc();
        assertThat(result.getUsername()).isEqualTo("AAA");
        assertThat(result.getAge()).isEqualTo(10);
    }

    @Test
    public void findTopByOrderByAgeDesc() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 15);
        Member m3 = new Member("AAA", 20);
        Member m4 = new Member("AAA", 30);
        Member m5 = new Member("BBB", 30);
        Member m6 = new Member("CCC", 30);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);
        memberRepository.save(m5);
        memberRepository.save(m6);

        //order by member0_.age DESC LIMIT 1;
        Member result = memberRepository.findTopByOrderByAgeDesc();
        assertThat(result.getAge()).isEqualTo(30);
        assertThat(result.getUsername()).isEqualTo("AAA");
    }

    @Test
    public void findAllByUsername() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 15);
        Member m3 = new Member("AAA", 20);
        Member m4 = new Member("AAA", 30);
        Member m5 = new Member("BBB", 30);
        Member m6 = new Member("CCC", 30);
        Member m7 = new Member("AAA", 40);
        Member m8 = new Member("AAA", 45);
        Member m9 = new Member("AAA", 50);
        Member m10 = new Member("AAA", 60);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);
        memberRepository.save(m5);
        memberRepository.save(m6);
        memberRepository.save(m7);
        memberRepository.save(m8);
        memberRepository.save(m9);
        memberRepository.save(m10);

        Pageable firstPageWithTwoElements = PageRequest.of(0, 2);
        Pageable secondPageWithFiveElements = PageRequest.of(1, 5); //offset = page * size(limit)

        //limit 2 offset 0
        Page<Member> allMembers = memberRepository.findAll(firstPageWithTwoElements);
        System.out.println("allMembers = " + allMembers);
        for (Member member : allMembers) {
            System.out.println("memberAll = " + member);
        }
        //where member0_.username='AAA' limit 5 offset 5
        List<Member> allAAAMembers = memberRepository.findAllByUsername("AAA", secondPageWithFiveElements);
        System.out.println("allAAAMembers = " + allAAAMembers);
        for (Member memberAAA : allAAAMembers) {
            System.out.println("memberAAA = " + memberAAA);
        }

    }

    @Test
    public void findFirst5ByUsername() {
        Member m1 = new Member("AAA", 1);
        Member m2 = new Member("AAA", 2);
        Member m3 = new Member("AAA", 3);
        Member m4 = new Member("AAA", 4);
        Member m5 = new Member("AAA", 5);
        Member m6 = new Member("AAA", 6);
        Member m7 = new Member("AAA", 7);
        Member m8 = new Member("AAA", 8);
        Member m9 = new Member("AAA", 9);
        Member m10 = new Member("AAA", 10);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);
        memberRepository.save(m5);
        memberRepository.save(m6);
        memberRepository.save(m7);
        memberRepository.save(m8);
        memberRepository.save(m9);
        memberRepository.save(m10);

//        Sort.TypedSort<Member> memberTypedSort = Sort.sort(Member.class);
//        Sort sort = memberTypedSort.by(Member::getId).ascending();
        Sort sort = Sort.by("id").ascending();

        //where member0_.username='AAA' order by member0_.member_id asc limit 5;
        List<Member> result = memberRepository.findFirst5ByUsername("AAA", sort);
        System.out.println("result = " + result);
        int i = 1;
        for (Member member : result) {
            assertThat(member.getAge()).isEqualTo(i);
            System.out.println("member = " + member);
            i++;
        }
    }

    @Test
    public void findTop10ByUsername() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("AAA", i*2+1)); //AAA 100건
            memberRepository.save(new Member("BBB", i*2+2)); //BBB 100건
        }

        Sort.TypedSort<Member> memberTypedSort = Sort.sort(Member.class);
        Sort sort = memberTypedSort.by(Member::getId).ascending();
        PageRequest firstPageWithTenElements = PageRequest.of(0, 10, sort);
        PageRequest secondPageWithTenElements = PageRequest.of(1, 10, sort);

        //where username='BBB' limit 10 offset 0
        List<Member> result = memberRepository.findTop10ByUsername("BBB", secondPageWithTenElements);
        System.out.println("result = " + result);
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void queryByUsername() {
        for (int i = 1; i < 101; i++) {
            memberRepository.save(new Member("AAA", i+(9*i))); //AAA 100건
            memberRepository.save(new Member("BBB", i+(9*i))); //BBB 100건
        }
        Pageable firstPageWithTenElements = PageRequest.of(0, 10);
//        Page<Member> result = memberRepository.findAll(firstPageWithTenElements);
//        Page<Member> result = memberRepository.findByUsername("AAA", firstPageWithTenElements);
//        Page<Member> result = memberRepository.getByUsername("AAA", firstPageWithTenElements);
        Page<Member> result = memberRepository.queryByUsername("AAA", firstPageWithTenElements);
        System.out.println("result = " + result);
        for (Member member : result) {
            System.out.println("member = " + member);
        }

    }

    @Test
    public void queryFirst10ByUsername() {
        for (int i = 1; i < 101; i++) {
            memberRepository.save(new Member("AAA", i+(9*i))); //AAA 100건
            memberRepository.save(new Member("BBB", i+(9*i))); //BBB 100건
        }
        Sort.TypedSort<Member> memberTypedSort = Sort.sort(Member.class);
        Sort sort = memberTypedSort.by(Member::getId).ascending()
                .and(memberTypedSort.by(Member::getUsername).descending());
//        Sort sort = Sort.by("username").ascending().and(Sort.by("age").descending());

        Pageable firstPageWithTenElementsSorted = PageRequest.of(0, 10, sort);

        Page<Member> result = memberRepository.queryFirst10ByUsername("AAA", firstPageWithTenElementsSorted);
        System.out.println("result = " + result);
        for (Member member : result) {
            System.out.println("member = " + member);
        }

    }


}