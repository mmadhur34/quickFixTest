package com.home.madhur;

import java.util.List;

/**
 * Created by madhur on 2/19/2017.
 */
public class Statistics {
    public double min = -1;
    public double max;
    public double avg;
    public long sum;

    Statistics(long[] list){

        int count = 0;
        for (long num : list) {
            sum += num;
            count++;

            if(min == -1) min= num;

            if (num > max) {
                max = num;
            }
            if (num < min) {
                min = num;
            }
        }

        this.avg = sum/(double)count;
    }

}
