package com.mabubu0203.sudoku.clients.api;

import com.mabubu0203.sudoku.constants.CommonConstants;
import com.mabubu0203.sudoku.constants.RestUrlConstants;
import com.mabubu0203.sudoku.exception.SudokuApplicationException;
import com.mabubu0203.sudoku.interfaces.NumberPlaceBean;
import com.mabubu0203.sudoku.interfaces.response.ScoreResponseBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * {@code /searchMaster}のエンドポイントのラッパーです。<br>
 * このクラスを経由してWebApi呼び出しを行ってください。<br>
 * .
 *
 * @author uratamanabu
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service
public class RestApiSearchEndPoints {

    @Value("${sudoku.uri.api}")
    private String sudokuUriApi;

    /**
     * {@code /sudoku}<br>
     *
     * @param restOperations
     * @param type
     * @param keyHash
     * @return NumberPlaceBean
     * @since 1.0
     */
    public NumberPlaceBean sudoku(
            final RestOperations restOperations,
            final int type,
            final String keyHash
    ) {

        final String sudoku = sudokuUriApi
                + RestUrlConstants.URL_SEARCH_MASTER + CommonConstants.SLASH
                + RestUrlConstants.URL_SUDOKU + "?type={type}&keyHash={keyHash}";

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("type", Integer.toString(type));
        uriVariables.put("keyHash", keyHash);
        URI uri = new UriTemplate(sudoku).expand(uriVariables);
        RequestEntity requestEntity =
                RequestEntity
                        .get(uri)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .build();
        try {
            ResponseEntity<NumberPlaceBean> generateEntity =
                    restOperations.exchange(requestEntity, NumberPlaceBean.class);
            return generateEntity.getBody();
        } catch (HttpClientErrorException e) {
            throw new SudokuApplicationException();
        }
    }

    /**
     * {@code /score}<br>
     *
     * @param restOperations
     * @param type
     * @param keyHash
     * @return NumberPlaceBean
     * @since 1.0
     */
    public ScoreResponseBean score(
            final RestOperations restOperations,
            final int type,
            final String keyHash
    ) {

        final String score = sudokuUriApi
                + RestUrlConstants.URL_SEARCH_MASTER + CommonConstants.SLASH
                + RestUrlConstants.URL_SCORE + "?type={type}&keyHash={keyHash}";

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("type", Integer.toString(type));
        uriVariables.put("keyHash", keyHash);
        URI uri = new UriTemplate(score).expand(uriVariables);
        RequestEntity requestEntity =
                RequestEntity
                        .get(uri)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .build();
        try {
            ResponseEntity<ScoreResponseBean> generateEntity =
                    restOperations.exchange(requestEntity, ScoreResponseBean.class);
            return generateEntity.getBody();
        } catch (HttpClientErrorException e) {
            throw new SudokuApplicationException();
        }
    }

}
