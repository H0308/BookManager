package org.epsda.bookmanager.utils;

import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import org.epsda.bookmanager.constants.Constants;
import org.epsda.bookmanager.pojo.response.dto.BillRecordExcel;
import org.epsda.bookmanager.pojo.response.dto.NoticeDisplay;
import org.epsda.bookmanager.pojo.response.vo.*;
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

    public static BorrowRecordResp generateBorrowRecordResp(Long id, String username,
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
        borrowRecordResp.setId(id);
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

    public static PurchaseRecordResp generatePurchaseRecordResp(Long id, String username,
                                                                String phone,
                                                                String email,
                                                                String BookName,
                                                                Integer status,
                                                                Integer purchaseCount,
                                                                BigDecimal purchasePrice) {
        PurchaseRecordResp purchaseRecordResp = new PurchaseRecordResp();
        purchaseRecordResp.setUsername(username);
        purchaseRecordResp.setId(id);
        purchaseRecordResp.setEmail(email);
        purchaseRecordResp.setPhone(phone);
        purchaseRecordResp.setBookName(BookName);
        purchaseRecordResp.setStatus(status);
        purchaseRecordResp.setPurchaseCount(purchaseCount);
        purchaseRecordResp.setPurchasePrice(purchasePrice);

        return purchaseRecordResp;
    }

    public static RoleResp generateRoleResp(Long id, String username, String email, String phone, String role) {
        RoleResp roleResp = new RoleResp();
        roleResp.setId(id);
        roleResp.setUsername(username);
        roleResp.setEmail(email);
        roleResp.setPhone(phone);
        roleResp.setRole(role);

        return roleResp;
    }

    public static BillRecordResp generateBillRecordResp(Long id, String username, String email, String phone, Integer status, BigDecimal bills) {
        BillRecordResp billRecordResp = new BillRecordResp();
        billRecordResp.setId(id);
        billRecordResp.setUsername(username);
        billRecordResp.setEmail(email);
        billRecordResp.setPhone(phone);
        billRecordResp.setStatus(status);
        billRecordResp.setBills(bills);

        return billRecordResp;
    }

    public static BillRecordExcel generateBilRecordExcel(Long id, Long userId, String username, String phone, String email,
                                                         Long borrowId, String borrowBookName, String borrowBookIsbn,
                                                         Long purchaseId, String purchaseBookName, String purchaseBookIsbn,
                                                         BigDecimal fine, BigDecimal purchasePrice, BigDecimal totalBill,
                                                         Integer status, LocalDateTime createTime, LocalDateTime updateTime) {
        BillRecordExcel billRecordExcel = new BillRecordExcel();
        billRecordExcel.setId(id);
        billRecordExcel.setUserId(userId);
        billRecordExcel.setUsername(username);
        billRecordExcel.setEmail(email);
        billRecordExcel.setPhone(phone);
        billRecordExcel.setBorrowId(borrowId);
        if (borrowBookName != null && borrowBookIsbn != null) {
            billRecordExcel.setBorrowBookName(borrowBookName);
            billRecordExcel.setBorrowBookIsbn(borrowBookIsbn);
            billRecordExcel.setFine(fine);
        }
        billRecordExcel.setPurchaseId(purchaseId);
        if (purchaseBookName != null && purchaseBookIsbn != null) {
            billRecordExcel.setPurchaseBookName(purchaseBookName);
            billRecordExcel.setPurchaseBookIsbn(purchaseBookIsbn);
            billRecordExcel.setPurchasePrice(purchasePrice);
        }
        billRecordExcel.setTotalBill(totalBill);
        if (Constants.BILL_UNPAID_FLAG.equals(status)) {
            billRecordExcel.setStatus(Constants.BILL_UNPAID_DESC);
        } else if (Constants.BILL_PAID_FLAG.equals(status)) {
            billRecordExcel.setStatus(Constants.BILL_PAID_DESC);
        }
        billRecordExcel.setCreateTime(createTime);
        billRecordExcel.setUpdateTime(updateTime);

        return billRecordExcel;
    }

    public static NoticeResp generateNoticeResp(Long id, String username, String title, Integer type, Integer status,
                                                LocalDateTime createTime, LocalDateTime updateTime) {
        NoticeResp noticeResp = new NoticeResp();
        noticeResp.setId(id);
        noticeResp.setUsername(username);
        noticeResp.setTitle(title);
        noticeResp.setType(type);
        noticeResp.setStatus(status);
        noticeResp.setCreateTime(createTime);
        noticeResp.setUpdateTime(updateTime);

        return noticeResp;
    }

    public static NoticeDisplay generateNoticeDisplay(String username, String title, String content,
                                                      Integer type, Integer status, LocalDateTime updateTime) {
        NoticeDisplay noticeDisplay = new NoticeDisplay();
        noticeDisplay.setUsername(username);
        noticeDisplay.setTitle(title);
        noticeDisplay.setType(type);
        noticeDisplay.setStatus(status);
        noticeDisplay.setContent(content);
        noticeDisplay.setUpdateTime(updateTime);

        return noticeDisplay;
    }
}
