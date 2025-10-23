package org.epsda.bookmanager.utils;

import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import org.epsda.bookmanager.pojo.response.vo.BorrowRecordResp;
import org.epsda.bookmanager.pojo.response.vo.PurchaseRecordResp;
import org.epsda.bookmanager.pojo.response.vo.RoleResp;
import org.springframework.beans.BeanUtils;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    public static BorrowRecordResp generateBorrowRecordResp(String username,
                                                            String phone,
                                                            String email,
                                                            String BookName,
                                                            String isbn,
                                                            LocalDateTime borrowTime,
                                                            LocalDateTime preReturnTime,
                                                            LocalDateTime realReturnTime,
                                                            Integer status,
                                                            BigDecimal fine) {
        BorrowRecordResp borrowRecordResp = new BorrowRecordResp();
        borrowRecordResp.setUsername(username);
        borrowRecordResp.setEmail(email);
        borrowRecordResp.setPhone(phone);
        borrowRecordResp.setBookName(BookName);
        borrowRecordResp.setIsbn(isbn);
        borrowRecordResp.setBorrowTime(borrowTime);
        borrowRecordResp.setPreReturnTime(preReturnTime);
        borrowRecordResp.setRealReturnTime(realReturnTime);
        borrowRecordResp.setStatus(status);
        borrowRecordResp.setFine(fine);
        return borrowRecordResp;
    }

    public static PurchaseRecordResp generatePurchaseRecordResp(String username,
                                                                String phone,
                                                                String email,
                                                                String BookName,
                                                                Integer status,
                                                                Integer purchaseCount,
                                                                BigDecimal purchasePrice) {
        PurchaseRecordResp purchaseRecordResp = new PurchaseRecordResp();
        purchaseRecordResp.setUsername(username);
        purchaseRecordResp.setEmail(email);
        purchaseRecordResp.setPhone(phone);
        purchaseRecordResp.setBookName(BookName);
        purchaseRecordResp.setStatus(status);
        purchaseRecordResp.setPurchaseCount(purchaseCount);
        purchaseRecordResp.setPurchasePrice(purchasePrice);

        return purchaseRecordResp;
    }

    public static RoleResp generateRoleResp(String username, String email, String phone, String role) {
        RoleResp roleResp = new RoleResp();
        roleResp.setUsername(username);
        roleResp.setEmail(email);
        roleResp.setPhone(phone);
        roleResp.setRole(role);

        return roleResp;
    }
}
