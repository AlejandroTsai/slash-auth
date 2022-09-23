package com.heycine.slash.auth.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.cglib.CglibUtil;
import com.heycine.slash.common.basic.BaseDTO;
import com.heycine.slash.common.basic.util.EnumUtil;
import com.heycine.slash.common.easypoi.ExcelUtil;
import com.heycine.slash.auth.business.entity.ExampleEntity;
import com.heycine.slash.auth.business.enums.ExampleEnum;
import com.heycine.slash.auth.business.repository.ExampleRepository;
import com.heycine.slash.auth.domain.dto.ExampleDTO;
import com.heycine.slash.auth.domain.excel.ExampleExcel;
import com.heycine.slash.auth.domain.excel.ExampleTemplateExcel;
import com.heycine.slash.auth.service.service.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * @author zzj
 */
@Service
public class ExampleServiceImpl implements ExampleService {

	@Autowired
	private ExampleRepository exampleRepository;

	/**
	 * 例子方法 -新增
	 *
	 * @param addDTO
	 */
	@Override
	public void exampleAdd(ExampleDTO addDTO) {
		ExampleEntity copy = CglibUtil.copy(addDTO, ExampleEntity.class);
		copy.setCreateTime(new Date());
		exampleRepository.save(copy);
	}

	/**
	 * 例子方法
	 *
	 * @param id
	 * @return
	 */
	@Override
	public ExampleDTO example(String id) {
		ExampleEntity byId = exampleRepository.getById(id);
		ExampleDTO exampleDTO = CglibUtil.copy(byId, ExampleDTO.class);
		exampleDTO.setIsFrost(
				EnumUtil.getByCode(byId.getIsFrost(), ExampleEnum.class).getInfo()
		);

		return exampleDTO;
	}

	/**
	 * 例子方法 -导出
	 */
	@Override
	public void export(HttpServletResponse response) {
		// 获取数据
		List<ExampleEntity> list = exampleRepository.list();
		List<ExampleExcel> exampleExcels = CglibUtil.copyList(list, ExampleExcel::new);

		ExcelUtil.exportExcel(
				exampleExcels,
				"例子数据",
				"例子数据",
				ExampleExcel.class,
				"例子数据.xlsx",
				response
		);
	}

	/**
	 * 例子方法 -导出按照模板
	 */
	@Override
	public void exportTemplate(HttpServletResponse response) {
		// 获取数据
		List<ExampleEntity> list = exampleRepository.list();
		// TODO : 需要自行转换数据结构
		List<ExampleTemplateExcel> exampleExcels = CglibUtil.copyList(list, ExampleTemplateExcel::new, (value, target, context) -> {
			// 源数据的类型判断
			/*if (value instanceof Integer) {
				return EnumUtil.getByCode((Integer)value, ExampleEnum.class).getInfo();
			} else if (value instanceof Date) {
				return DateUtil.format((Date) value, BaseDTO.DATE_TIME_PATTERN);
			}*/

			// 方法名判断,这种判断更准确
			String contextMethod = context.toString();
			// 是否冻结
			if (StrUtil.endWith(contextMethod, ExampleTemplateExcel.isFrost(), true)) {
				return EnumUtil.getByCode((Integer) value, ExampleEnum.class).getInfo();
			}
			// 创建时间
			if (StrUtil.endWith(contextMethod, ExampleTemplateExcel.createTime(), true)) {
				return DateUtil.format((Date) value, BaseDTO.DATE_TIME_PATTERN);
			}
			return value;
		});

		// 执行导出 -按照模板
		ExcelUtil.exportExcelOfTemplate(
				exampleExcels,
				ExampleExcel.class,
				"list",
				"例子数据模板",
				"templates/export_template.xlsx",
				"例子数据模板.xlsx",
				response
		);
	}


}
