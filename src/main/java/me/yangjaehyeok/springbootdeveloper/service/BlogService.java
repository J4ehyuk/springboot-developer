package me.yangjaehyeok.springbootdeveloper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.yangjaehyeok.springbootdeveloper.domain.Article;
import me.yangjaehyeok.springbootdeveloper.dto.AddArticleRequest;
import me.yangjaehyeok.springbootdeveloper.dto.ArticleResponse;
import me.yangjaehyeok.springbootdeveloper.dto.UpdateArticleRequest;
import me.yangjaehyeok.springbootdeveloper.repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor // final이 붙거나 @NotNull이 붙은 필드의 생성자 추가
@Service // 빈으로 등록
public class BlogService {

    private final BlogRepository blogRepository;

    // 블로그 글 [추가] 메서드
    public Article save(AddArticleRequest request) {
        return blogRepository.save(request.toEntity());
    }
    
    // 모든 글 가져오는 메서드
    public List<Article> findAll(){
        return blogRepository.findAll();
    }

    // 블로그 글 하나를 조회하는 메서드
    public Article findById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("not found: " + id));
    }

    // id 블로그 글을 삭제하는 메서드
    public void delete(long id) {
        blogRepository.deleteById(id);
    }

    // 특정 id의 글을 수정
    @Transactional // 현재 메서드를 하나의 트랜젝션으로 실행
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        article.update(request.getTitle(), request.getContent());

        return article; // update된 article 반환
    }
}
