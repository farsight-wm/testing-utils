package farsight.testing.utils.jexl.context;

import java.io.InputStream;

import com.wm.data.IData;

public interface ResourceContext {

	public String getAsString(String path);

	public byte[] getAsBytes(String path);

	public InputStream getAsStream(String path);

	public IData getAsIData(String path) throws Exception;

}