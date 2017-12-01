package com.gtfs;

import java.util.*;
import java.io.*;

public class graphStops
{
    //stop_id,stop_code,stop_name,stop_lat,stop_lon
    public String stop_id;
    public String trip_id;
    public long dept_time;

    public graphStops (String id, String name, long d)
    {
        this.stop_id = id;
        this.trip_id = name;
        this.dept_time = d;
    }


}
