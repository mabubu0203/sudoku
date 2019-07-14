package com.mabubu0203.sudoku.api.service.impl;

import com.mabubu0203.sudoku.api.service.UpdateService;
import com.mabubu0203.sudoku.clients.rdb.ScoreInfoTblsEndPoints;
import com.mabubu0203.sudoku.rdb.domain.ScoreInfoTbl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

import java.util.Optional;

/**
 * 更新する為のサービスクラスです。<br>
 * このクラスを経由してロジックを実行してください。<br>
 *
 * @author uratamanabu
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@AllArgsConstructor
@Service
public class UpdateServiceImpl implements UpdateService {

    @Autowired
    private ScoreInfoTblsEndPoints scoreInfoTblsEndPoints;

    @Override
    public ResponseEntity<Long> updateScore(
            final RestOperations restOperations,
            final ScoreInfoTbl updateScoreBean,
            final int type,
            final String keyHash) {

        Optional<com.mabubu0203.sudoku.domain.ScoreInfoTbl> scoreInfoTblOpt =
                scoreInfoTblsEndPoints.findByTypeAndKeyHash(restOperations, type, keyHash);

        if (scoreInfoTblOpt.isPresent()) {
            com.mabubu0203.sudoku.domain.ScoreInfoTbl scoreInfoTbl = scoreInfoTblOpt.get();
            scoreInfoTbl.setName(updateScoreBean.getName());
            scoreInfoTbl.setMemo(updateScoreBean.getMemo());
            scoreInfoTbl.setScore(updateScoreBean.getScore());
            boolean result = scoreInfoTblsEndPoints.update(restOperations, scoreInfoTbl);

            if (result) {
                return new ResponseEntity<>(scoreInfoTbl.getNo(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Long.MIN_VALUE, HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(Long.MIN_VALUE, HttpStatus.BAD_REQUEST);
        }

    }

}
