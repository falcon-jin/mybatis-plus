package com.falcon.mybatisplustest.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 用户实体对应表 user
 * </p>
 *
 * @author hubin
 * @since 2018-08-11
 */
@Data
@TableName("framework_user")
public class User {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */

    @TableId(value = "id", type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    @JsonSerialize(using = ToStringSerializer.class)
    private Long enterpriseId;


    private String code;


    private String account;


    private String password;


    private String name;


    private String realName;


    private String avatar;


    private String email;


    private String phone;


    private Date birthday;


    private Integer sex;


    @JsonSerialize(using = ToStringSerializer.class)
    private Long roleId;


    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;


    @JsonSerialize(using = ToStringSerializer.class)
    private Long postId;


    @JsonSerialize(using = ToStringSerializer.class)
    private Long firstLogin;


    @JsonSerialize(using = ToStringSerializer.class)
    private Long lastLoginEnterpriseId;


    @TableField(exist = false)
    private String roleName;


    @TableField(exist = false)
    private String deptName;


    @TableField(exist = false)
    private String postName;


}
