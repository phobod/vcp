package com.phobod.study.vcp.service.impl;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.phobod.study.vcp.Constants.Role;
import com.phobod.study.vcp.domain.Company;
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
	
	private Pageable pageable;
	private User user;
	private User userWithNonullId;
	private Company company;

	@Before
	public void setUp() throws Exception {
		pageable = new PageRequest(0, 10);
		company = new Company("Test Company", "Test Address", "test.company@email.com", "+1-111-111-1111");
		user = new User("TestUserName1", "TestUserSurname1", "TestUserLogin1", passwordEncoder.encode("1111"), "test.user1@email.com", company, Role.USER, "http://www.gravatar.com/avatar/00000000000000000000000000000000?d=mm");
		userWithNonullId = new User("TestUserName2", "TestUserSurname2", "TestUserLogin2", passwordEncoder.encode("1111"), "test.user2@email.com", company, Role.USER, "http://www.gravatar.com/avatar/00000000000000000000000000000000?d=mm");
		userWithNonullId.setId("id");
	}

	@Test
	public void testListAllUsers() {
		adminService.listAllUsers(pageable);
		verify(userRepository).findAllByOrderByNameAsc(pageable);
	}

	@Test
	public void testListAllCompanies() {
		adminService.listAllCompanies(pageable);
		verify(companyRepository).findAllByOrderByNameAsc(pageable);
	}

	@Test(expected = ValidationException.class)
	public void testSaveUserWithDuplicateKey() throws ValidationException{
		when(userRepository.save(user)).thenThrow(new DuplicateKeyException(""));
		adminService.saveUser(user);
	}

	@Test
	public void testSaveUserWithNullId() throws ValidationException{
		adminService.saveUser(user);
		verify(passwordEncoder).encode(user.getPassword());
		verify(userRepository).save(user);
	}

	@Test
	public void testSaveUserWithNonnullId() throws ValidationException{
		adminService.saveUser(userWithNonullId);
		verify(passwordEncoder, times(0)).encode(userWithNonullId.getPassword());
		verify(userRepository).save(userWithNonullId);
	}

	@Test(expected = ValidationException.class)
	public void testSaveCompanyWithDuplicateKey() {
		when(companyRepository.save(company)).thenThrow(new DuplicateKeyException(""));
		adminService.saveCompany(company);
	}

	@Test
	public void testSaveCompanySuccess() {
		adminService.saveCompany(company);
		verify(companyRepository).save(company);
	}

	@Test
	public void testDeleteUser() {
		adminService.deleteUser(user.getId());
		verify(userService).deleteAllVideosByUser(user.getId());
		verify(userRepository).delete(user.getId());
	}

	@Test
	public void testDeleteCompany() {
		List<User> listUsers = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			User user = new User();
			user.setId(String.valueOf(i));
			listUsers.add(user);
		}
		when(userRepository.findByCompanyId(company.getId())).thenReturn(listUsers);
		adminService.deleteCompany(company.getId());
		verify(userRepository).findByCompanyId(company.getId());
		verify(userService, times(5)).deleteAllVideosByUser(anyString());
		verify(userRepository, times(5)).delete(anyString());
		verify(companyRepository).delete(company.getId());
	}

}
