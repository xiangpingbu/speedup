package com.ecreditpal.mass.service.minganci.wordcut;

/**
 * <p>Pair class.</p>
 *
 * @author xibu
 * @version $Id: $Id
 */
public class Pair<K> {
    public K key;
    public Double freq = 0.0;

    /**
     * <p>Constructor for Pair.</p>
     *
     * @param key a K object.
     * @param freq a double.
     */
    public Pair(K key, double freq) {
	this.key = key;
	this.freq = freq;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
	return "Candidate [key=" + key + ", freq=" + freq + "]";
    }

}
