package com.myth.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Entity
@Table(name = "users")
@ApiModel(value="user", description = "Contains user's properties")
public class User extends BaseModel {

    @ApiModelProperty(example = "1")
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @ApiModelProperty(example = "Mike")
    @Column(name = "name")
    private String name;

    public User() {
    }

    public long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}
