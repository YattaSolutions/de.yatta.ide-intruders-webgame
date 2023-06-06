package de.yatta.guides.oauth2.client.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sample")
@Slf4j
@RequiredArgsConstructor
public class SampleController {
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @GetMapping
    public ResponseEntity<String> samplePage() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        OAuth2AuthenticationToken oauthToken =
                (OAuth2AuthenticationToken) authentication;

        OAuth2AuthorizedClient client =
                oAuth2AuthorizedClientService.loadAuthorizedClient(
                        oauthToken.getAuthorizedClientRegistrationId(),
                        oauthToken.getName());

        var accessToken = client.getAccessToken().getTokenValue();
        log.info(oauthToken.toString());
        log.info(client.toString());
        String response =
                String.format("User ID: %s <br> User Email Address: %s <br> User Access Token: %s ",
                        oauthToken.getPrincipal().getName(),
                        oauthToken.getPrincipal().getAttributes().get("email"),
                        client.getAccessToken().getTokenValue());
        return ResponseEntity.ok(response);
    }
}
