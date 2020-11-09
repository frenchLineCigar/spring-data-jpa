package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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