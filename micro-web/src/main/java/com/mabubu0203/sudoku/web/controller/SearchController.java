package com.mabubu0203.sudoku.web.controller;

import com.mabubu0203.sudoku.constants.WebUrlConstants;
import com.mabubu0203.sudoku.validator.constraint.KeyHash;
import com.mabubu0203.sudoku.validator.constraint.Type;
import com.mabubu0203.sudoku.web.form.DetailForm;
import com.mabubu0203.sudoku.web.form.SearchForm;
import com.mabubu0203.sudoku.web.form.validator.DetailFormValidator;
import com.mabubu0203.sudoku.web.form.validator.SearchFromValidator;
import com.mabubu0203.sudoku.web.helper.SearchHelper;
import com.mabubu0203.sudoku.web.helper.bean.HelperBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * <br>
 *
 * @author uratamanabu
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = {"/"})
public class SearchController {

    private final RestTemplateBuilder restTemplateBuilder;
    private final SearchHelper searchHelper;

    /**
     * <br>
     *
     * @param binder
     * @author uratamanabu
     * @since 1.0
     */
    @InitBinder
    public void initBinder(final WebDataBinder binder) {

        Optional<Object> object = Optional.ofNullable(binder.getTarget());
        object
                .filter((notNullBinder) -> notNullBinder instanceof DetailForm)
                .ifPresent(o -> binder.addValidators(new DetailFormValidator()));
        object
                .filter((notNullBinder) -> notNullBinder instanceof SearchForm)
                .ifPresent(o -> binder.addValidators(new SearchFromValidator()));
    }

    /**
     * {@code "/searchAnswer"}の初期ページへ遷移します。<br>
     *
     * @param form
     * @param model
     * @return /searchAnswer
     * @author uratamanabu
     * @since 1.0
     */
    @GetMapping(value = {WebUrlConstants.URL_SEARCH_ANSWER})
    public String searchAnswer(@ModelAttribute final SearchForm form, final Model model) {

        HelperBean handleBean = new HelperBean().setModel(model);
        this.searchHelper.searchAnswer(handleBean);
        return WebUrlConstants.Forward.SEARCH_ANSWER.getPath();
    }

    /**
     * {@code "/isSearch"}の初期ページへ遷移します。<br>
     *
     * @param form
     * @param bindingResult
     * @param model
     * @return /isSearch
     * @author uratamanabu
     * @since 1.0
     */
    @PostMapping(value = {WebUrlConstants.URL_IS_SEARCH})
    public String isSearch(
            @Validated @ModelAttribute final SearchForm form,
            final BindingResult bindingResult,
            final Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("validationError", "不正な値が入力されました。");
        } else {
            HelperBean handleBean = new HelperBean().setForm(form).setModel(model);
            this.searchHelper.isSearch(restTemplateBuilder.build(), handleBean);
        }
        return searchAnswer(form, model);
    }

    /**
     * {@code "/detail"}の初期ページへ遷移します。<br>
     *
     * @param type
     * @param keyHash
     * @param model
     * @return /detail
     * @author uratamanabu
     * @since 1.0
     */
    @GetMapping(
            value = {WebUrlConstants.URL_DETAIL},
            params = {"type", "keyHash"}
    )
    public String detail(
            @RequestParam("type") @Type(message = "選択肢から選択してください。") final int type,
            @RequestParam("keyHash") @KeyHash(message = "数値64桁を入力しましょう。") final String keyHash,
            final Model model) {

        if (keyHash.length() == 64) {
            DetailForm form = new DetailForm().setType(type).setKeyHash(keyHash);
            HelperBean handleBean = new HelperBean().setForm(form).setModel(model);
            this.searchHelper.detail(handleBean);
            return WebUrlConstants.Forward.DETAIL.getPath();
        } else {
            return "error";
        }
    }

    /**
     * {@code "/playNumberPlaceDetail"}の初期ページへ遷移します。<br>
     *
     * @param form
     * @param bindingResult
     * @param model
     * @return /playNumberPlaceDetail
     * @author uratamanabu
     * @since 1.0
     */
    @PostMapping(value = {"playNumberPlaceDetail"})
    public String playNumberPlaceDetail(
            @Validated @ModelAttribute final DetailForm form,
            final BindingResult bindingResult,
            final Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("validationError", "不正な値が入力されました。");
            return WebUrlConstants.Forward.PLAY_NUMBER_PLACE.getPath();
        } else {
            HelperBean handleBean = new HelperBean().setForm(form).setModel(model);
            this.searchHelper.playNumberPlaceDetail(restTemplateBuilder.build(), handleBean);
            return WebUrlConstants.Forward.PLAY_NUMBER_PLACE.getPath();
        }
    }

}
