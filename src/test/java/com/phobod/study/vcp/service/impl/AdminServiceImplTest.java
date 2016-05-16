package com.phobod.study.vcp.service.impl;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.phobod.study.vcp.component.TestUtils;
import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.exception.ValidationException;
import com.phobod.study.vcp.repository.storage.CompanyRepository;
import com.phobod.study.vcp.repository.storage.UserRepository;
import com.phobod.study.vcp.service.AdminService;
import com.phobod.study.vcp.service.UserService;

@RunWith(MockitoJUnitRunner.class)
public class AdminServiceImplTest {
	@InjectMocks
	private AdminService adminService = new AdminServiceImpl();
	
	@Mock	
	private UserRepository userRepository;
	
	@Mock	
	private CompanyRepository companyRepository;
	
	@Mock
	private UserService userService;
	
	@Mock	
	private PasswordEncoder passwordEncoder;
	
	@Rule
    public ExpectedException thrown= ExpectedException.none();
	
	private String userId;	
	private String companyId;	

	@Before
	public void setUp() throws Exception {
		userId = "userId";
		companyId = "companyId";
	}

	@Test
	public void testListAllUsers() {
		adminService.listAllUsers(TestUtils.getTestPageable());
		verify(userRepository).findAllByOrderByNameAsc(TestUtils.getTestPageable());
	}

	@Test
	public void testListAllCompanies() {
		adminService.listAllCompanies(TestUtils.getTestPageable());
		verify(companyRepository).findAllByOrderByNameAsc(TestUtils.getTestPageable());
	}

	@Test
	public void testSaveUserWithDuplicateKey() throws ValidationException{
		thrown.expect(ValidationException.class);
		thrown.expectMessage("Can't save user. User with the same parameter already exists");
		when(userRepository.findOne(userId)).thenReturn(TestUtils.getTestUserWithId(userId));
		when(userRepository.save(TestUtils.getTestUserWithId(userId))).thenThrow(new DuplicateKeyException(""));
		adminService.saveUser(TestUtils.getTestUserWithId(userId));
	}

	@Test(expected = ValidationException.class)
	public void testSaveUserWithIncorrectEmail() throws ValidationException{
		adminService.saveUser(TestUtils.getNewUserObjectWithEmailAndPassword("ds@ds", "111AAAaaa"));
	}

	@Test(expected = ValidationException.class)
	public void testSaveUserWithIncorrectPassword() throws ValidationException{
		adminService.saveUser(TestUtils.getNewUserObjectWithEmailAndPassword("ds@ds.com", "111"));
	}

	@Test
	public void testSaveNewUser() throws ValidationException{
		when(passwordEncoder.encode(TestUtils.getTestUserWithoutId().getPassword())).thenReturn(TestUtils.getTestUserWithoutId().getPassword());
		adminService.saveUser(TestUtils.getTestUserWithoutId());
		verify(passwordEncoder).encode(TestUtils.getTestUserWithoutId().getPassword());
		verify(userRepository).save(TestUtils.getTestUserWithoutId());
	}

	@Test
	public void testUpdateUser() throws ValidationException{
		when(userRepository.findOne(userId)).thenReturn(TestUtils.getTestUserWithId(userId));
		adminService.saveUser(TestUtils.getTestUserWithId(userId));
		verify(passwordEncoder, times(0)).encode(TestUtils.getTestUserWithId(userId).getPassword());
		verify(userRepository).findOne(userId);
		verify(userRepository).save(TestUtils.getTestUserWithId(userId));
	}

	@Test
	public void testSaveCompanyWithDuplicateKey() {
		thrown.expect(ValidationException.class);
		thrown.expectMessage("Can't save company. Company with the same parameter already exists");
		when(companyRepository.save(TestUtils.getTestCompanyWithoutId())).thenThrow(new DuplicateKeyException(""));
		adminService.saveCompany(TestUtils.getTestCompanyWithoutId());
	}

	@Test(expected = ValidationException.class)
	public void testSaveCompanyWithIncorrectEmail() {
		adminService.saveCompany(TestUtils.getNewCompanyObjectWithEmailAndPhone("ew@wew", "1234567"));
	}
	
	@Test(expected = ValidationException.class)
	public void testSaveCompanyWithIncorrectPhone() {
		adminService.saveCompany(TestUtils.getNewCompanyObjectWithEmailAndPhone("ew@wew.com", "asd1"));
	}

	@Test
	public void testSaveCompanySuccess() {
		adminService.saveCompany(TestUtils.getTestCompanyWithoutId());
		verify(companyRepository).save(TestUtils.getTestCompanyWithoutId());
	}

	@Test
	public void testDeleteUser() {
		adminService.deleteUser(userId);
		verify(userService).deleteAllVideosByUser(userId);
		verify(userRepository).delete(userId);
	}

	@Test
	public void testDeleteCompany() {
		List<User> listUsers = generateTestListUsers();
		when(userRepository.findByCompanyId(companyId)).thenReturn(listUsers);
		adminService.deleteCompany(companyId);
		verify(userRepository).findByCompanyId(companyId);
		verify(userService, times(5)).deleteAllVideosByUser(anyString());
		verify(userRepository, times(5)).delete(anyString());
		verify(companyRepository).delete(companyId);
	}

	private List<User> generateTestListUsers() {
		List<User> listUsers = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			User user = new User();
			user.setId(String.valueOf(i));
			listUsers.add(user);
		}
		return listUsers;
	}

}
