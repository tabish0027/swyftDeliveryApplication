
package io.devbeans.swyft.interface_retrofit_delivery;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RiderActivityDelivery {

    @SerializedName("data")
    @Expose
    public List<Datum> data = null;
    @SerializedName("taskId")
    @Expose
    private String taskId;
    @SerializedName("taskStatus")
    @Expose
    private String taskStatus;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }
    public void removeCompletedActivities(){
        List<Datum> process_data = new ArrayList<>();
        for(int i=0;i<data.size();i++){
            for(int j=0;j<data.get(i).getParcels().size() ;j++){
                if(data.get(i).getParcels().get(j).getStatus().equals("pending")||data.get(i).getParcels().get(j).getStatus().equals("scanned")||data.get(i).getParcels().get(j).getStatus().equals("started")){
                    process_data.add(data.get(i));
                    break;
                }
            }
        }
        data = process_data;
    }
}
