package campuscrowds.campuscrowds;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Matthew on 9/29/2017.
 * This class is used for holding all of the locations, a LocationBucket is referenced each time a location is accessed
 */

public class LocationBucket {
    private static LocationBucket sLocationBucket;
    private List<DiningLocation> mLocations;

    public static LocationBucket get(Context context){
        if(sLocationBucket == null){
            sLocationBucket = new LocationBucket(context);
        }
        return sLocationBucket;
    }

    private LocationBucket(Context context){
        mLocations = new ArrayList<>();
        DiningLocation Aromas = new DiningLocation("Aromas");
        DiningLocation Berts = new DiningLocation("Berts");
        DiningLocation LaPaloma = new DiningLocation("La Paloma");
        DiningLocation MissionCafe = new DiningLocation("Mission Cafe");
        DiningLocation SLP = new DiningLocation("SLP");
        DiningLocation ToreroTruck = new DiningLocation("Torero Truck");
        DiningLocation TuMerc = new DiningLocation("Tu Merc");
        mLocations.add(Aromas);
        mLocations.add(Berts);
        mLocations.add(LaPaloma);
        mLocations.add(MissionCafe);
        mLocations.add(SLP);
        mLocations.add(ToreroTruck);
        mLocations.add(TuMerc);
    }

    public List<DiningLocation> getLocations(){
        return mLocations;
    }

    public DiningLocation getLocation(UUID id){
        for(DiningLocation DL: mLocations){
            if(DL.getId().equals(id)){
                return DL;
            }
        }
        return null;
    }
}
