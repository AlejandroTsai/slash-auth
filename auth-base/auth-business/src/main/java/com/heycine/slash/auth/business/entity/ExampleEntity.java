package com.heycine.slash.auth.business.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 任务表
 *
 * @author zhiji.zhou
 * @date 2022/1/11
 */
@Data
@TableName("sh_example")
public class ExampleEntity {
	/**
	 * id
	 */
	@TableId
	private String id;

	/**
	 * name
	 */
	private String name;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 是否冻结（0：否，1：是）
	 */
	private Integer isFrost;
}
