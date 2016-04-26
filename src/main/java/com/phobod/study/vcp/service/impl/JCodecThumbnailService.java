package com.phobod.study.vcp.service.impl;

import java.awt.Graphics2D;
import java.awt.Image;
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
		BufferedImage originalImage = AWTUtil.toBufferedImage(nativeFrame);
		BufferedImage changedImage = resizeThubnail(originalImage, 640, 360);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(changedImage, "jpg", out);
		return out.toByteArray();
	}

	private BufferedImage resizeThubnail(BufferedImage originalImage, int width, int height) {
		Image image = originalImage.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
        BufferedImage changedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = changedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
		return changedImage;
	}

	private Picture getVideoFrameBySecond(Path videoFilePath, double second) throws IOException, JCodecException{
		FrameGrab grab = new FrameGrab(new FileChannelWrapper(FileChannel.open(videoFilePath)));
		return grab.seekToSecondPrecise(second).getNativeFrame();
	}

}
