package fr.umlv.thaw.util.counter;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SetCounter<E> {

	private final ConcurrentHashMap<E, Integer> map;
	
	/**
	 * Construct a generic thread-safe set with occurence counting.
	 */
	public SetCounter() {
		this.map = new ConcurrentHashMap<>();
	}
	
	/**
	 * Get all the elements from the set.
	 * Each element is present once.
	 * The returned set is immutable.
	 * 
	 * @return	The set of the elements.
	 */
	public Set<E> getAll() {
		return map.keySet();
	}

	/**
	 * Add the specified value to the set.
	 * The same value can be add many times,
	 * the structure will count the number of occurences of it.
	 * 
	 * @param 	value the specified value
	 * @return	True if the value is the first occurence in the set, False else.
	 * @throws 	NullPointerException if value is null
	 */
	public boolean add(E value) {
		Objects.requireNonNull(value);
		return (map.merge(value, 1, Integer::sum) == 1);
	}

	/**
	 * Remove the specified value to the set.
	 * The same value can be remove many times,
	 * the structure will count the number of occurences of it.
	 * 
	 * @param 	value the specified value
	 * @return	True if the value is the last occurence in the set, False else.
	 * @throws 	NullPointerException if value is null
	 */
	public boolean remove(E value) {
		Objects.requireNonNull(value);
		int result = map.merge(value, -1, Integer::sum);
		if (result < 0) {
			map.remove(value);
			return false;
		}
		if (result == 0) {
			map.remove(value);
			return true;
		}
		return false;
	}

	/**
	 * Get the size of the set.
	 * Each element is count once.
	 * 
	 * @return	The size of the set.
	 */
	public int size() {
		return map.size();
	}

}
