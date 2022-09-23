package com.heycine.slash.auth.service.controller;

import com.heycine.slash.common.basic.Group;
import com.heycine.slash.common.basic.http.R;
import com.heycine.slash.auth.domain.dto.ExampleDTO;
import com.heycine.slash.auth.service.service.ExampleService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 例子控制器
 *
 * @author zzj
 */
@RestController
@RequestMapping("/example")
@Api(tags = "示范例子")
public class ExampleController {

	@Autowired
	private ExampleService exampleService;

	/**
	 * 例子方法 新增
	 *
	 * @param addDTO
	 * @return
	 */
	@PostMapping("/add")
	public R<?> add(@RequestBody @Validated(Group.InsertGroup.class) ExampleDTO addDTO) {

		exampleService.exampleAdd(addDTO);

		return R.ok();
	}

	/**
	 * 例子方法 -查询
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("/detail")
	public R<ExampleDTO> example(@RequestParam String id) {

		ExampleDTO exampleDTO = exampleService.example(id);

		return R.ok(exampleDTO);
	}

	/**
	 * 例子方法 -导出
	 *
	 * @param response
	 * @return
	 */
	@GetMapping("/export")
	public R<?> export(HttpServletResponse response) {

		exampleService.export(response);

		return R.ok();
	}

	/**
	 * 例子方法 -导出按照模板
	 *
	 * @param response
	 * @return
	 */
	@GetMapping("/exportTemplate")
	public R<?> exportTemplate(HttpServletResponse response) {

		exampleService.exportTemplate(response);

		return R.ok();
	}

}
