package com.jmex.bui;

import com.jmex.bui.BComponent;
import com.jmex.bui.BContainer;
import com.jmex.bui.BoundedRangeModel;
import com.jmex.bui.BScrollPane.BViewport;
import com.jmex.bui.layout.BorderLayout;
import com.jmex.bui.util.Dimension;

public class BSimpleScrollPane extends BContainer
{
	public BSimpleScrollPane(BComponent child, boolean autohide, int scrollBarConstraint)
	{
		super(new BorderLayout(0, 0));
		this.mainarea = child;
		autohidescrollers = autohide;
		add(_vport = new BViewport(child, true, false, 10), BorderLayout.CENTER);
		add(_vbar = new BSimpleScrollBar(_vport.getVModel()), scrollBarConstraint);
	}

	public BSimpleScrollPane(BComponent child, int scrollBarConstraint)
	{
		this(child, true, scrollBarConstraint);
	}

	public BSimpleScrollPane(BComponent child)
	{
		this(child, true);
	}

	public BSimpleScrollPane(BComponent child, boolean autohide)
	{
		this(child, autohide, BorderLayout.EAST);
	}
	
	public void setVBarStyleClass(String styleClass)
	{
		_vbar.setStyleClass(styleClass);
	}

	public BComponent getMainArea()
	{
		return mainarea;
	}

	public BoundedRangeModel getModel()
	{
		return _vport.getVModel();
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height)
	{
	    if (_height != height) {
	    	int diff = height - _height;
	    	int ext = getModel().getExtent();
	    	int new_ext = ext+diff;
	    	int max = getModel().getMaximum();
	    	int min = getModel().getMinimum();
	    	int val = getModel().getValue();
	    	int new_val = val-diff;
	    	getModel().setRange(min, new_val, new_ext, max);
	    }
	    // Ok, lets do it then.
		super.setBounds(x, y, width, height);
	}

	@Override
	public void validate()
	{
		// Do the first validation
		super.validate();

		// Figure out if we need an other one.
		boolean changed_anything = false;
		if (autohidescrollers)
		{
			if (getModel().getExtent() < getModel().getRange() && _vbar.getParent() == null)
			{
				add(_vbar, BorderLayout.EAST);
				changed_anything = true;
			}
			else if (getModel().getExtent() >= getModel().getRange() && _vbar.getParent() != null)
			{
				remove(_vbar);
				changed_anything = true;
			}
		}
		
		// Do we need a second validation ?
		if (changed_anything)
		{
			invalidate();
			super.validate();
		}
	}
	
	@Override
	public void invalidate()
	{
		getViewport().invalidate();
		super.invalidate();
	}

	public BViewport getViewport()
	{
		return _vport;
	}
	
	protected BViewport _vport;
	protected BSimpleScrollBar _vbar;
	boolean autohidescrollers = true;
	private BComponent mainarea;
}
