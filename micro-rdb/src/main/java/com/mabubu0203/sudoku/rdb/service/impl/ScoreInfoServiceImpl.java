package com.mabubu0203.sudoku.rdb.service.impl;

import com.mabubu0203.sudoku.constants.CommonConstants;
import com.mabubu0203.sudoku.interfaces.NumberPlaceBean;
import com.mabubu0203.sudoku.interfaces.domain.ScoreInfoTbl;
import com.mabubu0203.sudoku.rdb.repository.ScoreRepository;
import com.mabubu0203.sudoku.rdb.service.ScoreInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * SCORE_INFO_TBLへのサービスクラスです。<br>
 * このクラスを経由してCRUD操作を実行してください。<br>
 *
 * @author uratamanabu
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ScoreInfoServiceImpl implements ScoreInfoService {

    private final ScoreRepository scoreRepository;
    private final ModelMapper modelMapper;

    @Override
    public ScoreInfoTbl insert(NumberPlaceBean numberplaceBean) {
        ScoreInfoTbl scoreInfoTbl = modelMapper.map(numberplaceBean, ScoreInfoTbl.class);
        scoreInfoTbl.setScore(0);
        scoreInfoTbl.setName(CommonConstants.EMPTY_STR);
        scoreInfoTbl.setMemo(CommonConstants.EMPTY_STR);
        scoreInfoTbl.setUpdateDate(LocalDateTime.now());
        return scoreRepository.save(scoreInfoTbl);
    }

}
