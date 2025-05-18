package me.yangjaehyeok.springbootdeveloper.dto;

// DTO(Data Transfer Object): 계층끼리 데이터 교환을 위한 객체
// DAO(Data Access Object): 데이터베이스와 연결되고 데이터를 조회하고 수정하는 데 사용되는 객체


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.yangjaehyeok.springbootdeveloper.domain.Article;

@NoArgsConstructor  // 기본 생성자 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자 추가
@Getter
public class AddArticleRequest {
    private String title;

    private String content;
    
    // 빌더 패턴을 사용해 DTO를 엔티티로 만들어주는 메서드
    public Article toEntity(){ // 생성자를 사용해 객체 생성
        return Article.builder()
                .title(title)
                .content(content)
                .build();
        
    }
}
