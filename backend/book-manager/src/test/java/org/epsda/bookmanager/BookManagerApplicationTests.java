package org.epsda.bookmanager;

import org.epsda.bookmanager.pojo.Category;
import org.epsda.bookmanager.pojo.Notice;
import org.epsda.bookmanager.pojo.response.vo.CategoryResp;
import org.epsda.bookmanager.utils.BeanUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BookManagerApplicationTests {

	@Test
	void testConvert() {
		Notice notice = new Notice();
		BeanUtil.convert(notice);

	}

}
