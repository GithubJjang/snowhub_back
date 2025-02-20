package com.example.AuthwithFirebase.FirebaseSpringboot.dummy.controller.dummy;

import com.example.AuthwithFirebase.FirebaseSpringboot.dummy.config.TestAccessToken;
import com.example.AuthwithFirebase.FirebaseSpringboot.dummy.model.User;
import com.example.AuthwithFirebase.FirebaseSpringboot.dummy.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@AllArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    @GetMapping("/test")
    public String googleLogin() {
        // login -> login페이지 띄우기 -> 해당 로그인 페이지에서 google버튼을 누르면, 해당 url로 이동.
        return "home.html";
    }
    @PostMapping("/home/signin")
    // JSON으로 데이터가 넘어옴.
    public String verifyToken(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody Map<String, String> requestBody
    ) throws FirebaseAuthException {
        String email = requestBody.get("email");
        String password = requestBody.get("password");
        // WebClient URL, firebase로 API요청을 보내기 위해서.
        WebClient webClient = WebClient.create();

        // Body elements
        String returnSecureToken = "true";

        // Body Payload
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("email", email);
        formData.add("password", password);
        formData.add("returnSecureToken", returnSecureToken);

        // Firebase API를 이용해서, 로그인을 진행한다. <- 검증을 한다.
        // 그리고, idToken, refreshToken 받아오기.
        String getUserInfo = webClient.post()
                .uri("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyBTLAv6wGA--ago8nUor445hdho3eIvqnA")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JSONObject jsonObject = new JSONObject(getUserInfo);

        String username = (String) jsonObject.get("displayName");
        String idToken = (String) jsonObject.get("idToken");
        String refreshToken = (String) jsonObject.get("refreshToken"); // refreshToken은 DB에 저장.
        userService.updateRefreshToken(refreshToken,email);


        Cookie idCookie = new Cookie("firebaseIdCookie",idToken);
        idCookie.setDomain("localhost");
        idCookie.setPath("/");
        idCookie.setMaxAge(3600);
        idCookie.setSecure(false);


        Cookie refreshCookie = new Cookie("firebaseRefreshCookie",refreshToken);
        refreshCookie.setDomain("localhost");
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(3600);
        refreshCookie.setSecure(false);



        response.addCookie(idCookie);
        response.addCookie(refreshCookie);

        TestAccessToken test = TestAccessToken.getInstance();
        test.setVal(idToken);

        return "redirect:/test"; // 첫 인덱스 페이지로 쿠키를 전송
    }

    @GetMapping("/test/logout")
    public String doGoogleLogout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("============Do Logout============");
        // idToken과 accessToken 관련 쿠키 삭제 및 firebase에서 발급 받은 토큰 무효
        String deleteList [] = {
                "IdTokenCookie",
                "refreshTokenCookie"
        };

        // 1.RefreshToken기반으로 DB에서 uid 가져오기 -> RefreshToken 무효화
        // (사용자가 로그아웃을 하면, 인증관련된 모든 정보는 다 삭제하는 편이 안전할 듯)
        // 카카오도 로그아웃 시, idToken,refreshToken 둘다 무효화 시킴.
        Cookie[] cookies = request.getCookies();
        List<Cookie> getRefreshTokenCookies = Arrays.stream(cookies).filter(
                cookie -> {
                    if(cookie.getName().equals("refreshTokenCookie")){
                        return true;
                    }
                    return false;
                }
        ).toList();
        if(getRefreshTokenCookies.size()!=1){// RefreshToken이 2개인 경우 => 서버에러
            throw new Exception("RefreshToken must be 1. but you got more than that. Something wrong");
        }
        String refreshToken = getRefreshTokenCookies.get(0).getValue();
        log.info("{}",refreshToken);

        // 1-2. uid 가져오기
        User findUser = userService.findRefreshToken(refreshToken);
        String uid = null;//!!!!!findUser.getUid();
        log.info("{}",uid);

        // 1-3. uid 기반으로 refreshToken 무효화 시키기
        FirebaseAuth.getInstance().revokeRefreshTokens(uid);
        UserRecord user = FirebaseAuth.getInstance().getUser(uid);
        // Convert to seconds as the auth_time in the token claims is in seconds too.
        long revocationSecond = user.getTokensValidAfterTimestamp() / 1000;
        log.info("Tokens revoked at: {}",revocationSecond);

        // 2. idTokenCookie, refreshTokenCookie 무효화 시키기.
        for(String str: deleteList){
            Cookie cookie = new Cookie(str, null); // 삭제할 쿠키에 대한 값을 null로 지정
            cookie.setDomain("localhost");
            cookie.setPath("/");
            cookie.setMaxAge(0); // 유효시간을 0으로 설정해서 바로 만료시킨다.

            response.addCookie(cookie); // 응답에 추가해서 없어지도록 함
        }
        return "redirect:/test";
    }

    @GetMapping("/test/google/login")
    public RedirectView doGoogleLogin(){
        log.info("============Go to Google Login============");
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("https://accounts.google.com/o/oauth2/v2/auth?client_id=477737423484-bdni5k16hncmbpcccl3ff9bqgq50c2fm.apps.googleusercontent.com&redirect_uri=http://localhost:8000/auth/google&response_type=code&scope=https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile&access_type=offline&prompt=consent");
        return redirectView;
    }
    @GetMapping("/test/kakao/login")
    public RedirectView doKakaoLogin(){
        log.info("============Go to Kakao Login============");
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("https://kauth.kakao.com/oauth/authorize?client_id=fcc716b8e5ae872c9c4ca01b821f3dea&redirect_uri=http://localhost:8000/auth/kakao&response_type=code&prompt=login");
        return redirectView;
    }




}
