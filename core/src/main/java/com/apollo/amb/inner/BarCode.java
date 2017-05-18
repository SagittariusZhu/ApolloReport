package com.apollo.amb.inner;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class BarCode {

	public static byte[] createBarCode(String code) {
		byte[] arr = null;

		try {
			//Create the barcode bean
			Code39Bean bean = new Code39Bean();

			final int dpi = 150;

			//Configure the barcode generator
			bean.setModuleWidth(UnitConv.in2mm(1.0f / dpi)); //makes the narrow bar 
			//width exactly one pixel
			bean.setWideFactor(3);
			bean.doQuietZone(false);

			//Output to byteArray
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				//Set up the canvas provider for monochrome JPEG output 
				BitmapCanvasProvider canvas = new BitmapCanvasProvider(
						out, "image/png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

				//Generate the barcode
				bean.generateBarcode(canvas, code);

				//Signal end of generation
				canvas.finish();
			} finally {
			}

			arr = out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return arr;
	}
	
	public static String bytesToHexString(byte[] src){  
	    StringBuilder stringBuilder = new StringBuilder("");  
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i = 0; i < src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);  
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    return stringBuilder.toString();  
	}

	public static void saveToFile(byte[] image, String path) {  
		FileOutputStream out = null;
		try {  
			try {
				out = new FileOutputStream(path);
				out.write(bytesToHexString(image).getBytes("ISO8859-1"));
			} finally {
				if (out != null)
					out.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}  
}