package com.common;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtil {

	/**
	 * 画像を指定の幅にリサイズ後、指定の高さ分上部を切り出す
	 * @param width リサイズする幅
	 * @param height トリムする高さ
	 * @param srcImgPath　元画像のパス
	 * @param distImgPath　処理後の画像パス
	 * @param extension　拡張子
	 */
	public static void trim(int width, int height, String srcImgPath, String distImgPath, String extension) {
	    try {
	    	File srcImg = new File(srcImgPath);
	    	if (!srcImg.exists())
	    	{
	    		return;
	    	}
			BufferedImage image = ImageIO.read(srcImg);
		    Dimension imgSize = new Dimension(image.getWidth(),image.getHeight());
		    Dimension newImgSize = getScaledDimension(imgSize, new Dimension(480,355));
		    int resize_width = (int)newImgSize.getWidth();
		    int resize_height = (int)newImgSize.getHeight();
		    image = resize(image, resize_width, resize_height);
		    if (resize_height >= height)
		    {
		    	// トリミング
		    	image = image.getSubimage(0, 0, width, height);
		    }
		    File outputfile = new File(distImgPath);
		    ImageIO.write(image, extension, outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

	    int original_width = imgSize.width;
	    int original_height = imgSize.height;
	    int bound_width = boundary.width;
	    int new_width = original_width;
	    int new_height = original_height;

	    if (original_width > bound_width) {
	        new_width = bound_width;
	        new_height = (new_width * original_height) / original_width;
	    }

	    return new Dimension(new_width, new_height);
	}
	
	public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, img.getType());

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}	
}
