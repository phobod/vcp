package com.phobod.study.vcp.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
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
			return createThumbnailInternal(videoFilePath);
		} catch (IOException | JCodecException e) {
			throw new ApplicationException("Create thumbnail failed: " + e.getMessage(), e);
		} 
	}

	private String createThumbnailInternal(Path videoFilePath) throws IOException, JCodecException, FileNotFoundException {
		Picture nativeFrame = getVideoFrameBySecond(videoFilePath, 0);
		if (nativeFrame == null) {
			throw new ApplicationException("Can't create thumbnail for file " + videoFilePath.getFileName());
		}
		String uniqueThumbnailFileName = generateUniqueThumbnailFileName();
		saveThumbnailFileToDisk(nativeFrame, uniqueThumbnailFileName);
		LOGGER.info("Created thumbnail for video {}", videoFilePath.getFileName());
		return "/media/thumbnail/" + uniqueThumbnailFileName;
	}

	private void saveThumbnailFileToDisk(Picture nativeFrame, String uniqueThumbnailFileName) throws IOException {
		BufferedImage img = AWTUtil.toBufferedImage(nativeFrame);
		ImageIO.write(img, "jpg", new File(mediaDir + "/thumbnail", uniqueThumbnailFileName));
	}
	
	private String generateUniqueThumbnailFileName() {
		return UUID.randomUUID() + ".jpg";
	}

	private Picture getVideoFrameBySecond(Path videoFilePath, double second) throws IOException, JCodecException, FileNotFoundException {
		FrameGrab grab = new FrameGrab(new FileChannelWrapper(FileChannel.open(videoFilePath)));
		return grab.seekToSecondPrecise(second).getNativeFrame();
	}

}
