package com.jmex.bui;

import com.jmex.bui.BButton;
import com.jmex.bui.BComponent;
import com.jmex.bui.BConstants;
import com.jmex.bui.BContainer;
import com.jmex.bui.BoundedRangeModel;
import com.jmex.bui.background.ImageBackground;
import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.ActionListener;
import com.jmex.bui.event.MouseAdapter;
import com.jmex.bui.event.MouseEvent;
import com.jmex.bui.event.MouseWheelListener;
import com.jmex.bui.layout.BorderLayout;
import com.jmex.bui.util.Dimension;
import com.jmex.bui.util.Insets;

/**
 * Displays a scroll bar for all your horizontal and vertical scrolling needs.
 */
public class BSimpleScrollBar extends BContainer implements BConstants
{
	//ImageBackground _well_background;

	/**
	 * Creates a scroll bar with the specified orientation which will interact
	 * with the supplied model.
	 */
	public BSimpleScrollBar(BoundedRangeModel model)
	{
		super(new BorderLayout());
		_model = model;
	}

	/**
	 * Returns a reference to the scrollbar's range model.
	 */
	public BoundedRangeModel getModel()
	{
		return _model;
	}

	// documentation inherited
	@Override
	public void wasAdded()
	{
		super.wasAdded();
		// listen for mouse wheel events
		addListener(_wheelListener = _model.createWheelListener());
		// create our buttons and backgrounds
		String oprefix = "scrollbar_v";
		_well = new BComponent();
		_well.setStyleClass(oprefix + "well");
		add(_well, BorderLayout.CENTER);
		// Now listen to the well
		_well.addListener(_mousedragListener);
		_less = new BButton("");
		_less.setStyleClass(oprefix + "less");
		add(_less, BorderLayout.NORTH);
		_less.addListener(_buttoner);
		_less.setAction("less");
		_more = new BButton("");
		_more.setStyleClass(oprefix + "more");
		add(_more, BorderLayout.SOUTH);
		_more.addListener(_buttoner);
		_more.setAction("more");
	}

	// documentation inherited
	@Override
	public void wasRemoved()
	{
		super.wasRemoved();
		if (_wheelListener != null)
		{
			removeListener(_wheelListener);
			_wheelListener = null;
		}
		if (_well != null)
		{
			remove(_well);
			_well = null;
		}
		if (_less != null)
		{
			remove(_less);
			_less = null;
		}
		if (_more != null)
		{
			remove(_more);
			_more = null;
		}
	}

	// documentation inherited
	@Override
	public BComponent getHitComponent(int mx, int my)
	{
		// we do special processing for the thumb
		return super.getHitComponent(mx, my);
	}

	/**
	 * Recomputes and repositions the scroll bar thumb to reflect the current
	 * configuration of the model. protected void update () { Insets winsets =
	 * _well.getInsets(); int theight = _well.getHeight() -
	 * winsets.getVertical(); int range = Math.max(_model.getRange(), 1); //
	 * avoid div0 int extent = Math.max(_model.getExtent(), 1); // avoid div0 {
	 * int wellSize = theight; theight = extent * wellSize / range; } }
	 */
	// documentation inherited
	@Override
	protected String getDefaultStyleClass()
	{
		return "scrollbar";
	}
	protected MouseAdapter _mousedragListener = new MouseAdapter()
	{
		@Override
		public void mousePressed(MouseEvent event)
		{
			_sv = _model.getValue();
			_sy = event.getY();
		}

		@Override
		public void mouseDragged(MouseEvent event)
		{
			if(_model == null || _well == null || _well.getInsets() == null)
				return;
			int dv = 0;
			int my = event.getY();
			dv = (_sy - my) * _model.getRange() / (_well.getHeight() - _well.getInsets().getVertical());
			if (dv != 0)
			{
				int oldval = _model.getValue();
				_model.setValue(_sv + dv);
				if (oldval != _model.getValue())
				{
					//ImageBackground _well_background = (ImageBackground) _well.getBackground();
					if (_well_background == null)
					{
						// HACK: here we duplicate the background (to get our own)
						_well_background = (ImageBackground) _well.getBackground();
						_well_background = new ImageBackground(_well_background.getMode(), _well_background.getImage());
						_well.setBackground(BComponent.DEFAULT, _well_background);
						_well.setBackground(BComponent.HOVER, _well_background);
					}
					//System.out.println("-(_sy - my):"+(-(_sy - my)));
					_well_background.setOffset(0, -(_sy - my));
				}
			}
		}
		protected int _sy, _sv;
	};
	protected ActionListener _buttoner = new ActionListener()
	{
		public void actionPerformed(ActionEvent event)
		{
			int delta = _model.getScrollIncrement();
			if (event.getAction().equals("less"))
			{
				_model.setValue(_model.getValue() - delta);
			}
			else
			{
				_model.setValue(_model.getValue() + delta);
			}
		}
	};
	protected BoundedRangeModel _model;
	protected BButton _less, _more;
	protected BComponent _well;
	protected MouseWheelListener _wheelListener;
	protected ImageBackground _well_background;
}
