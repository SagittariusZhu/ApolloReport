package com.apollo.amb.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.Ostermiller.util.CircularByteBuffer;
import com.apollo.amb.inner.GridFSImpl;
import com.apollo.amb.inner.ITextExport;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

@Controller
public class ReportService {

	@Autowired  
	private MongoTemplate template;

	@Autowired
	private GridFsOperations operations;

	@RequestMapping(value="/uploadFile", method=RequestMethod.GET)
	public @ResponseBody String provideUploadInfo() {
		return "You can upload a file by posting to this same URL.";
	}

	@RequestMapping(value="/uploadFile", method=RequestMethod.POST)
	public @ResponseBody Map handleFileUpload(
			@RequestParam("sfile") MultipartFile[] files){

		Map ret = new HashMap();
		long start = System.currentTimeMillis();

		List fileInfos = new ArrayList();
		for (MultipartFile file : files) {
			Map m = new HashMap();
			try {
				String filename = file.getOriginalFilename();
				GridFSFile gf = operations.store(file.getInputStream(), filename, file.getName());
				String id = gf.getId().toString();
				m.put("id", id);
				m.put("name", filename);
				fileInfos.add(m);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		long used = System.currentTimeMillis() - start;

		Map map = new HashMap();
		map.put("fileInfos", fileInfos);

		Map map2 = new HashMap();
		map2.put("upload.file.sys", map);

		List data = new ArrayList();
		data.add(map2);

		ret.put("data", data);
		ret.put("used", used);		

		return ret;
	}

	@RequestMapping(value="/removeFile", method=RequestMethod.GET)
	public @ResponseBody String handleFileRemove(@RequestParam("id") String id) {
		if (id.length() > 0) {
			operations.delete(new Query(where("_id").is(id)));
			return "1";
		}
		return "0";
	}

	@RequestMapping(value="/getFile", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<byte[]> handleFileDownload(@RequestParam("id") String id) throws Throwable {
		if (id.length() > 0) {
			GridFSDBFile gfile = operations.findOne(new Query(where("_id").is(id)));
			if (gfile != null) {
				try {
					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
					headers.setContentLength(gfile.getLength());
					headers.setContentDispositionFormData("attachment", 
							new String(gfile.getFilename().getBytes("utf-8"), "iso8859-1"));

					ByteArrayOutputStream baos=new ByteArrayOutputStream();
					gfile.writeTo(baos);
					return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		throw new FileNotFoundException(id);
	}

	@RequestMapping(value="/process", method=RequestMethod.POST, produces="application/json; charset=UTF-8")
	public @ResponseBody Map handleProcess(
			@Param("tid") String tid,
			@Param("wid") String wid,
			@Param("barcode") String barcode,
			@RequestBody Map params) {
		Map ret = new HashMap();

		GridFSImpl impl = new GridFSImpl(operations);

		if (tid.length() > 0) {			
			InputStream in = impl.getInputStream(tid);
			String filename = impl.getFilename(tid);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream watermark = null;

			if (wid != null && wid.length() > 0) {
				watermark = impl.getInputStream(wid);
			}

			try {
				ITextExport.process(in, barcode, out, watermark);
				String ofilename = "[AUTO]" + filename.substring(0, filename.lastIndexOf(".") - 1) + ".pdf";
				GridFSFile gf = operations.store(new ByteArrayInputStream(out.toByteArray()), ofilename, ofilename);
				String id = gf.getId().toString();
				ret.put("id", id);
				ret.put("name", filename);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return ret;
	}	

}
