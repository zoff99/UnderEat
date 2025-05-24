package com.zoffcc.applications.sorm;

@Table
public class Item
{

    @PrimaryKey
    public String name;

    @Column(indexed = true)
    public String category;

}
