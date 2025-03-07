package com.bonju.review.user.mapper;

import com.bonju.review.user.dto.KakaoUserInfoDto;
import com.bonju.review.exception.exception.KakaoUserParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class KakaoUserParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();


    public static KakaoUserInfoDto parseKakaoUserInfo(Map<String, Object> attributes) {
        try {
            if (attributes == null) {
                throw new KakaoUserParseException("attributes가 null입니다.");
            }

            // 1) id 추출
            Object idObj = attributes.get("id");
            if (idObj == null) {
                throw new KakaoUserParseException("'id' 필드가 없습니다.");
            }
            String id = String.valueOf(idObj);

            // 2) nickname 추출
            //   - properties라는 Map 안에 'nickname'이 있는 구조
            Object propsObj = attributes.get("properties");
            if (!(propsObj instanceof Map)) {
                throw new KakaoUserParseException("'properties' 필드가 없거나 Map 형태가 아닙니다.");
            }

            Map<String, Object> properties = (Map<String, Object>) propsObj;
            Object nicknameObj = properties.get("nickname");
            if (nicknameObj == null) {
                throw new KakaoUserParseException("'nickname' 필드가 없습니다.");
            }
            String nickname = String.valueOf(nicknameObj);

            // 필요한 정보가 제대로 추출되었다면 DTO 생성
            return new KakaoUserInfoDto(id, nickname);


        } catch (KakaoUserParseException e) {
            // 위에서 직접 throw한 예외는 그대로 다시 던집니다.
            throw e;
        } catch (Exception e) {
            // Jackson 파싱 예외 등, 기타 예상치 못한 예외 처리
            log.error("Failed to parse Kakao user info: {}", e.getMessage(), e);
            throw new KakaoUserParseException("JSON 파싱 중 문제가 발생했습니다.", e);
        }
    }
}
