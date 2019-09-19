package utility;

import java.util.List;

/**
 * Takes list as input and gives ability to iterate through batches of given list.
 * Returns consecutive {List#subList(int, int) sublists} of a list,
 * each of the same size (the final list may be smaller depends on batchSize). For example,
 * partitioning a list containing {@code [1, 2, 3, 4, 5]} with a partition
 * size of 3 return 2 batches {@code [[1, 2, 3], [4, 5]]} -- all in the original order.
 * <p/>
 * @return a list of consecutive sub Lists
 */


public class BatchIterator<T> {

    List<T> data;
    private int batchSize = 0;
    int currentPointer = 0;

    /**
     *
     * @param list original List
     * @param batchSize the desired size of each sublist (the last may be
     *             smaller depends on batchSize)
     */
    public BatchIterator(final List<T> list, final int batchSize){
        this.data = list;
        this.batchSize = batchSize;
    }

    /**
     * @return subList
     */
    public List<T> nextBatch() {
        int beginIndex  = this.currentPointer;
        int endIndex    = 0;
        if (this.currentPointer + batchSize > data.size()){
            endIndex = data.size();
        } else {
            endIndex = this.currentPointer + batchSize;
        }
        this.currentPointer = endIndex;
        return data.subList(beginIndex, endIndex);
    }

    /**
     * @return boolean
     */
    public boolean hasMoreElements() {
        return this.currentPointer < data.size();
    }

}
