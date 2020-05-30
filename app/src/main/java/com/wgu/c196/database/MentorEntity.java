package com.wgu.c196.database;

import android.arch.persistence.room.*;

@Entity(tableName = "mentors",
        indices = {@Index(value = {"name", "phoneNumber", "email"},
            unique = true
        )}
)
public class MentorEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;

    private String phoneNumber;

    private String email;

    /**
     * Constructor for creating a new mentor with an auto-generated id
     *
     * @param name String
     * @param phoneNumber String
     * @param email String
     */
    @Ignore
    public MentorEntity(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    /**
     * Constructor for when you want to create a new mentor and then assign values
     * separately
     */
    @Ignore
    public MentorEntity() { }

    /**
     * Constructor for editing an existing mentor
     *
     * @param id int
     * @param name String
     * @param phoneNumber Date
     * @param email Date
     */
    public MentorEntity(int id, String name, String phoneNumber, String email) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
