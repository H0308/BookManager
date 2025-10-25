package org.epsda.bookmanager.controller;

import jakarta.validation.constraints.NotNull;
import org.epsda.bookmanager.common.ResultWrapper;
import org.epsda.bookmanager.pojo.Notice;
import org.epsda.bookmanager.pojo.request.QueryNoticeReq;
import org.epsda.bookmanager.pojo.response.NoticeDisplayResp;
import org.epsda.bookmanager.pojo.response.QueryNoticeResp;
import org.epsda.bookmanager.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/24
 * Time: 19:54
 *
 * @Author: 憨八嘎
 */
@RequestMapping("/notice")
@RestController
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @RequestMapping("/query")
    public ResultWrapper<QueryNoticeResp> queryNotices(@Validated @RequestBody QueryNoticeReq queryNoticeReq) {
        return ResultWrapper.normal(noticeService.queryNotices(queryNoticeReq));
    }

    @RequestMapping("/display")
    public ResultWrapper<NoticeDisplayResp> displayLatestNotices() {
        return ResultWrapper.normal(noticeService.displayLatestNotices());
    }

    @RequestMapping("/history")
    public ResultWrapper<NoticeDisplayResp> queryHistoryNotice(@Validated @RequestBody QueryNoticeReq queryNoticeReq) {
        return ResultWrapper.normal(noticeService.queryHistoryNotice(queryNoticeReq));
    }

    @RequestMapping("/get")
    public ResultWrapper<Notice> getNoticeById(@NotNull Long noticeId) {
        return ResultWrapper.normal(noticeService.getNoticeById(noticeId));
    }

    @RequestMapping("/add")
    public ResultWrapper<Boolean> addNotice(@Validated @RequestBody Notice notice) {
        return ResultWrapper.normal(noticeService.addNotice(notice));
    }

    @RequestMapping("/edit")
    public ResultWrapper<Boolean> editNotice(@Validated @RequestBody Notice notice) {
        return ResultWrapper.normal(noticeService.editNotice(notice));
    }

    @RequestMapping("/delete")
    public ResultWrapper<Boolean> deleteNotice(@NotNull Long noticeId) {
        return ResultWrapper.normal(noticeService.deleteNotice(noticeId));
    }

    @RequestMapping("/batchDelete")
    public ResultWrapper<Boolean> batchDeleteNotice(@RequestParam List<Long> noticeIds) {
        return ResultWrapper.normal(noticeService.batchDeleteNotice(noticeIds));
    }

    @RequestMapping("/publish")
    public ResultWrapper<Boolean> publishNotice(@NotNull Long noticeId) {
        return ResultWrapper.normal(noticeService.publishNotice(noticeId));
    }

    @RequestMapping("/unpublish")
    public ResultWrapper<Boolean> unpublishNotice(@NotNull Long noticeId) {
        return ResultWrapper.normal(noticeService.unpublishNotice(noticeId));
    }
}
