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

import org.lwjgl.opengl.GL11;
import com.jme.renderer.Renderer;
import com.jmex.bui.event.MouseWheelListener;
import com.jmex.bui.layout.BorderLayout;
import com.jmex.bui.util.Dimension;
import com.jmex.bui.util.Insets;

/**
 * Provides a scrollable clipped view on a sub-heirarchy of components.
 */
public class BScrollPane extends BContainer
{
	public BScrollPane(BComponent child)
	{
		this(child, true, false);
	}

	public BScrollPane(BComponent child, boolean vert, boolean horiz)
	{
		this(child, true, false, 1);
	}

	public BScrollPane(BComponent child, boolean vert, boolean horiz, int snap)
	{
		super(new BorderLayout(0, 0));
		add(_vport = new BViewport(child, vert, horiz, snap), BorderLayout.CENTER);
		if (vert)
		{
			add(_vbar = new BScrollBar(BConstants.VERTICAL, _vport.getVModel()), BorderLayout.EAST);
		}
		if (horiz)
		{
			add(_hbar = new BScrollBar(BConstants.HORIZONTAL, _vport.getHModel()), BorderLayout.SOUTH);
		}
	}

	/**
	 * Returns a reference to the child of this scroll pane.
	 */
	public BComponent getChild()
	{
		return _vport.getTarget();
	}

	/**
	 * Returns a reference to the vertical scroll bar.
	 */
	public BScrollBar getVerticalScrollBar()
	{
		return _vbar;
	}

	/**
	 * Returns a reference to the horizontal scroll bar.
	 */
	public BScrollBar getHorizontalScrollBar()
	{
		return _hbar;
	}
	/**
	 * Does all the heavy lifting for the {@link BScrollPane}. TODO: support
	 * horizontal scrolling as well.
	 */
	public static class BViewport extends BContainer
	{
		public BViewport(BComponent target, boolean vert, boolean horiz, int snap)
		{
			if (vert)
			{
				_vmodel = new BoundedSnappingRangeModel(0, 0, 10, 10, snap);
			}
			if (horiz)
			{
				_hmodel = new BoundedSnappingRangeModel(0, 0, 10, 10, snap);
			}
			add(_target = target);
		}

		/**
		 * Returns a reference to the target of this viewport.
		 */
		public BComponent getTarget()
		{
			return _target;
		}

		/**
		 * Returns the range model defined by this viewport's size and the
		 * preferred size of its target component.
		 */
		public BoundedRangeModel getVModel()
		{
			return _vmodel;
		}

		/**
		 * Returns the range model defined by this viewport's size and the
		 * preferred size of its target component.
		 */
		public BoundedRangeModel getHModel()
		{
			return _hmodel;
		}

		// documentation inherited
		@Override
		public void invalidate()
		{
			// if we're not attached, don't worry about it
			BWindow window;
			BRootNode root;
			if (!_valid || (window = getWindow()) == null || (root = window.getRootNode()) == null)
			{
				return;
			}
			if (_valid)
			{
				getParent().invalidate();
			}
			_valid = false;
			root.rootInvalidated(this);
		}

		// documentation inherited
		@Override
		public void layout()
		{
			// resize our target component to the larger of our size and its
			// preferred size
			Insets insets = getInsets();
			int twidth = getWidth() - insets.getHorizontal();
			int theight = getHeight() - insets.getVertical();
			Dimension d = _target.getPreferredSize(twidth, theight);
			d.width = (_hmodel != null) ? Math.max(d.width, twidth) : twidth;
			d.height = (_vmodel != null) ? Math.max(d.height, theight) : theight;
			if (_target.getWidth() != d.width || _target.getHeight() != d.height)
			{
				_target.setBounds(insets.left, insets.bottom, d.width, d.height);
			}
			// lay out our target component
			_target.layout();
			// and recompute our scrollbar range
			if (_vmodel != null)
			{
				System.out.println("value[1]:" + _vmodel.getValue());
				_vmodel.setRange(0, _vmodel.getValue(), getHeight() - insets.getVertical(), d.height);
				System.out.println("value[1]:" + _vmodel.getValue());
			}
			if (_hmodel != null)
			{
				_hmodel.setRange(0, _hmodel.getValue(), getWidth() - insets.getHorizontal(), d.width);
			}
		}

		// documentation inherited
		@Override
		public int getAbsoluteX()
		{
			return super.getAbsoluteX() + getXOffset();
		}

		// documentation inherited
		@Override
		public int getAbsoluteY()
		{
			return super.getAbsoluteY() + getYOffset();
		}

		// documentation inherited
		@Override
		public BComponent getHitComponent(int mx, int my)
		{
			// if we're not within our bounds, we needn't check our target
			Insets insets = getInsets();
			if ((mx < _x + insets.left) || (my < _y + insets.bottom) || (mx >= _x + _width - insets.right) || (my > _y + _height - insets.top))
			{
				return null;
			}
			// translate the coordinate into our children's coordinates
			mx -= (_x + insets.left + getXOffset());
			my -= (_y + insets.bottom + getYOffset());
			BComponent hit = null;
			for (int ii = 0, ll = getComponentCount(); ii < ll; ii++)
			{
				BComponent child = getComponent(ii);
				if ((hit = child.getHitComponent(mx, my)) != null)
				{
					return hit;
				}
			}
			return this;
		}

		// documentation inherited
		@Override
		protected void wasAdded()
		{
			super.wasAdded();
			if (_vmodel != null)
			{
				addListener(_wheelListener = _vmodel.createWheelListener());
			}
			else if (_hmodel != null)
			{
				addListener(_wheelListener = _hmodel.createWheelListener());
			}
		}

		// documentation inherited
		@Override
		protected void wasRemoved()
		{
			super.wasRemoved();
			if (_wheelListener != null)
			{
				removeListener(_wheelListener);
				_wheelListener = null;
			}
		}

		// documentation inherited
		@Override
		protected Dimension computePreferredSize(int whint, int hhint)
		{
			return new Dimension(_target.getPreferredSize(whint, hhint));
		}

		// documentation inherited
		@Override
		protected void renderComponent(Renderer renderer)
		{
			// translate by our offset into the viewport
			Insets insets = getInsets();
			int yoffset = getYOffset();
			int xoffset = getXOffset();
			GL11.glTranslatef(xoffset, yoffset, 0);
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			GL11.glScissor((getAbsoluteX() + insets.left) - xoffset, (getAbsoluteY() + insets.bottom) - yoffset, _width - insets.getHorizontal(), _height - insets.getVertical());
			try
			{
				// and then render our target component
				_target.render(renderer);
			}
			finally
			{
				GL11.glDisable(GL11.GL_SCISSOR_TEST);
				GL11.glTranslatef(-xoffset, -yoffset, 0);
			}
		}

		protected final int getYOffset()
		{
			return _vmodel == null ? 0 : _vmodel.getValue() - (_vmodel.getMaximum() - _vmodel.getExtent());
		}

		protected final int getXOffset()
		{
			return _hmodel == null ? 0 : -_hmodel.getValue();
		}
		protected BoundedRangeModel _vmodel, _hmodel;
		protected BComponent _target;
		protected MouseWheelListener _wheelListener;
	}
	protected BViewport _vport;
	protected BScrollBar _vbar, _hbar;
}
