package com.saroj.test.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.saroj.test.Service.UserService;
import com.saroj.test.entities.ApiResponse;
import com.saroj.test.entities.User;
import com.saroj.test.pagination.DataTableRequest;
import com.saroj.test.pagination.PaginationCriteria;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class UserController {
	
	
	@Autowired
	@Qualifier("userService")
	private UserService userService;
	
	@Autowired
	@Qualifier("uservalidator")
	private UserValidator validator;
	
	@Autowired
	private MessageSource messageSource;
	
	@PostMapping(value="/createuser")
	public ApiResponse<String> creatNewUser(@RequestPart("user") User user , @Validated @RequestPart("file") MultipartFile file ,BindingResult result) {
		
		user.setUploadFile(file);
		ApiResponse<String> apiResponse =null;
		validator.validate(user, result);
			if (result.hasErrors()) {
				 Map<String, String> errorMap = getErrorMap(result);			
				 apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "error",errorMap);
				 return apiResponse;
			}

		User createdUser = userService.createNewUser(user);
		apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "success",createdUser);
		return apiResponse;
	}
	
	
	@GetMapping(value="/listAllUser")
	public ApiResponse<String> listUser() {
		
		List<User> userList = userService.showAllUser();
		ApiResponse<String> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "sucess", userList);
		//List<User> list = userService.createPaginatedQuery(pagination);
		return apiResponse;
	}
	
	@PostMapping(value="/sendPageRequestUserList")
	@ResponseBody
	public String getPaginatedUserList(HttpServletRequest request, HttpServletResponse response) {
		
		DataTableRequest<User> dataTableInRQ = new DataTableRequest<User>(request);
		PaginationCriteria pagination = dataTableInRQ.getPaginationRequest();
		List<User> list = userService.getPaginatedList(pagination);
		String createJsonResponse = userService.createJsonResponse(dataTableInRQ, list);
		return createJsonResponse;
	}
	
	
	@DeleteMapping("deleteUser/{id}")
	public ApiResponse<String> removeUserById(@PathVariable Long id) {
		userService.deleteUser(id);
		ApiResponse<String> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "sucess", id);
		return apiResponse;
		
	}
	
	@GetMapping("/getuser/{id}")
	public ApiResponse<String> getUser(@PathVariable Long id) {
		User userById = userService.getUserById(id);
		ApiResponse<String> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "sucess", userById);
		return apiResponse;
	}
	
	@PostMapping(value="/changeuser")
	@ResponseBody
	public ApiResponse<String> changeUser(@RequestPart("user") User user,BindingResult result,HttpServletResponse response) {
		ApiResponse<String> apiResponse =null;
	
		validator.validate(user, result);
		if (result.hasErrors()) {
			 Map<String, String> errorMap = getErrorMap(result);			
			 apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "error",errorMap);
			 return apiResponse;
		}else {
			User updatedUser = userService.updateUser(user);
			 apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "success", updatedUser);
			return apiResponse;
		}
		
	}
	
	
	
	@GetMapping("/getstates")
	public ApiResponse<String> getStateDropDown() {
		
		Map<String, String> stateMap = new TreeMap<String, String>() {{
	        
	        put("Andhra Pradesh","Andhra Pradesh");	
	        put("Arunachal Pradesh","Arunachal Pradesh");	
	        put("Assam","Assam");	
	        put("Bihar","Bihar");	
	        put("Chhattisgarh","Chhattisgarh");	
	        put("Goa","Goa");	
	        put("Gujarat","Gujarat");	
	        put("Haryana","Haryana");	
	        put("Himachal Pradesh","Himachal Pradesh");	
	        put("Jharkhand","Jharkhand");	
	        put("Karnataka","Karnataka");	
	        put("Kerala","Kerala");	
	        put("Madhya Pradesh","Madhya Pradesh");	
	        put("Maharashtra","Maharashtra");	
	        put("Manipur","Manipur");	
	        put("Meghalaya","Meghalaya");	
	        put("Mizoram","Mizoram");	
	        put("Odisha","Odisha");	
	        put("Punjab","Punjab");	
	        put("Rajasthan","Rajasthan");	
	        put("Sikkim","Sikkim");	
	        put("Tamil Nadu","Tamil Nadu");	
	        put("Tripura","Tripura");	
	        put("Uttar Pradesh","Uttar Pradesh");	
	        put("Uttarakhand","Uttarakhand");	
	        put("West Bengal","West Bengal");	
	    }};
	    
		ApiResponse<String> apiResponse  = new ApiResponse<>(HttpStatus.OK.value(), "success",stateMap);
		return apiResponse;
	}
	
	private Map<String, String> getErrorMap(BindingResult result) {
		
		Map<String,String> map = new HashMap<String,String>();
		 List<FieldError> fieldErrors = result.getFieldErrors();
		 for (FieldError fieldError : fieldErrors) {
			String message = messageSource.getMessage(fieldError, null);
			map.put(fieldError.getField(), message);
		}
		 
		 return map;
		 
	}

}
