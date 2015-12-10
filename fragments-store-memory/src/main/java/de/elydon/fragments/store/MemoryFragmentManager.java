package de.elydon.fragments.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.elydon.fragments.core.Fragment;
import de.elydon.fragments.core.FragmentManager;

/**
 * <p>
 * A {@link FragmentManager} that stores each {@link Fragment} in memory.
 * </p>
 * 
 * @author elydon
 *
 */
public class MemoryFragmentManager implements FragmentManager {

	private long nextId = 1;

	private final Map<Long, Fragment> fragments = new HashMap<>();

	@Override
	public Fragment get(long id) {
		synchronized (fragments) {
			return fragments.get(id);
		}
	}

	@Override
	public List<Fragment> getAll() {
		synchronized (fragments) {
			return new ArrayList<>(fragments.values());
		}
	}

	@Override
	public Fragment store(final Fragment fragment) {
		long id = fragment.getId();
		synchronized (fragments) {
			if (id == 0) {
				id = nextId;
				nextId++;
			}

			fragments.put(nextId, fragment);
		}

		return get(id);
	}

	@Override
	public Fragment delete(long id) {
		synchronized (fragments) {
			return fragments.remove(id);
		}
	}

	@Override
	public List<Fragment> search(final String query) {
		/*
		 * the search in this kind of data structure is indeed very costly
		 * (O(n)), as we have to loop through all elements to compare with the
		 * search query
		 */
		final List<Fragment> result = new ArrayList<>();
		if (query.matches("#\\d+")) {
			// searching for an id
			final String idStart = query.substring(1);

			synchronized (fragments) {
				for (final Map.Entry<Long, Fragment> entry : fragments.entrySet()) {
					final Long id = entry.getKey();
					if (id.toString().startsWith(idStart)) {
						result.add(entry.getValue());
					}
				}
			}

		} else {
			// search for text
			final String search = query.trim().toLowerCase();

			synchronized (fragments) {
				for (final Fragment fragment : fragments.values()) {
					final String text = fragment.getHeader().toLowerCase() + " " + fragment.getText().toLowerCase();
					if (text.contains(search)) {
						result.add(fragment);
					}
				}
			}
		}

		return result;
	}

	@Override
	public List<Fragment> fetchRelated(final Fragment fragment) {
		// not implemented yet
		return Collections.emptyList();
	}

}
