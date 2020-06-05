package farsight.testing.utils.jexl;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.jexl3.JexlContext.NamespaceResolver;
import org.apache.commons.lang3.math.NumberUtils;

import com.wm.data.IData;

import farsight.testing.utils.jexl.context.WmIDataContext;
import farsight.utils.idata.DataBuilder;

public class IDataJexlContext extends WmIDataContext implements NamespaceResolver {
	
	private HashMap<String, Object> namespaceMap = null;
	
	public IDataJexlContext(IData idata) {
		super(idata);
	}
	
	private IDataJexlContext(DataBuilder builder) {
		super(builder);
	}

	@Override
	public Object resolveNamespace(String name) {
		return namespaceMap == null ? null : namespaceMap.get(name);
	}
	
	public void registerNamespace(String name, Object namespace) {
		if(namespaceMap == null)
			namespaceMap = new HashMap<>();
		namespaceMap.put(name, namespace);
	}
	
	//helper functions
	
	public Number numeric(String input) {
		if(NumberUtils.isCreatable(input))
			return NumberUtils.createNumber(input);
		return 0;
	}
	
	public String string(Number number) {
		return number.toString();
	}
	
	public Iterator<Integer> range(int l, int r) {
		return range(l, r, 1);
	}
	
	public Iterator<Integer> range(int l, int r, int step) {
		if(step == 0)
			throw new IllegalArgumentException("Step size may not be zero!");
		step = Math.abs(step);
		final boolean inc = l < r;
		final int s = inc ? step : -step; 
		
		return new Iterator<Integer>() {

			final int end = r;
			int next = l; 
				
			@Override
			public boolean hasNext() {
				return inc ? next <= end : next >= end;  
			}

			@Override
			public Integer next() {
				int res = next;
				next+= s;
				return res;
			}
		};
	}
	
	public IData[] createDocumentList(int size) {
		return new IData[size];
	}
	

}