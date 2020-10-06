package com.saroj.test.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.saroj.test.entities.User;
import com.saroj.test.pagination.AppUtil;
import com.saroj.test.pagination.DataTableRequest;
import com.saroj.test.pagination.DataTableResults;
import com.saroj.test.pagination.PaginationCriteria;
import com.saroj.test.repository.UserRepository;

@Service("userService")
public class UserService {

	@Autowired
	@Qualifier("userRepo")
	private UserRepository userRepo;

	@Value("${uploadpath}")
	public String uploadPath;

	@PersistenceContext
	private EntityManager entityManager;

	public User createNewUser(User user) {
		user = uploadFileToServer(user);
		String stringDob = user.getStringDob()==null?"0":user.getStringDob();
		user.setDob(new Date(new Long(stringDob)));
		User user2 = new User();
		BeanUtils.copyProperties(user, user2);
		User savedUser = userRepo.save(user2);
		return savedUser;
	}

	public void deleteUser(Long id) {

		if (Objects.nonNull(id) && (id != 0)) {
			userRepo.deleteById(id);
		}

	}

	public User getUserById(Long id) {

		User user = null;
		if (Objects.nonNull(id) && (id != 0)) {
			user = userRepo.getOne(id);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			String stringDate = user.getDob().getTime()+"";
			user.setStringDob(stringDate.trim());
		}
		User user2 = new User();
		if(user!=null) {
			BeanUtils.copyProperties(user, user2);
		}
		return user2;
	}

	public User updateUser(User user) {
		User user2 = new User();
		String stringDob = user.getStringDob()==null?"0":user.getStringDob();
		user.setDob(new Date(new Long(stringDob)));
		User userById = getUserById(user.getId());
		userById.setId(user.getId());
		userById.setLoginId(user.getLoginId());
		userById.setName(user.getName());
		userById.setDob(user.getDob());
		userById.setPassword(user.getPassword());
		userById.setEmailId(user.getEmailId());
		userById.setPhoneNo(user.getPhoneNo());
		userById.setStateName(user.getStateName());
		userById.setFilePath(user.getFilePath());
		User entity = userRepo.save(userById);
		BeanUtils.copyProperties(entity, user2);
		return user2;
		
	}

	public List<User> showAllUser() {
		List<User> list = userRepo.findAll();
		return list;
	}

	private User uploadFileToServer(User user) {

		MultipartFile uploadFile = user.getUploadFile();
		String serverUploadPath = uploadPath;
		String originalFilename = uploadFile.getOriginalFilename();
		FileOutputStream fileOutputStream = null;
		FileInputStream inputStream = null;
		String filePath = "";

		if (originalFilename.contains("\\")) {
			originalFilename = originalFilename.substring(originalFilename.lastIndexOf("\\") + 1);
		}

		filePath = serverUploadPath + "/" + originalFilename;
		File uploadedFile = new File(serverUploadPath, originalFilename);
		user.setFilePath(filePath);

		try {
			fileOutputStream = new FileOutputStream(uploadedFile);
			fileOutputStream.write(uploadFile.getBytes());
			fileOutputStream.flush();

		} catch (Exception e) {
			e.printStackTrace();

		} finally {

			try {
				if (fileOutputStream != null && inputStream != null) {
					fileOutputStream.close();
					inputStream.close();
				}

			} catch (IOException e) {
				e.printStackTrace();

			}
		}
		return user;
	}

	public List<User> getPaginatedList(PaginationCriteria pagination) {

		String paginatedQuery = "";
		List<User> list = new ArrayList<User>();

		if (Objects.nonNull(pagination)) {
			String baseQuery = "SELECT id AS id,NAME AS NAME,login_id AS loginid,dob AS dob,email_id AS emailId,phone_no AS phoneNo,state_name AS stateName,file_path AS filePath,(SELECT COUNT(1) FROM user_table) AS totalrecords  FROM user_table";
			paginatedQuery = AppUtil.buildPaginatedQuery(baseQuery, pagination);
		}
		if (paginatedQuery != null && (!paginatedQuery.trim().equals(""))) {

			if (paginatedQuery.contains("-1")) {
				paginatedQuery = paginatedQuery.substring(0, paginatedQuery.indexOf("LIMIT"));
			}
			System.out.println(paginatedQuery);
			Query query = entityManager.createNativeQuery(paginatedQuery);
			List<Object[]> resultList = query.getResultList();
			if (!resultList.isEmpty()) {
				Iterator<Object[]> iterator = resultList.iterator();
				while (iterator.hasNext()) {
					Object[] objects = (Object[]) iterator.next();

					long pxId = Long.parseLong(objects[0].toString());
					Optional<User> optional = userRepo.findById(pxId);
					User user = optional.get();
					user.setTotalRecords(Long.parseLong(objects[8].toString()));
					// user.setTotalRecords(totalRecords);
					list.add(optional.get());
				}
			}
		}
		return list;
	}

	public String createJsonResponse(DataTableRequest dataTableInRQ, List<User> userList) {

		String json = "";
		if (Objects.nonNull(dataTableInRQ) && (!userList.isEmpty())) {

			DataTableResults<User> dataTableResult = new DataTableResults<User>();
			dataTableResult.setDraw(dataTableInRQ.getDraw());
			dataTableResult.setListOfDataObjects(userList);

			if (!AppUtil.isObjectEmpty(userList)) {
				if (dataTableResult != null) {
					dataTableResult.setRecordsTotal(userList.get(0).getTotalRecords().toString());
				}
				if (dataTableInRQ.getPaginationRequest().isFilterByEmpty()) {
					dataTableResult.setRecordsFiltered(userList.get(0).getTotalRecords().toString());
				} else {
					dataTableResult.setRecordsFiltered(Integer.toString(userList.size()));
				}

			}
			json = new Gson().toJson(dataTableResult);

		}

		return json;

	}

}
