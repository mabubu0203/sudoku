package com.mabubu0203.sudoku.clients.rdb.domains;

import com.mabubu0203.sudoku.constants.CommonConstants;
import com.mabubu0203.sudoku.constants.PathParameterConstants;
import com.mabubu0203.sudoku.interfaces.domain.ScoreInfoTbl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * {@code /scoreInfoTbls}のエンドポイントのラッパーです。<br>
 * このクラスを経由してWebApi呼び出しを行ってください。<br>
 * .
 *
 * @author uratamanabu
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service
public class ScoreInfoTblEndPoints {

    @Value("${sudoku.uri.rdb}")
    private String sudokuUriApi;

    /**
     * {@code /search/findByTypeAndKeyHash}<br>
     *
     * @param restOperations
     * @param type
     * @param keyHash
     * @return Optional
     * @since 1.0
     */
    public Optional<ScoreInfoTbl> findByTypeAndKeyHash(
            final RestOperations restOperations,
            final int type,
            final String keyHash
    ) {

        final String findByTypeAndKeyHash = sudokuUriApi + "scoreInfoTbls" + CommonConstants.SLASH + "search" + CommonConstants.SLASH + "findByTypeAndKeyHash";

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("type", Integer.toString(type));
        uriVariables.put("keyHash", keyHash);
        URI uri = new UriTemplate(findByTypeAndKeyHash + "?type={type}&keyHash={keyHash}").expand(uriVariables);
        RequestEntity requestEntity = RequestEntity
                .get(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaTypes.HAL_JSON_VALUE)
                .build();
        try {
            ResponseEntity<EntityModel<ScoreInfoTbl>> generateEntity = restOperations
                    .exchange(
                            requestEntity,
                            new ParameterizedTypeReference<>() {
                            }
                    );
            EntityModel<ScoreInfoTbl> resource = generateEntity.getBody();
            return Optional.of(resource.getContent());
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
     * {@code /{no}}<br>
     *
     * @param restOperations
     * @param updateScoreBean
     * @return boolean
     * @since 1.0
     */
    public boolean update(
            final RestOperations restOperations,
            final ScoreInfoTbl updateScoreBean
    ) {

        final String update = sudokuUriApi + "scoreInfoTbls" + CommonConstants.SLASH + PathParameterConstants.PATH_NO;

        updateScoreBean.setUpdateDate(LocalDateTime.now());
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("no", updateScoreBean.getNo().toString());
        URI uri = new UriTemplate(update).expand(uriVariables);
        RequestEntity requestEntity = RequestEntity
                .put(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaTypes.HAL_JSON_VALUE)
                .body(updateScoreBean);
        try {
            ResponseEntity<EntityModel<ScoreInfoTbl>> generateEntity = restOperations
                    .exchange(
                            requestEntity,
                            new ParameterizedTypeReference<>() {
                            }
                    );
            log.info(generateEntity.getBody().getContent().toString());
            return true;
        } catch (HttpClientErrorException e) {
            HttpStatus status = e.getStatusCode();
            switch (status) {
                case CONFLICT:
                    log.info("衝突しています。");
                default:
                    return false;
            }
        }
    }

}
