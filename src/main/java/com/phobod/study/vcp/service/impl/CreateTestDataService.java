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

@Service
public class CreateTestDataService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateTestDataService.class);
	private static final Random RANDOM = new Random();

	@Autowired
	private MongoTemplate mongoTemplate;
	
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
		List<Company> companies = createCompanies();
		List<User> users = createUsers(companies);
		createVideos(users);
		LOGGER.info("Test date created successful");
	}

	private File[] getMediaDirs(){
		return new File[] {new File(mediaDir + "/thumbnail"), new File(mediaDir + "/video"), new File(mediaDir + "/image")};
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
		for (File f : new File(mediaDir + "/image").listFiles()) {
			f.delete();
		}
		LOGGER.info("Media sub folders cleared");
	}

	private void clearCollection() {
		mongoTemplate.remove(new Query(), User.class);
		mongoTemplate.remove(new Query(), Company.class);
		mongoTemplate.remove(new Query(), Video.class);
		mongoTemplate.remove(new Query(), Token.class);
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
				new User("Tom", "Anderson", "Admin01", passwordEncoder.encode("sjdSDb34"), "tomas@mail.ru",
						companies.get(RANDOM.nextInt(companies.size())), Role.ADMIN,
						"http://www.radfaces.com/images/avatars/ickis.jpg"),
				new User("Jack", "Douu", "jactin", passwordEncoder.encode("qwerty123"), "jactin@mail.ru",
						companies.get(RANDOM.nextInt(companies.size())), Role.USER,
						"http://www.radfaces.com/images/avatars/krumm.jpg"),
				new User("Rachel", "Stone", "roston", passwordEncoder.encode("sdfvsm2d"), "roston@mail.ru",
						companies.get(RANDOM.nextInt(companies.size())), Role.USER,
						"http://www.radfaces.com/images/avatars/jane-lane.jpg"),
				new User("Steve", "Macleod", "duncan", passwordEncoder.encode("lsdb2HG"), "duncan@mail.ru",
						companies.get(RANDOM.nextInt(companies.size())), Role.USER,
						"http://www.radfaces.com/images/avatars/oblina.jpg"),
				new User("User", "User", "user", passwordEncoder.encode("1111"), "testuser@mail.ru",
						companies.get(RANDOM.nextInt(companies.size())), Role.USER,
						"http://www.radfaces.com/images/avatars/chucky.jpg"),
				new User("Admin", "Admin", "admin", passwordEncoder.encode("1111"), "vcp.recovery.service@gmail.com",
						mainCompany, Role.ADMIN,
						"http://www.gravatar.com/avatar/262f8cc87f07aa61a71b4819d64fa092?d=mm"));
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
						"https://www.dropbox.com/s/a5rccntx3zlm7m2/6b57a75c-56f3-438c-8a83-f218d9df0a5f.jpg?dl=1",
						"https://www.dropbox.com/s/l8pg31iui1xujmb/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Lights%20Out.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Lunch Break",
						"Haven't you heard? It's lunchtime! Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/1lxew2a2s9c7i5h/5a42f996-1818-4d68-aa12-b8157b756c73.jpg?dl=1",
						"https://www.dropbox.com/s/u46lkuav8kbn3dn/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Lunch%20Break.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Magnetic Appeal",
						"Getting the right tool for the job to get the right tool for the job. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/mk1kvhc83eavzkn/8d0ff910-5af6-4f09-85a9-0c4cdb3758ce.jpg?dl=1",
						"https://www.dropbox.com/s/7h581qia29dk8my/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Magnetic%20Appeal.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Mind The Gap",
						"Taking bridge building to another dimension. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/sn6xueybg7bjn8m/1ea069ca-945c-4eea-b000-543b0cdd79ef.jpg?dl=1",
						"https://www.dropbox.com/s/jbk8i2f5cjs3st6/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Mind%20The%20Gap.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Nailed It!",
						"In this week’s episode: You two had ONE job! Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/rlve77xy1krmy83/7880c27e-8daa-4dec-8729-e976d9036132.jpg?dl=1",
						"https://www.dropbox.com/s/7yugw0fox28fbrc/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Nailed%20It%21.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Pile Up",
						"Infinite boxes. Limited intelligence. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/ytxgkgvacj53js4/98c57db2-d8e8-4d3f-824c-6833be9b185b.jpg?dl=1",
						"https://www.dropbox.com/s/fponu51u1zvt8n8/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Pile%20Up.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Porkatron",
						"Innovating the only way they know how: accidentally. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/dzxjkpqrroq3dmy/1e75665e-3f07-4ce3-b2e0-513f8d4f3fb7.jpg?dl=1",
						"https://www.dropbox.com/s/98hns1ufdiwrqb0/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Porkatron.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Predicament In Paint",
						"What not to do when painting a floor. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/btsi566nhydznzl/b22ee010-ee41-4781-8daf-25bd71cd9e1a.jpg?dl=1",
						"https://www.dropbox.com/s/9sgpogpvfnistwr/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Predicament%20In%20Paint.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Race Nut",
						"Don't knock it 'till you rocket. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/mj7w4ciye6ejc20/236191e3-c947-41d6-8e4c-7ac40210e63e.jpg?dl=1",
						"https://www.dropbox.com/s/m72ty0zqdhsmlq8/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Race%20Nut.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Screw Up",
						"Right attitude, wrong altitude. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/hwgo0qfafmj3mdr/222176bb-f280-404f-8938-6885138efa1e.jpg?dl=1",
						"https://www.dropbox.com/s/pb8f86of3mphhd0/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Screw%20Up.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Step 1",
						"Unclear instructions unclear, not instructional. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/1biu462pser000d/987eb8a0-55ae-41c9-a5f1-77c225131119.jpg?dl=1",
						"https://www.dropbox.com/s/wyu2m2l7xvrl4ri/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Step%201.mp4?dl=1"),
				new Video("Piggy Tales- Pigs at Work - Sticky Situation",
						"\"Stuck up\" takes on a new meaning. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/yhbng3537s3rjax/8e8e80cf-94f6-452b-b311-07baeec3538e.jpg?dl=1",
						"https://www.dropbox.com/s/fxmjtnn8rdosp60/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Sticky%20Situation.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Tipping Point",
						"Work-life balance, on another level. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/5nmwyf582tekmth/719876a4-0f5e-431e-90ab-0cb4dced769f.jpg?dl=1",
						"https://www.dropbox.com/s/7wcecz9eiw0nt06/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Tipping%20Point.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Unhinged",
						"An elaborate solution to every simple problem. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/ep9tiiy7qb59opb/514fd267-4e97-4440-aeae-3fa83a93a7d7.jpg?dl=1",
						"https://www.dropbox.com/s/c1n33ok33lk2wdk/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Unhinged.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - All Geared Up",
						"Quality control, in reverse. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/kufup53mnne1jw2/bc2a4d4e-c743-4514-a314-af801601feec.jpg?dl=1",
						"https://www.dropbox.com/s/jc7ga6832ub155a/Piggy%20Tales_%20Pigs%20at%20Work%20-%20All%20Geared%20Up.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Dream House",
						"The keystone to constructing the perfect home. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/ini4mji9mm1pm9y/2d96cd38-75c2-4a34-b648-9f0efd0e7011.jpg?dl=1",
						"https://www.dropbox.com/s/vmsp68e4arnk2bk/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Dream%20House.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Fabulous Fluke",
						"Hard work only goes so far. Sometimes you need a miracle. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/29hy12qitc1577l/0d79b105-6ce7-44f8-9b9c-68c77859baae.jpg?dl=1",
						"https://www.dropbox.com/s/jm4qu4lcut862xv/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Fabulous%20Fluke.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Get the Hammer",
						"Reach for sky, receive hammer. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/8cpo0ss7fpv12kj/7af9756a-795a-4d8d-accc-8e53220a8af9.jpg?dl=1",
						"https://www.dropbox.com/s/q03emm8w90fodc9/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Get%20the%20Hammer.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Grand Opening",
						"New! Cutting-edge! Groundbreaking! Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/7ujg3fteuf5d4ex/4dcf564c-026d-4c64-9295-6f5988d24f09.jpg?dl=1",
						"https://www.dropbox.com/s/4sxgtjs2iljk5nx/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Grand%20Opening.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Home Sweet Home",
						"Arriving at the perfect design is sometimes a matter of adjusting expectations. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/ulw76pdlwqk37iy/39d04f48-4019-4319-ab45-86c4be8be372.jpg?dl=1",
						"https://www.dropbox.com/s/mbv9y38ik0oql12/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Home%20Sweet%20Home.mp4?dl=1"),
				new Video("Piggy Tales - Pigs at Work - Jackhammered",
						"Hearing protection? Check. Common sense? Not so much. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
						"https://www.dropbox.com/s/ewz08rbm0gtqvp6/814c4557-5c6e-4d0a-93fa-ec06984df96f.jpg?dl=1",
						"https://www.dropbox.com/s/0cgvlgegso1gh7l/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Jackhammered.mp4?dl=1"),
				new Video("Piggy Tales - Snooze",
						"When a piggy starts snoring, what would a fellow pig do to make him stop? Anything, that’s what!",
						"https://www.dropbox.com/s/f2q979ivag2scld/4d85b66d-fdcd-463a-ad07-9bef747e0050.jpg?dl=1",
						"https://www.dropbox.com/s/ve2xb1hsgz9kyh2/Piggy%20Tales_%20%E2%80%9CSnooze%E2%80%9D.mp4?dl=1"),
				new Video("Piggy Tales - Superpork",
						"Stop that thief! Will SuperPork and his trusted sidekick save the day? Go SuperPork, go!",
						"https://www.dropbox.com/s/fuvnurzsow730j4/44aea589-f6c2-4104-91f1-8d0fc3190a67.jpg?dl=1",
						"https://www.dropbox.com/s/apjnejfjhn17i9u/Piggy%20Tales_%20%E2%80%9CSuperpork%E2%80%9D.mp4?dl=1"),
				new Video("Piggy Tales - Cake Duel",
						"Those hungry hogs love nothing more than pigging out on sweet treats, but what if there’s only one slice of cake?",
						"https://www.dropbox.com/s/zzaar7sqwga23ej/e7aca886-cb6f-4adb-bc00-6149141e5002.jpg?dl=1",
						"https://www.dropbox.com/s/es0proys1zmayug/Piggy%20Tales_%20%E2%80%9CCake%20Duel%E2%80%9D.mp4?dl=1"),
				new Video("Piggy Tales - Peekaboo!",
						"One little piggy wants to play hide and seek, but his porky friend doesn't seem to understand the rules! Or so it seems...",
						"https://www.dropbox.com/s/7ic2vehiqz96hfe/06e7e475-9820-420e-9d6c-87a2d1ef7423.jpg?dl=1",
						"https://www.dropbox.com/s/ozdx95ctmx4dxx1/Piggy%20Tales_%20Peekaboo%21.mp4?dl=1"),
				new Video("Piggy Tales - Piggy In The Middle",
						"Two piggies are playing with a soccer ball, but when another pig wants to join in they play keep away. When will it be his turn?",
						"https://www.dropbox.com/s/eirt5vw7ytmiowi/97a18739-5c37-4856-8e32-a75ec849fde6.jpg?dl=1",
						"https://www.dropbox.com/s/kc5pmypxkqjauhr/Piggy%20Tales_%20Piggy%20In%20The%20Middle.mp4?dl=1"),
				new Video("Piggy Tales - Puffed Up",
						"What happens when you hold your breath? One poor piggy is about to find out!",
						"https://www.dropbox.com/s/jdzo35r9rdef5c0/449a275c-af74-49d3-809c-88774d78b729.jpg?dl=1",
						"https://www.dropbox.com/s/z9j4oqhdlsrawbg/Piggy%20Tales_%20Puffed%20Up.mp4?dl=1"),
				new Video("Piggy Tales - Push-button Trouble",
						"Another day, another button! Why can't those pesky piggies just do as they're told? Being curious must be in their DNA!",
						"https://www.dropbox.com/s/q6ww1jgw37kba7u/e2c6162a-c73b-421d-8e09-4dd5331ef8d1.jpg?dl=1",
						"https://www.dropbox.com/s/3slegkdhz4tnkbl/Piggy%20Tales_%20Push-button%20Trouble.mp4?dl=1"),
				new Video("Piggy Tales - Push-button",
						"Put a big red button in front of a pig and he'll push it! This little piggy just can't resist pressing it -- even though he doesn't know what it does!",
						"https://www.dropbox.com/s/ygvihnnkz8yzr1a/9d3856d0-2f25-421c-a3d9-3e0ad1ab327c.jpg?dl=1",
						"https://www.dropbox.com/s/i4a8qr8kxfzjh9b/Piggy%20Tales_%20Push-button.mp4?dl=1"),
				new Video("Piggy Tales - Shazam",
						"Abracadabra! Pigs really shouldn't mess with magic, but when these two come across a magic wand it leaves them spellbound!",
						"https://www.dropbox.com/s/ndmez7trgg5qc9v/fff30a68-4705-4b01-9392-8ddccc0f0ee7.jpg?dl=1",
						"https://www.dropbox.com/s/bxgmg32v402m5gl/Piggy%20Tales_%20Shazam.mp4?dl=1"),
				new Video("Piggy Tales - Snowed Up - Holiday Special",
						"Yes, even our favorite porky friends celebrate this festive season by frolicking around in the snow. Just don’t confuse the frosty white stuff with ice-cream...",
						"https://www.dropbox.com/s/1802amt9rm6mvsb/c0d05f0c-13c3-4a08-91a6-8e52f26e91d0.jpg?dl=1",
						"https://www.dropbox.com/s/x7tbqwo6pea0ta4/Piggy%20Tales_%20Snowed%20Up%20-%20Holiday%20Special.mp4?dl=1"),
				new Video("Piggy Tales - Stuck In",
						"Though many tried for the sword with all their strength, none could move the sword nor stir it – but do we really want pigs running around with swords anyway?",
						"https://www.dropbox.com/s/h6y5nmbkhid4uxc/7f8fd04f-3672-46a4-8a39-32d1cc1b148e.jpg?dl=1",
						"https://www.dropbox.com/s/djms9hph6fufsop/Piggy%20Tales_%20Stuck%20In.mp4?dl=1"),
				new Video("Piggy Tales - Super Glue",
						"What's green, stupid, and sticky all over? See what happens when one dimwitted pig finds a tube of glue -- and just has to pick it up",
						"https://www.dropbox.com/s/lwp11owxl5zfdzr/56e7e6a4-961a-464d-b716-bc330ff66263.jpg?dl=1",
						"https://www.dropbox.com/s/m5vsgkcw6czuyqa/Piggy%20Tales_%20Super%20Glue.mp4?dl=1"),
				new Video("Piggy Tales - Teeter Trotter",
						"Two piggies are playing on a seesaw. They're trying to get it rocking, but one of the pigs is just too big! So how can they get that big piggy up in the air?",
						"https://www.dropbox.com/s/mie551ufqa9khah/64e4fa2e-a201-456d-82f8-1210f9d93475.jpg?dl=1",
						"https://www.dropbox.com/s/y1m0ssr2jc4qkrq/Piggy%20Tales_%20Teeter%20Trotter.mp4?dl=1"),
				new Video("Piggy Tales - The Catch",
						"Ice hole fishing is usually a relaxing hobby. But when an impatient piggy casts his line, there’s no telling what’s going to be the catch of the day.",
						"https://www.dropbox.com/s/6x2nw4sgev1il8s/0220321e-ea26-462a-b660-c4c45df29e2d.jpg?dl=1",
						"https://www.dropbox.com/s/vhjbfa5u8kpqkdn/Piggy%20Tales_%20The%20Catch.mp4?dl=1"),
				new Video("Piggy Tales - The Cure",
						"When a poor little piggy has a cold, his caring friend really wants to help. Maybe some medicine will make him feel better...",
						"https://www.dropbox.com/s/riyqwumzm1iu72e/eeb73bdd-b1e2-47bf-8de5-74560b4682bb.jpg?dl=1",
						"https://www.dropbox.com/s/sv65re2ucej3wp4/Piggy%20Tales_%20The%20Cure.mp4?dl=1"),
				new Video("Piggy Tales - The Game",
						"Get ready for a chess master class from two of the “smartest” piggies around. It’s the ultimate display of strategy, skill and patience.",
						"https://www.dropbox.com/s/h532y0zl52pskvm/572764ef-9ed2-4a92-9ef9-6d95733bb4c3.jpg?dl=1",
						"https://www.dropbox.com/s/u4mvfzdi8r0fwpr/Piggy%20Tales_%20The%20Game.mp4?dl=1"),
				new Video("Piggy Tales - The Hole",
						"Where's that breeze coming from? When two piggies come across a mysterious hole in the ground, they throw caution to the wind.",
						"https://www.dropbox.com/s/rxmda77ru0vmnvk/21912885-65c3-4e05-8ab3-a0e3fb881575.jpg?dl=1",
						"https://www.dropbox.com/s/9plw8zdsm5ysmcv/Piggy%20Tales_%20The%20Hole.mp4?dl=1"),
				new Video("Piggy Tales - The Mirror",
						"Mirror mirror on the wall, who's the porkiest of them all? This little pig enjoys checking himself out -- but is he really THAT good looking?",
						"https://www.dropbox.com/s/ied4l7u7dxmflzi/04e40b0f-baea-4ed0-bc22-d73cca6dbea2.jpg?dl=1",
						"https://www.dropbox.com/s/1icoqr3mo8mzo3u/Piggy%20Tales_%20The%20Mirror.mp4?dl=1"),
				new Video("Piggy Tales - The Wishing Well",
						"You know what they say – be careful what you wish for. Unfortunately for the pigs, being careful is just not in their DNA.",
						"https://www.dropbox.com/s/wq5gpkdu7awiebd/e7524de7-0f7d-4f46-86a1-5e8837a12414.jpg?dl=1",
						"https://www.dropbox.com/s/vio7pffch11nbdk/Piggy%20Tales_%20The%20Wishing%20Well.mp4?dl=1"),
				new Video("Piggy Tales - Trampoline",
						"In the first episode, a curious Piggy comes across a trampoline and can't resist jumping on it -- even though he's not supposed to...",
						"https://www.dropbox.com/s/ycaxj2druq7qtm2/640da080-93df-40f8-a195-85102f45868f.jpg?dl=1",
						"https://www.dropbox.com/s/ae6uuo1g2cn2j9f/Piggy%20Tales_%20Trampoline.mp4?dl=1"),
				new Video("Piggy Tales - Up Or Down",
						"When a busy little pig calls an elevator, it keeps him waiting – but what’s taking so long?",
						"https://www.dropbox.com/s/dcdsmgd91h2tsqp/2c7c96a0-a01f-4058-b65a-965b42dd1fdd.jpg?dl=1",
						"https://www.dropbox.com/s/86egw71gu7uj0yy/Piggy%20Tales_%20Up%20Or%20Down.mp4?dl=1"),
				new Video("Piggy Tales - Up The Tempo",
						"What’s that sound? Where is it coming from? And why is this piggy’s hammer making such funny noises? You’ll have to watch to find out!",
						"https://www.dropbox.com/s/aqk33ieyowag7cp/100842e3-5f26-4303-96ae-a45e66e7df2e.jpg?dl=1",
						"https://www.dropbox.com/s/0g0mn43bx110lbp/Piggy%20Tales_%20Up%20The%20Tempo.mp4?dl=1"),
				new Video("Piggy Tales - Abduction",
						"Take me to your leader! A surprise visit from an intergalactic explorer wipes the smile from one unsuspecting piggy's face...",
						"https://www.dropbox.com/s/60jnsqvv8muhoz9/ead57575-76a1-4ac5-a486-448ce2993ffd.jpg?dl=1",
						"https://www.dropbox.com/s/v5ngiwijbmmwa9e/Piggy%20Tales_%20Abduction.mp4?dl=1"),
				new Video("Piggy Tales - Epic Sir Bucket",
						"A bucket makes a perfect helmet for a crusading knight -- or at least this playful piggy thinks so! But what's that mysterious thing hanging from that balloon?",
						"https://www.dropbox.com/s/3354xius53rtkri/78e90e95-946e-4a0a-833b-b6bbf6f2c45f.jpg?dl=1",
						"https://www.dropbox.com/s/78pvartdt4ckp12/Piggy%20Tales_%20Epic%20Sir%20Bucket.mp4?dl=1"),
				new Video("Piggy Tales - Fly Piggy, Fly",
						"Those piggies may not be very smart, but they sure are determined! This inventive piggy just won’t give up trying to get his machine up in the air!",
						"https://www.dropbox.com/s/9bq8vsxa44316kq/8f82d62a-b6bb-46bd-b948-8c558714706c.jpg?dl=1",
						"https://www.dropbox.com/s/43twnz1mkjv9gm1/Piggy%20Tales_%20Fly%20Piggy%2C%20Fly.mp4?dl=1"),
				new Video("Piggy Tales - Gloves",
						"Piggies don’t have hands! So when they come across a pair of gloves they have no idea what they’re for.",
						"https://www.dropbox.com/s/i4p7hs4avsdbrqj/5f0da2ad-c9b9-4234-ade5-72b980d19dfb.jpg?dl=1",
						"https://www.dropbox.com/s/asul4pkvd7akz1b/Piggy%20Tales_%20Gloves.mp4?dl=1"),
				new Video("Piggy Tales - Hog Hoops",
						"Join us courtside as one athletic piggy shows off his ball skillz! Hopefully nothing puts him off his aim...",
						"https://www.dropbox.com/s/9vu0l00ijysc92h/157578bd-4bf4-4406-a614-07edbdfea858.jpg?dl=1",
						"https://www.dropbox.com/s/f5r0xm6iemp8v5g/Piggy%20Tales_%20Hog%20Hoops.mp4?dl=1"),
				new Video("Piggy Tales - It's a wrap",
						"You know that feeling when a super fun party is over and it's time to clean up everything and go home? Well, I guess even piggies can get a bit emotional at times like that.",
						"https://www.dropbox.com/s/drnigpyj305n27t/67e651cf-b519-42f5-9ebd-6071aa4772f7.jpg?dl=1",
						"https://www.dropbox.com/s/lw47pzl2l76dsnw/Piggy%20Tales_%20It%27s%20a%20wrap.mp4?dl=1"),
				new Video("Piggy Tales - Jammed",
						"What food do piggies love almost as much as eggs? When a couple of hungry hogs find a jam jar, they’ll do anything to get it open.",
						"https://www.dropbox.com/s/ajud1xbwc21tjka/71924b02-3707-4a89-865c-4880441777cf.jpg?dl=1",
						"https://www.dropbox.com/s/rnv5f0bt2pr80tx/Piggy%20Tales_%20Jammed.mp4?dl=1"));
		};

}
