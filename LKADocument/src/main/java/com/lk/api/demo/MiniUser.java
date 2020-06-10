package com.lk.api.demo;


import java.util.Date;
import com.lk.api.annotation.ApiModel;
import com.lk.api.annotation.ApiModelProperty;



@ApiModel
public class MiniUser{
	
	private static final long serialVersionUID = 1L;
	
	public interface Register{};//注册
	
    @ApiModelProperty(value="用户编号",required=false)
	private Long userId;//用户编号
	
	@ApiModelProperty(value="openId",required=true,groups= {"A"})
	private String openId;//微信openId
	
	@ApiModelProperty(value="手机号",required=true,groups= {"A"})
	private String telPhone;//手机号
	
	private Date registerTime;//注册使用时间

	@ApiModelProperty(value="姓名")
	private String name;

	@ApiModelProperty(value="用户类型",description = "1-客户,2-招商员")
	private Integer userType;

	@ApiModelProperty(value="用户来源(1-链接,2-二维码,3-自然来客)",groups= {"A-n"})
	private Integer fromType;

	@ApiModelProperty(value="推广员ID",groups= {"A-n"})
	private Long promoterId;
/**************************************************************************************/

	@ApiModelProperty(value="短信验证码",required=false,groups= {"A-n"})
	private String verifCode;//短信验证码
	
	private String loginType;//登陆类型
	
}
