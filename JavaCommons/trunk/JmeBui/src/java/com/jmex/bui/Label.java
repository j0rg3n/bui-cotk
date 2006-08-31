//
// $Id$
//
// BUI - a user interface library for the JME 3D engine
// Copyright (C) 2005-2006, Michael Bayne, All Rights Reserved
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

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jmex.bui.icon.BIcon;
import com.jmex.bui.text.BText;
import com.jmex.bui.text.BTextFactory;
import com.jmex.bui.util.Dimension;
import com.jmex.bui.util.Insets;

/**
 * Handles the underlying layout and rendering for {@link BLabel} and {@link
 * BButton}.
 */
public class Label implements BConstants
{
	public Label(BTextComponent container)
	{
		_container = container;
	}

	/**
	 * Updates the text displayed by this label.
	 */
	public void setText(String text)
	{
		_value = text;
		_twidth = Short.MAX_VALUE;
		// clear out our old text texture
		releaseText();
		// if we're already part of the hierarchy, recreate our glyps
		if (_container.isAdded())
		{
			layoutAndComputeSize(_twidth);
		}
		// our size may have changed so we need to revalidate
		_container.invalidate();
	}

	/**
	 * Returns the text currently being displayed by this label.
	 */
	public String getText()
	{
		return _value;
	}

	/**
	 * Configures the label to display the specified icon.
	 */
	public void setIcon(BIcon icon)
	{
		int owidth = 0, oheight = 0, nwidth = 0, nheight = 0;
		if (_icon != null)
		{
			owidth = _icon.getWidth();
			oheight = _icon.getHeight();
			if (_container.isAdded())
			{
				_icon.wasRemoved();
			}
		}
		_icon = icon;
		if (_icon != null)
		{
			nwidth = _icon.getWidth();
			nheight = _icon.getHeight();
			if (_container.isAdded())
			{
				_icon.wasAdded();
			}
		}
		if (owidth != nwidth || oheight != nheight)
		{
			// reset our target width so that we force a text reflow to account
			// for the changed icon size
			_twidth = Short.MAX_VALUE;
			_container.invalidate();
		}
		else if (_container.isValid())
		{
			_container.layout();
		}
	}

	/**
	 * Returns the icon being displayed by this label.
	 */
	public BIcon getIcon()
	{
		return _icon;
	}

	/**
	 * Configures the gap between the icon and the text.
	 */
	public void setIconTextGap(int gap)
	{
		_gap = gap;
	}

	/**
	 * Returns the gap between the icon and the text.
	 */
	public int getIconTextGap()
	{
		return _gap;
	}

	/**
	 * Sets the orientation of this label with respect to its icon. If the
	 * horizontal (the default) the text is displayed to the right of the icon,
	 * if vertical the text is displayed below it.
	 */
	public void setOrientation(int orient)
	{
		_orient = orient;
		if (_container.isAdded())
		{
			_container.layout();
		}
	}

	/**
	 * Called by our containing component when it was added to the interface
	 * hierarchy.
	 */
	public void wasAdded()
	{
		if (_icon != null)
		{
			_icon.wasAdded();
		}
		if (_text != null)
		{
			_text.wasAdded();
		}
		layoutAndComputeSize(_twidth);
	}

	/**
	 * Called by our containing component when it was removed from the interface
	 * hierarchy.
	 */
	public void wasRemoved()
	{
		if (_icon != null)
		{
			_icon.wasRemoved();
		}
		if (_text != null)
		{
			_text.wasRemoved();
		}
	}

	/**
	 * Called by our containing component when its state changes.
	 */
	public void stateDidChange()
	{
		if (_container.isAdded())
		{
			layoutAndComputeSize(_twidth);
		}
	}

	/**
	 * Computes the preferred size of the label.
	 */
	public Dimension computePreferredSize(int whint, int hhint)
	{
		// if our cached preferred size is not valid, recompute it
		Config prefconfig = getConfig(whint > 0 ? whint : Short.MAX_VALUE);
		if (!prefconfig.equals(_prefconfig))
		{
			_prefconfig = prefconfig;
			_prefsize = layoutAndComputeSize(prefconfig.twidth);
		}
		return new Dimension(_prefsize);
	}

