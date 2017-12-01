package com.gtfs;

import java.util.*;
import java.io.*;

public class calendar
{
    //stop_id,stop_code,stop_name,stop_lat,stop_lon
    public String service_id;
    public String date;

    public calendar (String id, String d)
    {
        this.service_id = id;
        this.date = d;
    }


}
