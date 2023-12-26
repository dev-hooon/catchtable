package com.prgrms.catchtable.security.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class OAuthAttribute {

    String name;

    String email;

    String phoneNumber;

    String birthYear;

    String birthDay;

    String gender;

    @Builder
    public OAuthAttribute(String name, String email, String phoneNumber, String birthYear,
        String birthDay, String gender) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthYear = birthYear;
        this.birthDay = birthDay;
        this.gender = gender;
    }

    public static OAuthAttribute of(OAuth2User oAuth2User, String provider) {

        OAuthAttribute oAuthAttribute;

        if (provider.equals("kakao")) {
            Map<String, Object> userAttributes = oAuth2User.getAttributes();
            Map<String, Object> kakaoAccount = (Map<String, Object>) userAttributes.get(
                "kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            String nickName = (String) profile.get("nickname");
            String email = (String) kakaoAccount.get("email");
            String phoneNumber = (String) kakaoAccount.get("phone_number");
            String birthYear = (String) kakaoAccount.get("birthyear");
            String birthDay = (String) kakaoAccount.get("birthday");
            String gender = (String) kakaoAccount.get("gender");

            oAuthAttribute = OAuthAttribute.builder()
                .name(nickName)
                .email(email)
                .phoneNumber(phoneNumber)
                .birthYear(birthYear)
                .birthDay(birthDay)
                .gender(gender)
                .build();
        } else {
            oAuthAttribute = null;
        }

        return oAuthAttribute;
    }
}
