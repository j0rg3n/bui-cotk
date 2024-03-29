//
// $Id$
//
// BUI - a user interface library for the JME 3D engine
// Copyright (C) 2005, Michael Bayne, All Rights Reserved
//
// This library is free software; you can redistribute it and/or modify it
// under the terms of the GNU Lesser General Public License as published
// by the Free Software Foundation; either version 2.1 of the License, or
// (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package com.jmex.bui;

import com.jme.renderer.Renderer;

import com.jmex.bui.icon.BIcon;
import com.jmex.bui.util.Dimension;

/**
 * A simple component for displaying a textual label.
 */
public class BLabel extends BTextComponent
    implements BConstants
{
    /** Configures the label's strategy when it does not fit into its allocated
     * space. */
    public enum Fit { WRAP, TRUNCATE, SCALE };

    /**
     * Creates a label that will display the supplied text.
     */
    public BLabel (String text)
    {
        this(text, null);
    }

    /**
     * Creates a label that will display the supplied text using the specified
     * style class.
     */
    public BLabel (String text, String styleClass)
    {
    	this(text, styleClass, null);
    	/*
        _label = new Label(this);
        _label.setText(text);
        setStyleClass(styleClass);
        */
    }

    /**
     * Creates a label that will display the supplied icon.
     */
    public BLabel (BIcon icon)
    {
        this(icon, null);
    }

    /**
     * Creates a label that will display the supplied icon using the specified
     * style class.
     */
    public BLabel (BIcon icon, String styleClass)
    {
    	this(null, styleClass, icon);
    	/*
        _label = new Label(this);
        _label.setIcon(icon);
        setStyleClass(styleClass);
        */
    }
    
    /**
     * Creates a label that will display the supplied text and icon using the specified
     * style class. All arguments can be null.
     */
	public BLabel(String text, String styleClass, BIcon icon)
	{
		super();
		_label = new Label(this);
		if (text != null)
		{
			_label.setText(text);
		}
		if (styleClass != null)
		{
			setStyleClass(styleClass);
		}
		if (icon != null)
		{
			_label.setIcon(icon);
		}
	}
    /**
     * Configures the label to display the specified icon.
     */
    public void setIcon (BIcon icon)
    {
        _label.setIcon(icon);
    }

    /**
     * Returns the icon being displayed by this label.
     */
    public BIcon getIcon ()
    {
        return _label.getIcon();
    }

    /**
     * Configures the gap between the icon and the text.
     */
    public void setIconTextGap (int gap)
    {
        _label.setIconTextGap(gap);
    }

    /**
     * Returns the gap between the icon and the text.
     */
    public int getIconTextGap ()
    {
        return _label.getIconTextGap();
    }

    /**
     * Sets the orientation of this label with respect to its icon. If the
     * horizontal (the default) the text is displayed to the right of the icon,
     * if vertical the text is displayed below it.
     */
    public void setOrientation (int orient)
    {
        _label.setOrientation(orient);
    }

    /**
     * Configures whether this label will wrap, truncate or scale if it cannot
     * fit text into its allotted width. The default is to wrap.
     */
    public void setFit (Fit mode)
    {
        _label.setFit(mode);
    }
    
	public Fit getFit()
	{
		return _label._fit;
	}


    // documentation inherited
    public void setText (String text)
    {
        _label.setText(text);
    }

    // documentation inherited
    public String getText ()
    {
        return _label.getText();
    }

    // documentation inherited
    protected String getDefaultStyleClass ()
    {
        return "label";
    }

    // documentation inherited
    protected void wasAdded ()
    {
        super.wasAdded();
        _label.wasAdded();
    }

    // documentation inherited
    protected void wasRemoved ()
    {
        super.wasRemoved();
        _label.wasRemoved();
    }

    // documentation inherited
    protected void layout ()
    {
        super.layout();
        _label.layout(getInsets(), getWidth(), getHeight());
    }

    // documentation inherited
    protected void renderComponent (Renderer renderer)
    {
        super.renderComponent(renderer);
        _label.render(renderer, 0, 0, getWidth(), getHeight(), _alpha);
    }

    // documentation inherited
    protected Dimension computePreferredSize (int whint, int hhint)
    {
        return _label.computePreferredSize(whint, hhint);
    }

    protected Label _label;
}
