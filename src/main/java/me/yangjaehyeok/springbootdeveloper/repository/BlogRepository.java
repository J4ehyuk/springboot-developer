package me.yangjaehyeok.springbootdeveloper.repository;

import me.yangjaehyeok.springbootdeveloper.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

// 엔티티 Article과 PK타입 Long을 인수로 넣기
public interface BlogRepository extends JpaRepository<Article, Long> {
}
