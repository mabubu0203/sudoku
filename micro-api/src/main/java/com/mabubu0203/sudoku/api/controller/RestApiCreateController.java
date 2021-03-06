package com.mabubu0203.sudoku.api.controller;

import com.mabubu0203.sudoku.api.service.CreateService;
import com.mabubu0203.sudoku.constants.CommonConstants;
import com.mabubu0203.sudoku.constants.PathParameterConstants;
import com.mabubu0203.sudoku.constants.RestUrlConstants;
import com.mabubu0203.sudoku.controller.RestBaseController;
import com.mabubu0203.sudoku.interfaces.NumberPlaceBean;
import com.mabubu0203.sudoku.interfaces.request.ResisterSudokuRecordRequestBean;
import com.mabubu0203.sudoku.validator.constraint.Type;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * 生成する為のcontrollerです。<br>
 * このcontrollerを起点にエンドポイントが生成されます。<br>
 *
 * @author uratamanabu
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = {CommonConstants.SLASH + RestUrlConstants.URL_CREATE_MASTER + CommonConstants.SLASH},
        consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE},
        produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
)
public class RestApiCreateController extends RestBaseController {

    private final CreateService service;
    private final RestTemplateBuilder restTemplateBuilder;

    /**
     * Web側から呼び出されます。<br>
     *
     * @param type
     * @param uriComponentsBuilder
     * @return ResponseEntity
     * @author uratamanabu
     * @since 1.0
     */
    @GetMapping(value = {PathParameterConstants.PATH_TYPE})
    public ResponseEntity<String> createFromWeb(
            @PathVariable(name = "type") @Type final Integer type,
            final UriComponentsBuilder uriComponentsBuilder
    ) {

        log.info("Creating From Web");
        URI uri;
        RequestEntity requestEntity;
        NumberPlaceBean numberPlaceBean;
        uri =
                uriComponentsBuilder
                        .cloneBuilder()
                        .pathSegment(
                                RestUrlConstants.URL_CREATE_MASTER,
                                RestUrlConstants.URL_GENERATE,
                                PathParameterConstants.PATH_TYPE)
                        .buildAndExpand(type)
                        .toUri();
        requestEntity = RequestEntity
                .get(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE)
                .build();
        try {
            // 1. 生成します。
            ResponseEntity<NumberPlaceBean> generateEntity =
                    restTemplateBuilder.build().exchange(requestEntity, NumberPlaceBean.class);

            if (generateEntity.getStatusCode() != HttpStatus.CREATED) {
                log.error("一意制約違反です。");
                return new ResponseEntity<>(CommonConstants.EMPTY_STR, HttpStatus.CONFLICT);
            }
            numberPlaceBean = generateEntity.getBody();
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return null;
        }

        String answerKey = numberPlaceBean.getAnswerKey();
        uri =
                uriComponentsBuilder
                        .cloneBuilder()
                        .pathSegment(
                                RestUrlConstants.URL_SEARCH_MASTER,
                                PathParameterConstants.PATH_ANSWER_KEY)
                        .buildAndExpand(answerKey)
                        .toUri();
        requestEntity = RequestEntity
                .get(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE)
                .build();
        try {
            // 2.存在確認します。
            ResponseEntity<Boolean> isSudokuExistEntity =
                    restTemplateBuilder.build().exchange(requestEntity, Boolean.class);
            if (isSudokuExistEntity.getBody().booleanValue()) {
                log.error("一意制約違反です。");
                return new ResponseEntity<>(CommonConstants.EMPTY_STR, HttpStatus.CONFLICT);
            }
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return null;
        }

        ResisterSudokuRecordRequestBean request = new ResisterSudokuRecordRequestBean();
        request.setNumberPlaceBean(numberPlaceBean);
        uri =
                uriComponentsBuilder
                        .cloneBuilder()
                        .pathSegment(
                                RestUrlConstants.URL_CREATE_MASTER,
                                RestUrlConstants.URL_GENERATE)
                        .build()
                        .toUri();
        requestEntity = RequestEntity
                .post(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE)
                .body(request);
        try {
            // 3.保存します。
            ResponseEntity<String> resisterEntity = restTemplateBuilder.build().exchange(requestEntity, String.class);
            return resisterEntity;
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 指定した数独をRDBに保存します。<br>
     *
     * @param request
     * @return ResponseEntity
     * @author uratamanabu
     * @since 1.0
     */
    @PostMapping(value = {RestUrlConstants.URL_GENERATE})
    public ResponseEntity<String> resisterSudoku(
            @RequestBody @Validated final ResisterSudokuRecordRequestBean request
    ) {

        log.info("resisterSudoku");
        return service.insertAnswerAndScore(
                restTemplateBuilder.build(),
                request.getNumberPlaceBean()
        );
    }

    /**
     * 指定した{@code type}に従い数独を作成します。<br>
     *
     * @param type
     * @return ResponseEntity
     * @author uratamanabu
     * @since 1.0
     */
    @GetMapping(
            value = {
                    RestUrlConstants.URL_GENERATE + CommonConstants.SLASH + PathParameterConstants.PATH_TYPE
            }
    )
    public ResponseEntity<NumberPlaceBean> generateSudoku(
            @PathVariable(name = "type") @Type final Integer type
    ) {

        log.info("generateSudoku");
        return service.generate(type.intValue());
    }

}
