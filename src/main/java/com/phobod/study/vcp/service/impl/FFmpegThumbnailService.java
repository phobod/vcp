package com.phobod.study.vcp.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.component.ExternalApplicationUtils;
import com.phobod.study.vcp.exception.CantProcessMediaContentException;
import com.phobod.study.vcp.service.ThumbnailService;

@Service("ffmpegThumbnailService")
public class FFmpegThumbnailService implements ThumbnailService{
	private static final Logger LOGGER = LoggerFactory.getLogger(FFmpegThumbnailService.class);
	
	@Value("${ffmpeg}")
	private String ffmpeg;
	
	@Value("${ffprobe}")
	private String ffprobe;
	
	@Override
	public byte[] createThumbnail(Path videoFilePath) throws CantProcessMediaContentException {
		try {
            return createThumbnailInternal(videoFilePath);
        } catch (IOException | InterruptedException | NumberFormatException e) {
            throw new CantProcessMediaContentException("Create thumbnail failed: " + e.getMessage(), e);
        }
	}

    private byte[] createThumbnailInternal(Path videoFilePath) throws IOException, InterruptedException, CantProcessMediaContentException {
    	if (videoFilePath == null) {
    		throw new CantProcessMediaContentException("Create thumbnail failed. Video file path is Null");
		}
        int duration = getDuration(videoFilePath.toString());
        return getThumbnailBySecond(duration / 2, videoFilePath.toString());
    }

    private int getDuration(String filename) throws IOException, NumberFormatException, InterruptedException{
        ProcessBuilder pb = new ProcessBuilder(ffprobe, "-v", "error", "-show_entries", "format=duration", "-of", "default=noprint_wrappers=1:nokey=1", filename);     
        String duration = ExternalApplicationUtils.execution(pb);
        if (duration != null) {
        	return Double.valueOf(duration).intValue();
        }
        return 0;
    }

    private byte[] getThumbnailBySecond(int second, String filename) throws CantProcessMediaContentException {
        Path tempProcessedImagePath = generateUniqueTempFilePath();
        try {
            createTempThumbnail(second, filename, tempProcessedImagePath);
            ByteArrayOutputStream out = getThumbnailFromTempFile(tempProcessedImagePath);
            return out.toByteArray();
		} catch (Exception e) {
			throw new CantProcessMediaContentException("Create thumbnail failed: " + e.getMessage(), e);
		} finally {
	        if (tempProcessedImagePath != null) {
	            try {
	                Files.deleteIfExists(tempProcessedImagePath);
	            } catch (Exception e) {
	            	LOGGER.warn("Can't remove temp file: " + tempProcessedImagePath, e);
	            }
	        }
		}
    }

	private Path generateUniqueTempFilePath() {
		return Paths.get(System.getProperty("java.io.tmpdir") + "processed" + UUID.randomUUID() + ".jpg");
	}

	private void createTempThumbnail(int second, String filename, Path tempProcessedImagePath) throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder(ffmpeg, "-i", filename, "-s", "640X360", "-ss", String.valueOf(second), "-vcodec", "mjpeg", "-vframes", "1", "-f", "image2", tempProcessedImagePath.toString());
        ExternalApplicationUtils.execution(pb);
	}
    
	private ByteArrayOutputStream getThumbnailFromTempFile(Path tempProcessedImagePath) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedImage img = ImageIO.read(tempProcessedImagePath.toFile());
        ImageIO.write(img, "jpg", out);
		return out;
	}

}
