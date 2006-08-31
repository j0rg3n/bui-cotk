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
package com.jmex.bui.util;

/**
 * Represents the bounds of a component.
 */
public class Rectangle
{
	/** The x position of the entity in question. */
	public int x;
	/** The y position of the entity in question. */
	public int y;
	/** The width of the entity in question. */
	public int width;
	/** The height of the entity in question. */
	public int height;

	public Rectangle(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Rectangle(Rectangle other)
	{
		x = other.x;
		y = other.y;
		width = other.width;
		height = other.height;
	}

	public Rectangle()
	{
	}

	/**
	 * Adds the specified rectangle to this rectangle, causing this rectangle to
	 * become the union of itself and the specified rectangle.
	 */
	public void add(int x1, int y1, int width1, int height1)
	{
		int fx = Math.max(this.x + this.width, x1 + width1);
		int fy = Math.max(this.y + this.height, y1 + height1);
		this.x = Math.min(x1, this.x);
		this.y = Math.min(y1, this.y);
		this.width = fx - this.x;
		this.height = fy - this.y;
	}

	// documentation inherited
	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Rectangle)
		{
			Rectangle orect = (Rectangle) other;
			return x == orect.x && y == orect.y && width == orect.width && height == orect.height;
		}
		return false;
	}

	// documentation inherited
	@Override
	public int hashCode()
	{
		return x ^ y ^ width ^ height;
	}

	/** Generates a string representation of this instance. */
	@Override
	public String toString()
	{
		return width + "x" + height + (x >= 0 ? "+" : "") + x + (y >= 0 ? "+" : "") + y;
	}
}
