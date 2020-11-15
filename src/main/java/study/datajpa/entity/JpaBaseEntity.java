package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * 등록일, 수정일 적용 (공통)
 */
@Getter
@MappedSuperclass //매핑 정보만 상속 : 엔티티 공통 매핑 정보가 필요할 때 속성만 상속 받아서 사용
public class JpaBaseEntity {

    @Column(updatable = false) //createdDate는 변경되지 못하게 설정, 실수로 바꾸더라도 DB의 값이 변경되지 않는다. 기본값: @Column(updatable = true, insertable = true)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist //persist(최초 등록)전 발생하는 이벤트
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now; //null이 아닌 값을 채워 둬야 나중에 쿼리 날릴때 편하다
    }

    @PreUpdate //update(변경) 전 발생하는 이벤트
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }

}
