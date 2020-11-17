package study.datajpa.repository;

/**
 * 스프링 데이터 JPA 네이티브 쿼리 + 인터페이스 기반 Projections 활용
 * select m.member_id as id, m.username, t.name as teamName from member m left join team t limit ? offset ?
 */
public interface MemberProjection {

    Long getId();
    String getUsername();
    String getTeamName();

}
