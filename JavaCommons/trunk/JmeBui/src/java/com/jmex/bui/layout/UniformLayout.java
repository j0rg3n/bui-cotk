package com.jmex.bui.layout;

import java.util.Hashtable;

import com.jmex.bui.BComponent;
import com.jmex.bui.BContainer;
import com.jmex.bui.util.Dimension;
import com.jmex.bui.util.Insets;
import com.jmex.bui.util.Point;
import com.jmex.bui.util.Rectangle;

public class UniformLayout extends BLayoutManager
{
	/** Example showing full parent size. */
	public static final UniformSize FULL_PARENT_SIZE = new UniformSize(0, 0, 0, 0, 1, 0, 1, 0);
	/** Example showing a 100x100 in the center */
	public static final UniformSize CENTERED_100x100_SIZE = new UniformSize(0.5f, -50, 0.5f, -50, 0, 100, 0, 100);
	/** Example showing centered with 10px padding on each side */
	public static final UniformSize CENTERED_WITH_10PX_PADDING = new UniformSize(0f, 10, 0f, 10, 1, -20, 1, -20);
	
	Hashtable<BComponent, UniformSize> constraints = new Hashtable<BComponent, UniformSize>();
	Dimension preferredSize = new Dimension(-1, -1);
	private boolean _flipped = false;
	
	public static class UniformSize {
		
		/**
		 * The default, full parent size constructor.
		 */
		public UniformSize() {
			this(0, 0, 0, 0, 1, 0, 1, 0);
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
	
	public UniformLayout(boolean flipped)
	{
		_flipped = flipped;
	}

	@Override
	public Dimension computePreferredSize(BContainer target, int whint, int hhint)
	{
		int w = target.getWidth();
		int h = target.getHeight();
		
		// TODO: make this work with other things than "full size"
		if(preferredSize.width == -1 || preferredSize.height == -1)
		{
			preferredSize.width = target.getWindow().getWidth();
			preferredSize.height = target.getWindow().getHeight();
		}
		
		return preferredSize;
	}

	@Override
	public void layoutContainer(BContainer target)
	{
        Insets insets = target.getInsets();
        int height = target.getHeight();
        
        for (int ii = 0, cc = target.getComponentCount(); ii < cc; ii++) {
            BComponent comp = target.getComponent(ii);
            if (!comp.isVisible()) {
                continue;
            }
            BContainer parent = comp.getParent();
            UniformSize size = constraints.get(comp);
            //Rectangle r = (Rectangle)cons;
            int x = insets.left + size.getX(parent);
            int y = (_flipped ? height - size.getH(parent) - insets.top - size.getY(parent) : 
                insets.bottom + size.getY(parent));
            int w = size.getW(parent);
            int h = size.getH(parent);
            comp.setBounds(x, y, w, h);
        }
	}
	
	@Override
	public void addLayoutComponent(BComponent comp, Object comp_constraints)
	{
		if(comp_constraints instanceof UniformSize)
		{
			constraints.put(comp, (UniformSize) comp_constraints);
		}
		else
		{
			throw new IllegalArgumentException("The constraint must be a UniformSize");
		}
	}
	
    @Override
	public void removeLayoutComponent (BComponent comp)
    {
        constraints.remove(comp);
    }
}
