package com.phobod.study.vcp.generator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.phobod.study.vcp.config.MongoConfig;
import com.phobod.study.vcp.domain.Company;
import com.phobod.study.vcp.domain.Role;
import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.domain.Video;

public class TestDataGenerator {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestDataGenerator.class);
	private static final String[] VIDEO_URLS = {
			"https://www.dropbox.com/s/gaqcdten0i3c57w/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Lights%20Out.mp4?dl=1",
			"https://www.dropbox.com/s/g81nkv1b48eixpx/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Lunch%20Break.mp4?dl=1",
			"https://www.dropbox.com/s/bhn1gprxo4o3uv3/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Magnetic%20Appeal.mp4?dl=1",
			"https://www.dropbox.com/s/z5vfoihiv2jc648/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Mind%20The%20Gap.mp4?dl=1",
			"https://www.dropbox.com/s/1ynivda3jjamfsn/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Nailed%20It%21.mp4?dl=1",
			"https://www.dropbox.com/s/h6ge9qbjwejz9i8/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Pile%20Up.mp4?dl=1",
			"https://www.dropbox.com/s/69451wieudpwkky/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Porkatron.mp4?dl=1",
			"https://www.dropbox.com/s/ddy406xl7csgaaz/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Predicament%20In%20Paint.mp4?dl=1",
			"https://www.dropbox.com/s/0dw6zq413vauyo2/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Race%20Nut.mp4?dl=1",
			"https://www.dropbox.com/s/okddb07155u4qb6/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Screw%20Up.mp4?dl=1",
			"https://www.dropbox.com/s/2hpd6wlk4ja2rgs/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Step%201.mp4?dl=1",
			"https://www.dropbox.com/s/b978s1jkr3ypp9g/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Sticky%20Situation.mp4?dl=1",
			"https://www.dropbox.com/s/0tmx1kkwcggaaa6/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Tipping%20Point.mp4?dl=1",
			"https://www.dropbox.com/s/v65kwhrbxv7n0md/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Unhinged.mp4?dl=1",
			"https://www.dropbox.com/s/i56jv70l8r6yg1b/Piggy%20Tales_%20Pigs%20at%20Work%20-%20All%20Geared%20Up.mp4?dl=1",
			"https://www.dropbox.com/s/zjthm1prvyoi2cq/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Dream%20House.mp4?dl=1",
			"https://www.dropbox.com/s/s6dqyvzju7yanti/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Fabulous%20Fluke.mp4?dl=1",
			"https://www.dropbox.com/s/z6249qva9rpaa6i/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Get%20the%20Hammer.mp4?dl=1",
			"https://www.dropbox.com/s/ocejq5ektyher3t/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Grand%20Opening.mp4?dl=1",
			"https://www.dropbox.com/s/0nmxbr1y0zc7bzr/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Home%20Sweet%20Home.mp4?dl=1",
			"https://www.dropbox.com/s/275kpm9a4fnnpw1/Piggy%20Tales_%20Pigs%20at%20Work%20-%20Jackhammered.mp4?dl=1",
			"https://www.dropbox.com/s/dg03846a9ni2ge5/Piggy%20Tales_%20%E2%80%9CSnooze%E2%80%9D.mp4?dl=1",
			"https://www.dropbox.com/s/mflzk4pdcf1txik/Piggy%20Tales_%20%E2%80%9CSuperpork%E2%80%9D.mp4?dl=1",
			"https://www.dropbox.com/s/m9mk62i1jw2otet/Piggy%20Tales_%20%E2%80%9CCake%20Duel%E2%80%9D.mp4?dl=1",
			"https://www.dropbox.com/s/bb0dm1qphbx6pc3/Piggy%20Tales_%20Peekaboo%21.mp4?dl=1",
			"https://www.dropbox.com/s/qx2c4jnpxaahigm/Piggy%20Tales_%20Piggy%20In%20The%20Middle.mp4?dl=1",
			"https://www.dropbox.com/s/emp5sk8lwptiw0j/Piggy%20Tales_%20Puffed%20Up.mp4?dl=1",
			"https://www.dropbox.com/s/m1ko7ginibbt5iu/Piggy%20Tales_%20Push-button%20Trouble.mp4?dl=1",
			"https://www.dropbox.com/s/m5mpue7jug3d0ls/Piggy%20Tales_%20Push-button.mp4?dl=1",
			"https://www.dropbox.com/s/8mz0zrduvzcmhvh/Piggy%20Tales_%20Shazam.mp4?dl=1",
			"https://www.dropbox.com/s/4dz7v8cxybn0g9l/Piggy%20Tales_%20Snowed%20Up%20-%20Holiday%20Special.mp4?dl=1",
			"https://www.dropbox.com/s/63lw7qtg4lqe1ij/Piggy%20Tales_%20Stuck%20In.mp4?dl=1",
			"https://www.dropbox.com/s/1szkoovyustlwpc/Piggy%20Tales_%20Super%20Glue.mp4?dl=1",
			"https://www.dropbox.com/s/6z0vrgw23amy7a4/Piggy%20Tales_%20Teeter%20Trotter.mp4?dl=1",
			"https://www.dropbox.com/s/yiza1abnhjbtrmb/Piggy%20Tales_%20The%20Catch.mp4?dl=1",
			"https://www.dropbox.com/s/av9glukct668jf6/Piggy%20Tales_%20The%20Cure.mp4?dl=1",
			"https://www.dropbox.com/s/rs7o8vzw1wnjjx6/Piggy%20Tales_%20The%20Game.mp4?dl=1",
			"https://www.dropbox.com/s/1hi7o4snvytmaul/Piggy%20Tales_%20The%20Hole.mp4?dl=1",
			"https://www.dropbox.com/s/zgr5kf5zio2jvzm/Piggy%20Tales_%20The%20Mirror.mp4?dl=1",
			"https://www.dropbox.com/s/wwht97adf9x8uhi/Piggy%20Tales_%20The%20Wishing%20Well.mp4?dl=1",
			"https://www.dropbox.com/s/g3tc26khq11fxv6/Piggy%20Tales_%20Trampoline.mp4?dl=1",
			"https://www.dropbox.com/s/o5qm1b6bcscu94t/Piggy%20Tales_%20Up%20Or%20Down.mp4?dl=1",
			"https://www.dropbox.com/s/kslu0upaqxh9zd6/Piggy%20Tales_%20Up%20The%20Tempo.mp4?dl=1",
			"https://www.dropbox.com/s/36nfocn207c3p0u/Piggy%20Tales_%20Abduction.mp4?dl=1",
			"https://www.dropbox.com/s/kd4b9y4daisq4og/Piggy%20Tales_%20Epic%20Sir%20Bucket.mp4?dl=1",
			"https://www.dropbox.com/s/quwf1agbbvnbril/Piggy%20Tales_%20Fly%20Piggy%2C%20Fly.mp4?dl=1",
			"https://www.dropbox.com/s/lwe3cy013w03ejk/Piggy%20Tales_%20Gloves.mp4?dl=1",
			"https://www.dropbox.com/s/grj8a6zaklixw5w/Piggy%20Tales_%20Hog%20Hoops.mp4?dl=1",
			"https://www.dropbox.com/s/bsvxjn2j1t056j9/Piggy%20Tales_%20It%27s%20a%20wrap.mp4?dl=1",
			"https://www.dropbox.com/s/6n7ggxo51vs8s3p/Piggy%20Tales_%20Jammed.mp4?dl=1" };

	private static final String[] VIDEO_TITLES = { "Piggy Tales - Pigs at Work - Lights Out",
			"Piggy Tales - Pigs at Work - Lunch Break", "Piggy Tales - Pigs at Work - Magnetic Appeal",
			"Piggy Tales - Pigs at Work - Mind The Gap", "Piggy Tales - Pigs at Work - Nailed It!",
			"Piggy Tales - Pigs at Work - Pile Up", "Piggy Tales - Pigs at Work - Porkatron",
			"Piggy Tales - Pigs at Work - Predicament In Paint", "Piggy Tales - Pigs at Work - Race Nut",
			"Piggy Tales - Pigs at Work - Screw Up", "Piggy Tales - Pigs at Work - Step 1",
			"Piggy Tales- Pigs at Work - Sticky Situation", "Piggy Tales - Pigs at Work - Tipping Point",
			"Piggy Tales - Pigs at Work - Unhinged", "Piggy Tales - Pigs at Work - All Geared Up",
			"Piggy Tales - Pigs at Work - Dream House", "Piggy Tales - Pigs at Work - Fabulous Fluke",
			"Piggy Tales - Pigs at Work - Get the Hammer", "Piggy Tales - Pigs at Work - Grand Opening",
			"Piggy Tales - Pigs at Work - Home Sweet Home", "Piggy Tales - Pigs at Work - Jackhammered",
			"Piggy Tales - Snooze", "Piggy Tales - Superpork", "Piggy Tales - Cake Duel", "Piggy Tales - Peekaboo!",
			"Piggy Tales - Piggy In The Middle", "Piggy Tales - Puffed Up", "Piggy Tales - Push-button Trouble",
			"Piggy Tales - Push-button", "Piggy Tales - Shazam", "Piggy Tales - Snowed Up - Holiday Special",
			"Piggy Tales - Stuck In", "Piggy Tales - Super Glue", "Piggy Tales - Teeter Trotter",
			"Piggy Tales - The Catch", "Piggy Tales - The Cure", "Piggy Tales - The Game", "Piggy Tales - The Hole",
			"Piggy Tales - The Mirror", "Piggy Tales - The Wishing Well", "Piggy Tales - Trampoline",
			"Piggy Tales - Up Or Down", "Piggy Tales - Up The Tempo", "Piggy Tales - Abduction",
			"Piggy Tales - Epic Sir Bucket", "Piggy Tales - Fly Piggy, Fly", "Piggy Tales - Gloves",
			"Piggy Tales - Hog Hoops", "Piggy Tales - It's a wrap", "Piggy Tales - Jammed" };

	private static final String[] VIDEO_DESCRIPTION = {
			"How many pigs does it take to screw in a lightbulb? We may never find out. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"Haven't you heard? It's lunchtime! Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"Getting the right tool for the job to get the right tool for the job. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"Taking bridge building to another dimension. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"In this week’s episode: You two had ONE job! Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"Infinite boxes. Limited intelligence. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"Innovating the only way they know how: accidentally. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"What not to do when painting a floor. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"Don't knock it 'till you rocket. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"Right attitude, wrong altitude. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"Unclear instructions unclear, not instructional. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"\"Stuck up\" takes on a new meaning. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"Work-life balance, on another level. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"An elaborate solution to every simple problem. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"Quality control, in reverse. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"The keystone to constructing the perfect home. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"Hard work only goes so far. Sometimes you need a miracle. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"Reach for sky, receive hammer. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"New! Cutting-edge! Groundbreaking! Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"Arriving at the perfect design is sometimes a matter of adjusting expectations. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"Hearing protection? Check. Common sense? Not so much. Introducing a Piggy Tales series, \"Pigs at work\". Piggy Tales gives you a glimpse into the work lives of our favorite green minions.",
			"When a piggy starts snoring, what would a fellow pig do to make him stop? Anything, that’s what!",
			"Stop that thief! Will SuperPork and his trusted sidekick save the day? Go SuperPork, go!",
			"Those hungry hogs love nothing more than pigging out on sweet treats, but what if there’s only one slice of cake?",
			"One little piggy wants to play hide and seek, but his porky friend doesn't seem to understand the rules! Or so it seems...",
			"Two piggies are playing with a soccer ball, but when another pig wants to join in they play keep away. When will it be his turn?",
			"What happens when you hold your breath? One poor piggy is about to find out!",
			"Another day, another button! Why can't those pesky piggies just do as they're told? Being curious must be in their DNA!",
			"Put a big red button in front of a pig and he'll push it! This little piggy just can't resist pressing it -- even though he doesn't know what it does!",
			"Abracadabra! Pigs really shouldn't mess with magic, but when these two come across a magic wand it leaves them spellbound!",
			"Yes, even our favorite porky friends celebrate this festive season by frolicking around in the snow. Just don’t confuse the frosty white stuff with ice-cream...",
			"Though many tried for the sword with all their strength, none could move the sword nor stir it – but do we really want pigs running around with swords anyway?",
			"What's green, stupid, and sticky all over? See what happens when one dimwitted pig finds a tube of glue -- and just has to pick it up",
			"Two piggies are playing on a seesaw. They're trying to get it rocking, but one of the pigs is just too big! So how can they get that big piggy up in the air?",
			"Ice hole fishing is usually a relaxing hobby. But when an impatient piggy casts his line, there’s no telling what’s going to be the catch of the day.",
			"When a poor little piggy has a cold, his caring friend really wants to help. Maybe some medicine will make him feel better...",
			"Get ready for a chess master class from two of the “smartest” piggies around. It’s the ultimate display of strategy, skill and patience.",
			"Where's that breeze coming from? When two piggies come across a mysterious hole in the ground, they throw caution to the wind.",
			"Mirror mirror on the wall, who's the porkiest of them all? This little pig enjoys checking himself out -- but is he really THAT good looking?",
			"You know what they say – be careful what you wish for. Unfortunately for the pigs, being careful is just not in their DNA. ",
			"In the first episode, a curious Piggy comes across a trampoline and can't resist jumping on it -- even though he's not supposed to...",
			"When a busy little pig calls an elevator, it keeps him waiting – but what’s taking so long?",
			"What’s that sound? Where is it coming from? And why is this piggy’s hammer making such funny noises? You’ll have to watch to find out!",
			"Take me to your leader! A surprise visit from an intergalactic explorer wipes the smile from one unsuspecting piggy's face...",
			"A bucket makes a perfect helmet for a crusading knight -- or at least this playful piggy thinks so! But what's that mysterious thing hanging from that balloon?",
			"Those piggies may not be very smart, but they sure are determined! This inventive piggy just won’t give up trying to get his machine up in the air!",
			"Piggies don’t have hands! So when they come across a pair of gloves they have no idea what they’re for.",
			"Join us courtside as one athletic piggy shows off his ball skillz! Hopefully nothing puts him off his aim...",
			"You know that feeling when a super fun party is over and it's time to clean up everything and go home? Well, I guess even piggies can get a bit emotional at times like that.",
			"What food do piggies love almost as much as eggs? When a couple of hungry hogs find a jam jar, they’ll do anything to get it open." };
	private static final Random RANDOM = new Random();

	public static void main(String[] args) throws Exception {
		try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()) {
			ctx.register(MongoConfig.class);
			ctx.refresh();

			MongoOperations mongoOps = ctx.getBean(MongoOperations.class);

			mongoOps.remove(new Query(), User.class);
			mongoOps.remove(new Query(), Company.class);
			mongoOps.remove(new Query(), Video.class);

			clearMediaSubFolders();

			List<Company> companies = buildCompanies();
			for (Company company : companies) {
				mongoOps.insert(company);
			}
			List<User> users = buildUsers(companies);
			for (User user : users) {
				mongoOps.insert(user);
			}
			List<Video> videos = buildVideos(users);
			mongoOps.insert(videos, Video.class);
			addVideosToUsers(users, videos);
			for (User user : users) {
				Update update = new Update().set("videos", user.getVideos());
				mongoOps.updateFirst(Query.query(Criteria.where("id").is(user.getId())), update, User.class);
			}

			LOGGER.info("Insert completed successful");
		}
	}

	private static void clearMediaSubFolders() {
		for (File f : new File("src/main/webapp/media/thumbnail").listFiles()) {
			f.delete();
		}
		for (File f : new File("src/main/webapp/media/video").listFiles()) {
			f.delete();
		}
		LOGGER.info("Media sub folders cleared");
	}

	private static List<Company> buildCompanies() {
		return Arrays.asList(new Company("Coca-Cola", "Atlanta, USA", "info@coca-cola.com", "+1-800-438-2653"),
				new Company("Microsoft", "Redmond, USA", "info@microsoft.com", "+1-800-882-8080"),
				new Company("Google", "Mountain View, USA", "info@google.com", "+1-650-253-0000"));
	}

	private static List<User> buildUsers(List<Company> companies) {
		return Arrays.asList(
				new User("Tom", "Anderson", "Admin01", "sjdSDb34", "tomas@mail.ru",
						companies.get(RANDOM.nextInt(companies.size())), Role.ADMIN,
						"http://www.radfaces.com/images/avatars/ickis.jpg"),
				new User("Jack", "Douu", "jactin", "qwerty123", "jactin@mail.ru",
						companies.get(RANDOM.nextInt(companies.size())), Role.USER,
						"http://www.radfaces.com/images/avatars/krumm.jpg"),
				new User("Rachel", "Stone", "roston", "sdfvsm2d", "roston@mail.ru",
						companies.get(RANDOM.nextInt(companies.size())), Role.USER,
						"http://www.radfaces.com/images/avatars/jane-lane.jpg"),
				new User("Steve", "Macleod", "duncan", "lsdb2HG", "duncan@mail.ru",
						companies.get(RANDOM.nextInt(companies.size())), Role.USER,
						"http://www.radfaces.com/images/avatars/oblina.jpg"));
	}

	private static List<Video> buildVideos(List<User> users) throws IOException, JCodecException {
		List<Video> videos = new ArrayList<Video>();
		int count = 0;
		for (String videoUrl : VIDEO_URLS) {
			String uid = UUID.randomUUID().toString() + ".mp4";
			File destVideo = new File("src/main/webapp/media/video", uid);
			try (InputStream in = new URL(videoUrl).openStream()) {
				Files.copy(in, Paths.get(destVideo.getAbsolutePath()));
			}
			double sec = count > 20 ? 3d : 6d;
			videos.add(new Video(VIDEO_TITLES[count], VIDEO_DESCRIPTION[count], createThumbnail(destVideo,sec),
					"/media/video/" + uid, RANDOM.nextInt(1000), users.get(RANDOM.nextInt(users.size()))));
			count++;
			LOGGER.info("Video {} processed", destVideo.getName());
		}
		return videos;
	}

	private static String createThumbnail(File destVideo, double sec) throws IOException, JCodecException {
		String thumbnail = null;
		FrameGrab grab = new FrameGrab(
				new FileChannelWrapper(FileChannel.open(Paths.get(destVideo.getAbsolutePath()))));
		Picture frame = grab.seekToSecondPrecise(sec).getNativeFrame();
		if (frame != null) {
			BufferedImage img = AWTUtil.toBufferedImage(frame);
			String uid = UUID.randomUUID() + ".jpg";
			ImageIO.write(img, "jpg", new File("src/main/webapp/media/thumbnail", uid));
			thumbnail = "/media/thumbnail/" + uid;
		}
		LOGGER.info("Created thumbnail for video {}", destVideo.getName());
		return thumbnail;
	}

	private static void addVideosToUsers(List<User> users, List<Video> videos) {
		for (Video video : videos) {
			for (User user : users) {
				if (user.equals(video.getOwner())) {
					user.addVideo(video);
				}
			}
		}
	}

}
