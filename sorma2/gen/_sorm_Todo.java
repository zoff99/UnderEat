package com.zoffcc.applications.sorm;

@Table
public class Todo
{


    @PrimaryKey
    public long id;

    @Column(indexed = true)
    public String title;

    @Column
    @Nullable
    public String content;

    @Column(indexed = true, defaultExpr = "0")
    public boolean done;

    @Column(indexed = true, helpers = Column.Helpers.ORDERS, defaultExpr = "0")
    public String createdTime;
}

