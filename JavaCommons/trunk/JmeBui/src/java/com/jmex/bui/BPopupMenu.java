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

import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.BEvent;
import com.jmex.bui.event.MouseEvent;
import com.jmex.bui.layout.GroupLayout;

/**
 * Displays a popup menu of items, one of which can be selected.
 */
public class BPopupMenu extends BPopupWindow
{
	public BPopupMenu(BWindow parent)
	{
		this(parent, false);
	}

	public BPopupMenu(BWindow parent, boolean horizontal)
	{
		this(parent, horizontal, -1);
	}

	public BPopupMenu(BWindow parent, boolean horizontal, int width)
	{
		super(parent, null, width);
		GroupLayout gl = horizontal ? GroupLayout.makeHStretch() : GroupLayout.makeVStretch();
		gl.setGap(0);
		setLayoutManager(gl);
		_modal = true;
	}

	/**
	 * Adds the supplied item to this menu.
	 */
	public void addMenuItem(BMenuItem item)
	{
		// nothing more complicated needs to be done, yay!
		add(item, GroupLayout.FIXED);
	}

	// documentation inherited
	@Override
	public boolean dispatchEvent(BEvent event)
	{
		if (event instanceof MouseEvent)
		{
			MouseEvent mev = (MouseEvent) event;
			// if the mouse clicked outside of our window bounds, dismiss
			// ourselves
			if (mev.getType() == MouseEvent.MOUSE_PRESSED && getHitComponent(mev.getX(), mev.getY()) == null)
			{
				dismiss();
				return true;
			}
		}
		return super.dispatchEvent(event);
	}

	// documentation inherited
	@Override
	protected String getDefaultStyleClass()
	{
		return "popupmenu";
	}

	/**
	 * Called by any child {@link BMenuItem}s when they are selected.
	 */
	protected void itemSelected(BMenuItem item, long when, int modifiers)
	{
		emitEvent(new ActionEvent(item, when, modifiers, item.getAction()));
		dismiss();
	}
}
