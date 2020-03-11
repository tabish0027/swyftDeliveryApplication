package io.devbeans.swyft.interface_retrofit;

public class markattendance {
    public String riderId;
    public String attendanceId;

    public markattendance(String riderId, String attendanceId) {
        this.riderId = riderId;
        this.attendanceId = attendanceId;
    }

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public String getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }
}
