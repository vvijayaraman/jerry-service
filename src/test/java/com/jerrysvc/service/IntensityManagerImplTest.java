package com.jerrysvc.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class IntensityManagerImplTest {

    @InjectMocks
    private IntensityManagerImpl intensityManager;

    @Test
    public void add_simplePositiveRange_correctSegments() {
        intensityManager.add(10, 20, 5);
        assertEquals(List.of(
                Map.entry(10L, 5L),
                Map.entry(20L, 0L)
        ), getSegmentList());
    }

    @Test
    public void add_sameIntervals_correctSegments() {
        intensityManager.add(10, 20, 5);
        intensityManager.add(10, 20, 5);
        intensityManager.add(10, 20, 5);

        assertEquals(List.of(
                Map.entry(10L, 15L),
                Map.entry(20L, 0L)
        ), getSegmentList());
    }

    @Test
    public void shouldMergeDisjointRangesCorrectly() {
        intensityManager.add(10, 20, 3);
        intensityManager.add(30, 40, 4);
        assertEquals(List.of(
                Map.entry(10L, 3L),
                Map.entry(20L, 0L),
                Map.entry(30L, 4L),
                Map.entry(40L, 0L)
        ), getSegmentList());
    }

    @Test
    public void add_reducesToZero_segmentsCleaned() {
        intensityManager.add(10, 20, 5);
        intensityManager.add(10, 20, -5); // cancels out
        assertTrue(intensityManager.get().isEmpty());
    }

    @Test
    public void add_zeroAmount_noOp() {
        intensityManager.add(10, 20, 0);
        assertTrue(intensityManager.get().isEmpty());
    }

    @Test
    public void add_whenSequenceAdded_returnCorrectIntervals() {

        // Step 1: add(10, 30, 1)
        intensityManager.add(10, 30, 1);
        List<Map.Entry<Long, Long>> segments = getSegmentList();
        assertEquals(10L, segments.get(0).getKey());
        assertEquals(1L, segments.get(0).getValue());
        assertEquals(30L, segments.get(1).getKey());
        assertEquals(0L, segments.get(1).getValue());

        // Step 2: add(20, 40, 1)
        intensityManager.add(20, 40, 1);
        List<Map.Entry<Long, Long>> segments2 = getSegmentList();
        assertEquals(4, segments2.size());

        assertEquals(10L, segments2.get(0).getKey());
        assertEquals(1L, segments2.get(0).getValue());

        assertEquals(20L, segments2.get(1).getKey());
        assertEquals(2L, segments2.get(1).getValue());

        assertEquals(30L, segments2.get(2).getKey());
        assertEquals(1L, segments2.get(2).getValue());

        assertEquals(40L, segments2.get(3).getKey());
        assertEquals(0L, segments2.get(3).getValue());

        // Step 3: add(10, 40, -2)
        intensityManager.add(10, 40, -2);
        assertEquals(
                List.of(
                        Map.entry(10L, -1L),
                        Map.entry(20L, 0L),
                        Map.entry(30L, -1L),
                        Map.entry(40L, 0L)
                ),
                getSegmentList()
        );
    }

    @Test
    public void add_whenMultipleSequentialAdds_returnCorrectSegments() {

        // Step 1: add(10, 30, 1)
        intensityManager.add(10, 30, 1);
        assertEquals(
                List.of(
                        Map.entry(10L, 1L),
                        Map.entry(30L, 0L)
                ),
                getSegmentList()
        );

        intensityManager.add(20, 40, 1);
        assertEquals(
                List.of(
                        Map.entry(10L, 1L),
                        Map.entry(20L, 2L),
                        Map.entry(30L, 1L),
                        Map.entry(40L, 0L)
                ),
                getSegmentList()
        );

        intensityManager.add(10, 40, -1);
        assertEquals(
                List.of(
                        Map.entry(20L, 1L),
                        Map.entry(30L, 0L)
                ),
                getSegmentList()
        );

        intensityManager.add(10, 40, -1);
        assertEquals(
                List.of(
                        Map.entry(10L, -1L),
                        Map.entry(20L, 0L),
                        Map.entry(30L, -1L),
                        Map.entry(40L, 0L)
                ),
                getSegmentList()
        );
    }

    // Helper method to extract segments as a list of Map.Entry for assertion
    private List<Map.Entry<Long, Long>> getSegmentList() {
        return new ArrayList<>(intensityManager.get().entrySet());
    }


}