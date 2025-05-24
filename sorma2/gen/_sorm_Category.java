package com.zoffcc.applications.sorm;

@Table
public class Category
{

    @PrimaryKey
    public long id;

    @Column(uniqueOnConflict = OnConflict.IGNORE)
    public String name;

}