	/**
	 * Lays out the label text and icon.
	 */
	public void layout(Insets insets)
	{
		// compute any offsets needed to center or align things
		Dimension size = layoutAndComputeSize(_container.getWidth() - insets.getHorizontal());
		int xoff = 0, yoff = 0;
		switch (_orient)
		{
		case HORIZONTAL:
			if (_icon != null)
			{
				_ix = getXOffset(insets, size.width);
				_iy = getYOffset(insets, _icon.getHeight());
				xoff = (_icon.getWidth() + _gap);
			}
			if (_text != null)
			{
				_tx = getXOffset(insets, size.width) + xoff;
				_ty = getYOffset(insets, _text.size.height);
			}
			break;
		case VERTICAL:
			if (_text != null)
			{
				_tx = getXOffset(insets, _text.size.width);
				_ty = getYOffset(insets, size.height);
				yoff = (_text.size.height + _gap);
			}
			if (_icon != null)
			{
				_ix = getXOffset(insets, _icon.getWidth());
				_iy = getYOffset(insets, size.height) + yoff;
			}
			break;
		case OVERLAPPING:
			if (_icon != null)
			{
				_ix = getXOffset(insets, _icon.getWidth());
				_iy = getYOffset(insets, _icon.getHeight());
			}
			if (_text != null)
			{
				_tx = getXOffset(insets, _text.size.width);
				_ty = getYOffset(insets, _text.size.height);
			}
			break;
		}
	}

	/**
	 * Renders the label text and icon.
	 */
	public void render(Renderer renderer, float alpha)
	{
		if (_icon != null)
		{
			_icon.render(renderer, _ix, _iy, alpha);
		}
		if (_text != null)
		{
			_text.render(renderer, _tx, _ty, _container.getHorizontalAlignment(), alpha);
		}
	}

	/**
	 * Releases any underlying texture resources created by this label.
	 */
	public void releaseText()
	{
		if (_text != null)
		{
			if (_container.isAdded())
			{
				_text.wasRemoved();
			}
			_text = null;
		}
	}

	protected Dimension layoutAndComputeSize(int tgtwidth)
	{
		// make a note of our current target width
		_twidth = tgtwidth;
		// maybe relayout our text so that we can find out how tall it will be
		if (_value != null)
		{
			// account for the space taken up by the icon
			if (_icon != null && _orient == HORIZONTAL)
			{
				tgtwidth -= _gap;
				tgtwidth -= _icon.getWidth();
			}
			// re-line-break our text
			recreateGlyphs(tgtwidth);
		}
		int iwidth = 0, iheight = 0, twidth = 0, theight = 0, gap = 0;
		if (_icon != null)
		{
			iwidth = _icon.getWidth();
			iheight = _icon.getHeight();
		}
		if (_text != null)
		{
			if (_icon != null)
			{
				gap = _gap;
			}
			twidth = _text.size.width;
			theight = _text.size.height;
		}
		int width, height;
		switch (_orient)
		{
		default:
		case HORIZONTAL:
			width = iwidth + gap + twidth;
			height = Math.max(iheight, theight);
			break;
		case VERTICAL:
			width = Math.max(iwidth, twidth);
			height = iheight + gap + theight;
			break;
		case OVERLAPPING:
			width = Math.max(iwidth, twidth);
			height = Math.max(iheight, theight);
			break;
		}
		return new Dimension(width, height);
	}

	protected int getXOffset(Insets insets, int width)
	{
		switch (_container.getHorizontalAlignment())
		{
		default:
		case LEFT:
			return insets.left;
		case RIGHT:
			return _container.getWidth() - width - insets.right;
		case CENTER:
			return (_container.getWidth() - insets.getHorizontal() - width) / 2 + insets.left;
		}
	}

	protected int getYOffset(Insets insets, int height)
	{
		switch (_container.getVerticalAlignment())
		{
		default:
		case TOP:
			return _container.getHeight() - height - insets.top;
		case BOTTOM:
			return insets.bottom;
		case CENTER:
			return (_container.getHeight() - insets.getVertical() - height) / 2 + insets.bottom;
		}
	}

