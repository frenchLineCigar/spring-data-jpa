package study.datajpa.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

/**
 * Created by frenchline707@gmail.com on 2020-11-08
 * Blog : http://frenchline707.tistory.com
 * Github : http://github.com/frenchLineCigar
 */
@Repository
public class MemberJpaRepository { //순수 JPA 기반 리포지토리

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age", Member.class)
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    public List<Member> findByUsername(String username) {
        //JPA NamedQuery
        return em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
    }

    /**
     * 순수 JPA 페이징과 정렬
     * * 검색 조건: 나이가 10살
     * * 정렬 조건: 이름으로 내림차순(desc)
     * * 페이징 조건: 첫 번째 페이지, 페이지당 보여줄 데이터는 3건
     */
    //페이징 하는데 필요한 컨텐츠를 offset, limit로 짤라서 가져오는 쿼리
    public List<Member> findByAge(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc", Member.class)
                .setParameter("age", age)
                .setFirstResult(offset) //몇번째 row부터 가져올 것인가
                .setMaxResults(limit)  //몇 개를 가져올 것인가
                .getResultList();
    }
    //페이지 계산 로직을 위한 totalCount를 가져오는 쿼리
    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    //회원의 나이를 한번에 변경하는 쿼리 예제
    public int bulkAgePlus(int age) {
        return em.createQuery("update Member m set m.age = m.age + 1 where m.age >= :age")
                            .setParameter("age", age)
                            .executeUpdate();
    }

    public int bulkAgePlusX(int starting, int x) {
        String qlString = "update Member m set m.age = m.age + :x where m.age >= :starting";
        int resultCount = em.createQuery(qlString)
                .setParameter("x", x)
                .setParameter("starting", starting)
                .executeUpdate();

        em.clear(); //벌크 연산 이후 영속성 컨텍스트를 비우자

        return resultCount;
    }

}
