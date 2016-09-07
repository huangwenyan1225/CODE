package com.thinkgem.jeesite.modules.act.rest.servlet;

import javax.servlet.ServletOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FilterServletOutputStream extends ServletOutputStream {

	private DataOutputStream stream;

	public FilterServletOutputStream(OutputStream output) {
		stream = new DataOutputStream(output);
	}

	public void write(int b) throws IOException {
		stream.write(b);
	}

	public void write(byte[] b) throws IOException {
		stream.write(b);
	}

//	@Override public void setWriteListener(WriteListener writeListener)
//	{
//
//	}
//
//	@Override public boolean isReady()
//	{
//		return true;
//	}

	public void write(byte[] b, int off, int len) throws IOException {
		stream.write(b, off, len);
	}

}