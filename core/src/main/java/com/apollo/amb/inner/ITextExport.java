package com.apollo.amb.inner;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

import org.apache.commons.io.IOUtils;

import com.Ostermiller.util.CircularByteBuffer;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfBasicElement;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.document.RtfDocument;
import com.lowagie.text.rtf.headerfooter.RtfHeaderFooter;
import com.lowagie.text.rtf.parser.RtfImportMappings;
import com.lowagie.text.rtf.parser.RtfParser;
import com.lowagie.text.rtf.table.RtfCell;

/**
 * IText操作类
 *
 */
public class ITextExport {

	private static String HEADERR_TAG = "{\\headerr";
	private static String HEADERF_TAG = "{\\headerf";
	private static String SHOWPIC_TAG = "{\\*\\shppict";
	private static String BLIPUID_TAG = "{\\*\\blipuid";

	public static void addBarCode(InputStream in, OutputStream out, String barCode) {
		try {
			//create new barImage
			String barStr = BarCode.bytesToHexString(BarCode.createBarCode(barCode));

			//convert stream to bufferedInputStream
			BufferedInputStream is = null;
			if (in instanceof BufferedInputStream)
				is = (BufferedInputStream) in;
			else
				is = new BufferedInputStream(in);

			//read data from input stream
			byte[] data = new byte[is.available()];
			is.read(data);

			//replace barImage with new image
			String content = new String(data, "ISO8859-1");
			StringBuilder builder = new StringBuilder("");
			boolean found = false;

			int idx = content.indexOf(HEADERR_TAG);
			if (idx < 0) idx = content.indexOf(HEADERF_TAG);
			if (idx >= 0) {
				idx = content.indexOf(SHOWPIC_TAG, idx);
				if (idx >= 0) {
					idx = content.indexOf(BLIPUID_TAG, idx);
					if (idx >= 0) {
						idx = content.indexOf("}", idx);
						if (idx >= 0) {
							idx += 1;
							String imgStr = "";
							if (idx > 0) {
								int endIdx = content.indexOf("}", idx);
								imgStr = content.substring(idx, endIdx);
								builder.append(content.substring(0, idx));
								builder.append(barStr);
								builder.append(content.substring(endIdx));
								found = true;
							}
						}
					}
				}
			}
			if (!found) {
				builder.append(content);
			}
			out.write(builder.toString().getBytes("ISO8859-1"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createRtfContext(String rtfFile, byte[] barCode, OutputStream out) {
		System.setProperty("os.name", "Windows Vista");
		Document doc = new Document(PageSize.A4, 90, 90, 72, 72);
		//		Document doc = new Document(PageSize.A4, 20, 20, 20, 20);
		try {

			OutputStream rtfOut = new FileOutputStream("C:\\home\\projects\\sap\\temp\\test2.rtf");

			RtfWriter2 rtf = RtfWriter2.getInstance(doc, rtfOut);
			//			PdfWriter.getInstance(doc, out);

			BaseFont bfChinese = BaseFont.createFont("STSongStd-Light",  
					"UniGB-UCS2-H", false);  

			Font bold_fontChinese = new Font(bfChinese, 12, Font.BOLD,  
					Color.BLACK);  
			Font italic_fontChinese = new Font(bfChinese, 12, Font.ITALIC,  
					Color.BLACK);  
			Font impressFont = new Font(bfChinese, 16, Font.BOLDITALIC,  
					Color.BLACK);  

			doc.open();

			//			doc.add(new Paragraph("中文字体", bold_fontChinese));

			//			Element rtfEl = new Cell();
			//			rtf.importRtfDocumentIntoElement(rtfEl, new FileInputStream(rtfFile));
			//			doc.add(rtfEl);

			////			// create a new parser to load the RTF file
			//			RtfParser parser = new RtfParser(doc);
			////
			////			// read the rtf file into a compatible document
			//			parser.convertRtfDocument(new FileInputStream(rtfFile), doc);


			Image img = null;
			img = Image.getInstance(barCode);
			float height = img.getHeight();
			float width = img.getWidth();
			img.setAlignment(Image.MIDDLE);
			img.scalePercent(530 / width * 100, 30 / height * 100);

			RtfHeaderFooter header = new RtfHeaderFooter(img);
			doc.setHeader(header);

			//			rtf.importRtfDocument(new FileInputStream(rtfFile));

			doc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 第一种解决方案 在不改变图片形状的同时，判断，如果h>w，则按h压缩，否则在w>h或w=h的情况下，按宽度压缩
	 * 
	 * @param h
	 * @param w
	 * @return
	 */

	public static int getPercent(float h, float w) {
		int p = 0;
		float p2 = 0.0f;
		if (h > w) {
			p2 = 297 / h * 100;
		} else {
			p2 = 210 / w * 100;
		}
		p = Math.round(p2);
		return p;
	}

	/**
	 * 第二种解决方案，统一按照宽度压缩 这样来的效果是，所有图片的宽度是相等的，自我认为给客户的效果是最好的
	 * 
	 * @param args
	 */
	public static float getPercent2(float h, float w) {
		float p1 = 0.0f;
		float p2 = 0.0f;
		p1 = 20 / h * 100;
		p2 = 530 / w * 100;
		return Math.min(p1, p2);
	}

	public static void waterMark(InputStream in, InputStream imageStream, OutputStream out) {  
		try {  
			PdfReader reader = new PdfReader(in);  
			PdfStamper stamper = new PdfStamper(reader, out); 

			PdfContentByte under;  

			int total = reader.getNumberOfPages() + 1;
			
			Image image = Image.getInstance(IOUtils.toByteArray(imageStream));  

			float percentage = 1;  
			//这里都是图片最原始的宽度与高度  
			float resizedWidth = image.getWidth();
			float resizedHeight = image.getHeight();

			//这时判断图片宽度是否大于页面宽度减去也边距，如果是，那么缩小，如果还大，继续缩小，  
			//这样这个缩小的百分比percentage会越来越小  
			while (resizedWidth > 594 * 0.8)  
			{  
				percentage = percentage * 0.9f;  
				resizedHeight = image.getHeight() * percentage;  
				resizedWidth = image.getWidth() * percentage;  
			}  
			//There is a 0.8 here. If the height of the image is too close to the page size height,  
			//the image will seem so big  
			while (resizedHeight > 840 * 0.8)  
			{  
				percentage = percentage * 0.9f;  
				resizedHeight = image.getHeight() * percentage;  
				resizedWidth = image.getWidth() * percentage;  
			}  

			//这里用计算出来的百分比来缩小图片  
			image.scalePercent(percentage * 100);   
			//让图片的中心点与页面的中心店进行重合  
			image.setAbsolutePosition(594/2 - resizedWidth / 2, 840 / 2 - resizedHeight / 2);  

			for (int i = 1; i < total; i++) {  
				under = stamper.getUnderContent(i);  
				// 添加水印图片  
				under.addImage(image);  
			}
			/*
            int j = waterMarkName.length();  
            char c = 0;  
            int rise = 0;  
            for (int i = 1; i < total; i++) {  
                rise = 400;  
                under = stamper.getUnderContent(i);  
                under.beginText();  
                under.setFontAndSize(base, 30);  

                if (j >= 15) {  
                    under.setTextMatrix(200, 120);  
                    for (int k = 0; k < j; k++) {  
                        under.setTextRise(rise);  
                        c = waterMarkName.charAt(k);  
                        under.showText(c + "");  
                    }  
                } else {  
                    under.setTextMatrix(240, 100);  
                    for (int k = 0; k < j; k++) {  
                        under.setTextRise(rise);  
                        c = waterMarkName.charAt(k);  
                        under.showText(c + "");  
                        rise -= 18;  

                    }  
                }  

                // 添加水印文字  
                under.endText();  
                // 画个圈  
                under.ellipse(250, 450, 350, 550);  
                under.setLineWidth(1f);  
                under.stroke();  

            }  
			 */

			stamper.close();  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
	}

	public static void process(InputStream in, String barCode, OutputStream out, String waterMarkFile) throws Exception {
		InputStream waterStream = new FileInputStream(waterMarkFile);
		process(in, barCode, out, waterStream);
	}
	
	public static void process(InputStream in, String barCode, OutputStream out, InputStream waterMark) throws Exception {	
		CircularByteBuffer cbb = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE); 

		ITextExport.addBarCode(in, cbb.getOutputStream(), barCode);

		cbb.getOutputStream().close();

		CircularByteBuffer cbb2 = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE); 
		LibreOfficeConvert.convert(cbb.getInputStream(), cbb2.getOutputStream());

		cbb2.getOutputStream().close();

		if (waterMark != null)
			ITextExport.waterMark(cbb2.getInputStream(), waterMark, out);
		else {
			byte[] arr = new byte[cbb2.getInputStream().available()];
			cbb2.getInputStream().read(arr);
			out.write(arr);
		}
	}

	public static String extract(InputStream is) throws Exception {
		String bodyText = "";
		RTFEditorKit rtf = new RTFEditorKit();  
		DefaultStyledDocument styledDoc = new DefaultStyledDocument();

		rtf.read(is, styledDoc, 0);
		byte[] arr = styledDoc.getText(0, styledDoc.getLength()).getBytes("iso8859_1");

		bodyText = new String(arr, "gb2312");

		return bodyText;
	}
}