package farsight.testing.utils.jexl.functions;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.wm.data.IData;

import farsight.testing.utils.jexl.IDataJexlContext;
import farsight.testing.utils.jexl.context.ResourceContext;
import farsight.utils.idata.SerializeableInputStream;

public class LoadFunction {

	private final ResourceContext resourceContext;
	private IDataJexlContext pipelineContext;

	public LoadFunction(ResourceContext resourceContext, IDataJexlContext pipelineContext) {
		this.resourceContext = resourceContext;
		this.pipelineContext = pipelineContext;
	}

	public String string(String path) {
		return resourceContext.getAsString(path);
	}

	public byte[] bytes(String path) {
		return resourceContext.getAsBytes(path);
	}

	public InputStream stream(String path) {
		return resourceContext.getAsStream(path);
	}

	public IData idata(String path) throws Exception {
		return resourceContext.getAsIData(path);
	}

	public InputStream streamFromString(String content) {
		return new SerializeableInputStream(content.getBytes(StandardCharsets.UTF_8));
	}

	public void pipeline(String path) throws Exception {
		pipelineContext.replace(resourceContext.getAsIData(path));
	}

	/*
	 * Shortcut: load:replace(<path>)
	 * 
	 * for: replace(load:idata(<path>))
	 */
	public void replace(String path) throws Exception {
		pipelineContext.replace(idata(path));
	}

	/*
	 * Shortcut load:merge(<path>, boolean)
	 * 
	 * for function merge(load:idata(<path>), boolean)
	 */
	public void merge(String path, boolean dominant) throws Exception {
		pipelineContext.merge(idata(path), dominant);
	}

	public void merge(String path) throws Exception {
		merge(path, true);
	}
}
