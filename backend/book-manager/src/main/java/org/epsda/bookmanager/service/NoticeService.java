package org.epsda.bookmanager.service;

import jakarta.validation.constraints.NotNull;
import org.epsda.bookmanager.pojo.Notice;
import org.epsda.bookmanager.pojo.request.QueryNoticeReq;
import org.epsda.bookmanager.pojo.response.NoticeDisplayResp;
import org.epsda.bookmanager.pojo.response.QueryNoticeResp;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/24
 * Time: 19:55
 *
 * @Author: 憨八嘎
 */
public interface NoticeService {
    QueryNoticeResp queryNotices(QueryNoticeReq queryNoticeReq);

    NoticeDisplayResp displayLatestNotices();

    NoticeDisplayResp queryHistoryNotice(QueryNoticeReq queryNoticeReq);

    Notice getNoticeById(Long noticeId);

    Boolean addNotice(Notice notice);

    Boolean editNotice(Notice notice);

    Boolean deleteNotice(@NotNull Long noticeId);

    Boolean batchDeleteNotice(List<Long> noticeIds);

    Boolean publishNotice(@NotNull Long noticeId);

    Boolean unpublishNotice(@NotNull Long noticeId);
}
