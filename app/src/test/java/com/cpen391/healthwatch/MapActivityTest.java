package com.cpen391.healthwatch;

import com.cpen391.healthwatch.map.MapActivity;
import com.cpen391.healthwatch.stub.StubMap;
import com.cpen391.healthwatch.stub.StubMarkerAnimationFactory;
import com.cpen391.healthwatch.util.GlobalFactory;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

/**
 * Created by william on 2018/3/7.
 *
 */
@RunWith(RobolectricTestRunner.class)
public class MapActivityTest {
    private MapActivity activity;

    @Before
    public void setUp() throws Exception {
        GlobalFactory.setAbstractMarkerAnimationFactory(new StubMarkerAnimationFactory());
        activity = Robolectric.buildActivity(MapActivity.class)
                .create()
                .start()
                .resume()
                .get();
        activity.initMapInterface(new StubMap());
    }

}
