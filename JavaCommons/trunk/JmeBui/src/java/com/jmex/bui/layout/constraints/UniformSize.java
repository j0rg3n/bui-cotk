package com.jmex.bui.layout.constraints;

import com.jmex.bui.BContainer;

public class UniformSize {
	
	/**
	 * The default, full parent size constructor.
	 */
	public UniformSize() {
		this(0, 0, 0, 0, 1, 0, 1, 0);
	}

	/**
	 * A constructor for making a non-relative size.
	 * 
	 * @param x_absolute
	 * @param y_absolute
	 * @param w_absolute
	 * @param h_absolute
	 */
	public UniformSize(float x_absolute, float y_absolute, float w_absolute, float h_absolute) {
		this(0, x_absolute, 0, y_absolute, 0, w_absolute, 0, h_absolute);
	}

	/**
	 * The complete constructor.
	 * 
	 * @param x_parent
	 * @param x_absolute
	 * @param y_parent
	 * @param y_absolute
	 * @param w_parent
	 * @param w_absolute
	 * @param h_parent
	 * @param h_absolute
	 */
	public UniformSize(float x_parent, float x_absolute, float y_parent, float y_absolute, float w_parent, float w_absolute, float h_parent, float h_absolute) {
		this.x_parent   = x_parent;
		this.x_absolute = x_absolute;
		this.y_parent   = y_parent;
		this.y_absolute = y_absolute;
		this.w_parent   = w_parent;
		this.w_absolute = w_absolute;
		this.h_parent   = h_parent;
		this.h_absolute = h_absolute;
	}
	public float x_parent, x_absolute;
	public float y_parent, y_absolute;
	public float w_parent, w_absolute;
	public float h_parent, h_absolute;
	
	public int getX(BContainer parent)
	{
		return (int)(x_parent*parent.getWidth() + x_absolute);
	}
	
	public int getY(BContainer parent)
	{
		return (int)(y_parent*parent.getHeight() + y_absolute);
	}
	
	public int getW(BContainer parent)
	{
		return (int)(w_parent*parent.getWidth() + w_absolute);
	}
	
	public int getH(BContainer parent)
	{
		return (int)(h_parent*parent.getHeight() + h_absolute);
	}
}
