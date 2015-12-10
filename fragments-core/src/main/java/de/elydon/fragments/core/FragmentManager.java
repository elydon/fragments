package de.elydon.fragments.core;

import java.util.List;

/**
 * <p>
 * Manages the {@link Fragment fragments} in the system.
 * </p>
 * 
 * @author elydon
 *
 */
public interface FragmentManager {

	/**
	 * <p>
	 * Returns the {@link Fragment} with the specified id or {@code null}, if no
	 * such fragment exists.
	 * </p>
	 * 
	 * @param id
	 * @return
	 */
	Fragment get(long id);

	/**
	 * <p>
	 * Returns a (probably unsorted) list of all {@link Fragment fragments} in
	 * the system.
	 * </p>
	 * 
	 * @return
	 */
	List<Fragment> getAll();

	/**
	 * <p>
	 * Stores the {@link Fragment} in the system. If it has not been stored yet,
	 * this method {@link Fragment#setId(long) sets a new ID}.
	 * </p>
	 * 
	 * @param fragment
	 * @return The stored fragment
	 * @throws IllegalArgumentException
	 *             If the fragment is {@code null}
	 */
	Fragment store(Fragment fragment);

	/**
	 * <p>
	 * Deletes the fragment with the specified ID.
	 * </p>
	 * 
	 * @param id
	 * @return The deleted fragment
	 * @throws IllegalArgumentException
	 *             If no fragment with the specified ID exists
	 */
	Fragment delete(long id);

	/**
	 * <p>
	 * Searches for {@link Fragment fragments} that match the search query.
	 * </p>
	 * <p>
	 * The syntax of the query as well as the search algorithm are
	 * implementation specific.
	 * </p>
	 * 
	 * @param query
	 * @return The found list of fragments, or the empty list if none were found
	 * @throws IllegalArgumentException
	 *             If the query is {@code null}
	 */
	List<Fragment> search(String query);

	/**
	 * <p>
	 * Fetches a list of related fragments to the specified one.
	 * </p>
	 * <p>
	 * The algorithm of determining which fragments are related is
	 * implementation specific.
	 * </p>
	 * 
	 * @param fragment
	 * @return A list of related fragments, or the empty list if none are found
	 * @throws IllegalArgumentException
	 *             If the fragment is {@code null}
	 */
	List<Fragment> fetchRelated(Fragment fragment);

}
