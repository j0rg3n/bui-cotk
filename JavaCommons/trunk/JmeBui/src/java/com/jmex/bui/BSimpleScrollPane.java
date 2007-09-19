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

	public BComponent getMainArea()
	{
		return mainarea;
	}

	public BoundedRangeModel getModel()
	{
		return _vport.getVModel();
	}

	@Override
	public void validate()
	{
		// Make sure our main-area is also re-evaluated
		/*
		 * Dimension dim = mainarea.getPreferredSize(_vport.getWidth(),
		 * _vport.getHeight()); System.out.println("mainarea[1]: "+dim.height);
		 * mainarea.invalidate(); mainarea.validate();
		 * System.out.println("mainarea[2]: "+mainarea.getHeight());
		 */
		// _vport.getVModel().setValue(_vport.getVModel().getValue());
		// Do the first validation
		super.validate();
		// Is the mainpane too small compared to the viewport and scroll ?
		// mainarea.validate();
		// _vport.getVModel().setRange(_vport.getVModel().getMinimum(),
		// _vport.getVModel().getValue(), _vport.getVModel().getExtent(),
		// _vport.getVModel().getMaximum());
		boolean changed_anything = false;
		if (autohidescrollers)
		{
			if (_vport.getVModel().getExtent() < _vport.getVModel().getRange() && _vbar.getParent() == null)
			{
				add(_vbar, BorderLayout.EAST);
				changed_anything = true;
				System.out.println("Added the scrollbar");
			}
			else if (_vport.getVModel().getExtent() >= _vport.getVModel().getRange() && _vbar.getParent() != null)
			{
				remove(_vbar);
				changed_anything = true;
				System.out.println("Removed the scrollbar");
			}
		}
		// Do we need a second validation ?
		if (changed_anything)
			super.validate();
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
