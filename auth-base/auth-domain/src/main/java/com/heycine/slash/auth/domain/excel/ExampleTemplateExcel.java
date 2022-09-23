package com.heycine.slash.auth.domain.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.heycine.slash.common.basic.BaseDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * 识别任务
 *
 * @author zzj
 */
@Data
public class ExampleTemplateExcel implements Serializable {
	private static final long serialVersionUID = 8754350573874175461L;

	@Excel(name = "ID编号", width = 45)
	private String id;

	@Excel(name = "名称", width = 40)
	private String name;

	@Excel(name = "是否冻结", width = 40, replace = {"正常_0", "已被冻结_1"})
	private String isFrost;

	@Excel(name = "创建时间", width = 40, exportFormat = BaseDTO.DATE_TIME_PATTERN)
	private String createTime;

	public static String isFrost() {
		return "isFrost";
	}

	public static String createTime() {
		return "createTime";
	}
}
