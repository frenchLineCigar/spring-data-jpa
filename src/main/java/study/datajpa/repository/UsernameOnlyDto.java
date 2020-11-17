package study.datajpa.repository;

/**
 * 클래스 기반의 Projection : 프록시가 아닌 구체 클래스로 동작
 */

public class UsernameOnlyDto {

    private final String username;

    //생성자의 파라미터 명('username')으로 매칭 시켜 Projection 한다. 'username2'처럼 달라지면 X
    public UsernameOnlyDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
