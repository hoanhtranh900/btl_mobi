package com.mrash.instagramclone.Model;

import java.util.Date;

public class User {
    private Long id;
//    @Column(name = "USER_NAME")
    private String username;

//    @JsonIgnore
//    @Column(name = "PASSWORD")
    private String password;

//    @Column(name = "ADDRESS")
    private String address;

//    @Column(name = "TYPE")
    private Long type;

//    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

//    @Column(name = "SURNAME")
    private String surname;

//    @Column(name = "GIVEN_NAME")
    private String givenName;

//    @Column(name = "FULL_NAME")
    private String fullName;

//    @Column(name = "STATUS")
    private Long status;

//    @Column(name = "IS_DELETE")
    private Long isDelete;

//    @Column(name = "EMAIL")
    private String email;

//    @Column(name = "POSITION")
    private String position;

//    @Column(name = "GENDER", length = 1)
    private Long gender;

//    @Column(name = "BIRTHDAY")
    private Date birthday;

//    @Transient
    private String statusStr;
//    @Transient
    private String isDeleteStr;
//    @Transient
    private String addressFull;

//    @Transient
    private String avatar;

//    @org.hibernate.annotations.Comment("ID người tạo")
//    @Column(name = "CREATOR_ID")
    private Long creatorId;

//    @Column(name = "CREATOR_NAME")
    private String creatorName;

//    @Column(name = "CREATE_TIME")
    private Date createTime;
//    @Column(name = "UPDATER_ID")
    private Long updatorId;
//    @Column(name = "UPDATER_NAME")
    private String updatorName;
//    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Long isDelete) {
        this.isDelete = isDelete;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Long getGender() {
        return gender;
    }

    public void setGender(Long gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getIsDeleteStr() {
        return isDeleteStr;
    }

    public void setIsDeleteStr(String isDeleteStr) {
        this.isDeleteStr = isDeleteStr;
    }

    public String getAddressFull() {
        return addressFull;
    }

    public void setAddressFull(String addressFull) {
        this.addressFull = addressFull;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getUpdatorId() {
        return updatorId;
    }

    public void setUpdatorId(Long updatorId) {
        this.updatorId = updatorId;
    }

    public String getUpdatorName() {
        return updatorName;
    }

    public void setUpdatorName(String updatorName) {
        this.updatorName = updatorName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public User() {
    }

    public User(Long id, String username, String password, String address, Long type, String phoneNumber, String surname, String givenName, String fullName, Long status, Long isDelete, String email, String position, Long gender, Date birthday, String statusStr, String isDeleteStr, String addressFull, String avatar, Long creatorId, String creatorName, Date createTime, Long updatorId, String updatorName, Date updateTime) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.address = address;
        this.type = type;
        this.phoneNumber = phoneNumber;
        this.surname = surname;
        this.givenName = givenName;
        this.fullName = fullName;
        this.status = status;
        this.isDelete = isDelete;
        this.email = email;
        this.position = position;
        this.gender = gender;
        this.birthday = birthday;
        this.statusStr = statusStr;
        this.isDeleteStr = isDeleteStr;
        this.addressFull = addressFull;
        this.avatar = avatar;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.createTime = createTime;
        this.updatorId = updatorId;
        this.updatorName = updatorName;
        this.updateTime = updateTime;
    }
}
