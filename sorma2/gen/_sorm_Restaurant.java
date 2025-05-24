package com.zoffcc.applications.sorm;

@Table
public class Restaurant
{
    @PrimaryKey(autoincrement = true, auto = true)
    public long id;

    @Column()
    public String name;
    @Column()
    public long category_id;
    @Column()
    public String address;
    @Column()
    public String area_code;
    @Column()
    public long lat;
    @Column()
    public long lon;
    @Column()
    public int rating;
    @Column()
    public String comment;
    @Column()
    public boolean active;
    @Column()
    public boolean for_summer;
}
