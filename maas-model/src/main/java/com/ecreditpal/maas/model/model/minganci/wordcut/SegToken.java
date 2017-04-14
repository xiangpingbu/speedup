package com.ecreditpal.maas.model.model.minganci.wordcut;

/**
 * <p>SegToken class.</p>
 *
 * @author xibu
 * @version $Id: $Id
 */
public class SegToken {
    public String word;

    public int startOffset;

    public int endOffset;


    /**
     * <p>Constructor for SegToken.</p>
     *
     * @param word a {@link String} object.
     * @param startOffset a int.
     * @param endOffset a int.
     */
    public SegToken(String word, int startOffset, int endOffset) {
        this.word = word;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }


    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "[" + word + ", " + startOffset + ", " + endOffset + "]";
    }

}
