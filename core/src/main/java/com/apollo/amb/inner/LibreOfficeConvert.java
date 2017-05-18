package com.apollo.amb.inner;

import java.io.InputStream;
import java.io.OutputStream;

import com.apollo.amb.AppContext;
import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

public class LibreOfficeConvert {
	
	public static void convert(InputStream in, OutputStream out) throws Exception { 
        OpenOfficeConnection connection = new SocketOpenOfficeConnection(
        		AppContext.getConf().getLibreOfficeServer(),
        		AppContext.getConf().getLibreOfficePort());
        try {   
            connection.connect();   
            DocumentConverter converter = new OpenOfficeDocumentConverter(connection);   
            DefaultDocumentFormatRegistry formatReg = new  DefaultDocumentFormatRegistry();
            DocumentFormat rtfFormat = formatReg.getFormatByFileExtension("rtf");
            DocumentFormat pdfFormat = formatReg.getFormatByFileExtension("pdf");
            converter.convert(in, rtfFormat, out, pdfFormat); 
        } finally {   
            try{ if(connection != null){connection.disconnect(); connection = null;}}catch(Exception e){}   
        } 
	}

}
