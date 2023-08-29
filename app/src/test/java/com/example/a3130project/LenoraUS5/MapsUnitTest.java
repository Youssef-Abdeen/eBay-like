package com.example.a3130project.LenoraUS5;//package com.example.a3130project.Maps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import com.google.android.gms.maps.model.LatLng;

public class MapsUnitTest {

    Item example = new Item("Statue of Liberty", "A famous landmark in New York City", 40.6892, -74.0445);

    @Test
    public void items_NotNull(){ assertNotNull(MapsActivity.getItemList());}

    @Test
    public void marker_CreatedCorrectly(){
        LatLng sydney = new LatLng(-34, 151);
        assertEquals(-34, sydney.latitude, 0);
    }
    @Test
    public void list_ContainsLandmark(){
        assertFalse(MapsActivity.getItemList().contains(example));
    }


}
