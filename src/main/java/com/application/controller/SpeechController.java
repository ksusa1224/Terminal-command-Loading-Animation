package com.application.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpeechController {
	
	@Value("${settings.SPEECH_DATA_FOLDER_PATH}")
	private String SPEECH_DATA_FOLDER_PATH;

	/**
	 * スピーチデータをtomcat配下ではない場所から取得する
	 * @param filename
	 * @param res
	 */
	@RequestMapping("/speech/{filename:.+}")
	public void userImage(@PathVariable String filename, HttpServletResponse res){
		res.setHeader("Cache-Control", "no-store");
		res.setHeader("Pragma", "no-cache");
		res.setDateHeader("Expires", 0);
		File file = new File(SPEECH_DATA_FOLDER_PATH + filename);
		if(file.exists())
		{
			try(FileInputStream fis = new FileInputStream(file)){
				FileCopyUtils.copy(fis, res.getOutputStream());
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}
}
