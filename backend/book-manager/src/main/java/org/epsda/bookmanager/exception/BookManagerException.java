package org.epsda.bookmanager.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 9:08
 *
 * @Author: 憨八嘎
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BookManagerException extends RuntimeException{
    public Integer code;
    public String message;

    public BookManagerException() {
    }

    public BookManagerException(Integer code) {
        this.code = code;
    }

    public BookManagerException(String message) {
        this.message = message;
    }

    public BookManagerException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
