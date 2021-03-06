/*
 * Copyright (c) 2007-2012 The Broad Institute, Inc.
 * SOFTWARE COPYRIGHT NOTICE
 * This software and its documentation are the copyright of the Broad Institute, Inc. All rights are reserved.
 *
 * This software is supplied without any warranty or guaranteed support whatsoever. The Broad Institute is not responsible for its use, misuse, or functionality.
 *
 * This software is licensed under the terms of the GNU Lesser General Public License (LGPL),
 * Version 2.1 which is available at http://www.opensource.org/licenses/lgpl-2.1.php.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.broad.igv.ui.panel;

import com.jidesoft.swing.JideScrollPane;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * @author jrobinso
 */
public class TrackPanelScrollPane extends JideScrollPane implements Paintable {

    private static Logger log = Logger.getLogger(TrackPanelScrollPane.class);

    TrackPanel trackPanel;
    boolean isScrolling = false;

    public TrackPanelScrollPane() {
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        setForeground(new java.awt.Color(153, 153, 153));
        setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        getVerticalScrollBar().setUnitIncrement(16);

        addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
                trackPanel.getNamePanel().repaint();
            }
        });

        // A fix for name panel painting problems.   Not sure why this is neccessary, but it is.
        // The adustment listener forces a repaint after a scroll action is complete.
        final JScrollBar sb = this.getVerticalScrollBar();
        sb.addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
                if (isScrolling) {
                    if (!adjustmentEvent.getValueIsAdjusting()) {
                        isScrolling = false;
                        trackPanel.getNamePanel().repaint();
                    }
                } else {
                    isScrolling = adjustmentEvent.getValueIsAdjusting();
                }

            }
        });
    }

    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        if (trackPanel != null)
            trackPanel.setBackground(color);
    }

    @Override
    public void setViewportView(Component trackSetView) {
        if (!(trackSetView instanceof TrackPanel)) {
            throw new IllegalArgumentException("Class TrackPanelScrollPane can only contain a TrackPanel");
        }
        super.setViewportView(trackSetView);
        this.trackPanel = (TrackPanel) trackSetView;
    }

    public TrackPanel getTrackPanel() {
        return trackPanel;
    }


    public String getTrackPanelName() {
        return trackPanel.getName();
    }

    public void minimizeHeight() {
        int prefHeight = trackPanel.getPreferredPanelHeight();
        if (prefHeight < trackPanel.getViewportHeight()) {
            this.setSize(getWidth(), prefHeight);
        }
    }

    public DataPanelContainer getDataPanel() {
        return trackPanel.getDataPanelContainer();
    }

    public boolean isEmpty() {
        return !trackPanel.hasTracks();
    }

    public TrackNamePanel getNamePanel() {
        return trackPanel.getNamePanel();
    }

    @Override
    public void doLayout() {
        log.trace("Layout");
        super.doLayout();    //To change body of overridden methods use File | Settings | File Templates.
        trackPanel.revalidate();
        //trackPanel.doLayout();
    }

    public void paintOffscreen(Graphics2D g, Rectangle tspRect) {

        trackPanel.paintOffscreen(g, tspRect);
    }
}
