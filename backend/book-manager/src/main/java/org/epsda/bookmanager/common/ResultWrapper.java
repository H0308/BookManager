package org.epsda.bookmanager.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.epsda.bookmanager.constants.Constants;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 9:05
 *
 * @Author: 憨八嘎
 */
@Data
@AllArgsConstructor
public class ResultWrapper<T> {
    private Integer code;
    private String errMsg;
    private T data;

    // 正常情况
    public static <T> ResultWrapper<T> normal(T data) {
        return new ResultWrapper<>(Constants.NORMAL, "", data);
    }

    // 错误情况
    public static <T> ResultWrapper<T> fail(T data) {
        return new ResultWrapper<>(Constants.SERVER_ERROR, "", data);
    }

    public static <T> ResultWrapper<T> fail(Integer code, String errMsg) {
        return new ResultWrapper<>(Constants.SERVER_ERROR, errMsg, null);
    }

    public static <T> ResultWrapper<T> fail(String errMsg, T data) {
        return new ResultWrapper<>(Constants.SERVER_ERROR, errMsg, data);
    }
}