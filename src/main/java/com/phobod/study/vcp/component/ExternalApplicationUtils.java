package com.phobod.study.vcp.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalApplicationUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExternalApplicationUtils.class);
	
	public static String execution(ProcessBuilder pb) throws IOException, InterruptedException {
		StringBuffer sb = new StringBuffer();
		pb.redirectErrorStream(true);
        Process p = null;
        try {
			p = pb.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				LOGGER.debug(line);
				sb.append(line);
			}
			LOGGER.debug("wait over " + p.waitFor());
			return sb.toString();
		} finally {
			if (p != null) {
				p.destroy();
			}
		}
	}


}
