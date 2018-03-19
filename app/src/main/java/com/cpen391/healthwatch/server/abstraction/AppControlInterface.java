package com.cpen391.healthwatch.server.abstraction;

import com.android.volley.Request;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by william on 2018/3/6.
 * This interface keeps track of network resources, that is used for the server interface.
 */
public interface AppControlInterface {
    /**
     * Adds the specified request to the global queue using the default TAG.
     *
     * @param req the request to add to the request queue.
     * @param <T> the type of the request.
     */
    <T> void addToRequestQueue(Request<T> req);

    /**
     *
     * @return the image loader used for image caching purposes.
     */
    ImageLoader getImageLoader();
}
