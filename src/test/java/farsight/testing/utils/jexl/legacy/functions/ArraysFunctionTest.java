package farsight.testing.utils.jexl.legacy.functions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.junit.Test;

import farsight.testing.utils.jexl.legacy.JexlExpressionFactory;

public class ArraysFunctionTest {

	@Test
	public void shouldPatternMatchWithinArray() {
		JexlExpression expr = JexlExpressionFactory.createExpression(
				"arrays:matches(values, '.*orld')");
		MapContext mc = new MapContext();
		mc.set("values", new String[] { "This", "is", "hello world" });
		assertTrue((Boolean) expr.evaluate(mc));
	}

	@Test
	public void shouldStringContainWithinArray() {
		JexlExpression expr = JexlExpressionFactory.createExpression(
				"arrays:contains(values, 'or')");
		MapContext mc = new MapContext();
		mc.set("values", new String[] { "This", "is", "hello world" });
		assertTrue((Boolean) expr.evaluate(mc));
	}

	@Test
	public void shouldHandleNullArrayInputForStringContain() {
		JexlExpression expr = JexlExpressionFactory.createExpression(
				"arrays:contains(values, 'or')");
		MapContext mc = new MapContext();
		mc.set("values", null);
		assertFalse((Boolean) expr.evaluate(mc));
	}

	@Test
	public void shouldHandleNullArrayInputForStringMatch() {
		JexlExpression expr = JexlExpressionFactory.createExpression(
				"arrays:matches(values, '.*orld')");
		MapContext mc = new MapContext();
		mc.set("values", null);
		assertFalse((Boolean)expr.evaluate(mc));
	}
	@Test
	public void shouldHandleNullMatch() {
		String [] array = {"foo", null, "bar"};
		assertTrue(new ArraysFunction().contains(array, null));
		
		JexlExpression expr = JexlExpressionFactory.createExpression(
				"arrays:contains(values, null)");
		MapContext mc = new MapContext();
		mc.set("values", array);
		assertTrue((Boolean) expr.evaluate(mc));
	}

}
