package tt.hashtranslator.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import tt.hashtranslator.request.HashesRequest;
import tt.hashtranslator.response.HashesResponse;
import tt.hashtranslator.service.HashTranslatorService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static tt.hashtranslator.util.Constants.TOKEN_VALIDATION_URL;

@Slf4j
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class HashTranslatorController {
    private final HashTranslatorService hashTranslatorService;
    private final RestTemplate restTemplate;

    @PostMapping
    public ResponseEntity<String> decrypt(@RequestHeader HttpHeaders headers, @RequestBody HashesRequest request) {
        String authorizationHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        boolean isTokenValid = validateToken(authorizationHeader);

        if (isTokenValid) {
            List<String> hashes = request.getHashes();
            String id = hashTranslatorService.saveRequest(hashes);
            hashTranslatorService.decrypt(id, hashes);
            return ResponseEntity.status(HttpStatus.CREATED).body(id);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<String> findById(@RequestHeader HttpHeaders headers, @PathVariable String id) throws JsonProcessingException {
        String authorizationHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        boolean isTokenValid = validateToken(authorizationHeader);

        if (isTokenValid) {
            Map<String, String> hashes = hashTranslatorService.findById(id);
            HashesResponse response = new HashesResponse();
            response.setHashes(Collections.singletonList(hashes));
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(response);
            return ResponseEntity.status(HttpStatus.OK).body(json);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }
    }

    private boolean validateToken(String authorizationHeader) {
        ;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizationHeader);

        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
                TOKEN_VALIDATION_URL,
                HttpMethod.GET,
                requestEntity,
                Boolean.class
        );

        return Boolean.TRUE.equals(responseEntity.getBody());
    }
}
