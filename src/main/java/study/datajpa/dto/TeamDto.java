package study.datajpa.dto;

import study.datajpa.entity.Team;

/**
 * Created by frenchline707@gmail.com on 2020-11-16
 * Blog : http://frenchline707.tistory.com
 * Github : http://github.com/frenchLineCigar
 */

public class TeamDto {

    private Long id;
    private String name;

    public TeamDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public TeamDto(Team team) {
        this.id = team.getId();
        this.name = team.getName();
    }


}
