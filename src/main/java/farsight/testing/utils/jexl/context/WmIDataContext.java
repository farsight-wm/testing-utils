package farsight.testing.utils.jexl.context;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.jexl3.JexlContext;

import com.wm.data.IData;
import com.wm.data.IDataUtil;
import com.wm.util.Table;

import farsight.utils.idata.DataBuilder;

public class WmIDataContext implements JexlContext, WmObject {
	
	private static class IDataListContext extends AbstractLazyListContext<Object> {
		private IDataListContext(IData[] idataList) {
			super(Arrays.asList((Object[]) idataList));
		}
		
		@Override
		public Object get(int index) {
			return wrap(super.get(index));
		}
		
		
		@Override
		public Object put(Integer key, Object value) {
			super.set(key, value);
			return null;
		}
		
		
		
	}
	
	
	protected final DataBuilder builder;
	
	public WmIDataContext(IData idata) {
		this(DataBuilder.wrap(idata));
	}
	
	protected WmIDataContext(DataBuilder builder) {
		this.builder = builder;
	}
	
	public static Object wrap(Object o) {
		if(o == null)
			return null;
		else if(o instanceof IData) {
			return new WmIDataContext((IData) o);
		} else if(o instanceof IData[]) {
			return new IDataListContext((IData[]) o);
		} else if(o instanceof Table) {
			return new WmTableContext((Table) o);
		}
		return o;
	}

	@Override
	public Object getWmObject() {
		return builder.build();
	}

	@Override
	public Object get(String name) {
		return wrap(builder.read(name, '.'));
	}

	@Override
	public void set(String name, Object value) {
		builder.insert(name, transform(value), '.');
	}
	
	public void put(String key, Object value) {
		builder.put(key, transform(value));
	}
	
	@SuppressWarnings("unchecked")
	public static Object transform(Object value) {
		if(value == null)
			return null;
		if(value instanceof WmObject)
			return transform((WmObject)value);
		if(value instanceof Map<?, ?>) {
			return transform((Map<Object, Object>)value);
		}
		//TODO handle SET?!
		
		return value;
	}
	
	public static IData transform(Map<Object, Object> map) {
		DataBuilder builder = DataBuilder.create();
		for(Entry<Object, Object> entry: map.entrySet()) {
			builder.put(String.valueOf(entry.getKey()), transform(entry.getValue()));
		}
		return builder.getIData();
	}
	
	public static Object transform(WmObject object) {
		return object.getWmObject();
	}
	
	@Override
	public boolean has(String name) {
		return builder.containsKey(name);
	}
	
	// context API
	
	public IData getIData() {
		return builder.build();
	}
	
	
	// LoadFunction API
	
	public void replace(IData pipeline) {
		builder.replace(pipeline);
	}
	
	public void merge(IData data, boolean dominant) throws Exception {
		builder.merge(data, dominant);
	}
	
	public void merge(IData data) throws Exception {
		merge(data, true);
	}
	
	// jexl API
	
	public void clear() {
		builder.clear();
	}
	
	public void clear(String... preserve) {
		builder.filter(preserve);
	}
	
	public void remove(String key) {
		builder.remove(key);
	}
	
	// syntactic sugar: you may remove structures by name or by reference
	public void remove(WmIDataContext context) {
		builder.removeValue(context.getIData());
	}
	
	
	public WmTableContext createWmTable(String[] cols) {
		return WmTableContext.createNewTable(cols);
	}
	
	public IData deepClone() throws IOException {
		return IDataUtil.deepClone(builder.getIData());
	}

	
	

}