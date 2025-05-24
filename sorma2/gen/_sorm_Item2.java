package com.zoffcc.applications.sorm;

@Table
public class Item2
{

    @PrimaryKey
    public String name;

    @Column(indexed = true)
    public String category1;

    @Nullable
    @Column(indexed = true)
    public String category2;

    @Column
    public String zonedTimestamp;

    @Column
    public String localDateTime;
}

