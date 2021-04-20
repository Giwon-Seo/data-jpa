package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // @Query(name="Member.findByUsername")
    // 엔테티에서 메서드명으로 namedQuery 를 찾는다. 따라서 없어도됨
    // 실무에서 거의 사용안함 -> 엔티티에 쿼리 쓰는게 이상함 , 다른 기능이 대체함
    // 가장 큰 장점 쿼리가 잘못된경우, application 로딩 시점에 오류를 감지
    List<Member> findByUsername(@Param("username") String username);
}
