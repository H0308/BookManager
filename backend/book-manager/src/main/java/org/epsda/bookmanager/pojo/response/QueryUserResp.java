package org.epsda.bookmanager.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.epsda.bookmanager.pojo.response.vo.UserResp;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/22
 * Time: 18:31
 *
 * @Author: 憨八嘎
 */
@Data
@AllArgsConstructor
public class QueryUserResp {
    private Long currentPage;
    private Long totalPages;
    private Long totalCount;
    private List<UserResp> currentCategories;
}
