package study.datajpa.dto;

import lombok.Data;

/**
 * Created by frenchline707@gmail.com on 2020-11-10
 * Blog : http://frenchline707.tistory.com
 * Github : http://github.com/frenchLineCigar
 */

@Data
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }
}