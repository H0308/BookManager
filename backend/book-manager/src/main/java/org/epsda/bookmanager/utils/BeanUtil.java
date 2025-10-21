package org.epsda.bookmanager.utils;

import jakarta.validation.constraints.NotNull;
import org.epsda.bookmanager.pojo.Book;
import org.epsda.bookmanager.pojo.response.BookWithAvailableCount;
import org.springframework.beans.BeanUtils;

/**
* Created with IntelliJ IDEA.
* Description:
* User: 18483
* Date: 2025/10/21
* Time: 13:09
* @Author: 憨八嘎
*/
public class BeanUtil {
    public static BookWithAvailableCount convert(@NotNull Book book) {
        BookWithAvailableCount bookWithAvailableCount = new BookWithAvailableCount();
        // 通过BeanUtils进行转换
        BeanUtils.copyProperties(book, bookWithAvailableCount);
        return bookWithAvailableCount;
    }
}
