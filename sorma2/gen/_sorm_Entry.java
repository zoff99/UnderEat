package com.zoffcc.applications.sorm;

/**
 * An example for table connstraints and indexes.
 */
@Table
public class Entry
{

    @PrimaryKey
    public long id;


    @Column
    public String resourceType;

    @Column
    public long resourceId;
}
