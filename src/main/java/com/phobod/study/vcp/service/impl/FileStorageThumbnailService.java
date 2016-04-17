package com.phobod.study.vcp.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.exception.ApplicationException;
import com.phobod.study.vcp.service.ThumbnailService;

@Service
public class FileStorageThumbnailService implements ThumbnailService{
	private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageThumbnailService.class);

	@Value("${media.dir}")
	private String mediaDir;
	
	@Override
	public String createThumbnail(Path videoFilePath) {
		try {
			String thumbnail = null;
			FrameGrab grab = new FrameGrab(
					new FileChannelWrapper(FileChannel.open(videoFilePath)));
			int sec = new Random().nextInt(3) + 3;
			Picture frame = grab.seekToSecondPrecise(sec).getNativeFrame();
			if (frame != null) {
				BufferedImage img = AWTUtil.toBufferedImage(frame);
				String uid = UUID.randomUUID() + ".jpg";
				ImageIO.write(img, "jpg", new File(mediaDir + "/thumbnail", uid));
				thumbnail = "/media/thumbnail/" + uid;
			}
			LOGGER.info("Created thumbnail for video {}", videoFilePath.getFileName());
			return thumbnail;
		} catch (IOException | JCodecException e) {
			throw new ApplicationException("Create thumbnail failed: " + e.getMessage(), e);
		} 
	}

}
