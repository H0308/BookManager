package org.epsda.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.epsda.bookmanager.constants.Constants;
import org.epsda.bookmanager.exception.BookManagerException;
import org.epsda.bookmanager.mapper.NoticeMapper;
import org.epsda.bookmanager.mapper.UserMapper;
import org.epsda.bookmanager.pojo.Notice;
import org.epsda.bookmanager.pojo.User;
import org.epsda.bookmanager.pojo.request.QueryNoticeReq;
import org.epsda.bookmanager.pojo.response.NoticeDisplayResp;
import org.epsda.bookmanager.pojo.response.NoticeHistoryResp;
import org.epsda.bookmanager.pojo.response.QueryNoticeResp;
import org.epsda.bookmanager.pojo.response.dto.NoticeDisplay;
import org.epsda.bookmanager.pojo.response.vo.NoticeResp;
import org.epsda.bookmanager.service.NoticeService;
import org.epsda.bookmanager.utils.BeanUtil;
import org.epsda.bookmanager.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
@Service
public class NoticeServiceImpl implements NoticeService {

    @Value("${admin.admin-name}")
    private String adminName;

    @Autowired
    private NoticeMapper noticeMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public QueryNoticeResp queryNotices(QueryNoticeReq queryNoticeReq) {
        if (Constants.USER_FLAG.equals(SecurityUtil.getRoleIdFromPrinciple())) {
            throw new BookManagerException("当前用户无权进行公告查询");
        }

        Integer pageNum = queryNoticeReq.getPageNum();
        Integer pageSize = queryNoticeReq.getPageSize();

        Page<Notice> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        String title = queryNoticeReq.getTitle();
        String content = queryNoticeReq.getContent();
        Integer type = queryNoticeReq.getType();
        Integer status = queryNoticeReq.getStatus();

        wrapper.like(StringUtils.hasText(title), Notice::getTitle, title)
                .like(StringUtils.hasText(content), Notice::getContent, content)
                .eq(type != null, Notice::getType, type)
                .eq(status != null, Notice::getStatus, status);

        Page<Notice> pages = noticeMapper.selectPage(page, wrapper);
        List<Notice> records = pages.getRecords();
        List<NoticeResp> noticeResps = generateNoticeRespList(records);

        return new QueryNoticeResp(pages.getCurrent(), pages.getPages(), pages.getTotal(), noticeResps);
    }

    @Override
    public NoticeDisplayResp displayLatestNotices() {
        // 主页只显示前五条公告
        Page<Notice> page = new Page<>(1, Constants.DISPLAY_LATEST_NOTICE_COUNT);

        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Notice::getUpdateTime).ne(Notice::getStatus, Constants.NOTICE_NOT_PUBLISHED);

        Page<Notice> pages = noticeMapper.selectPage(page, wrapper);
        List<Notice> records = pages.getRecords();
        List<NoticeDisplay> noticeDisplays = generateNoticeDisplayList(records);

