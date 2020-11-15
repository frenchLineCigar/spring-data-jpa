package study.datajpa.repository;

import study.datajpa.entity.Member;

import java.util.List;

/**
 * 사용자 정의 인터페이스
 * : 실무에서 주로 QueryDSL이나 SpringJdbcTemplate을 함께 사용할 때 자주 사용
 */
public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}
