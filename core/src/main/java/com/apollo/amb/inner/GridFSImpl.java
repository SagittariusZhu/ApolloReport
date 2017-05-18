package com.apollo.amb.inner;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;

import com.Ostermiller.util.CircularByteBuffer;
import com.mongodb.gridfs.GridFSDBFile;

public class GridFSImpl {

	private GridFsOperations operations;
	
	public GridFSImpl(GridFsOperations operations) {
		this.operations = operations;
	}
	
	public String getFilename(String id) { 
		GridFSDBFile gfile = operations.findOne(new Query(where("_id").is(id)));
		String filename = "";
		if (gfile != null)
			filename = gfile.getFilename();		
		return filename; 
	}
	
	public InputStream getInputStream(String id) {
		GridFSDBFile gfile = operations.findOne(new Query(where("_id").is(id)));
		if (gfile != null) {
			CircularByteBuffer cbb = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE); 
			try {
				gfile.writeTo(cbb.getOutputStream());
				cbb.getOutputStream().close();
				return cbb.getInputStream();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return null;		
	}
}
