package com.heycine.slash.auth.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.heycine.slash.common.basic.BaseDTO;
import com.heycine.slash.common.basic.Group;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * ::优雅编程，此刻做起！
 * ::Elegant programming, start now!
 *
 * @author zhiji.zhou
 * @date 2022/8/30 下午5:38
 */
@Data
@ApiModel("ExampleDTO")
public class ExampleDTO {

	@ApiModelProperty(value = "ID编号")
	private String id;

	@ApiModelProperty(value = "名称")
	@NotBlank(message = "name.required", groups = {Group.InsertGroup.class, Group.UpdateGroup.class})
	private String name;

	@ApiModelProperty(value = "创建时间")
	@JsonFormat(pattern = BaseDTO.DATE_TIME_PATTERN, timezone = BaseDTO.TIMEZONE)
	private Date createTime;

	@ApiModelProperty(value = "是否冻结（0：否，1：是）")
	private String isFrost;
}
