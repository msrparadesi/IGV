/*
 * Copyright (c) 2007-2013 The Broad Institute, Inc.
 * SOFTWARE COPYRIGHT NOTICE
 * This software and its documentation are the copyright of the Broad Institute, Inc. All rights are reserved.
 *
 * This software is supplied without any warranty or guaranteed support whatsoever. The Broad Institute is not responsible for its use, misuse, or functionality.
 *
 * This software is licensed under the terms of the GNU Lesser General Public License (LGPL),
 * Version 2.1 which is available at http://www.opensource.org/licenses/lgpl-2.1.php.
 */

package org.broad.igv.util.stats;

import org.broad.igv.util.collections.IntArrayList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Computes the Kaplan-Meier survival curve
 * <p/>
 * Reference: http://cancerguide.org/scurve_km.html
 *
 * @author jrobinso
 * @date Dec 3, 2010
 */
public class KaplanMeierEstimator {


    /**
     * Return the kaplan-meier curve as a list of intervals.
     *
     * @param time     times in ascending order
     * @param censured array of boolean values indicating if the event is a death or censure
     * @return
     */
    static List<Interval> compute(int[] time, boolean[] censured) {

        if (time.length != censured.length) {
            // throw exception
        }
        if (time.length < 2) {
            // throw exception
        }


        // step 1 -- find the intervals
        ArrayList<Interval> intervals = new ArrayList();
        int startTime = 0;
        int endTime = 0;
        for (int i = 0; i < time.length; i++) {
            endTime = time[i];
            if (censured[i] == false && endTime > startTime) {
                intervals.add(new Interval(startTime, endTime));
                startTime = endTime;
            }
        }
        if (endTime > startTime) {
            intervals.add(new Interval(startTime, endTime));
        }

        // init variables.  Initially everyone is at risk, and the cumulative survival is 1
        float atRisk = time.length;
        float cumulativeSurvival = 1;
        Iterator<Interval> intervalIter = intervals.iterator();
        Interval currentInterval = intervalIter.next();
        currentInterval.setCumulativeSurvival(cumulativeSurvival);

        for (int i = 0; i < time.length; i++) {

            int t = time[i];

            // If we have moved past the current interval compute the cumulative survival and adjust the # at risk
            // for the start of the next interval.
            if (t > currentInterval.getEnd()) {
                atRisk -= currentInterval.getNumberCensured();
                float survivors = atRisk - currentInterval.getNumberDied();
                float tmp = survivors / atRisk;
                cumulativeSurvival *= tmp;

                // Skip to the next interval
                atRisk -= currentInterval.getNumberDied();
                while (intervalIter.hasNext() && t > currentInterval.getEnd()) {
                    currentInterval = intervalIter.next();
                    currentInterval.setCumulativeSurvival(cumulativeSurvival);
                }
            }

            if (censured[i]) {
                currentInterval.addCensure(time[i]);

            } else {
                currentInterval.incDied();
            }
        }
        currentInterval.setCumulativeSurvival(cumulativeSurvival);

        return intervals;

    }


    public static class Interval extends org.broad.igv.data.Interval{
        private int numberDied;
        private IntArrayList censored = new IntArrayList();
        private float cumulativeSurvival;

        public Interval(int start, int end) {
            super(null, start, end);
        }

        void incDied() {
            numberDied++;
        }

        void addCensure(int time) {
            censored.add(time);
        }

        public void setStart(int start) {
            this.start = start;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public int getNumberDied() {
            return numberDied;
        }


        public IntArrayList getCensored() {
            return censored;
        }

        public float getCumulativeSurvival() {
            return cumulativeSurvival;
        }

        public void setCumulativeSurvival(float cumulativeSurvival) {
            this.cumulativeSurvival = cumulativeSurvival;
        }

        public int getNumberCensured() {
            return censored.size();
        }
    }


    public static void main(String[] args) {

        int[] survival = {1, 2, 3, 4, 5, 10, 12};
        boolean[] alive = {false, true, true, false, true, false, true};

        List<Interval> intervals = compute(survival, alive);
        for (Interval i : intervals) {
            System.out.println(i.getStart() + "\t" + i.getEnd() + "\t" + i.getNumberDied() + "\t" + i.getCensored().size() + "\t" + i.getCumulativeSurvival());
        }

    }

}
