package com.mabubu0203.sudoku.api.service.impl;

import com.mabubu0203.sudoku.api.service.SearchService;
import com.mabubu0203.sudoku.interfaces.NumberPlaceBean;
import com.mabubu0203.sudoku.interfaces.PagenationHelper;
import com.mabubu0203.sudoku.interfaces.SearchConditionBean;
import com.mabubu0203.sudoku.interfaces.response.ScoreResponseBean;
import com.mabubu0203.sudoku.interfaces.response.SearchResultBean;
import com.mabubu0203.sudoku.interfaces.response.SearchSudokuRecordResponseBean;
import com.mabubu0203.sudoku.rdb.domain.AnswerInfoTbl;
import com.mabubu0203.sudoku.rdb.domain.ScoreInfoTbl;
import com.mabubu0203.sudoku.rdb.service.AnswerInfoService;
import com.mabubu0203.sudoku.rdb.service.ScoreInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author uratamanabu
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@AllArgsConstructor
@Service
public class SearchServiceImpl implements SearchService {

    private final AnswerInfoService answerInfoService;
    private final ScoreInfoService scoreInfoService;

    @Override
    public ResponseEntity<Boolean> isExist(final String answerKey) {

        List<AnswerInfoTbl> list = answerInfoService.findByAnswerKey(answerKey);
        if (list.isEmpty()) {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<SearchSudokuRecordResponseBean> search(
            final SearchConditionBean conditionBean,
            final int pageNumber,
            final int pageSize) {

        Sort sort = Sort.by(Sort.Direction.DESC, "no");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<AnswerInfoTbl> page = answerInfoService.findRecords(conditionBean, pageable);
        if (Objects.nonNull(page) && page.hasContent()) {
            Page<SearchResultBean> modiftyPage = convertJacksonFile(page);
            SearchSudokuRecordResponseBean response = new SearchSudokuRecordResponseBean();
            response.setPage(modiftyPage);
            response.setPh(new PagenationHelper(modiftyPage));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            SearchSudokuRecordResponseBean response = new SearchSudokuRecordResponseBean();
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }
    }

    @Override
    public ResponseEntity<NumberPlaceBean> getNumberPlaceDetail(final int type) {

        AnswerInfoTbl answerInfoTbl = answerInfoService.findByType(type);
        NumberPlaceBean numberPlaceBean = answerInfoService.answerInfoTblConvertBean(answerInfoTbl);
        return new ResponseEntity<>(numberPlaceBean, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<NumberPlaceBean> getNumberPlaceDetail(
            final int type, final String keyHash) {

        AnswerInfoTbl answerInfoTbl = answerInfoService.findByTypeAndKeyHash(type, keyHash);
        NumberPlaceBean numberPlaceBean = answerInfoService.answerInfoTblConvertBean(answerInfoTbl);
        return new ResponseEntity<>(numberPlaceBean, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ScoreResponseBean> getScore(
            final int type, final String keyHash, final ModelMapper modelMapper) {

        ScoreInfoTbl scoreInfoTbl = scoreInfoService.findByTypeAndKeyHash(type, keyHash);
        ScoreResponseBean response = modelMapper.map(scoreInfoTbl, ScoreResponseBean.class);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Page<SearchResultBean> convertJacksonFile(final Page<AnswerInfoTbl> page) {

        Pageable pageable = PageRequest.of(page.getNumber(), page.getSize());
        List<AnswerInfoTbl> content = page.getContent();
        List<SearchResultBean> modifyContent = new ArrayList<>();
        for (AnswerInfoTbl record : content) {
            SearchResultBean bean = new SearchResultBean();
            bean.setNo(record.getNo());
            bean.setType(record.getType());
            bean.setKeyHash(record.getKeyHash());
            ScoreInfoTbl scoreInfoTbl = record.getScoreInfoTbl();
            bean.setName(scoreInfoTbl.getName());
            bean.setScore(scoreInfoTbl.getScore());
            bean.setMemo(scoreInfoTbl.getMemo());
            modifyContent.add(bean);
        }
        Page<SearchResultBean> result = new PageImpl(modifyContent, pageable, page.getTotalElements());
        return result;
    }

}
