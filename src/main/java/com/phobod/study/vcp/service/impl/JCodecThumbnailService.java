package com.phobod.study.vcp.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.exception.CantProcessMediaContentException;
import com.phobod.study.vcp.service.ThumbnailService;

@Service
public class JCodecThumbnailService implements ThumbnailService{
	@Override
	public byte[] createThumbnail(Path videoFilePath) {
		try {
			return createThumbnailInternal(videoFilePath);
		} catch (IOException | JCodecException e) {
			throw new CantProcessMediaContentException("Create thumbnail failed: " + e.getMessage(), e);
		} 
	}

	private byte[] createThumbnailInternal(Path videoFilePath) throws IOException, JCodecException{
		Picture nativeFrame = getVideoFrameBySecond(videoFilePath, 0);
		if (nativeFrame == null) {
			throw new CantProcessMediaContentException("Can't create thumbnail for file " + videoFilePath.getFileName());
		}
		BufferedImage img = AWTUtil.toBufferedImage(nativeFrame);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(img, "jpg", out);
		return out.toByteArray();
	}

	private Picture getVideoFrameBySecond(Path videoFilePath, double second) throws IOException, JCodecException{
		FrameGrab grab = new FrameGrab(new FileChannelWrapper(FileChannel.open(videoFilePath)));
		return grab.seekToSecondPrecise(second).getNativeFrame();
	}

}
