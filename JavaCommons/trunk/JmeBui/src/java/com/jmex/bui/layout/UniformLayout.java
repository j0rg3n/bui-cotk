package com.jmex.bui.layout;

import java.util.Hashtable;

import com.jmex.bui.BComponent;
import com.jmex.bui.BContainer;
import com.jmex.bui.layout.constraints.UniformSize;
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
			throw new IllegalArgumentException("The constraint must be a UniformSize, but it was: "+comp_constraints);
		}
	}
	
    @Override
	public void removeLayoutComponent (BComponent comp)
    {
        constraints.remove(comp);
    }
}
