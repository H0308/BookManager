package org.epsda.bookmanager.constants;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 9:06
 *
 * @Author: 憨八嘎
 */
public record Constants() {
    public static final String TOKEN_HEADER = "user_login_header_token";
    public static final String USER_INFO_REDIS_PREFIX = "user";
    public static final String BLOG_INFO_REDIS_PREFIX = "blog";
    public static final String REDIS_NAMESPACE_SEP = ":";
    public static final String REDIS_DEFAULT_PREFIX = "redis";
    public static final String RABBITMQ_USER_QUEUE = "user_queue";
    public static final String RABBITMQ_USER_EXCHANGE = "user_exchange";

    public static final Integer NORMAL = 0;
    public static final Integer SERVER_ERROR = 1;
    public static final Integer SYSTEM_ERROR = 2;
    public static final Integer RESOURCE_NOT_FOUND = 3;
    public static final String SERVER_ERROR_MESSAGE = "服务器异常";
    public static final String SYSTEM_ERROR_MESSAGE = "博客系统异常";
    public static final String RESOURCE_NOT_FOUND_MESSAGE = "资源不存在";

    public static final long REDIS_EXPIRE_TIMEOUT = 14 * 24 * 60 * 60; // 2周，单位为s
    public static final long REAL_DELETE_EXAMINE_TIMEOUT = 3 * 24 * 60 * 60 * 1000;

    public static final Integer NOT_DELETE_FIELD_FLAG = 0;
    public static final Integer DELETED_FIELD_FLAG = 1;

    public static final Integer BORROWING_FLAG = 0;
    public static final Integer RETURN_FLAG = 1;
    public static final Integer OVERDUE_FLAG = 2;

    public static final Integer BORROW_TIME_DESC = 0; // 借阅日期
    public static final Integer PRE_RETURN_TIME_DESC = 1; // 应还日期
    public static final Integer REAL_RETURN_TIME_DESC = 2; // 实际归还日期

    public static final Integer BOOK_AVAILABLE_FLAG = 0;
    public static final Integer BOOK_UNAVAILABLE_FLAG = 1;

    public static final Integer BOOK_UNPAID_FLAG = 0;
    public static final Integer BOOK_PAID_FLAG = 1;

    public static final Integer USER_AVAILABLE_FLAG = 0;
    public static final Integer USER_UNAVAILABLE_FLAG = 1;

    public static final Integer ADMIN_FLAG = 0;
    public static final Integer USER_FLAG = 1;
}
