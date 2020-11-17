package study.datajpa.repository;

/**
 * Projection 을 활용한 중첩 구조 처리
 */
public interface NestedClosedProjections {
    
    String getUsername();
    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }

}

/**
 * Projection 을 활용한 중첩 구조 처리
 * Ex) 회원 이름(username)을 가져올 때 연관된 팀 이름 까지 조회
 * - 프로젝션 대상이 root 엔티티면 유용하다.
 * - 프로젝션 대상이 root 엔티티를 넘어가면 JPQL SELECT 최적화가 안된다!
 *
 *     select
 *         member0_.username as col_0_0_, //정확히 타겟팅
 *         team1_.team_id as col_1_0_, //전체 조회
 *         team1_.team_id as team_id1_2_, //전체 조회
 *         team1_.created_date as created_2_2_, //전체 조회
 *         team1_.updated_date as updated_3_2_, //전체 조회
 *         team1_.name as name4_2_  //전체 조회
 *     from
 *         member member0_
 *     left outer join
 *         team team1_
 *             on member0_.team_id=team1_.team_id
 *     where
 *         member0_.username=?
 */
