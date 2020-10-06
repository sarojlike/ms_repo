package com.saroj.test.controller;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.saroj.test.entities.User;

@Component("uservalidator")
public class UserValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		
		return User.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		
		 User user=(User)target;
		 ValidationUtils.rejectIfEmpty(errors, "loginId", "empty.loginId");
		 ValidationUtils.rejectIfEmpty(errors, "name", "empty.name");
		 ValidationUtils.rejectIfEmpty(errors, "stringDob", "empty.dob");
		 ValidationUtils.rejectIfEmpty(errors, "password", "empty.password");
		 ValidationUtils.rejectIfEmpty(errors, "emailId", "empty.emailId");
		 ValidationUtils.rejectIfEmpty(errors, "phoneNo", "empty.phoneNo");
		 
		 String phoneNo = user.getPhoneNo()==null?"":user.getPhoneNo().toString().trim();
		 if(!phoneNo.equals("")) {
			 if(phoneNo.length()!=10) {
				 errors.rejectValue("phoneNo", "length.phoneNo");
			 }
		 }
	}

}
