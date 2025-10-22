package com.filehub.FileHubServer;

import org.springframework.stereotype.Service;

import java.util.Date;

import static com.filehub.FileHubServer.FMConstants.sdf;
@Service
public class Utils {
    public String millisToTime(long millis)
    {
        return sdf.format(new Date(millis));
    }

}
