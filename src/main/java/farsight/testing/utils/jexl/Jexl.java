package farsight.testing.utils.jexl;

import java.util.ArrayList;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.JexlScript;

import com.wm.data.IData;
import com.wm.data.IDataFactory;

import farsight.testing.utils.jexl.context.ResourceContext;
import farsight.testing.utils.jexl.context.WmIDataContext;
import farsight.testing.utils.jexl.functions.LoadFunction;

public class Jexl {
	
	public static class ScriptEnvironment {
		
		private final IDataJexlContext context;
		private ArrayList<Object> parameter;
		private ArrayList<String> names;
		
		public ScriptEnvironment(IData root) {
			this.context = new IDataJexlContext(root);
		}
		
		public Object execute(String script) {
			JexlScript jexlScript = Jexl.createScript(script, getNames());
			return jexlScript.execute(context, getArgs());
		}

		private String[] getNames() {
			if(names == null)
				return null;
			return names.toArray(new String[names.size()]);
		}

		private Object[] getArgs() {
			if(parameter == null)
				return null;
			return parameter.toArray(new Object[parameter.size()]);
		}
		
		public ScriptEnvironment registerResources(ResourceContext resources) {
			context.registerNamespace("load", new LoadFunction(resources, context));
			return this;
		}
		
		public ScriptEnvironment registerNamespace(String name, Object namespace) {
			context.registerNamespace(name, namespace);
			return this;
		}
		
		public ScriptEnvironment addArgument(String name, Object arg) {
			if(parameter == null) {
				parameter = new ArrayList<>();
				names = new ArrayList<>();
			}
			
			names.add(name);
			parameter.add(WmIDataContext.wrap(arg));
			
			return this;
		}
		
		
	}
	
	
	private static final JexlEngine jexlEngine;

	static {
		jexlEngine = new JexlBuilder().cache(512).silent(false).strict(false).create();
	}

	private Jexl() {}

	public static JexlExpression createExpression(String expr) {
		return jexlEngine.createExpression(expr);
	}
	
	public static JexlScript createScript(String script, String... names) {
		return jexlEngine.createScript(script, names);
	}

	public static JexlEngine getEngine() {
		return jexlEngine;
	}
	
	// API
	
	public static IData executeScript(String script, IData data, ResourceContext resources) {
		if(data == null)
			data = IDataFactory.create();
		IDataJexlContext ctx = new IDataJexlContext(data);
		
		if(resources != null)
			ctx.registerNamespace("load", resources);
		
		return executeScript(script, ctx);
	}
	
	public static IData executeScript(String script, IDataJexlContext ctx) {
		createScript(script).execute(ctx);
		return ctx.getIData();
	}

}
