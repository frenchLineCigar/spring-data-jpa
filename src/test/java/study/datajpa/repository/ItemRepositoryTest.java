package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by frenchline707@gmail.com on 2020-11-17
 * Blog : http://frenchline707.tistory.com
 * Github : http://github.com/frenchLineCigar
 */

@SpringBootTest
class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

    /**
     * 새로운 엔티티 판단 (기본 전략)
     * 1. 엔티티의 식별자(PK)가 객체일 때 `null`로 판단
     * 2. 엔티티의 식별자(PK)가 자바 기본 타입(Primitive type)일 때 `0`으로 판단
     */
    @Test
    public void save() {
        Item item = new Item("A"); //식별자(PK)에 값이 셋팅된 경우 save에서 persist() 호출이 되지 않음(새로운 객체로 판단하지 X)
        itemRepository.save(item); //merge()가 호출된다
    }
    /**
     * [ 코드 설명 ]
     * 1. merge()는 기본적으로 엔티티가 DB에 있을 거라고 가정을 하고 동작한다.
     *  1) 일단 DB에 'A'가 있는지 찾는다
     *  select * from Item where item_id = 'A';
     *  2) 없으면 새로운 엔티티로 판단한다.
     *  insert into item (id) values ('A');
     * 2. merge()는 Save or Update 느낌의 기능을 제공하는데 굉장히 애매하다.
     *  Update를 해주긴 하는데, 데이터를 강제로 갈아끼우고 그래서 좋지 않다.
     * 3. 항상 데이터의 변경은 변경 감지(Dirty Checking) 기능을 사용해야 하고,
     *  데이터의 저장은 persist()를 써야한다.
     * 4. 사실 merge()를 쓸 일은 거의 없다. 굉장히 특수한 상황에서 사용하는데,
     *  엔티티가 떨어진(Detached) 상황에서 다시 붙을 때 사용하는데 실무에서 그런 경우는 거의 없다.
     * 5. 기본적으로 merge()를 쓰지 않겠다고 생각해야 한다.
     */

    /**
     * [ @GeneratedValue를 사용하지 못하는 경우 ]
     * 기본적으로는 깔고 들어가는데, 실무에서 @GeneratedValue를 못 쓸 때도 있다.
     * 데이터가 정말 많으면 테이블 분할해야 하고 어쩔 수 없이 식별자(@Id) 를 임의로 생성해야 할 때가 있다.
     * 그럴 때를 위해 스프링 데이터 JPA는 Persistable라는 인터페이스를 제공한다.
     * Persistable 을 활용해 이 문제를 해결한다.
     * 1) 해당 엔티티에 implements Persistable<`식별자 타입`>으로 getId()와 isNew()를 오버라이딩해 구현하면 된다.
     * 2) 엔티티 필드에 @CreatedDate를 사용해 isNew()의 조건에 createdDate == null 의 여부로 새로운 엔티티로 판단한다.
     * 3) insert into item (create_date, id) values ('2020-11-17T12:09:43.761+0900', 'A');
     */

}