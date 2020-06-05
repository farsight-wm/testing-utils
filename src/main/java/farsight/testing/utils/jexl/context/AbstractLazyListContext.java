package farsight.testing.utils.jexl.context;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Uses an AbstractMap interface, so that JexlExpression do not need an
 * instantiation for each array item, but only those that are actually accessed!
 * 
 */
public class AbstractLazyListContext<V> extends AbstractMap<Integer, V> {

	private final List<V> list;
	private Set<java.util.Map.Entry<Integer, V>> entrySet;

	public AbstractLazyListContext(List<V> list) {
		this.list = list;
	}

	private int index(Object key) {
		try {
			int index = (Integer) key;
			if (index < 0 || index > list.size())
				return -1;
			return index;
		} catch (Exception e) {
			return -1;
		}
	}
	
	public V get(int index) {
		return index < 0 ? null : list.get(index);
	}
	
	public void set(int index, V value) {
		if(index < 0 || index >= list.size())
			throw new IndexOutOfBoundsException();
		list.set(index, value);
	}
	

	@Override
	public V get(Object key) {
		return get(index(key));
	}

	@Override
	public boolean containsKey(Object key) {
		return index(key) >= 0;
	}

	@Override
	public boolean containsValue(Object value) {
		return false; // NotSupported?
	}

	@Override
	public Set<java.util.Map.Entry<Integer, V>> entrySet() {
		if (entrySet == null) // XXX or dirty on change?!
			entrySet = createEntrySet();
		return entrySet;
	}

	private Set<java.util.Map.Entry<Integer, V>> createEntrySet() {
		final int len = list.size();

		return new AbstractSet<Map.Entry<Integer, V>>() {

			@Override
			public Iterator<java.util.Map.Entry<Integer, V>> iterator() {

				return new Iterator<Map.Entry<Integer, V>>() {
					int pos = 0;

					@Override
					public Entry<Integer, V> next() {
						return new Entry<Integer, V>() {
							final int entryPos = pos++;

							@Override
							public Integer getKey() {
								return entryPos;
							}

							@Override
							public V getValue() {
								return get(entryPos);
							}

							@Override
							public V setValue(Object value) {
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

}