        return new NoticeDisplayResp(noticeDisplays);
    }

    @Override
    public NoticeHistoryResp queryHistoryNotice(QueryNoticeReq queryNoticeReq) {
        Integer pageNum = queryNoticeReq.getPageNum();
        Integer pageSize = queryNoticeReq.getPageSize();

        Page<Notice> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        String title = queryNoticeReq.getTitle();
        String content = queryNoticeReq.getContent();
        Integer type = queryNoticeReq.getType();
        Integer status = queryNoticeReq.getStatus();

        wrapper.like(StringUtils.hasText(title), Notice::getTitle, title)
                .like(StringUtils.hasText(content), Notice::getContent, content)
                .eq(type != null, Notice::getType, type)
                .eq(status != null, Notice::getStatus, status);

        Page<Notice> pages = noticeMapper.selectPage(page, wrapper);
        List<Notice> records = pages.getRecords();
        List<NoticeDisplay> noticeDisplays = generateNoticeDisplayList(records);

        return new NoticeHistoryResp(pages.getCurrent(), pages.getPages(), pages.getTotal(), noticeDisplays);
    }

    @Override
    public Notice getNoticeById(Long noticeId) {
        return noticeMapper.selectById(noticeId);
    }

    @Override
    public Boolean addNotice(Notice notice) {
        if (Constants.USER_FLAG.equals(SecurityUtil.getRoleIdFromPrinciple())) {
            throw new BookManagerException("当前用户无权进行新增公告");
        }
        Long userId = notice.getUserId();
        User user = userMapper.selectById(userId);
        if (Constants.USER_UNAVAILABLE_FLAG.equals(user.getDeleteFlag())) {
            throw new BookManagerException("用户已注销，无法发布公告");
        }

        if (Constants.USER_FLAG.equals(user.getRoleId())) {
            throw new BookManagerException("用户权限不足，无法发布公告");
        }

        return noticeMapper.insert(notice) == 1;
    }

    @Override
    public Boolean editNotice(Notice notice) {
        if (Constants.USER_FLAG.equals(SecurityUtil.getRoleIdFromPrinciple())) {
            throw new BookManagerException("当前用户无权进行编辑公告");
        }
        Long userId = notice.getUserId();
        User user = userMapper.selectById(userId);

        if (notice == null) {
            throw new BookManagerException("当前公告不存在，无法修改");
        }

        if (Constants.USER_UNAVAILABLE_FLAG.equals(user.getDeleteFlag())) {
            throw new BookManagerException("用户已注销，无法发布公告");
        }

        if (Constants.USER_FLAG.equals(user.getRoleId())) {
            throw new BookManagerException("用户权限不足，无法发布公告");
        }

        return noticeMapper.update(notice, new LambdaQueryWrapper<Notice>().eq(Notice::getId, notice.getId())) == 1;
    }

    @Override
    public Boolean deleteNotice(Long noticeId) {
        if (Constants.USER_FLAG.equals(SecurityUtil.getRoleIdFromPrinciple())) {
            throw new BookManagerException("当前用户无权进行删除公告");
        }
        Notice notice = noticeMapper.selectById(noticeId);
        if (notice == null) {
            throw new BookManagerException("当前公告不存在，无法删除");
        }

        return noticeMapper.delete(new LambdaQueryWrapper<Notice>().eq(Notice::getId, noticeId)) == 1;
    }

    @Override
    public Boolean batchDeleteNotice(List<Long> noticeIds) {
        var count = 0;
        for (var noticeId : noticeIds) {
            Boolean ret = deleteNotice(noticeId);
            if (ret) {
                count++;
            }
        }

        return count == noticeIds.size();
    }

    @Override
    public Boolean publishNotice(Long noticeId) {
        if (Constants.USER_FLAG.equals(SecurityUtil.getRoleIdFromPrinciple())) {
            throw new BookManagerException("当前用户无权进行发布公告");
        }
        Notice notice = noticeMapper.selectById(noticeId);
        if (notice == null || Constants.NOTICE_PUBLISHED.equals(notice.getStatus())) {
            throw new BookManagerException("当前公告不存在，或者公告已经发布，无法二次发布");
        }

        notice.setStatus(Constants.NOTICE_PUBLISHED);
        notice.setUpdateTime(LocalDateTime.now());
        return noticeMapper.update(notice, new LambdaQueryWrapper<Notice>().eq(Notice::getId, noticeId)) == 1;
    }

    @Override
    public Boolean unpublishNotice(Long noticeId) {
        if (Constants.USER_FLAG.equals(SecurityUtil.getRoleIdFromPrinciple())) {
            throw new BookManagerException("当前用户无权进行撤回公告");
        }
        Notice notice = noticeMapper.selectById(noticeId);
        if (notice == null || Constants.NOTICE_NOT_PUBLISHED.equals(notice.getStatus())) {
            throw new BookManagerException("当前公告不存在，或者公告当前处于未发布，无法取消发布");
        }

        notice.setStatus(Constants.NOTICE_NOT_PUBLISHED);
        return noticeMapper.update(notice, new LambdaQueryWrapper<Notice>().eq(Notice::getId, noticeId)) == 1;
    }

    private List<NoticeResp> generateNoticeRespList(List<Notice> records) {
        List<NoticeResp> noticeResps = new ArrayList<>();
        for (var record : records) {
            String recordTitle = record.getTitle();
            Long userId = record.getUserId();
            User user = userMapper.selectById(userId);
            String username = user.getUsername();
            if (adminName.equals(username)) {
                username = "图书管理系统管理员";
            }

            LocalDateTime createTime = record.getCreateTime();
            LocalDateTime updateTime = record.getUpdateTime();
            Integer recordType = record.getType();
            Integer recordStatus = record.getStatus();
            Long id = record.getId();

            NoticeResp noticeResp = BeanUtil.generateNoticeResp(id, username, recordTitle, recordType, recordStatus, createTime, updateTime);
            noticeResps.add(noticeResp);
        }

        return noticeResps;
    }

    private List<NoticeDisplay> generateNoticeDisplayList(List<Notice> records) {
        List<NoticeDisplay> noticeDisplays = new ArrayList<>();
        for (var record : records) {
            String recordTitle = record.getTitle();
            Long userId = record.getUserId();
            String recordContent = record.getContent();
            User user = userMapper.selectById(userId);
            String username = user.getUsername();
            if (adminName.equals(username)) {
                username = "图书管理系统管理员";
            }
            LocalDateTime updateTime = record.getUpdateTime();
            Long id = record.getId();
            Integer recordType = record.getType();
            Integer recordStatus = record.getStatus();

            NoticeDisplay noticeDisplay = BeanUtil.generateNoticeDisplay(username, recordTitle, recordContent, recordType, recordStatus, updateTime);
            noticeDisplays.add(noticeDisplay);
        }

        return noticeDisplays;
    }
}
