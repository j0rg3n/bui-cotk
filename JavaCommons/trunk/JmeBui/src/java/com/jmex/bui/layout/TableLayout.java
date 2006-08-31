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
package com.jmex.bui.layout;

import java.util.Arrays;
import com.jmex.bui.BComponent;
import com.jmex.bui.BContainer;
import com.jmex.bui.util.Dimension;
import com.jmex.bui.util.Insets;

/**
 * Lays out components in a simple grid arrangement, wherein the width and
 * height of each column and row is defined by the widest preferred width and
 * height of any component in that column and row.
 * 
 * <p>
 * The table layout defaults to left horizontal alignment and top vertical
 * alignment.
 */
public class TableLayout extends BLayoutManager
{
	/** An enumeration class representing alignments. */
	public static class Alignment
	{
	}
	/** Left justifies the table contents within the container. */
	public static final Alignment LEFT = new Alignment();
	/** Centers the table contents within the container. */
	public static final Alignment CENTER = new Alignment();
	/** Right justifies the table contents within the container. */
	public static final Alignment RIGHT = new Alignment();
	/** Top justifies the table contents within the container. */
	public static final Alignment TOP = new Alignment();
	/** Bottom justifies the table contents within the container. */
	public static final Alignment BOTTOM = new Alignment();
	/**
	 * Divides the column space among the columns in proportion to their
	 * preferred size. This only works with {@link #setHorizontalAlignment}.
	 */
	public static final Alignment STRETCH = new Alignment();

	/**
	 * Creates a table layout with the specified number of columns and a zero
	 * pixel gap between rows and columns.
	 */
	public TableLayout(int columns)
	{
		this(columns, 0, 0);
	}

	/**
	 * Creates a table layout with the specified number of columns and the
	 * specifeid gap between rows and columns.
	 */
	public TableLayout(int columns, int rowgap, int colgap)
	{
		_columnWidths = new int[columns];
		_setColumnWidths = new int[columns];
		Arrays.fill(_setColumnWidths, -1);
		_rowgap = rowgap;
		_colgap = colgap;
	}

	/**
	 * Configures the horizontal alignment (or stretching) of this table. This
	 * must be called before the container using this layout is validated.
	 */
	public void setHorizontalAlignment(Alignment align)
	{
		_halign = align;
	}

	/**
	 * Configures the vertical alignment of this table. This must be called
	 * before the container using this layout is validated.
	 */
	public void setVerticalAlignment(Alignment align)
	{
		_valign = align;
	}

	/**
	 * Configures whether or not the table will force all rows to be a uniform
	 * size. This must be called before the container using this layout is
	 * validated.
	 */
	public void setEqualRows(boolean equalRows)
	{
		_equalRows = equalRows;
	}

	// documentation inherited
	@Override
	public Dimension computePreferredSize(BContainer target, int whint, int hhint)
	{
		computeMetrics(target);
		int cx = (_columnWidths.length - 1) * _colgap;
		int rx = (computeRows(target) - 1) * _rowgap;
		return new Dimension(sum(_columnWidths) + cx, sum(_rowHeights) + rx);
	}

	// documentation inherited
	@Override
	public void layoutContainer(BContainer target)
	{
		computeMetrics(target);
		int totwidth = sum(_columnWidths) + (_columnWidths.length - 1) * _colgap;
		int totheight = sum(_rowHeights) + (computeRows(target) - 1) * _rowgap;
		Insets insets = target.getInsets();
		// account for our horizontal alignment
		int sx = insets.left;
		if (_halign == RIGHT)
		{
			sx += target.getWidth() - insets.getHorizontal() - totwidth;
		}
		else if (_halign == CENTER)
		{
			sx += (target.getWidth() - insets.getHorizontal() - totwidth) / 2;
		}
		// account for our vertical alignment
		int y = insets.bottom;
		if (_valign == CENTER)
		{
			y += totheight + (target.getHeight() - insets.getVertical() - totheight) / 2;
		}
		else if (_valign == TOP)
		{
			y = target.getHeight() - insets.top;
		}
		int row = 0, col = 0, x = sx;
		for (int ii = 0, ll = target.getComponentCount(); ii < ll; ii++)
		{
			BComponent child = target.getComponent(ii);
			child.setBounds(x, y - _rowHeights[row], _columnWidths[col], _rowHeights[row]);
			x += (_columnWidths[col] + _colgap);
			if (++col == _columnWidths.length)
			{
				y -= (_rowHeights[row] + _rowgap);
				row++;
				col = 0;
				x = sx;
			}
		}
	}

	protected void computeMetrics(BContainer target)
	{
		int rows = computeRows(target);
		if (_rowHeights == null || _rowHeights.length != rows)
		{
			_rowHeights = new int[rows];
		}
		else
		{
			Arrays.fill(_rowHeights, 0);
		}
		Arrays.fill(_columnWidths, 0);
		int row = 0, col = 0, maxrh = 0;
		for (int ii = 0, ll = target.getComponentCount(); ii < ll; ii++)
		{
			BComponent child = target.getComponent(ii);
			Dimension psize = child.getPreferredSize(-1, -1);
			if (psize.height > _rowHeights[row])
			{
				_rowHeights[row] = psize.height;
				if (maxrh < _rowHeights[row])
				{
					maxrh = _rowHeights[row];
				}
			}
			if (_setColumnWidths[col] != -1)
			{
				_columnWidths[col] = _setColumnWidths[col];
			}
			else if (psize.width > _columnWidths[col])
			{
				_columnWidths[col] = psize.width;
			}
			if (++col == _columnWidths.length)
			{
				col = 0;
				row++;
			}
		}
		// if we are stretching, adjust the column widths accordingly
		int naturalWidth;
		if (_halign == STRETCH && (naturalWidth = sum(_columnWidths)) > 0)
		{
			int avail = target.getWidth() - target.getInsets().getHorizontal() - naturalWidth - (_colgap * (_columnWidths.length - 1));
			int used = 0;
			for (int ii = 0; ii < _columnWidths.length; ii++)
			{
				// Only to those that are not set explicitly
				if (_setColumnWidths[ii] == -1)
				{
					int adjust = _columnWidths[ii] * avail / naturalWidth;
					_columnWidths[ii] += adjust;
					used += adjust;
				}
			}
			// add any rounding error to the first column that was not exlicitly
			// set
			if (_columnWidths.length > 0)
			{
				for (int ii = 0; ii < _columnWidths.length; ii++)
				{
					if (_setColumnWidths[ii] == -1)
					{
						_columnWidths[ii] += (avail - used);
						break;
					}
				}
			}
		}
		// if we're equalizing rows, make all row heights the max
		if (_equalRows)
		{
			Arrays.fill(_rowHeights, maxrh);
		}
	}

	protected int computeRows(BContainer target)
	{
		int ccount = target.getComponentCount();
		int rows = ccount / _columnWidths.length;
		if (ccount % _columnWidths.length != 0)
		{
			rows++;
		}
		return rows;
	}

	protected int sum(int[] values)
	{
		int total = 0;
		for (int ii = 0; ii < values.length; ii++)
		{
			total += values[ii];
		}
		return total;
	}
	protected Alignment _halign = LEFT, _valign = TOP;
	protected boolean _equalRows;
	protected int _rowgap, _colgap;
	protected int[] _columnWidths;
	protected int[] _setColumnWidths;
	protected int[] _rowHeights;

	public void setColumnWidth(int col, int width)
	{
		_setColumnWidths[col] = width;
		_columnWidths[col] = width;
	}
}
