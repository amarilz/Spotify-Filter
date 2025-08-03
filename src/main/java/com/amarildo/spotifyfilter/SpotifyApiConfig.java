package com.amarildo.spotifyfilter;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Scanner;

@Configuration
@Slf4j
public class SpotifyApiConfig {

    @Value("${spotify.clientId}")
    private String clientId;

    @Value("${spotify.clientSecret}")
    private String clientSecret;

    @Value("${spotify.redirectUri}")
    private String redirectUri;

    @Value("${spotify.scope}")
    private String scope;

    @Bean
    public SpotifyApi getSpotifyApi() throws Exception {
        URI redirect = SpotifyHttpManager.makeUri(redirectUri);

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirect)
                .build();

        AuthorizationCodeUriRequest uriRequest = spotifyApi.authorizationCodeUri()
                .scope(scope)
                .build();

        URI userRedirect = uriRequest.execute();
        String code = obtainCodeFromUser(userRedirect);

        AuthorizationCodeRequest tokenRequest = spotifyApi.authorizationCode(code).build();
        AuthorizationCodeCredentials credentials = tokenRequest.execute();

        spotifyApi.setAccessToken(credentials.getAccessToken());
        spotifyApi.setRefreshToken(credentials.getRefreshToken());
        return spotifyApi;
    }

    private String obtainCodeFromUser(@NotNull URI uri) throws IOException, URISyntaxException {
        openBrowser(uri.toString());
        log.info("Enter the redirect URL you received: ");
        Scanner scanner = new Scanner(System.in);
        return extractCodeFromUrl(scanner.nextLine());
    }

    private void openBrowser(String url) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder pb = switch (os) {
            case String s when s.contains("mac") -> new ProcessBuilder("open", url);
            case String s when s.contains("win") -> new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url);
            case String s when s.contains("nix") || s.contains("nux") -> new ProcessBuilder("xdg-open", url);
            default -> throw new UnsupportedOperationException("OS not supported: " + os);
        };
        pb.start();
    }

    private String extractCodeFromUrl(String redirectUrl) throws URISyntaxException {
        URI uri = new URI(redirectUrl);
        return Arrays.stream(uri.getQuery().split("&"))
                .filter(param -> param.startsWith("code="))
                .map(param -> param.substring("code=".length()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Code not found in URL"));
    }
}
