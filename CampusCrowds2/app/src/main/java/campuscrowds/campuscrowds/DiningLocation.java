package campuscrowds.campuscrowds;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Matthew on 9/29/2017.
 * This class if for each of the dining locations
 */

public class DiningLocation {
    private UUID mId;
    private String mLocationName;
    private Date mLastUpdated;
    private Boolean mWasUpdated;

    //Initialize locations with an ID and a name
    public DiningLocation(String name){
        mId = UUID.randomUUID();
        mLocationName = name;
        mWasUpdated = false;
    }

    public UUID getId(){
        return mId;
    }

    public String getLocationName() {
        return mLocationName;
    }

    public void setLocationName(String locationName) {
        mLocationName = locationName;
    }

    //Returns the date that the last picture was taken for the specific location
    public Date getLastUpdated() {
        return mLastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        mLastUpdated = lastUpdated;
    }

    public Boolean getWasUpdated() {
        return mWasUpdated;
    }

    public void setWasUpdated(Boolean wasUpdated) {
        mWasUpdated = wasUpdated;
    }
}
