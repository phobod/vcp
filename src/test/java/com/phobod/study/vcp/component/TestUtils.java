package com.phobod.study.vcp.component;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.phobod.study.vcp.Constants.Role;
import com.phobod.study.vcp.domain.Company;
import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.form.VideoUploadForm;

public class TestUtils {
	private static User userWithId = new User("TestUserName", "TestUserSurname", "TestUserLogin", "111AAAaaa", "user@email.com", new Company(), Role.USER, "http://www.gravatar.com/avatar/00000000000000000000000000000000?d=mm");
	private static User otherUserWithId = new User("TestUserName1", "TestUserSurname1", "TestUserLogin1", "111AAAaaa1", "user1@email.com", new Company(), Role.USER, "http://www.gravatar.com/avatar/00000000000000000000000000000000?d=mm");
	private static User userWithoutId = new User("TestUserName", "TestUserSurname", "TestUserLogin", "111AAAaaa", "user@email.com", new Company(), Role.USER, "http://www.gravatar.com/avatar/00000000000000000000000000000000?d=mm");

	private static Company companyWithId = new Company("Test Company", "Test Address", "company@email.com", "+1-111-111-1111");
	private static Company companyWithoutId = new Company("Test Company", "Test Address", "company@email.com", "+1-111-111-1111");

	private static Video videoWithId = new Video("Test Title", "Test Description", "testThumbnailUrl", "testVideoUrl");
	private static Video videoWithIdAndUser = new Video("Test Title", "Test Description", "testThumbnailUrl", "testVideoUrl");
	private static Video videoWithoutId = new Video("Test Title", "Test Description", "testThumbnailUrl", "testVideoUrl");
	
	private static Pageable pageable = new PageRequest(0, 10);
	private static VideoUploadForm videoUploadForm = new VideoUploadForm("title","description",null);
	
	public static User getTestUserWithId(String id){
		userWithId.setId(id);
		return userWithId;
	}

	public static User getTestOtherUserWithId(String id){
		otherUserWithId.setId(id);
		return otherUserWithId;
	}

	public static User getTestUserWithoutId(){
		return userWithoutId;
	}

	public static User getNewUserObject(){
		return new User();
	}
	
	public static User getNewUserObjectWithEmailAndPassword(String email, String password){
		User user = new User();
		user.setPassword(password);
		user.setEmail(email);
		return user;
	}
	
	public static Company getTestCompanyWithId(String id){
		companyWithId.setId(id);
		return companyWithId;
	}
	
	public static Company getTestCompanyWithoutId(){
		return companyWithoutId;
	}

	public static Company getNewCompanyObject(){
		return new Company();
	}
	
	public static Company getNewCompanyObjectWithEmailAndPhone(String email, String phone){
		Company company = new Company();
		company.setPhone(phone);
		company.setEmail(email);
		return company;
	}
	
	public static Video getTestVideoWithId(String id){
		videoWithId.setId(id);
		return videoWithId;
	}

	public static Video getTestVideoWithIdAndUser(String id, User user){
		videoWithIdAndUser.setId(id);
		videoWithIdAndUser.setOwner(user);
		return videoWithIdAndUser;
	}

	public static Video getTestVideoWithoutId(){
		return videoWithoutId;
	}
	
	public static Pageable getTestPageable(){
		return pageable;
	}
	
	public static VideoUploadForm getVideoUploadForm(){
		return videoUploadForm;
	}
	
}
