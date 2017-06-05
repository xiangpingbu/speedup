package com.ecreditpal.maas.service.model.minganci.wordcut;

/**
 * <p>Node class.</p>
 *
 * @author xibu
 * @version $Id: $Id
 */
public class Node {
    public Character value;
    public Node parent;

    /**
     * <p>Constructor for Node.</p>
     *
     * @param value a {@link Character} object.
     * @param parent a {@link Node} object.
     */
    public Node(Character value, Node parent) {
        this.value = value;
        this.parent = parent;
    }
}
