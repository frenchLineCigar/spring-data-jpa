package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.dto.TeamDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;
import study.datajpa.repository.TeamRepository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by frenchline707@gmail.com on 2020-11-16
 * Blog : http://frenchline707.tistory.com
 * Github : http://github.com/frenchLineCigar
 */
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    //도메인 클래스 컨버터 사용 전
    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    //PK를 파라미터로 받을 경우 도메인 클래스 컨버터 기능을 사용할 수 있다
    //도메인 클래스 컨버터 사용 후
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    //스프링 데이터가 제공하는 페이징과 정렬 기능을 스프링 MVC에서 편리하게 사용할 수 있다
    @GetMapping("/members")
    public Page<MemberDto> list(Pageable pageable) { //파라미터들이 바인딩 될 때 PageRequest 객체 생성 후 값을 할당해 인젝션
        return memberRepository.findAll(pageable)
                .map(MemberDto::new);
    }

    //스프링 데이터 - 페이징 개별 기본값 설정: @PageableDefault
    @GetMapping("/members2")
    public Page<MemberDto> list2(@PageableDefault(size = 5, sort = "username", direction = Sort.Direction.ASC) Pageable pageable) { //파라미터들이 바인딩 될 때 PageRequest 객체 생성 후 값을 할당해 인젝션
        return memberRepository.findAll(pageable)
                .map(MemberDto::new);
    }

    //페이징 정보가 둘 이상이면 접두사로 구분: @Qualifier
    @GetMapping("/members_prefix")
    public Map<String, Object> list(@Qualifier("member") Pageable memberPageable, @Qualifier("team") Pageable teamPageable) {
        Page<MemberDto> memberPage = memberRepository.findAll(memberPageable).map(MemberDto::new);
        Page<TeamDto> teamPage = teamRepository.findAll(teamPageable).map(TeamDto::new);

        Map<String, Object> pageMap = new HashMap<>();
        pageMap.put("memberPage", memberPage);
        pageMap.put("teamPage", teamPage);

        return pageMap;
    }

    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }

}
