package org.epsda.bookmanager.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.epsda.bookmanager.pojo.response.vo.CategoryResp;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 23:07
 *
 * @Author: 憨八嘎
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryCategoryResp {
    private Long currentPage;
    private Long totalPages;
    private Long totalCount;
    private List<CategoryResp> currentCategories;
}
