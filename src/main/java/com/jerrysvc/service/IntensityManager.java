package com.jerrysvc.service;

import org.springframework.stereotype.Service;

import java.util.TreeMap;

/**
 * An interface to facilitate an intensity manager.
 * All calls to add and set intensities should go through this interface
 * <p>
 * Assuming long as all params
 */
public interface IntensityManager {

    /**
     * Adds the given intensity
     *
     * @param from   from intensity (can start from -infinity)
     * @param to     to intensity (can go upto +infinity) - to is exclusive
     * @param amount integer amount to add to the intensity
     */
    void add(long from, long to, long amount);

    /**
     * Overrides given intensity if already exists, if not create new
     *
     * @param from   from intensity (can start from -infinity)
     * @param to     to intensity (can go upto +infinity) - to is exclusive
     * @param amount integer amount to add to the intensity
     */
    void set(long from, long to, long amount);


    /**
     * Helper method just to return segments for unit test purposes
     */
    TreeMap<Long, Long> get();
}
