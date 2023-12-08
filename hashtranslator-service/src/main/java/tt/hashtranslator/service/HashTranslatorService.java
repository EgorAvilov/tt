package tt.hashtranslator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tt.hashtranslator.entity.Request;
import tt.hashtranslator.repository.RequestRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static tt.hashtranslator.util.Constants.CODE_PARAM;
import static tt.hashtranslator.util.Constants.CODE_VALUE;
import static tt.hashtranslator.util.Constants.EMAIL_PARAM;
import static tt.hashtranslator.util.Constants.EMAIL_VALUE;
import static tt.hashtranslator.util.Constants.HASH_PARAM;
import static tt.hashtranslator.util.Constants.HASH_TYPE_PARAM;
import static tt.hashtranslator.util.Constants.MD5_DECRYPT_URL;
import static tt.hashtranslator.util.Constants.MD5_VALUE;


@Slf4j
@Service
@RequiredArgsConstructor
public class HashTranslatorService {

    private final RestTemplate restTemplate;
    private final RequestRepository requestRepository;

    @Async("threadPoolTaskExecutor")
    public void decrypt(String id, List<String> hashes) {

        String joinedHashes = String.join(";", hashes);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(HASH_PARAM + joinedHashes)
                .queryParam(HASH_TYPE_PARAM, MD5_VALUE)
                .queryParam(EMAIL_PARAM, EMAIL_VALUE)
                .queryParam(CODE_PARAM, CODE_VALUE);

        String url = builder.build().toUriString();
        String responseBody = restTemplate.getForObject(MD5_DECRYPT_URL + url, String.class);

        if (responseBody == null) {
            return;
        }
        List<String> decryptHashes = splitResponse(responseBody);
        Map<String, String> resultHash = new LinkedHashMap<>();

        for (int i = 0; i < decryptHashes.size(); i++) {
            String hash = hashes.get(i);
            String decryptHash = decryptHashes.get(i);
            resultHash.put(hash, decryptHash);
        }
        updateRequest(id, resultHash);
    }

    public Map<String, String> findById(String id) {
        return requestRepository.findById(id)
                .map(request -> new HashMap<>(request.getHashes()))
                .orElseThrow(() -> new RuntimeException("No record with such id"));
    }

    private void updateRequest(String id, Map<String, String> resultMap) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No record with such id"));
        request.setHashes(resultMap);
        requestRepository.save(request);
    }


    public String saveRequest(List<String> hashes) {
        validateRequest(hashes);
        Map<String, String> hashesMap = hashes.stream()
                .collect(Collectors.toMap(hash -> hash, hash -> ""));
        Request request = new Request();
        request.setHashes(hashesMap);
        return requestRepository.save(request).getId();
    }

    private void validateRequest(List<String> hashes) {
        hashes.stream()
                .filter(hash -> hash.length() != 32)
                .findAny()
                .ifPresent(invalidHash -> {
                    throw new RuntimeException("Invalid hash: " + invalidHash);
                });
    }

    private List<String> splitResponse(String response) {
        List<String> splitResponse = new ArrayList<>();
        StringBuilder currentSegment = new StringBuilder();

        for (char c : response.toCharArray()) {
            if (c == ';') {
                if (currentSegment.length() > 0) {
                    splitResponse.add(currentSegment.toString());
                    currentSegment = new StringBuilder();
                } else {
                    splitResponse.add("");
                }
            } else {
                currentSegment.append(c);
            }
        }
        return splitResponse;
    }
}
