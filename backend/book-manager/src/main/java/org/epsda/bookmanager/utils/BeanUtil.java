package org.epsda.bookmanager.utils;

import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;

/**
* Created with IntelliJ IDEA.
* Description:
* User: 18483
* Date: 2025/10/21
* Time: 13:09
* @Author: 憨八嘎
*/
public class BeanUtil {
    private static final String POJO_RESPONSE_VO_PACKAGE = ".response.vo.";
    private static final String RESP_SUFFIX = "Resp";

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public static <T, R> R convert(@NotNull T data) {
        String dataCls = data.getClass().getName();
        int lastDot = dataCls.lastIndexOf(".");
        String packageName = dataCls.substring(0, lastDot);
        String beforeClsName = dataCls.substring(lastDot + 1);
        String respClsName = packageName + POJO_RESPONSE_VO_PACKAGE + beforeClsName + RESP_SUFFIX;
        Class<?> respCls = Class.forName(respClsName);
        // 获取到构造方法
        Constructor<?> constructor = respCls.getConstructor();
        // 创建无参实例
        Object resp = constructor.newInstance();
        R returnResp = (R) resp;
        BeanUtils.copyProperties(data, returnResp);

        return returnResp;
    }

    // public static BookResp convert(@NotNull Book book) {
    //     BookResp bookResp = new BookResp();
    //     // 通过BeanUtils进行转换
    //     BeanUtils.copyProperties(book, bookResp);
    //     return bookResp;
    // }
    //
    // public static CategoryResp convert(@NotNull Category category) {
    //     CategoryResp categoryResp = new CategoryResp();
    //     // 通过BeanUtils进行转换
    //     BeanUtils.copyProperties(category, categoryResp);
    //     return categoryResp;
    // }
}
