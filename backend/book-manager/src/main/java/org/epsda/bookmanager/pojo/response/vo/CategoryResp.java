package org.epsda.bookmanager.pojo.response.vo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/22
 * Time: 9:09
 *
 * @Author: 憨八嘎
 */
@Data
public class CategoryResp {
    private Long id;
    private String categoryName;
    private Integer categoryCount;
}
