package com.phobod.study.vcp.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import com.phobod.study.vcp.exception.CantProcessUserException;
import com.phobod.study.vcp.service.AvatarService;

@Service
public class GravatarAvatarService implements AvatarService {
	
	@Override
	public String generateAvatarUrl(String email) {
		try {
			return generateAvatarUrlInternal(email); 
		} catch (CantProcessUserException e) {
			throw new CantProcessUserException("Can't hash email for avatar url: " + e.getMessage(), e);
		}
	}

	private String generateAvatarUrlInternal(String email) {
		if (email == null) {
			throw new CantProcessUserException("Can't hash email for avatar url. Email is Null");
		}
		String hash = md5Hex(prepareEmail(email));
		return "http://www.gravatar.com/avatar/" + hash + "?d=mm";
	}

	private String prepareEmail(String email){
		return email.trim().toLowerCase();
	}
	
	private String md5Hex(String message) throws CantProcessUserException{
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return hex(md.digest(message.getBytes("CP1252")));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e){
			throw new CantProcessUserException("Can't hash email for avatar url: " + e.getMessage(), e);
		}
	}

	private String hex(byte[] array) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; ++i) {
			sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString();
	}

}
