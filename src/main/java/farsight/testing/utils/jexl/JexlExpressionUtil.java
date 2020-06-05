package farsight.testing.utils.jexl;

import java.util.ArrayList;

import org.apache.commons.jexl3.JexlExpression;

import com.wm.data.IData;
import com.wm.data.IDataFactory;

import farsight.testing.utils.jexl.context.ResourceContext;
import farsight.testing.utils.jexl.context.WmIDataContext;
import farsight.testing.utils.jexl.functions.LoadFunction;
import farsight.testing.utils.jexl.legacy.JexlExpressionFactory;

public class JexlExpressionUtil {
	
	public static IData executeValueExpressions(IData idata, String jexlValueExpressions, ResourceContext resourceContext) {
		if(idata == null)
			idata = IDataFactory.create();
		IDataJexlContext ctx = new IDataJexlContext(idata);
		ctx.registerNamespace("load", new LoadFunction(resourceContext, ctx));
		
		int stmtCounter = 0;
		try {
			for (String expr : jexlValueExpressions.split(";")) {
				JexlExpressionFactory.createExpression(expr).evaluate(ctx);
				stmtCounter++;
			}
		} catch(Exception e) {
			System.err.println("Error interpreting JexlExpression(" + stmtCounter + "):");
			System.err.println(jexlValueExpressions);
			e.printStackTrace(System.err);
			throw e;
		}
		
		return idata;
	}
	
	public static boolean evaluatePipelineExpression(IData idata, String jexlPipelineExpression) {
		try {
			JexlExpression expression = JexlExpressionFactory.createExpression(jexlPipelineExpression);
			return (Boolean) expression.evaluate(new IDataJexlContext(idata));
		} catch (Exception e) {
			System.err.println("parsing the expression '"+jexlPipelineExpression+"' failed");
			e.printStackTrace(System.err);
			throw e;
		}
	}
	
	public static IData evaluateDocumentExpression(String expression, IData source) {
		Object o = new IDataJexlContext(source).get(expression);
		return o != null && o instanceof WmIDataContext ? ((WmIDataContext)o).getIData() : null;
	}
	
	//nameing!!!
	public static String findFailedPath(String expression, IData source) {
		ArrayList<String> result = new ArrayList<>();
		WmIDataContext ctx = new WmIDataContext(source);
		Object o = null;
		for(String part: expression.split("\\.")) {
			result.add(part);
			o = ctx.get(part);
			if(o instanceof WmIDataContext) {
				ctx = (WmIDataContext) o;
			} else {
				break;
			}
		}
		return String.join(".", result);
	}

}
