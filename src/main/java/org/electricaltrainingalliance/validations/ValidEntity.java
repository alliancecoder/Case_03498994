package org.electricaltrainingalliance.validations;

/**
 * This interface example and use shows how to implement complex validation
 * within a class above what the javax annotations allow (E.g. ToDos with a
 * priority * > 10 require a description.)
 * 
 * @author Stephen W. Boyd <sboyd@electricaltrainingalliance.org>
 */
public interface ValidEntity {
    boolean isValid();
}