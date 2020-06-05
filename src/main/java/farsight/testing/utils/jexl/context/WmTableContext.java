package farsight.testing.utils.jexl.context;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.jexl3.JexlContext;

import com.wm.util.Table;
import com.wm.util.Values;

public class WmTableContext extends AbstractMap<Integer, WmTableContext.RowContext> implements WmObject {
	
	public class RowContext implements JexlContext {
		
		private final Object[] row;
		
		private RowContext(Object[] row) {
			this.row = row;
		}

		@Override
		public Object get(String name) {
			int index = columnIndex(name);
			//XXX Wrapped?
			return index > 0 && index < row.length ? row[index] : null;
		}

		

		@Override
		public void set(String name, Object value) {
			int index = columnIndex(name);
			if(index > 0 && index < row.length)
				row[index] = value;
		}

		@Override
		public boolean has(String name) {
			return columns.containsKey(name);
		}
		
	}
	
	
	private final Table table;
	private final HashMap<String, Integer> columns;
	private Set<java.util.Map.Entry<Integer, RowContext>> entrySet = null;
	
	public WmTableContext(Table table) {
		this.table = table;
		columns = new HashMap<>();
		for(int i = 0; i < table.cols.length; i++)
			columns.put(table.cols[i], i);
	}
	
	@Override
	public Object getWmObject() {
		return table;
	}
	
	private int index(Object key) {
		try {
			int index = (Integer) key;
			if(index < 0 || index > table.rows.size())
				return -1;
			return index;
		} catch(Exception e) {
			return -1;
		}
	}
	
	private int columnIndex(String name) {
		Integer index = columns.get(name);
		return index == 0 ? -1 : index;
	}
	
	@Override
	public WmTableContext.RowContext get(Object key) {
		int index = index(key);
		return index < 0 ? null : new RowContext((Object[]) table.rows.get(index));
	}
	
	@Override
	public boolean containsKey(Object key) {
		return index(key) >= 0;
	}
	
	@Override
	public boolean containsValue(Object value) {
		return false; //NotSupported?
	}

	@Override
	public Set<java.util.Map.Entry<Integer, RowContext>> entrySet() {
		if(entrySet == null) //XXX or dirty on change?!
			entrySet = createEntrySet();
		return entrySet;
	}

	private Set<java.util.Map.Entry<Integer, RowContext>> createEntrySet() {
		final int len = table.getRowCount();

		return new AbstractSet<Map.Entry<Integer, RowContext>>() {

			@Override
			public Iterator<java.util.Map.Entry<Integer, RowContext>> iterator() {
				
				return new Iterator<Map.Entry<Integer, RowContext>>() {
					int pos = 0;
					
					@Override
					public Entry<Integer, RowContext> next() {
						return new Entry<Integer, RowContext>() {
							final int entryPos = pos++; 
							
							@Override
							public Integer getKey() {
								return entryPos;
							}

							@Override
							public RowContext getValue() {
								return new RowContext((Object[]) table.rows.get(entryPos));
							}

							@Override
							public RowContext setValue(RowContext value) {
								throw new UnsupportedOperationException();
							}
						};
					}
					
					@Override
					public boolean hasNext() {
						return pos < len;
					}
				};
			}

			@Override
			public int size() {
				return len;
			}
		};
	}
	
	public static WmTableContext createNewTable(String[] cols) {
		return new WmTableContext(new Table(cols));
	}
	
	public WmTableContext addRow(WmIDataContext data) {
		table.addRow(Values.use(data.getIData()));
		return this;
	}
	
	public WmTableContext addRow(Object[] data) {
		Object[] row = new Object[table.cols.length];
		System.arraycopy(data, 0, row, 0, Math.min(row.length, data.length));
		table.addRow(row);
		return this;
	}
	
	public WmTableContext removeRow(int index) throws Exception {
		table.deleteItemAt(index);
		return this;
	}
	
	public void clear() {
		table.rows.clear();
	}
	
	public WmTableContext clearRows() {
		clear();
		return this;
	}
	
	
	

}
