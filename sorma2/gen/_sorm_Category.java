package com.zoffcc.applications.sorm;

@Table
public class Category
{
    @PrimaryKey(autoincrement = true, auto = true)
    public long id;

    @Column(indexed = true, helpers = Column.Helpers.ALL, defaultExpr = "")
    public String name;
}
