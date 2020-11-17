# 스프링 데이터 JPA 분석

"스프링 데이터 JPA가 실제 내부적으로 어떻게 동작하고 있는지 구현체를 분석해보자"

## 스프링 데이터 JPA 구현체 분석
* 스프링 데이터 JPA가 제공하는 공통 인터페이스의 구현체
* `org.springframework.data.jpa.repository.support.SimpleJpaRepository`

**리스트 12.31 SimpleJpaRepository**
```
package org.springframework.data.jpa.repository.support;

@Repository
@Transactional(readOnly = true) //클래스 레벨에서, 스프링 데이터 JPA의 모든 기능은 일단 트랜잭션(읽기 전용) 걸고 시작
public class SimpleJpaRepository<T, ID> ...{
    
    @Transactional //메서드 레벨에서, JPA의 모든 변경은 트랜잭션 안에서 동작
    @Override
    public <S extends T> S save(S entity) {
        
        if (entityInformation.isNew(entity)) {
            em.persist(entity);
        } else {
            return em.merge(entity);
        }
    }
    ...
}
```
* `@Repository` 적용: JPA 예외를 스프링이 추상화한 예외로 변환
* `@Transactional` 트랜잭션 적용
    * JPA의 모든 변경은 트랜잭션 안에서 동작
    * 스프링 데이터 JPA는 변경(등록, 수정, 삭제) 메서드를 트랜잭션 처리 -> 그렇지 않을 경우 트랜잭션이 없다고 예외가 터진다
    * 서비스 계층에서 트랜잭션을 시작하지 않으면 리파지토리에서 트랜잭션 시작
    * 서비스 계층에서 트랜잭션을 시작하면 리파지토리는 해당 트랜잭션을 전파 받아서 사용
    * 그래서 스프링 데이터 JPA를 사용할 때 트랜잭션이 없어도(사실은 트랜잭션이 리포지토리 계층에 걸려있는 것임) 
      데이터 등록, 변경이 가능했음
      
* `@Transactional(readOnly = true)`
    * 데이터를 단순히 조회만 하고 변경하지 않는 트랜잭션에서 `readOnly = true`
      옵션을 사용하면 플러시를 생략해서 약간의 성능 향상을 얻을 수 있음
    * 자세한 내용은 JPA 책 15.4.2 읽기 전용 쿼리의 성능 최적화 참고
        - https://github.com/beadss/jpa-study/issues/42
        - https://github.com/cheese10yun/TIL/blob/master/Spring/jpa/jpa.md#읽기-전용-쿼리의-성능-최적화
        - https://joont92.github.io/jpa/JPA-성능-최적화/#읽기-전용-쿼리의-성능-최적화
        - https://happyer16.tistory.com/entry/JPA-15장-고급-주제와-성능-최적화
        
>참고: @Transactional 을 건다는 것은?
> - 사실 JDBC의 메커니즘은 어떻게 동작하냐면, 이 @Transactional을 걸면
> 데이터베이스의 커넥션에 setAutoCommit(false)라는 옵션을 넘긴다. -> connection.setAutoCommit(false)
> - @Transactional(readOnly = true)을 걸면 일단 그런 과정이 다 일어난다.
> readOnly = true 라고 해도 실제 트랜잭션 얻는 것과 똑같이 동작한다.
> 다만, 차이점은 스프링이 JPA의 기능 처리를 하나 제외 하는데 바로 flush를 안 해버린다!
> 기본적으로 트랜잭션이 끝날 때 JPA의 영속성 컨텍스트에 있는 정보들을 DB에 flush를 하고 commit을 한다.
> 그런데 readOnly = true 가 있으면 flush를 안한다.
> - flush를 안한다는 것은? 변경 감지(dirty checking)가 일어나지 않고 DB에 데이터 변경을 안하겠다는 것.
> 왜냐하면 읽기 전용(readOnly)으로 변경할 것이 없다고 전제 했으니까.
> @Transactional(readOnly = true) -> flush 안함 -> 변경 감지 과정이 생략되고 약간의 성능 최적화를 얻을 수 있음. 크진 않다.


**매우 중요!!!**
* **`save()` 메서드**
    * 새로운 엔티티면 저장(`persist`)
    * 새로운 엔티티가 아니면 병합(`merge`) 
```
	@Transactional
	@Override
	public <S extends T> S save(S entity) {

		if (entityInformation.isNew(entity)) { //새로운 엔티티면 persist
			em.persist(entity);
			return entity;
		} else { //아니면(=기존에 한번 DB에 들어갔다 나온 엔티티면) merge
			return em.merge(entity);
		}
	}
```
merge는 기존 데이터를 넘어온 파라미터(entity)로 덮어쓰기(overwrite)한다.
따라서 파라미터에 셋팅되지 않은 값들은 기존 값들을 그대로 null로 덮어쓰므로 주의해야 한다.
merge의 단점은 일단 DB에 select를 한번 날린다.
만약 데이터가 없다면 새로운 객체로 가정을 하고 데이터를 새로 넣는다. 그런데 어쨌든 DB에 select 쿼리가 한번 나가는 게 단점이다.
그래서 가급적이면 merge를 쓰면 안된다.
데이터 변경은 `변경 감지(dirty checking)`로 해야 한다.
엔티티의 값을 미리 바꿔놓으면 트랜잭션이 끝날 때 자동으로 데이터가 변경(update)
merge를 업데이트를 하는 경우에 쓰지 않는다!
merge는 영속 상태의 엔티티가 어떠한 이유로 영속 상태를 벗어 났는데 다시 영속 상태가 되어야 될 때 사용한다.