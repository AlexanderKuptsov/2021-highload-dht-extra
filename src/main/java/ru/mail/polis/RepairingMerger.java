package ru.mail.polis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class RepairingMerger {
    /**
     * Merges <b>sorted</b> streams of {@link Record}s from cluster {@link Node}s and
     * <b>repairs</b> discrepancies using {@link Node#update(Record)} facility.
     * <p>
     * The priority of {@link Record}s:
     * <ol>
     *     <li>key is lesser</li>
     *     <li>timestamp is greater</li>
     *     <li>tombstone</li>
     *     <li>value is lesser</li>
     * </ol>
     *
     * @param nodesWithIterators mapping of {@link Node}s to corresponding {@link Record} streams
     * @return sorted stream of the freshest {@link Record} <b>without tombstones</b>
     */
    public static Iterator<Record> mergeAndRepair(Map<Node, Iterator<Record>> nodesWithIterators) {
        Map<Node, PeekingIterator> map = new HashMap<>();

        for (Map.Entry<Node, Iterator<Record>> entry : nodesWithIterators.entrySet()) {
            map.put(entry.getKey(), new PeekingIterator(entry.getValue()));
        }
        MergeMultiIterator mergeMultiIterator = new MergeMultiIterator(map);
        return new MergeTombstoneMultiIterator(mergeMultiIterator);
    }
}
