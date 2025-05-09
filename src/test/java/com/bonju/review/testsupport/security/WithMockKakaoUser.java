package com.bonju.review.testsupport.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.*;
import java.util.Arrays;
import java.util.Map;

@Target({ ElementType.METHOD, ElementType.TYPE }) // 필드와 클래스에서 사용 선언
@Retention(RetentionPolicy.RUNTIME) // 프로그램 끝까지 적용된다.
@WithSecurityContext(factory = WithMockKakaoUser.Factory.class)
public @interface WithMockKakaoUser {

  String kakaoId()           default "123";
  String nickname()          default "테스트";
  String[] roles()           default { "USER" };

  class Factory implements WithSecurityContextFactory<WithMockKakaoUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockKakaoUser anno) {
      Map<String, Object> attr = Map.of(
              "id", anno.kakaoId(),
              "properties", Map.of("nickname", anno.nickname())
      );
      OAuth2User user = new DefaultOAuth2User(
              AuthorityUtils.createAuthorityList(
                      Arrays.stream(anno.roles())
                              .map(r -> "ROLE_" + r)
                              .toList()),
              attr, "id"
      );
      OAuth2AuthenticationToken auth =
              new OAuth2AuthenticationToken(user, user.getAuthorities(), "kakao");
      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(auth);
      return context;
    }
  }
}
