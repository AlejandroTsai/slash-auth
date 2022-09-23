package com.heycine.slash.auth.service.service;

import com.heycine.slash.auth.domain.dto.ExampleDTO;

import javax.servlet.http.HttpServletResponse;

/**
 * @author zzj
 */
public interface ExampleService {
	/**
	 * 例子方法 -新增
	 * @param addDTO
	 */
	void exampleAdd(ExampleDTO addDTO);

	/**
	 * 例子方法 -查询
	 *
	 * @param id
	 * @return
	 */
	ExampleDTO example(String id);

	/**
	 * 例子方法 -导出
	 */
	void export(HttpServletResponse response);

	/**
	 * 例子方法 -导出按照模板
	 */
	void exportTemplate(HttpServletResponse response);
}
