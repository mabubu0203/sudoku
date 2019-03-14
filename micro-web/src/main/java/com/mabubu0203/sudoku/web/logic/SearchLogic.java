package com.mabubu0203.sudoku.web.logic;

import com.mabubu0203.sudoku.exception.SudokuApplicationException;
import com.mabubu0203.sudoku.interfaces.NumberPlaceBean;
import com.mabubu0203.sudoku.interfaces.request.SearchSudokuRecordRequestBean;
import com.mabubu0203.sudoku.interfaces.response.SearchSudokuRecordResponseBean;
import com.mabubu0203.sudoku.logic.deprecated.Sudoku;
import com.mabubu0203.sudoku.utils.ESListWrapUtils;
import com.mabubu0203.sudoku.utils.ESMapWrapUtils;
import com.mabubu0203.sudoku.utils.SudokuUtils;
import com.mabubu0203.sudoku.web.deprecated.LogicHandleBean;
import com.mabubu0203.sudoku.web.form.DetailForm;
import com.mabubu0203.sudoku.web.form.PlayForm;
import com.mabubu0203.sudoku.web.form.SearchForm;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author uratamanabu
 * @version 1.0
 * @since 1.0
 */
@Slf4j
public class SearchLogic {

    /**
     * @author uratamanabu
     * @version 1.0
     * @since 1.0
     */
    public void searchAnswer(final LogicHandleBean handleBean) {

        Model model = handleBean.getModel();
        if (model == null) {
            throw new SudokuApplicationException();
        } else {
            model.addAttribute("selectTypes", ESMapWrapUtils.getSelectTypes());
            model.addAttribute("selectorNo", ESMapWrapUtils.getSelectorNo());
            model.addAttribute("selectorKeyHash", ESMapWrapUtils.getSelectorKeyHash());
            model.addAttribute("selectorScore", ESMapWrapUtils.getSelectorScore());
            model.addAttribute("selectorName", ESMapWrapUtils.getSelectorName());
        }
    }

    /**
     * @author uratamanabu
     * @version 1.0
     * @since 1.0
     */
    public void isSearch(final RestOperations restOperations, final LogicHandleBean handleBean) {

        SearchForm form = (SearchForm) handleBean.getForm();
        if (form == null) {
            throw new SudokuApplicationException();
        }
        Model model = handleBean.getModel();
        if (model == null) {
            throw new SudokuApplicationException();
        }
        model.addAttribute("selectTypes", ESMapWrapUtils.getSelectTypes());
        model.addAttribute("selectorNo", ESMapWrapUtils.getSelectorNo());
        model.addAttribute("selectorKeyHash", ESMapWrapUtils.getSelectorKeyHash());
        model.addAttribute("selectorScore", ESMapWrapUtils.getSelectorScore());
        model.addAttribute("selectorName", ESMapWrapUtils.getSelectorName());
        SearchSudokuRecordRequestBean request =
                new ModelMapper().map(form, SearchSudokuRecordRequestBean.class);
        try {
            URI uri = new URI("http://localhost:8085/SudokuApi/searchMaster/");
            RequestEntity requestEntity = RequestEntity.post(uri).body(request);
            ResponseEntity<SearchSudokuRecordResponseBean> generateEntity =
                    restOperations.exchange(requestEntity, SearchSudokuRecordResponseBean.class);
            // TODO:移植中
//            Page page = generateEntity.getBody().getPage();
//            // ページ番号を設定し直す
//            form.setPageNumber(page.getNumber());
//            model.addAttribute("page", page);
//            model.addAttribute("ph", generateEntity.getBody().getPh());
        } catch (URISyntaxException | RestClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author uratamanabu
     * @version 1.0
     * @since 1.0
     */
    public void detail(final LogicHandleBean handleBean) {

        DetailForm form = (DetailForm) handleBean.getForm();
        if (form == null) {
            throw new SudokuApplicationException();
        }
        Model model = handleBean.getModel();
        if (model == null) {
            throw new SudokuApplicationException();
        }
        model.addAttribute("detailForm", form);
        model.addAttribute("selectLevels", ESMapWrapUtils.getSelectLevels());
    }

    /**
     * @author uratamanabu
     * @version 1.0
     * @since 1.0
     */
    public void playNumberPlaceDetail(
            final RestOperations restOperations, final LogicHandleBean handleBean)
            throws SudokuApplicationException {

        DetailForm form = (DetailForm) handleBean.getForm();
        if (form == null) {
            throw new SudokuApplicationException();
        }
        Model model = handleBean.getModel();
        if (model == null) {
            throw new SudokuApplicationException();
        }
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("type", Integer.toString(form.getType()));
        uriVariables.put("keyHash", form.getKeyHash());
        URI uri =
                new UriTemplate(
                        "http://localhost:8085/SudokuApi/searchMaster/sudoku?type={type}&keyHash={keyHash}")
                        .expand(uriVariables);
        RequestEntity requestEntity = RequestEntity.get(uri).build();
        try {
            ResponseEntity<NumberPlaceBean> generateEntity =
                    restOperations.exchange(requestEntity, NumberPlaceBean.class);
            NumberPlaceBean numberPlaceBean = generateEntity.getBody();
            // 一件から虫食いのリストを取得します。
            numberPlaceBean =
                    new Sudoku(form.getType()).filteredOfLevel(numberPlaceBean, form.getSelectLevel());
            PlayForm playForm = new ModelMapper().map(numberPlaceBean, PlayForm.class);
            playForm.setCount(0);
            playForm.setScore(SudokuUtils.calculationScore(form.getType(), form.getSelectLevel()));
            model.addAttribute("playForm", playForm);
            model.addAttribute("selectNums", ESListWrapUtils.getSelectNum(form.getType()));
            model.addAttribute("selectCells", ESListWrapUtils.createCells(form.getType()));
        } catch (RestClientException e) {
            e.printStackTrace();
        }
    }

}
