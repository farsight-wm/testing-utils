package farsight.testing.utils.jexl.legacy;

import java.util.StringTokenizer;

import org.apache.commons.jexl3.JexlContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;

public class IDataJexlContext implements JexlContext {

	private static final Logger logger = LogManager.getLogger(IDataJexlContext.class);
	private final IData idata;

	public IDataJexlContext(IData idata) {
		this.idata = idata;
	}

	@Override
	public Object get(String name) {
		String decodedName = name.indexOf("__") == -1 ? name : ExpressionProcessor.decode(name);
		IDataCursor cursor = idata.getCursor();
		Object o = IDataUtil.get(cursor, decodedName);
		cursor.destroy();
		Object ret = o;
		try {
			if (o instanceof IData[]) {
				IData[] idataArr = (IData[]) o;
				IDataJexlContext[] arr = new IDataJexlContext[idataArr.length];
				for (int i = 0; i < idataArr.length; i++) {
					arr[i] = new IDataJexlContext(idataArr[i]);
				}
				ret = arr;
			} else if (o instanceof IData) {
				ret = new IDataJexlContext((IData) o);
			}
		} catch (Exception e) {
			logger.error("Error evaluating: " + decodedName, e);
			throw new JexlParsingException("Error evaluating: " + decodedName, e);
		}
		return ret;
	}

	@Override
	public void set(String name, Object value) {
		StringTokenizer st = new StringTokenizer(name, ".");
		IData id = idata;
		while (st.hasMoreElements()) {
			IDataCursor idc = id.getCursor();
			String encodedName = ExpressionProcessor.escapedToEncoded(st.nextToken());
			if (st.hasMoreTokens()) {
				id = IDataFactory.create();
				IDataUtil.put(idc, encodedName, id);
				idc.destroy();
				continue;
			}
			if(value instanceof String[][] || value instanceof String[] || value instanceof String){
				IDataUtil.put(idc, encodedName, value);
			}else{
				IDataUtil.put(idc, encodedName, value.toString());
			}
			idc.destroy();
		}
	}

	@Override
	public boolean has(String name) {
		IDataCursor cursor = idata.getCursor();
		Object o = IDataUtil.get(cursor, ExpressionProcessor.decode(name));
		cursor.destroy();
		return o != null;
	}

	public IData toIData() {
		return idata;
	}
}
