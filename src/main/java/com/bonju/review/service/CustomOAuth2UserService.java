package com.bonju.review.service;

import com.bonju.review.dto.KakaoUserInfoDto;
import com.bonju.review.entity.User;
import com.bonju.review.mapper.KakaoUserParser;
import com.bonju.review.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 카카오 정보 가져오기
        Map<String, Object> attributes = oAuth2User.getAttributes();
        KakaoUserInfoDto kakaoUserInfoDto = KakaoUserParser.parseKakaoUserInfo(attributes);

        // 저장 또는 업데이트 하기
        saveOrUpdateUser(kakaoUserInfoDto);

        return oAuth2User;
    }

    @Transactional
    private void saveOrUpdateUser(KakaoUserInfoDto kakaoUserInfoDto) {
        userRepository.findByKaKaoId(kakaoUserInfoDto.getId())
                .map(existingUser -> {
                    // 유저 정보가 있다면 닉네임만 업데이트
                    existingUser.setNickname(kakaoUserInfoDto.getNickname());
                    return existingUser;
                })
                .orElseGet(() -> {
                    // 새로운 유저 생성
                    User newUser = new User(kakaoUserInfoDto.getId(), kakaoUserInfoDto.getNickname());
                    return userRepository.save(newUser);
                });
    }
}
