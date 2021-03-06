package com.mabubu0203.sudoku.api.controller;

import com.mabubu0203.sudoku.api.service.SearchService;
import com.mabubu0203.sudoku.constants.CommonConstants;
import com.mabubu0203.sudoku.constants.PathParameterConstants;
import com.mabubu0203.sudoku.constants.RestUrlConstants;
import com.mabubu0203.sudoku.controller.RestBaseController;
import com.mabubu0203.sudoku.interfaces.NumberPlaceBean;
import com.mabubu0203.sudoku.interfaces.response.ScoreResponseBean;
import com.mabubu0203.sudoku.validator.constraint.AnswerKey;
import com.mabubu0203.sudoku.validator.constraint.KeyHash;
import com.mabubu0203.sudoku.validator.constraint.Type;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 検索する為のcontrollerです。<br>
 * このcontrollerを起点にエンドポイントが生成されます。<br>
 *
 * @author uratamanabu
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(
        value = {CommonConstants.SLASH + RestUrlConstants.URL_SEARCH_MASTER + CommonConstants.SLASH},
        consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE},
        produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
)
public class RestApiSearchController extends RestBaseController {

    private final SearchService service;
    private final RestTemplateBuilder restTemplateBuilder;
    private final ModelMapper modelMapper;

    /**
     * 指定した{@code type}と{@code keyHash}より数独を取得します。<br>
     *
     * @param type
     * @param keyHash
     * @return ResponseEntity
     * @author uratamanabu
     * @since 1.0
     */
    @GetMapping(value = {RestUrlConstants.URL_SUDOKU})
    public ResponseEntity<NumberPlaceBean> getNumberPlaceDetail(
            @RequestParam(name = "type") @Type(message = "数値1桁を入力しましょう。") final Integer type,
            @RequestParam(name = "keyHash", required = false) @KeyHash(message = "数値64桁を入力しましょう。") final String keyHash
    ) {

        log.info("getNumberPlaceDetail");
        if (StringUtils.isEmpty(keyHash)) {
            return service.getNumberPlaceDetail(restTemplateBuilder.build(), type.intValue());
        } else {
            return service.getNumberPlaceDetail(restTemplateBuilder.build(), type.intValue(), keyHash);
        }
    }

    /**
     * 指定した{@code type}と{@code keyHash}より数独のスコアを取得します。<br>
     *
     * @param type
     * @param keyHash
     * @return ResponseEntity
     * @author uratamanabu
     * @since 1.0
     */
    @GetMapping(value = {RestUrlConstants.URL_SCORE})
    public ResponseEntity<ScoreResponseBean> getScore(
            @RequestParam(name = "type") @Type(message = "数値1桁を入力しましょう。") final Integer type,
            @RequestParam(name = "keyHash") @KeyHash(message = "数値64桁を入力しましょう。") final String keyHash
    ) {

        log.info("getScore");
        return service.getScore(restTemplateBuilder.build(), type.intValue(), keyHash);
    }

    /**
     * 指定した{@code answerKey}より数独の存在確認をします。<br>
     *
     * @param answerKey
     * @return ResponseEntity
     * @author uratamanabu
     * @since 1.0
     */
    @GetMapping(value = {PathParameterConstants.PATH_ANSWER_KEY})
    public ResponseEntity<Boolean> isSudokuExist(
            @PathVariable(name = "answerKey") @AnswerKey(message = "数値64桁を入力しましょう。") final String answerKey
    ) {

        log.info("isSudokuExist");
        return service.isExist(restTemplateBuilder.build(), answerKey);
    }

}
