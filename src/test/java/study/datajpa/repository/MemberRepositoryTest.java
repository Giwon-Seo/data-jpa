package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member saveMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(saveMember.getId()).get();
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);

    }


    @Test
    public void  findByUsernameAndAgeGreaterThen(){
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("AAA",20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery(){
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("AAA",20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);

    }

    @Test
    public void testQuery(){
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("AAA",20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);

        assertThat(result.get(0)).isEqualTo(m1);

    }

    @Test
    public void findUsernameList(){
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("BBB",20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> userNameList = memberRepository.findUserNameList();

        assertThat(userNameList.contains("AAA")).isTrue();
        assertThat(userNameList.contains("BBB")).isTrue();
        assertThat(userNameList.contains("CCC")).isFalse();
    }

    @Test
    public void findUsernameDto(){

        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA",10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();

        assertThat(memberDto.get(0).getUsername()).isEqualTo("AAA");
        assertThat(memberDto.get(0).getTeamName() ).isEqualTo("teamA");

    }

    @Test
    public void findByNames(){

        Member m1 = new Member("AAA",10);
        Member m2 = new Member("BBB",20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA","BBB"));

    }

    @Test()
    public void returnType(){
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("BBB",20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findListByUsername("asdfasdf");// 데이터가 없을때 null 이아님
        System.out.println("result = " + result.size() );

        Member findMember = memberRepository.findMemberByUsername("AAA");
        Member findMember2 = memberRepository.findMemberByUsername("asfasdfasdf");
        System.out.println("findMember2 = " + findMember2 );

        Optional<Member> findMemberOptional = memberRepository.findOptionalByUsername("asdfasdf");
        System.out.println("findMemberOptional = " + findMemberOptional );

        // optional에서 단건조회일때, 결과가 단건이 아닌경우(=여러개인경우, Exception(=IncorrectResultSizeDataAccessException) 발생)
        Member m3 = new Member("AAA",20);
        memberRepository.save(m3);


        findMemberOptional = memberRepository.findOptionalByUsername("AAA");

    }


    @Test
    public void paging(){

        // give
        memberRepository.save(new Member("member1", 10 ));
        memberRepository.save(new Member("member2", 10 ));
        memberRepository.save(new Member("member3", 10 ));
        memberRepository.save(new Member("member4", 10 ));
        memberRepository.save(new Member("member5", 10 ));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        // Return Type : Page
        // 일반직인 게시물의 페이지 형식
        // 특징 : count 쿼리를 한번더 실행한다. ( 페이징을 위한 쿼리 )
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        // 엔티티를 DTO로 변환
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        // then
        List<Member> content = page.getContent(); 
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호 주의! 페이지 index는 0부터 시작!!
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

        // Return Type : Slice
        // 페이지 형식이 아니고, 다음에 있는 게시물을 가져오는 용도로 사용용
        // 특징 : limit 갯수를 하나더 가져온다 -> '더보기'기능을 이용하기 위해서서
        /*
        Slice<Member> slice = memberRepository.findByAge(age,pageRequest);

        List<Member> content = slice.getContent();
        assertThat(content.size()).isEqualTo(3);
        assertThat(slice.getNumber()).isEqualTo(0); // 페이지 번호
        assertThat(slice.hasNext()).isTrue();
        */

        // Return Type : List
        // 그냥 내가 원하는 리스트만 가져오고 싶을때 사용
        // List<Member> memberList = memberRepository.findByAge(age,pageRequest);
    }
}