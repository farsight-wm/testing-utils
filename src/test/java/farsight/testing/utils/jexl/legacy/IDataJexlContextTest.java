package farsight.testing.utils.jexl.legacy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;

public class IDataJexlContextTest {

	@Test
	public void shouldReturnIDataValues() {
		IDataJexlContext idjc = new IDataJexlContext(getIData());

		assertTrue(idjc.has("k"+ExpressionProcessor.ENC_HYPHEN+"1"));
		assertEquals("v1", idjc.get("k"+ExpressionProcessor.ENC_HYPHEN+"1"));

		assertTrue(idjc.has("k2"));

		IDataJexlContext k2 = (IDataJexlContext) idjc.get("k2");
		assertTrue(k2.has("k21"));
		assertEquals("v21", k2.get("k21"));
	}

	@Test
	public void shouldSetIData() {
		IData idata = IDataFactory.create();
		IDataJexlContext idjc = new IDataJexlContext(idata);
		idjc.set("lorem", "ipsum");
		idjc.set("foo.bar", "12");
		
		IDataCursor idc = idata.getCursor();
		assertEquals("ipsum", IDataUtil.get(idc, "lorem"));
		
		IDataCursor idcf = ((IData)IDataUtil.get(idc, "foo")).getCursor();
		assertEquals("12", IDataUtil.get(idcf, "bar"));
	}
	
	private IData getIData() {
		IData idata = IDataFactory.create();
		IDataCursor cursor = idata.getCursor();
		IDataUtil.put(cursor, "k-1", "v1");

		IData idata2 = IDataFactory.create();
		IDataCursor cursor2 = idata2.getCursor();
		IDataUtil.put(cursor2, "k21", "v21");

		IDataUtil.put(cursor, "k2", idata2);

		return idata;
	}
}
