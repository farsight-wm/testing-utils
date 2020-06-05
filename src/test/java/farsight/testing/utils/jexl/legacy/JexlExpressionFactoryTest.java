package farsight.testing.utils.jexl.legacy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.jexl3.JexlExpression;
import org.junit.Test;

import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;

public class JexlExpressionFactoryTest {

	@Test
	public void shouldRetrieveFromSubData() {
		JexlExpression expr = JexlExpressionFactory.createExpression("alpha.beta == \"hello\"");
		IData alpha = IDataFactory.create();
		putWithinIData(alpha, "beta", "goodbye");
		IData idata = IDataFactory.create();
		putWithinIData(idata, "alpha", alpha);
		Boolean result = (Boolean) expr.evaluate(new IDataJexlContext(idata));
		assertFalse(result);
		
		putWithinIData(alpha, "beta", "hello");
		result = (Boolean) expr.evaluate(new IDataJexlContext(idata));
		assertTrue(result);
	}

	@Test
	public void shouldMatchPipeline() throws Exception {
		JexlExpression expr = JexlExpressionFactory.createExpression("foo == 2");
		IData idata = IDataFactory.create();
		Boolean result = (Boolean) expr.evaluate(new IDataJexlContext(idata));
		assertFalse(result);
		putWithinIData(idata, "foo", 2);
		result = (Boolean) expr.evaluate(new IDataJexlContext(idata));
		assertTrue(result);
		putWithinIData(idata, "foo", 1);
		result = (Boolean) expr.evaluate(new IDataJexlContext(idata));
		assertFalse(result);
	}

	@Test
	public void shouldCorrectlyEvaluateEscapesWithinExpressions() {
		testExpression("foo_2 == 2", "foo_2", "2");
		testExpression("foo\\:3 == 3", "foo:3", "3");
		testExpression("foo\\ 4 == 4", "foo 4", "4");
		testExpression("foo\\-5 == 5", "foo-5", "5");
		testExpression("foo\\-\\- == 5", "foo--", "5");
		testExpression("f\\-oo == 5", "f-oo", "5");
		testExpression("\\-oo == 5", "-oo", "5");
		testExpression("a\\-b\\-c == 5", "a-b-c", "5");
		try {
			testExpression("foo\\b == 5", "foo--", "5");
			fail();
		} catch (RuntimeException e) {
			// NOOP
		}
		
	}

	private void testExpression(String expression, String varName, String varValue) {
		JexlExpression expr = JexlExpressionFactory.createExpression(expression);
		IData idata = IDataFactory.create();
		Boolean result = (Boolean) expr.evaluate(new IDataJexlContext(idata));
		assertFalse(result);
		putWithinIData(idata, varName, varValue);
		result = (Boolean) expr.evaluate(new IDataJexlContext(idata));
		assertTrue(result);
	}
	
	private void putWithinIData(IData idata, String k, Object v) {
		IDataCursor cursor = idata.getCursor();
		IDataUtil.put(cursor, k, v);
		cursor.destroy();
	}
}
