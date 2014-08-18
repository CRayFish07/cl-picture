package com.cl.picture.controller;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cl.picture.utils.ConfigUtil;
import com.cl.picture.utils.ConstantUtil;
import com.cl.picture.utils.SessionUtil;
import com.cl.picture.utils.StringUtil;
import com.cl.privilege.api.IPrivilegeBaseApiService;
import com.cl.privilege.model.User;



 
/**
 *主界面及登录验证相关的控制器 
 */

@Controller
@RequestMapping("/controller")
public class IndexController {
	
	@Autowired
	private IPrivilegeBaseApiService privilegeBaseApiService;
	@Autowired
	private ConfigUtil configUtil;

	@RequestMapping("/main")
    public String main(String visitedModule,HttpServletRequest request,ModelMap map) {

		visitedModule = "pi";
		
		//初始化用户、菜单
		User user = SessionUtil.getSessionUser(request);
		String menus = privilegeBaseApiService.getModuleTree(user.getId(),visitedModule,"");
        map.put("user", user);
        map.put("menus", menus);
        
        int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY); 
        map.put("hours", hours);
        
        return "main.ftl";
    }
	
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request) throws Exception
	{
		SessionUtil.clearSession(request);
		//被拦截器拦截处理
		return "redirect:" + configUtil.getCasServerUrl()+"/logout?service=" + configUtil.getCasServiceUrl();
	}
	
	@RequestMapping("/modifypasswordform")
	public String modifypasswordform(HttpServletRequest request) throws Exception
	{
		return "modifypasswordform.ftl";
	}
	
	@ResponseBody
	@RequestMapping("/modifypassword")
	public String modifypassword(String oldpassword,String password,HttpServletRequest request) throws Exception
	{
		if(StringUtil.isStrEmpty(oldpassword) || StringUtil.isStrEmpty(password))	return ConstantUtil.Fail;
		//初始化用户、菜单
		User user = SessionUtil.getSessionUser(request);
		if(!user.getPassword().equals(StringUtil.makeMD5(oldpassword))) return ConstantUtil.Fail;
		User newUser = new User();
		newUser.setId(user.getId());
		newUser.setPassword(StringUtil.makeMD5(password));
		newUser.setUpdateDate(new Date());
		newUser.setUpdatePerson(user.getUsername());
		privilegeBaseApiService.updateUserById(newUser);
		
		//更新session
		user.setPassword(newUser.getPassword());
		request.getSession().setAttribute(SessionUtil.SessionSystemLoginUserName,user);
		
		return ConstantUtil.Success;
	}
	
	@RequestMapping("/view")
	public String view(String visitedModule,String visitedResource,HttpServletRequest request,ModelMap map)
	{
		//初始化用户、菜单
		User user = SessionUtil.getSessionUser(request);
		String menus = privilegeBaseApiService.getModuleTree(user.getId(),visitedModule,visitedResource);
        map.put("user", user);
        map.put("menus", menus);
        
        map.put("visitedModule", visitedModule);
        map.put("visitedResource", visitedResource);
        
		return "view.ftl";
	}
	
	@RequestMapping("/viewresult")
	public String viewresult(String visitedModule,String visitedResource,String year,String month,String no,HttpServletRequest request,ModelMap map)
	{
		//初始化用户、菜单
		User user = SessionUtil.getSessionUser(request);
		String menus = privilegeBaseApiService.getModuleTree(user.getId(),visitedModule,visitedResource);
        map.put("user", user);
        map.put("menus", menus);

        map.put("visitedModule", visitedModule);
        map.put("visitedResource", visitedResource);
    	map.put("year", year);
    	map.put("month", month);
    	map.put("no", no);
        
		return "view.ftl";
	}
	
	@RequestMapping("/upload")
	public String upload(String visitedModule,String visitedResource,HttpServletRequest request,ModelMap map)
	{
		//初始化用户、菜单
		User user = SessionUtil.getSessionUser(request);
		String menus = privilegeBaseApiService.getModuleTree(user.getId(),visitedModule,visitedResource);
        map.put("user", user);
        map.put("menus", menus);
        
		return "upload.ftl";
	}
}