	/**
	 * Clears out old glyphs and creates new ones for our current text.
	 */
	protected void recreateGlyphs(int twidth)
	{
		// no need to recreate our glyphs if our config hasn't changed
		Config config = getConfig(twidth);
		if (config.equals(_config) && _text != null)
		{
			return;
		}
		_config = config;
		// clear out any previous rendered text
		releaseText();
		// if we have no text, we're done
		if (_value == null || _value.equals(""))
		{
			return;
		}
		// sanity check
		if (twidth < 0)
		{
			Log.log.warning("Requested to layout with negative target width " + "[text=" + _value + ", twidth=" + twidth + "].");
			Thread.dumpStack();
			return;
		}
		// render up some new text
		BTextFactory tfact = _container.getTextFactory();
		_text = new Text();
		if (_container.useTextWrap())
		{
			_text.lines = tfact.wrapText(_value, config.color, config.effect, config.effectColor, twidth);
		}
		else
		{
			_text.lines = new BText[] { tfact.createText(_value, config.color, config.effect, config.effectColor, false) };
		}
		for (int ii = 0; ii < _text.lines.length; ii++)
		{
			_text.size.width = Math.max(_text.size.width, _text.lines[ii].getSize().width);
			_text.size.height += _text.lines[ii].getSize().height;
		}
		if (_container.isAdded())
		{
			_text.wasAdded();
		}
	}

	/**
	 * Returns our current text configuration.
	 */
	protected Config getConfig(int twidth)
	{
		Config config = new Config();
		config.text = _value;
		config.color = _container.getColor();
		config.effect = _container.getTextEffect();
		config.effectColor = _container.getEffectColor();
		config.twidth = twidth;
		return config;
	}
	protected static class Config
	{
		public String text;
		public ColorRGBA color;
		public int effect;
		public ColorRGBA effectColor;
		public int twidth;

		@Override
		public boolean equals(Object other)
		{
			if (other == null)
			{
				return false;
			}
			Config oc = (Config) other;
			if (twidth != oc.twidth)
			{
				return false;
			}
			if (effect != oc.effect)
			{
				return false;
			}
			if (text != oc.text && (text == null || !text.equals(oc.text)))
			{
				return false;
			}
			if (!color.equals(oc.color))
			{
				return false;
			}
			if (effectColor != oc.effectColor && (effectColor == null || !effectColor.equals(oc.effectColor)))
			{
				return false;
			}
			return true;
		}

		@Override
		public String toString()
		{
			return text + "(" + color + "," + effect + "," + effectColor + "," + twidth + ")";
		}
	}
	protected static class Text
	{
		public BText[] lines;
		public Dimension size = new Dimension();

		public void render(Renderer renderer, int tx, int ty, int halign, float alpha)
		{
			// render the lines from the bottom up
			for (int ii = lines.length - 1; ii >= 0; ii--)
			{
				int lx = tx;
				if (halign == RIGHT)
				{
					lx += size.width - lines[ii].getSize().width;
				}
				else if (halign == CENTER)
				{
					lx += (size.width - lines[ii].getSize().width) / 2;
				}
				lines[ii].render(renderer, lx, ty, alpha);
				ty += lines[ii].getSize().height;
			}
		}

		public void wasAdded()
		{
			for (int ii = 0; ii < lines.length; ii++)
			{
				lines[ii].wasAdded();
			}
		}

		public void wasRemoved()
		{
			for (int ii = 0; ii < lines.length; ii++)
			{
				lines[ii].wasRemoved();
			}
		}
	}
	protected BTextComponent _container;
	protected String _value;
	protected int _orient = HORIZONTAL;
	protected int _gap = 3;
	protected BIcon _icon;
	protected int _ix, _iy;
	protected Config _config;
	protected Text _text;
	protected int _tx, _ty, _twidth = Short.MAX_VALUE;
	protected float _alpha = 1f;
	protected Config _prefconfig;
	protected Dimension _prefsize;
}
