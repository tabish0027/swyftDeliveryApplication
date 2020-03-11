package io.devbeans.swyft.interface_retrofit;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("zoneIds")
    @Expose
    private List<String> zoneIds = null;
    @SerializedName("timeSlotIds")
    @Expose
    private List<String> timeSlotIds = null;
    @SerializedName("vehicleId")
    @Expose
    private String vehicleId;
    @SerializedName("isOnline")
    @Expose
    private Boolean isOnline;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("profilePicture")
    @Expose
    private String profilePicture;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("todayHours")
    @Expose
    private Double todayHours;
    @SerializedName("previousWeekHours")
    @Expose
    private Double previousWeekHours;
    @SerializedName("previousMonthHours")
    @Expose
    private Double previousMonthHours;
    @SerializedName("todayDistance")
    @Expose
    private Double todayDistance;
    @SerializedName("previousWeekDistance")
    @Expose
    private Double previousWeekDistance;
    @SerializedName("previousMonthDistance")
    @Expose
    private Double previousMonthDistance;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getZoneIds() {
        return zoneIds;
    }

    public void setZoneIds(List<String> zoneIds) {
        this.zoneIds = zoneIds;
    }

    public List<String> getTimeSlotIds() {
        return timeSlotIds;
    }

    public void setTimeSlotIds(List<String> timeSlotIds) {
        this.timeSlotIds = timeSlotIds;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getTodayHours() {
        return todayHours;
    }

    public void setTodayHours(Double todayHours) {
        this.todayHours = todayHours;
    }

    public Double getPreviousWeekHours() {
        return previousWeekHours;
    }

    public void setPreviousWeekHours(Double previousWeekHours) {
        this.previousWeekHours = previousWeekHours;
    }

    public Double getPreviousMonthHours() {
        return previousMonthHours;
    }

    public void setPreviousMonthHours(Double previousMonthHours) {
        this.previousMonthHours = previousMonthHours;
    }

    public Object getTodayDistance() {
        return todayDistance;
    }

    public void setTodayDistance(Double todayDistance) {
        this.todayDistance = todayDistance;
    }

    public Double getPreviousWeekDistance() {
        return previousWeekDistance;
    }

    public void setPreviousWeekDistance(Double previousWeekDistance) {
        this.previousWeekDistance = previousWeekDistance;
    }

    public Double getPreviousMonthDistance() {
        return previousMonthDistance;
    }

    public void setPreviousMonthDistance(Double previousMonthDistance) {
        this.previousMonthDistance = previousMonthDistance;
    }

}
