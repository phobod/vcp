package com.phobod.study.vcp.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.Constants.Role;
import com.phobod.study.vcp.domain.Company;
import com.phobod.study.vcp.domain.Token;
import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.repository.statistics.VideoStatisticsRepository;

@Service
public class CreateTestDataService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateTestDataService.class);
	private static final Random RANDOM = new Random();

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private VideoStatisticsRepository videoStatisticsRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${media.dir}")
	private String mediaDir;

	@Value("${mongo.recreate.db}")
	private boolean mongoRecreateDb;

	private String reason = "mongo.recreate.db=true";

	@PostConstruct
	public void createTestDataIfNecessary() {
		if (shouldTestDataBeCreated()) {
			LOGGER.info("Detected create test data command: " + reason);
			createTestData();
		} else {
			LOGGER.info("Mongo db exists");
		}
	}

	private boolean shouldTestDataBeCreated() {
		if (!mongoRecreateDb) {
			if (mongoTemplate.count(null, Company.class) == 0) {
				reason = "Collection company not found";
				return true;
			} else if (mongoTemplate.count(null, User.class) == 0) {
				reason = "Collection user not found";
				return true;
			} else if (mongoTemplate.count(null, Video.class) == 0) {
				reason = "Collection video not found";
				return true;
			}
		}
		return mongoRecreateDb;
	}

	private void createTestData() {
		createMediaDirsIfNecessary();
		clearMediaSubFolders();
		clearCollection();
		clearStatistics();
		List<Company> companies = createCompanies();
		List<User> users = createUsers(companies);
		createVideos(users);
		LOGGER.info("Test date created successful");
	}

	private File[] getMediaDirs(){
		return new File[] {new File(mediaDir + "/thumbnail"), new File(mediaDir + "/video")};
	}
	
	private void createMediaDirsIfNecessary(){
		for(File dir : getMediaDirs()) {
			if(!dir.exists()) {
				if(dir.mkdirs()){
					LOGGER.info("Created media dir: " + dir.getAbsolutePath());
				} else {
					LOGGER.error("Can't create media dir: " + dir.getAbsolutePath());
				}
			}
		}
	}
	
	private void clearMediaSubFolders() {
		for (File f : new File(mediaDir + "/thumbnail").listFiles()) {
			f.delete();
		}
		for (File f : new File(mediaDir + "/video").listFiles()) {
			f.delete();
		}
		LOGGER.info("Media sub folders cleared");
	}

	private void clearCollection() {
		mongoTemplate.remove(new Query(), User.class);
		mongoTemplate.remove(new Query(), Company.class);
		mongoTemplate.remove(new Query(), Video.class);
		mongoTemplate.remove(new Query(), Token.class);
		LOGGER.info("Collection cleared");
	}
	
	private void clearStatistics() {
		videoStatisticsRepository.deleteAll();
		LOGGER.info("Statistics cleared");
	}

	private List<Company> createCompanies() {
		List<Company> companies = getTestCompanies();
		mongoTemplate.insert(companies, Company.class);
		LOGGER.info("Created {} test companies", companies.size());
		return companies;
	}

	private List<Company> getTestCompanies() {
		return Arrays.asList(
				new Company("Coca-Cola", "Atlanta, USA", "info@coca-cola.com", "+1-800-438-2653"),
				new Company("Microsoft", "Redmond, USA", "info@microsoft.com", "+1-800-882-8080"),
				new Company("Google", "Mountain View, USA", "info@google.com", "+1-650-253-0000"),
				new Company("VCP", "Odessa, Ukraine", "vcp.service@gmail.com", "+38-067-111-1111"));
	}

	private List<User> createUsers(List<Company> companies) {
		List<User> users = getTestUsers(companies);
		mongoTemplate.insert(users, User.class);
		LOGGER.info("Created {} test users", users.size());
		return users;
	}

	private List<User> getTestUsers(List<Company> companies) {
		Company mainCompany = null;
		for (Company company : companies) {
			if (company.getName().equals("VCP")) {
				mainCompany = company;
			}
		}
		return Arrays.asList(
				new User("Tom", "Anderson", "Admin01", passwordEncoder.encode("sjdSDb34"), "AmREIT@yandex.ru", companies.get(RANDOM.nextInt(companies.size())), Role.ADMIN, "http://www.gravatar.com/avatar/2ecec780360672c51b13f99f187e7285?d=mm"),
				new User("Jack", "Douu", "jactin", passwordEncoder.encode("qwerty123"), "Bnefica@yandex.ru", companies.get(RANDOM.nextInt(companies.size())), Role.USER, "http://www.gravatar.com/avatar/27526da8cc68e227b84e0418b1667c17?d=mm"),
				new User("Rachel", "Stone", "roston", passwordEncoder.encode("sdfvsm2d"), "Cmlabs@yandex.ru", companies.get(RANDOM.nextInt(companies.size())), Role.USER, "http://www.gravatar.com/avatar/46c7101ae6cb2714c914a0af79e2a822?d=mm"),
				new User("Steve", "Macleod", "duncan", passwordEncoder.encode("lsdb2HG"), "dnesice@yandex.ru", companies.get(RANDOM.nextInt(companies.size())), Role.USER, "http://www.gravatar.com/avatar/cb22dcb5c2341afc4487f4814b177f34?d=mm"),
				new User("User", "User", "user", passwordEncoder.encode("1111"), "emaddeh@yandex.ru", companies.get(RANDOM.nextInt(companies.size())), Role.USER, "http://www.gravatar.com/avatar/e5c8b280949740fa8a68922258187e44?d=mm"),
				new User("Admin", "Admin", "admin", passwordEncoder.encode("1111"), "vcp.recovery.service@gmail.com", mainCompany, Role.ADMIN, "http://www.gravatar.com/avatar/262f8cc87f07aa61a71b4819d64fa092?d=mm"));
	}

	private void createVideos(List<User> users){
		List<Video> testVideos = getTestVideos();
		List<User> listOnlyUsers = new ArrayList<>();
		for (User user : users) {
			if (user.getRole() == Role.USER) {
				listOnlyUsers.add(user);
			}
		}
		complementTestVideos(listOnlyUsers, testVideos);
		mongoTemplate.insert(testVideos, Video.class);
		LOGGER.info("Created {} video files", testVideos.size());
	}

	private void complementTestVideos(List<User> users, List<Video> testVideos) {
		for (Video video : testVideos) {
			video.setViews(RANDOM.nextInt(5000));
			video.setOwner(users.get(RANDOM.nextInt(users.size())));
		}
	}

	private List<Video> getTestVideos() {
		return Arrays.asList(
				new Video("Piggy Tales - Pigs at Work - Lights Out",
						"How many pigs does it take to screw in a lightbulb? We may never find out. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/bihusy2rqxmgyty/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Lights%20Out.jpg?dl=1",
						"https://www.dropbox.com/s/h2zj457zhss3eov/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Lights%20Out.jpg?dl=1",
						"https://www.dropbox.com/s/jr6jv6q5ysie4xr/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Lights%20Out.jpg?dl=1",
						"https://www.dropbox.com/s/l8pg31iui1xujmb/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Lights%20Out.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Lunch Break",
						"Haven't you heard? It's lunchtime! Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/rmrcratybbz949z/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Lunch%20Break.jpg?dl=1",
						"https://www.dropbox.com/s/4jcq08yv23mtmuc/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Lunch%20Break.jpg?dl=1",
						"https://www.dropbox.com/s/ok6q30zu9d6yaeq/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Lunch%20Break.jpg?dl=1",
						"https://www.dropbox.com/s/u46lkuav8kbn3dn/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Lunch%20Break.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Magnetic Appeal",
						"Getting the right tool for the job to get the right tool for the job. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/k4c5ws29zh0a8m8/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Magnetic%20Appeal.jpg?dl=1",
						"https://www.dropbox.com/s/oz1l5klnium0l8p/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Magnetic%20Appeal.jpg?dl=1",
						"https://www.dropbox.com/s/s28u5l3gpnnjy2d/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Magnetic%20Appeal.jpg?dl=1",
						"https://www.dropbox.com/s/7h581qia29dk8my/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Magnetic%20Appeal.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Mind The Gap",
						"Taking bridge building to another dimension. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/5ysqp9dirgny4xq/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Mind%20The%20Gap.jpg?dl=1",
						"https://www.dropbox.com/s/6pw6mv46z0ay8bb/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Mind%20The%20Gap.jpg?dl=1",
						"https://www.dropbox.com/s/75q6riymxyzqvbq/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Mind%20The%20Gap.jpg?dl=1",
						"https://www.dropbox.com/s/jbk8i2f5cjs3st6/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Mind%20The%20Gap.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Nailed It!",
						"In this week’s episode: You two had ONE job! Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/uwov34dvlx26e1k/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Nailed%20It%21.jpg?dl=1",
						"https://www.dropbox.com/s/zv9mvx1nrui6fox/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Nailed%20It%21.jpg?dl=1",
						"https://www.dropbox.com/s/gv30ygoemkmdlww/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Nailed%20It%21.jpg?dl=1",
						"https://www.dropbox.com/s/7yugw0fox28fbrc/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Nailed%20It%21.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Pile Up",
						"Infinite boxes. Limited intelligence. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/wwkcgqc087ngvif/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Pile%20Up.jpg?dl=1",
						"https://www.dropbox.com/s/5mtnyxd3t8h0jjk/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Pile%20Up.jpg?dl=1",
						"https://www.dropbox.com/s/un9kfx0c9my83rt/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Pile%20Up.jpg?dl=1",
						"https://www.dropbox.com/s/fponu51u1zvt8n8/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Pile%20Up.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Porkatron",
						"Innovating the only way they know how: accidentally. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/m8hhge541y4y84u/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Porkatron.jpg?dl=1",
						"https://www.dropbox.com/s/cozedm5ijm15741/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Porkatron.jpg?dl=1",
						"https://www.dropbox.com/s/183esrjuwzpzvlt/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Porkatron.jpg?dl=1",
						"https://www.dropbox.com/s/98hns1ufdiwrqb0/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Porkatron.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Predicament In Paint",
						"What not to do when painting a floor. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/77nmgwy6cari0v6/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Predicament%20In%20Paint.jpg?dl=1",
						"https://www.dropbox.com/s/mq0gv5zasdllx1y/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Predicament%20In%20Paint.jpg?dl=1",
						"https://www.dropbox.com/s/c7dxs1vozcnciz0/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Predicament%20In%20Paint.jpg?dl=1",
						"https://www.dropbox.com/s/9sgpogpvfnistwr/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Predicament%20In%20Paint.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Race Nut",
						"Don't knock it 'till you rocket. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/wu25vmcr4j1snp8/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Race%20Nut.jpg?dl=1",
						"https://www.dropbox.com/s/d4nnm8err8x7nbr/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Race%20Nut.jpg?dl=1",
						"https://www.dropbox.com/s/f5fb95xn51hc2pq/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Race%20Nut.jpg?dl=1",
						"https://www.dropbox.com/s/m72ty0zqdhsmlq8/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Race%20Nut.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Screw Up",
						"Right attitude, wrong altitude. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/lpb9e83kpx6ic0a/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Screw%20Up.jpg?dl=1",
						"https://www.dropbox.com/s/2t0vwfdg6ldhsgy/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Screw%20Up.jpg?dl=1",
						"https://www.dropbox.com/s/fecogalljxflrj7/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Screw%20Up.jpg?dl=1",
						"https://www.dropbox.com/s/pb8f86of3mphhd0/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Screw%20Up.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Step 1",
						"Unclear instructions unclear, not instructional. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/bqivesz8r4ctpc2/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Step%201.jpg?dl=1",
						"https://www.dropbox.com/s/7h8i653bshgqzdm/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Step%201.jpg?dl=1",
						"https://www.dropbox.com/s/pl5rq94qgnnno43/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Step%201.jpg?dl=1",
						"https://www.dropbox.com/s/wyu2m2l7xvrl4ri/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Step%201.mp4?dl=1"),
				new Video("Piggy Tales- Pigs at Work - Sticky Situation",
						"\"Stuck up\" takes on a new meaning. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/l8inn3r7lomnrqz/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Sticky%20Situation.jpg?dl=1",
						"https://www.dropbox.com/s/p9i5hsmod9zvvnn/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Sticky%20Situation.jpg?dl=1",
						"https://www.dropbox.com/s/j5qqhlrf2fsg3ch/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Sticky%20Situation.jpg?dl=1",
						"https://www.dropbox.com/s/fxmjtnn8rdosp60/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Sticky%20Situation.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Tipping Point",
						"Work-life balance, on another level. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/oefrpwbc44rjb1e/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Tipping%20Point.jpg?dl=1",
						"https://www.dropbox.com/s/x8zn3bm48r2cf9l/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Tipping%20Point.jpg?dl=1",
						"https://www.dropbox.com/s/7ivt86p2a4fe1bz/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Tipping%20Point.jpg?dl=1",
						"https://www.dropbox.com/s/7wcecz9eiw0nt06/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Tipping%20Point.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Unhinged",
						"An elaborate solution to every simple problem. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/ygxolflbzo1hmy6/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Unhinged.jpg?dl=1",
						"https://www.dropbox.com/s/qssel5g4z9b3a9y/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Unhinged.jpg?dl=1",
						"https://www.dropbox.com/s/ss8lq0o9y7uelbl/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Unhinged.jpg?dl=1",
						"https://www.dropbox.com/s/c1n33ok33lk2wdk/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Unhinged.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - All Geared Up",
						"Quality control, in reverse. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/wpnnrg17qstpuzc/Piggy%20Tales_%20Pigs%20at%20Work%20-%20All%20Geared%20Up.jpg?dl=1",
						"https://www.dropbox.com/s/d6mhb23lek4dhar/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20All%20Geared%20Up.jpg?dl=1",
						"https://www.dropbox.com/s/xvxu2wdholp9nvr/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20All%20Geared%20Up.jpg?dl=1",
						"https://www.dropbox.com/s/jc7ga6832ub155a/Piggy%20Tales_%20Pigs%20at%20Work%20-%20All%20Geared%20Up.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Dream House",
						"The keystone to constructing the perfect home. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/dbvhv2bms9c5axv/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Dream%20House.jpg?dl=1",
						"https://www.dropbox.com/s/zijgxahz63dlv4y/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Dream%20House.jpg?dl=1",
						"https://www.dropbox.com/s/y6omq8mc718hpbn/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Dream%20House.jpg?dl=1",
						"https://www.dropbox.com/s/vmsp68e4arnk2bk/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Dream%20House.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Fabulous Fluke",
						"Hard work only goes so far. Sometimes you need a miracle. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/uoofk59p835x2t2/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Fabulous%20Fluke.jpg?dl=1",
						"https://www.dropbox.com/s/68cxp1yyuner971/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Fabulous%20Fluke.jpg?dl=1",
						"https://www.dropbox.com/s/rpcguusiajp4t8h/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Fabulous%20Fluke.jpg?dl=1",
						"https://www.dropbox.com/s/jm4qu4lcut862xv/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Fabulous%20Fluke.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Get the Hammer",
						"Reach for sky, receive hammer. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/xvtlp04y649327o/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Get%20the%20Hammer.jpg?dl=1",
						"https://www.dropbox.com/s/slisd3twwd3izee/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Get%20the%20Hammer.jpg?dl=1",
						"https://www.dropbox.com/s/n8fcfj551yplspl/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Get%20the%20Hammer.jpg?dl=1",
						"https://www.dropbox.com/s/q03emm8w90fodc9/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Get%20the%20Hammer.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Grand Opening",
						"New! Cutting-edge! Groundbreaking! Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/q3wtq6i7selazpe/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Grand%20Opening.jpg?dl=1",
						"https://www.dropbox.com/s/7u8dsjtr7bqccyf/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Grand%20Opening.jpg?dl=1",
						"https://www.dropbox.com/s/g5csuwku0xielwi/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Grand%20Opening.jpg?dl=1",
						"https://www.dropbox.com/s/4sxgtjs2iljk5nx/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Grand%20Opening.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Home Sweet Home",
						"Arriving at the perfect design is sometimes a matter of adjusting expectations. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/3hx1xxci8zhudhq/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Home%20Sweet%20Home.jpg?dl=1",
						"https://www.dropbox.com/s/oy3r9lw75md4h5a/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Home%20Sweet%20Home.jpg?dl=1",
						"https://www.dropbox.com/s/frjx52f4baba56e/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Home%20Sweet%20Home.jpg?dl=1",
						"https://www.dropbox.com/s/mbv9y38ik0oql12/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Home%20Sweet%20Home.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Jackhammered",
						"Hearing protection? Check. Common sense? Not so much. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/8730ailfh4jq30z/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Jackhammered.jpg?dl=1",
						"https://www.dropbox.com/s/o53zj7bzw8ud9k5/medium-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Jackhammered.jpg?dl=1",
						"https://www.dropbox.com/s/11uf03epsccni5n/small-Piggy%20Tales_%20Pigs%20at%20Work%20-%20Jackhammered.jpg?dl=1",
						"https://www.dropbox.com/s/0cgvlgegso1gh7l/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Jackhammered.mp4?dl=1"),
				new Video("Piggy Tales - Snooze",
						"When a piggy starts snoring, what would a fellow pig do to make him stop? Anything, that’s what!",
						"https://www.dropbox.com/s/7p55s2ealrr98sw/Piggy%20Tales_%20%E2%80%9CSnooze%E2%80%9D.jpg?dl=1",
						"https://www.dropbox.com/s/rtkplhsdrx6nyfb/medium-Piggy%20Tales_%20%E2%80%9CSnooze%E2%80%9D.jpg?dl=1",
						"https://www.dropbox.com/s/4rxdvo11l1rj1op/small-Piggy%20Tales_%20%E2%80%9CSnooze%E2%80%9D.jpg?dl=1",
						"https://www.dropbox.com/s/ve2xb1hsgz9kyh2/Piggy%20Tales_%20%E2%80%9CSnooze%E2%80%9D.mp4?dl=1"),
				new Video("Piggy Tales - Superpork",
						"Stop that thief! Will SuperPork and his trusted sidekick save the day? Go SuperPork, go!",
						"https://www.dropbox.com/s/1iuwdp25f55vmzr/Piggy%20Tales_%20%E2%80%9CSuperpork%E2%80%9D.jpg?dl=1",
						"https://www.dropbox.com/s/1id5l42rzvvn4na/medium-Piggy%20Tales_%20%E2%80%9CSuperpork%E2%80%9D.jpg?dl=1",
						"https://www.dropbox.com/s/mw4qj4w9nk8jlck/small-Piggy%20Tales_%20%E2%80%9CSuperpork%E2%80%9D.jpg?dl=1",
						"https://www.dropbox.com/s/apjnejfjhn17i9u/Piggy%20Tales_%20%E2%80%9CSuperpork%E2%80%9D.mp4?dl=1"),
				new Video("Piggy Tales - Cake Duel",
						"Those hungry hogs love nothing more than pigging out on sweet treats, but what if there’s only one slice of cake?",
						"https://www.dropbox.com/s/4676ugp2v7n838l/Piggy%20Tales_%20%E2%80%9CCake%20Duel%E2%80%9D.jpg?dl=1",
						"https://www.dropbox.com/s/f9nk9x1ifbbkpl6/medium-Piggy%20Tales_%20%E2%80%9CCake%20Duel%E2%80%9D.jpg?dl=1",
						"https://www.dropbox.com/s/h8nzrr3dwqs6tfg/small-Piggy%20Tales_%20%E2%80%9CCake%20Duel%E2%80%9D.jpg?dl=1",
						"https://www.dropbox.com/s/es0proys1zmayug/Piggy%20Tales_%20%E2%80%9CCake%20Duel%E2%80%9D.mp4?dl=1"),
				new Video("Piggy Tales - Peekaboo!",
						"One little piggy wants to play hide and seek, but his porky friend doesn't seem to understand the rules! Or so it seems...",
						"https://www.dropbox.com/s/sfip2wp6d2wkkhc/Piggy%20Tales_%20Peekaboo%21.jpg?dl=1",
						"https://www.dropbox.com/s/zuknl3uyn2l6dga/medium-Piggy%20Tales_%20Peekaboo%21.jpg?dl=1",
						"https://www.dropbox.com/s/qahp6cch3l0ec3i/small-Piggy%20Tales_%20Peekaboo%21.jpg?dl=1",
						"https://www.dropbox.com/s/ozdx95ctmx4dxx1/Piggy%20Tales_%20Peekaboo%21.mp4?dl=1"),
				new Video("Piggy Tales - Piggy In The Middle",
						"Two piggies are playing with a soccer ball, but when another pig wants to join in they play keep away. When will it be his turn?",
						"https://www.dropbox.com/s/g0zmv3jo69gpf7a/Piggy%20Tales_%20Piggy%20In%20The%20Middle.jpg?dl=1",
						"https://www.dropbox.com/s/elt0y37ofkzdh7r/medium-Piggy%20Tales_%20Piggy%20In%20The%20Middle.jpg?dl=1",
						"https://www.dropbox.com/s/l5q2kjz2hrtvm78/small-Piggy%20Tales_%20Piggy%20In%20The%20Middle.jpg?dl=1",
						"https://www.dropbox.com/s/kc5pmypxkqjauhr/Piggy%20Tales_%20Piggy%20In%20The%20Middle.mp4?dl=1"),
				new Video("Piggy Tales - Puffed Up",
						"What happens when you hold your breath? One poor piggy is about to find out!",
						"https://www.dropbox.com/s/m8dzq0jntljinke/Piggy%20Tales_%20Puffed%20Up.jpg?dl=1",
						"https://www.dropbox.com/s/pmms6f6mssg4fgu/medium-Piggy%20Tales_%20Puffed%20Up.jpg?dl=1",
						"https://www.dropbox.com/s/0fydiajv52c3zb8/small-Piggy%20Tales_%20Puffed%20Up.jpg?dl=1",
						"https://www.dropbox.com/s/z9j4oqhdlsrawbg/Piggy%20Tales_%20Puffed%20Up.mp4?dl=1"),
				new Video("Piggy Tales - Push-button Trouble",
						"Another day, another button! Why can't those pesky piggies just do as they're told? Being curious must be in their DNA!",
						"https://www.dropbox.com/s/oidoi5kbb7p3p6k/Piggy%20Tales_%20Push-button%20Trouble.jpg?dl=1",
						"https://www.dropbox.com/s/f9npmfkz4h2gdes/medium-Piggy%20Tales_%20Push-button%20Trouble.jpg?dl=1",
						"https://www.dropbox.com/s/zofc8yci6wexgyg/small-Piggy%20Tales_%20Push-button%20Trouble.jpg?dl=1",
						"https://www.dropbox.com/s/3slegkdhz4tnkbl/Piggy%20Tales_%20Push-button%20Trouble.mp4?dl=1"),
				new Video("Piggy Tales - Push-button",
						"Put a big red button in front of a pig and he'll push it! This little piggy just can't resist pressing it -- even though he doesn't know what it does!",
						"https://www.dropbox.com/s/m7qpsqn32erd5sm/Piggy%20Tales_%20Push-button.jpg?dl=1",
						"https://www.dropbox.com/s/1ge2ycy4dewb9g2/medium-Piggy%20Tales_%20Push-button.jpg?dl=1",
						"https://www.dropbox.com/s/964z0hl2k3vonpq/small-Piggy%20Tales_%20Push-button.jpg?dl=1",
						"https://www.dropbox.com/s/i4a8qr8kxfzjh9b/Piggy%20Tales_%20Push-button.mp4?dl=1"),
				new Video("Piggy Tales - Shazam",
						"Abracadabra! Pigs really shouldn't mess with magic, but when these two come across a magic wand it leaves them spellbound!",
						"https://www.dropbox.com/s/cx9ju93cigj2tgz/Piggy%20Tales_%20Shazam.jpg?dl=1",
						"https://www.dropbox.com/s/7git2q5jt0jjs2x/medium-Piggy%20Tales_%20Shazam.jpg?dl=1",
						"https://www.dropbox.com/s/2jvmei8o5jpzfj6/small-Piggy%20Tales_%20Shazam.jpg?dl=1",
						"https://www.dropbox.com/s/bxgmg32v402m5gl/Piggy%20Tales_%20Shazam.mp4?dl=1"),
				new Video("Piggy Tales - Snowed Up - Holiday Special",
						"Yes, even our favorite porky friends celebrate this festive season by frolicking around in the snow. Just don’t confuse the frosty white stuff with ice-cream...",
						"https://www.dropbox.com/s/xn93m195b6csein/Piggy%20Tales_%20Snowed%20Up%20-%20Holiday%20Special.jpg?dl=1",
						"https://www.dropbox.com/s/8q9pr2z9rspb0xc/medium-Piggy%20Tales_%20Snowed%20Up%20-%20Holiday%20Special.jpg?dl=1",
						"https://www.dropbox.com/s/xel52bbpguzk4ca/small-Piggy%20Tales_%20Snowed%20Up%20-%20Holiday%20Special.jpg?dl=1",
						"https://www.dropbox.com/s/x7tbqwo6pea0ta4/Piggy%20Tales_%20Snowed%20Up%20-%20Holiday%20Special.mp4?dl=1"),
				new Video("Piggy Tales - Stuck In",
						"Though many tried for the sword with all their strength, none could move the sword nor stir it – but do we really want pigs running around with swords anyway?",
						"https://www.dropbox.com/s/b8g11err4gje1jj/Piggy%20Tales_%20Stuck%20In.jpg?dl=1",
						"https://www.dropbox.com/s/0f6p40iy5d7440c/medium-Piggy%20Tales_%20Stuck%20In.jpg?dl=1",
						"https://www.dropbox.com/s/5k4peobwb7rcdlw/small-Piggy%20Tales_%20Stuck%20In.jpg?dl=1",
						"https://www.dropbox.com/s/djms9hph6fufsop/Piggy%20Tales_%20Stuck%20In.mp4?dl=1"),
				new Video("Piggy Tales - Super Glue",
						"What's green, stupid, and sticky all over? See what happens when one dimwitted pig finds a tube of glue -- and just has to pick it up",
						"https://www.dropbox.com/s/0oam35r5uicju5p/Piggy%20Tales_%20Super%20Glue.jpg?dl=1",
						"https://www.dropbox.com/s/y2t13gqffejh5v3/medium-Piggy%20Tales_%20Super%20Glue.jpg?dl=1",
						"https://www.dropbox.com/s/q2186vxy0x7mhfi/small-Piggy%20Tales_%20Super%20Glue.jpg?dl=1",
						"https://www.dropbox.com/s/m5vsgkcw6czuyqa/Piggy%20Tales_%20Super%20Glue.mp4?dl=1"),
				new Video("Piggy Tales - Teeter Trotter",
						"Two piggies are playing on a seesaw. They're trying to get it rocking, but one of the pigs is just too big! So how can they get that big piggy up in the air?",
						"https://www.dropbox.com/s/pr5t5azfgrgq2fn/Piggy%20Tales_%20Teeter%20Trotter.jpg?dl=1",
						"https://www.dropbox.com/s/p6q9fu8h2e56kpt/medium-Piggy%20Tales_%20Teeter%20Trotter.jpg?dl=1",
						"https://www.dropbox.com/s/17s4y9hcovzv16d/small-Piggy%20Tales_%20Teeter%20Trotter.jpg?dl=1",
						"https://www.dropbox.com/s/y1m0ssr2jc4qkrq/Piggy%20Tales_%20Teeter%20Trotter.mp4?dl=1"),
				new Video("Piggy Tales - The Catch",
						"Ice hole fishing is usually a relaxing hobby. But when an impatient piggy casts his line, there’s no telling what’s going to be the catch of the day.",
						"https://www.dropbox.com/s/29nzf9s3klu1xfm/Piggy%20Tales_%20The%20Catch.jpg?dl=1",
						"https://www.dropbox.com/s/002067tp0nbqooq/medium-Piggy%20Tales_%20The%20Catch.jpg?dl=1",
						"https://www.dropbox.com/s/xxul6mn9eny794h/small-Piggy%20Tales_%20The%20Catch.jpg?dl=1",
						"https://www.dropbox.com/s/vhjbfa5u8kpqkdn/Piggy%20Tales_%20The%20Catch.mp4?dl=1"),
				new Video("Piggy Tales - The Cure",
						"When a poor little piggy has a cold, his caring friend really wants to help. Maybe some medicine will make him feel better...",
						"https://www.dropbox.com/s/ju1qiwz8s47grh7/Piggy%20Tales_%20The%20Cure.jpg?dl=1",
						"https://www.dropbox.com/s/o1bq9jcvxuz4ysn/medium-Piggy%20Tales_%20The%20Cure.jpg?dl=1",
						"https://www.dropbox.com/s/8m5jdlkdwpyek5v/small-Piggy%20Tales_%20The%20Cure.jpg?dl=1",
						"https://www.dropbox.com/s/sv65re2ucej3wp4/Piggy%20Tales_%20The%20Cure.mp4?dl=1"),
				new Video("Piggy Tales - The Game",
						"Get ready for a chess master class from two of the “smartest” piggies around. It’s the ultimate display of strategy, skill and patience.",
						"https://www.dropbox.com/s/vq5mfoxbvmh0zyf/Piggy%20Tales_%20The%20Game.jpg?dl=1",
						"https://www.dropbox.com/s/2jya5ijmlxsp6nm/medium-Piggy%20Tales_%20The%20Game.jpg?dl=1",
						"https://www.dropbox.com/s/c9qyt59wi1mkg7j/small-Piggy%20Tales_%20The%20Game.jpg?dl=1",
						"https://www.dropbox.com/s/u4mvfzdi8r0fwpr/Piggy%20Tales_%20The%20Game.mp4?dl=1"),
				new Video("Piggy Tales - The Hole",
						"Where's that breeze coming from? When two piggies come across a mysterious hole in the ground, they throw caution to the wind.",
						"https://www.dropbox.com/s/p9uf627ttsgyjzv/Piggy%20Tales_%20The%20Hole.jpg?dl=1",
						"https://www.dropbox.com/s/qx3p6x9twxcyrq2/medium-Piggy%20Tales_%20The%20Hole.jpg?dl=1",
						"https://www.dropbox.com/s/kr0jd4tsuwavhf4/small-Piggy%20Tales_%20The%20Hole.jpg?dl=1",
						"https://www.dropbox.com/s/9plw8zdsm5ysmcv/Piggy%20Tales_%20The%20Hole.mp4?dl=1"),
				new Video("Piggy Tales - The Mirror",
						"Mirror mirror on the wall, who's the porkiest of them all? This little pig enjoys checking himself out -- but is he really THAT good looking?",
						"https://www.dropbox.com/s/rhn4lgkphe23nsj/Piggy%20Tales_%20The%20Mirror.jpg?dl=1",
						"https://www.dropbox.com/s/7g19s3dovdrxi3r/medium-Piggy%20Tales_%20The%20Mirror.jpg?dl=1",
						"https://www.dropbox.com/s/ejxgh2sxvr162kh/small-Piggy%20Tales_%20The%20Mirror.jpg?dl=1",
						"https://www.dropbox.com/s/1icoqr3mo8mzo3u/Piggy%20Tales_%20The%20Mirror.mp4?dl=1"),
				new Video("Piggy Tales - The Wishing Well",
						"You know what they say – be careful what you wish for. Unfortunately for the pigs, being careful is just not in their DNA.",
						"https://www.dropbox.com/s/tii19i3qheadrwy/Piggy%20Tales_%20The%20Wishing%20Well.jpg?dl=1",
						"https://www.dropbox.com/s/h7jxc8qfc35jv2i/medium-Piggy%20Tales_%20The%20Wishing%20Well.jpg?dl=1",
						"https://www.dropbox.com/s/x497i7efbivikwe/small-Piggy%20Tales_%20The%20Wishing%20Well.jpg?dl=1",
						"https://www.dropbox.com/s/vio7pffch11nbdk/Piggy%20Tales_%20The%20Wishing%20Well.mp4?dl=1"),
				new Video("Piggy Tales - Trampoline",
						"In the first episode, a curious Piggy comes across a trampoline and can't resist jumping on it -- even though he's not supposed to...",
						"https://www.dropbox.com/s/t6av1kul3i753fp/Piggy%20Tales_%20Trampoline.jpg?dl=1",
						"https://www.dropbox.com/s/bt3i5tw5et7zszc/medium-Piggy%20Tales_%20Trampoline.jpg?dl=1",
						"https://www.dropbox.com/s/nnf2syb057rnwqv/small-Piggy%20Tales_%20Trampoline.jpg?dl=1",
						"https://www.dropbox.com/s/ae6uuo1g2cn2j9f/Piggy%20Tales_%20Trampoline.mp4?dl=1"),
				new Video("Piggy Tales - Up Or Down",
						"When a busy little pig calls an elevator, it keeps him waiting – but what’s taking so long?",
						"https://www.dropbox.com/s/4vbz1efsya973fq/Piggy%20Tales_%20Up%20Or%20Down.jpg?dl=1",
						"https://www.dropbox.com/s/v1gqs2zctcm26c4/medium-Piggy%20Tales_%20Up%20Or%20Down.jpg?dl=1",
						"https://www.dropbox.com/s/jjur1zer9qocrwp/small-Piggy%20Tales_%20Up%20Or%20Down.jpg?dl=1",
						"https://www.dropbox.com/s/86egw71gu7uj0yy/Piggy%20Tales_%20Up%20Or%20Down.mp4?dl=1"),
				new Video("Piggy Tales - Up The Tempo",
						"What’s that sound? Where is it coming from? And why is this piggy’s hammer making such funny noises? You’ll have to watch to find out!",
						"https://www.dropbox.com/s/q4yqn79mykircz9/Piggy%20Tales_%20Up%20The%20Tempo.jpg?dl=1",
						"https://www.dropbox.com/s/ya8cpz2h822d6s7/medium-Piggy%20Tales_%20Up%20The%20Tempo.jpg?dl=1",
						"https://www.dropbox.com/s/1whppn2pn8pcvfq/small-Piggy%20Tales_%20Up%20The%20Tempo.jpg?dl=1",
						"https://www.dropbox.com/s/0g0mn43bx110lbp/Piggy%20Tales_%20Up%20The%20Tempo.mp4?dl=1"),
				new Video("Piggy Tales - Abduction",
						"Take me to your leader! A surprise visit from an intergalactic explorer wipes the smile from one unsuspecting piggy's face...",
						"https://www.dropbox.com/s/dzwl0bsnul7a5be/Piggy%20Tales_%20Abduction.jpg?dl=1",
						"https://www.dropbox.com/s/65om05zo92pkujs/medium-Piggy%20Tales_%20Abduction.jpg?dl=1",
						"https://www.dropbox.com/s/ozrrb17aswib6ar/small-Piggy%20Tales_%20Abduction.jpg?dl=1",
						"https://www.dropbox.com/s/v5ngiwijbmmwa9e/Piggy%20Tales_%20Abduction.mp4?dl=1"),
				new Video("Piggy Tales - Epic Sir Bucket",
						"A bucket makes a perfect helmet for a crusading knight -- or at least this playful piggy thinks so! But what's that mysterious thing hanging from that balloon?",
						"https://www.dropbox.com/s/iszed1kfbjaf8rt/Piggy%20Tales_%20Epic%20Sir%20Bucket.jpg?dl=1",
						"https://www.dropbox.com/s/48sbb2v8o7vm1wz/medium-Piggy%20Tales_%20Epic%20Sir%20Bucket.jpg?dl=1",
						"https://www.dropbox.com/s/a370bqa74fkhp9e/small-Piggy%20Tales_%20Epic%20Sir%20Bucket.jpg?dl=1",
						"https://www.dropbox.com/s/78pvartdt4ckp12/Piggy%20Tales_%20Epic%20Sir%20Bucket.mp4?dl=1"),
				new Video("Piggy Tales - Fly Piggy, Fly",
						"Those piggies may not be very smart, but they sure are determined! This inventive piggy just won’t give up trying to get his machine up in the air!",
						"https://www.dropbox.com/s/s1w5g357v37uwa7/Piggy%20Tales_%20Fly%20Piggy%2C%20Fly.jpg?dl=1",
						"https://www.dropbox.com/s/a5dtam9dcdorn9j/medium-Piggy%20Tales_%20Fly%20Piggy%2C%20Fly.jpg?dl=1",
						"https://www.dropbox.com/s/u4ajn9f53hdso0j/small-Piggy%20Tales_%20Fly%20Piggy%2C%20Fly.jpg?dl=1",
						"https://www.dropbox.com/s/43twnz1mkjv9gm1/Piggy%20Tales_%20Fly%20Piggy%2C%20Fly.mp4?dl=1"),
				new Video("Piggy Tales - Gloves",
						"Piggies don’t have hands! So when they come across a pair of gloves they have no idea what they’re for.",
						"https://www.dropbox.com/s/4eqc1vr1w0946m3/Piggy%20Tales_%20Gloves.jpg?dl=1",
						"https://www.dropbox.com/s/810q2nhmqkrssu2/medium-Piggy%20Tales_%20Gloves.jpg?dl=1",
						"https://www.dropbox.com/s/5w79ld3we1q1wqk/small-Piggy%20Tales_%20Gloves.jpg?dl=1",
						"https://www.dropbox.com/s/asul4pkvd7akz1b/Piggy%20Tales_%20Gloves.mp4?dl=1"),
				new Video("Piggy Tales - Hog Hoops",
						"Join us courtside as one athletic piggy shows off his ball skillz! Hopefully nothing puts him off his aim...",
						"https://www.dropbox.com/s/2chu5o1sqv07uzs/Piggy%20Tales_%20Hog%20Hoops.jpg?dl=1",
						"https://www.dropbox.com/s/y7385i1m30jegex/medium-Piggy%20Tales_%20Hog%20Hoops.jpg?dl=1",
						"https://www.dropbox.com/s/4swvkd6pudgmqeb/small-Piggy%20Tales_%20Hog%20Hoops.jpg?dl=1",
						"https://www.dropbox.com/s/f5r0xm6iemp8v5g/Piggy%20Tales_%20Hog%20Hoops.mp4?dl=1"),
				new Video("Piggy Tales - It's a wrap",
						"You know that feeling when a super fun party is over and it's time to clean up everything and go home? Well, I guess even piggies can get a bit emotional at times like that.",
						"https://www.dropbox.com/s/e0fmg6ujm5jbbvq/Piggy%20Tales_%20It%27s%20a%20wrap.jpg?dl=1",
						"https://www.dropbox.com/s/1ug0coghb41jsrd/medium-Piggy%20Tales_%20It%27s%20a%20wrap.jpg?dl=1",
						"https://www.dropbox.com/s/fu9eyrjmkvyu9ch/small-Piggy%20Tales_%20It%27s%20a%20wrap.jpg?dl=1",
						"https://www.dropbox.com/s/lw47pzl2l76dsnw/Piggy%20Tales_%20It%27s%20a%20wrap.mp4?dl=1"),
				new Video("Piggy Tales - Jammed",
						"What food do piggies love almost as much as eggs? When a couple of hungry hogs find a jam jar, they’ll do anything to get it open.",
						"https://www.dropbox.com/s/zwbaln5vdmq2wrq/Piggy%20Tales_%20Jammed.jpg?dl=1",
						"https://www.dropbox.com/s/1wdvo27jxturf5c/medium-Piggy%20Tales_%20Jammed.jpg?dl=1",
						"https://www.dropbox.com/s/249b0johhuejo08/small-Piggy%20Tales_%20Jammed.jpg?dl=1",
						"https://www.dropbox.com/s/rnv5f0bt2pr80tx/Piggy%20Tales_%20Jammed.mp4?dl=1"));
		};

}
