package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // @Query(name="Member.findByUsername")
    // 엔테티에서 메서드명으로 namedQuery 를 찾는다. 따라서 없어도됨
    // 실무에서 거의 사용안함 -> 엔티티에 쿼리 쓰는게 이상함 , 다른 기능이 대체함
    // 가장 큰 장점 쿼리가 잘못된경우, application 로딩 시점에 오류를 감지
    List<Member> findByUsername(@Param("username") String username);

    // 실제 실무에서 많이 사용
    // @Query에서 오타가난경우 application loading 시점에서 에러 감지
    @Query("select m from Member m where m.username =:username and m.age = :age")
    List<Member> findUser( @Param("username") String username, @Param("age") int age);

    // 단순 조회
    @Query("select m.username from Member m")
    List<String> findUserNameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username);// 단건 Optional

    // Page<Member> findByAge(int age, Pageable pageable);

    /* 리스트, 페이지 카운트 를 분리
    @Query(value="select m from Member m left join m.team t"
            , countQuery = "select count(m.username) from Member m")
    */
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age+1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);
}
