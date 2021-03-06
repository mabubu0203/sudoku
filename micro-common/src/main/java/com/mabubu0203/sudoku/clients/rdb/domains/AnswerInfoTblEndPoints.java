package com.mabubu0203.sudoku.clients.rdb.domains;

import com.mabubu0203.sudoku.constants.CommonConstants;
import com.mabubu0203.sudoku.interfaces.domain.AnswerInfoTbl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * {@code /answerInfoTbls}のエンドポイントのラッパーです。<br>
 * このクラスを経由してWebApi呼び出しを行ってください。<br>
 * .
 *
 * @author uratamanabu
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service
public class AnswerInfoTblEndPoints {

    @Value("${sudoku.uri.rdb}")
    private String sudokuUriApi;

    /**
     * {@code /search/findFirstByType}<br>
     *
     * @param restOperations
     * @param type
     * @return Optional
     * @since 1.0
     */
    public Optional<AnswerInfoTbl> findFirstByType(
            final RestOperations restOperations,
            final int type
    ) {

        final String findFirstByType = sudokuUriApi + "answerInfoTbls" + CommonConstants.SLASH + "search" + CommonConstants.SLASH + "findFirstByType";

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("type", Integer.toString(type));
        URI uri = new UriTemplate(findFirstByType + "?type={type}").expand(uriVariables);
        RequestEntity requestEntity = RequestEntity
                .get(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaTypes.HAL_JSON_VALUE)
                .build();

        try {
            ResponseEntity<EntityModel<AnswerInfoTbl>> generateEntity = restOperations
                    .exchange(
                            requestEntity,
                            new ParameterizedTypeReference<>() {
                            }
                    );
            return Optional.of(generateEntity.getBody().getContent());
        } catch (HttpClientErrorException e) {
            HttpStatus status = e.getStatusCode();
            switch (status) {
                case NOT_FOUND:
                    log.info("見つかりませんでした。");
                default:
                    return Optional.empty();
            }
        }
    }

    /**
     * {@code /search/findByAnswerKey}<br>
     *
     * @param restOperations
     * @param answerKey
     * @return Optional
     * @since 1.0
     */
    public List<AnswerInfoTbl> findByAnswerKey(
            final RestOperations restOperations,
            final String answerKey
    ) {

        final String findByAnswerKey = sudokuUriApi + "answerInfoTbls" + CommonConstants.SLASH + "search" + CommonConstants.SLASH + "findByAnswerKey";

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("answerKey", answerKey);
        URI uri = new UriTemplate(findByAnswerKey + "?answerKey={answerKey}").expand(uriVariables);
        RequestEntity requestEntity = RequestEntity
                .get(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaTypes.HAL_JSON_VALUE)
                .build();
        try {
            ResponseEntity<PagedModel<EntityModel<AnswerInfoTbl>>> generateEntity = restOperations
                    .exchange(
                            requestEntity,
                            new ParameterizedTypeReference<>() {
                            }
                    );
            return generateEntity.getBody().getContent()
                    .stream()
                    .map(EntityModel::getContent)
                    .collect(toList());
        } catch (HttpClientErrorException e) {
            HttpStatus status = e.getStatusCode();
            switch (status) {
                case NOT_FOUND:
                    log.info("見つかりませんでした。");
                default:
                    return null;
            }
        }
    }

    /**
     * {@code /search/findByTypeAndKeyHash}<br>
     *
     * @param restOperations
     * @param type
     * @param keyHash
     * @return Optional
     * @since 1.0
     */
    public Optional<AnswerInfoTbl> findByTypeAndKeyHash(
            final RestOperations restOperations,
            final int type,
            final String keyHash
    ) {

        final String findByTypeAndKeyhash = sudokuUriApi + "answerInfoTbls" + CommonConstants.SLASH + "search" + CommonConstants.SLASH + "findByTypeAndKeyHash";

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("type", Integer.toString(type));
        uriVariables.put("keyHash", keyHash);
        URI uri = new UriTemplate(findByTypeAndKeyhash + "?type={type}&keyHash={keyHash}").expand(uriVariables);
        RequestEntity requestEntity = RequestEntity
                .get(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaTypes.HAL_JSON_VALUE)
                .build();
        try {
            ResponseEntity<EntityModel<AnswerInfoTbl>> generateEntity = restOperations
                    .exchange(
                            requestEntity,
                            new ParameterizedTypeReference<>() {
                            }
                    );
            return Optional.of(generateEntity.getBody().getContent());
        } catch (HttpClientErrorException e) {
            HttpStatus status = e.getStatusCode();
            switch (status) {
                case NOT_FOUND:
                    log.info("見つかりませんでした。");
                default:
                    return Optional.empty();
            }
        }
    }

}