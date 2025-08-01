package com.jerrysvc.service;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class IntensityManagerImpl implements IntensityManager {

    // TreeMap is sorted and stores number line points as key and intensity as value
    private final TreeMap<Long, Long> segments;

    public IntensityManagerImpl() {
        // I'm using a thread safe map here to avoid race conditions modifying the map
        this.segments = new TreeMap<>();
    }

    /**
     *  Helper method just to return segments for unit test purposes
     *
     */
    public TreeMap<Long, Long> get() {
        return segments;
    }

    /* Algorithm
       Step 1 - Insert from and to into hashmap with either 0 or the floor's value.
       Step 2 - For each entry that falls within [from,to (exclusive)], add amount to existing value
       Step 3 - Remove values on the left hand side that has 0 as value cause default intensity is 0
       Step 4 - If two intervals have the same intensity, it can be merged except the last value
     */
    @Override
    public void add(long from, long to, long amount) {
        // from cannot be greater than to
        if (from >= to) {
            throw new IllegalArgumentException("from must be less than to");
        }

        // adds appropriate value into map
        set(from, to, amount);

        // merge any consecutive intervals with same intensity
        mergeIntervals();

    }

    @Override
    public void set(long from, long to, long amount) {
        // insert from and to intervals if it doesn't exist, if it does do the math
        insertInterval(from);
        insertInterval(to);

        // Go through each of the keys inside [from, to] and add amount
        // subMap basically lets you extract all the entries from including and to excluding
        for (long key : segments.subMap(from, true, to, false).keySet()) {
            segments.put(key, segments.get(key) + amount);
        }

    }

    private void insertInterval(long point) {
        if (!segments.containsKey(point)) {
            // checks for the leftmost point and return it
            Map.Entry<Long, Long> entry = segments.floorEntry(point);
            long value = entry == null ? 0 : entry.getValue();
            segments.put(point, value);
        }
    }

    private void mergeIntervals() {
        // Remove any interval starting with zero because default is zero
        while (!CollectionUtils.isEmpty(segments) && segments.firstEntry().getValue() == 0) {
            segments.pollFirstEntry();
        }

        // Remove all points that are redundant, meaning they have the same intensity as previous
        List<Long> toRemove = new ArrayList<>();
        Long lastValue = null;

        for (Map.Entry<Long, Long> entry : segments.entrySet()) {
            // if current value same as last, mark it for removal
            if (entry.getValue().equals(lastValue)) {
                toRemove.add(entry.getKey());
            } else {
                lastValue = entry.getValue();
            }

        }

        // Remove redundant keys from the map
        for (long key : toRemove) {
            segments.remove(key);
        }
    }
}
