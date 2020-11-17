package study.datajpa.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by frenchline707@gmail.com on 2020-11-17
 * Blog : http://frenchline707.tistory.com
 * Github : http://github.com/frenchLineCigar
 */

public class MemberSpec {
    /**
     * Specifications (명세)
     * 명세를 정의할 때는 `toPredicate(...)` 메서드를 구현해 메서드에 활용하면 된다.
     */
    //팀 이름을 검색조건으로 넣어보자
    public static Specification<Member> teamName(final String teamName) {
        return (Specification<Member>) (root, query, builder) -> { //root : Member

            if (StringUtils.isEmpty(teamName)) {
                return null;
            }
            //복잡한 JPA Criteria 문법을 사용해서 코드 작성 (실용성 X)
            Join<Member, Team> t = root.join("team", JoinType.INNER);// 회원과 조인
            return builder.equal(t.get("name"), teamName); // where team.name = :teamName 조건 추가
        };
    }

    public static Specification<Member> username(final String username) {
        return (Specification<Member>) (root, query, builder) -> builder.equal(root.get("username"), username);
    }

}
