package com.jmex.bui;

import java.util.ArrayList;
import java.util.Vector;
import com.jme.renderer.ColorRGBA;
import com.jmex.bui.background.BBackground;
import com.jmex.bui.background.TintedBackground;
import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.InputEvent;
import com.jmex.bui.icon.BIcon;
import com.jmex.bui.layout.TableLayout;

/**
 * This is a list of items that can be selected (either multiple, or one one).
 * 
 * It should look somewhat like this (in multi-select mode):
 * 
 * <select size="10" MULTIPLE> <option>Item 1</option> <option>Item 2</option>
 * <option>Item 3</option> <option>Item 4</option> <option>Item 5</option>
 * <option>Item 6</option> <option>Item 7</option> </select>
 * 
 * @author emanuel
 * 
 */
public class BSelectList extends BSimpleScrollPane
{
	public enum SelectMode
	{
		/** You can only select one item. */
		SINGLE_SELECT,
		/** You can select multiple items. */
		MULTI_SELECT,
		/** You can select one region only, like items 1, 2, 3 or 6, 7, 8, 9. */
		CONTIGIOUS_SELECT
	}
	SelectMode _select_mode = SelectMode.SINGLE_SELECT;

	/**
	 * Creates an empty combo box.
	 */
	public BSelectList()
	{
		super(new BContainer(new TableLayout(1)), true);
		TableLayout lay = (TableLayout) ((BContainer) getMainArea()).getLayoutManager();
		lay.setHorizontalAlignment(TableLayout.STRETCH);
		lay.setVerticalAlignment(TableLayout.TOP);
	}

	/**
	 * Creates a combo box with the supplied set of items. The result of {@link
	 * Object#toString} for each item will be displayed in the list.
	 */
	public BSelectList(Object[] items)
	{
		this();
		setItems(items);
	}

	/**
	 * Appends an item to our list of items. The result of {@link
	 * Object#toString} for the item will be displayed in the list.
	 */
	public void addItem(Object item)
	{
		addItem(_items.size(), item);
	}

	/**
	 * Inserts an item into our list of items at the specified position (zero
	 * being before all other items and so forth). The result of
	 * {@link Object#toString} for the item will be displayed in the list.
	 */
	public void addItem(int index, Object item)
	{
		ItemListItem tmp;
		_items.add(index, tmp = new ItemListItem(item, index));
		// Single-select must have something selected.
		if (index == 0 && _select_mode == SelectMode.SINGLE_SELECT)
			tmp.selected = true;
		((BContainer) getMainArea()).add(tmp);
	}

	/**
	 * Replaces any existing items in this combo box with the supplied items.
	 */
	public void setItems(Object[] items)
	{
		_last_clicked_item = null;
		((BContainer) getMainArea()).removeAll();
		_items.clear();
		getViewport().getVModel().setValue(0);
		for (int ii = 0; ii < items.length; ii++)
		{
			addItem(items[ii]);
		}
		invalidate(); // Scrollbars might need updating due to this.
	}

	/**
	 * Returns the index of the selected item or -1 if no item is selected.
	 */
	public int getSelectedIndex()
	{
		ItemListItem sel = getSelectedMenuItem();
		if (sel == null)
			return -1;
		return sel.index;
	}

	/**
	 * Returns the selected item or null if no item is selected.
	 */
	public Object getSelectedItem()
	{
		ItemListItem sel = getSelectedMenuItem();
		if (sel == null)
			return null;
		return sel.item;
	}

	private ItemListItem getSelectedMenuItem()
	{
		if (_select_mode != SelectMode.SINGLE_SELECT)
			return null;
		for (ItemListItem i : _items)
		{
			if (i.selected)
				return i;
		}
		return null;
	}

	/**
	 * Return an array of all the items that are tagged as selected.
	 * 
	 * @return
	 */
	public Vector<Object> getSelectedItems()
	{
		Vector<Object> sels = new Vector<Object>();
		for (ItemListItem i : _items)
		{
			if (i.selected)
				sels.add(i);
		}
		return sels;
	}

	/**
	 * Selects the item with the specified index.
	 */
	public void selectItem(int index)
	{
		selectItem(_items.get(index), 0L, 0);
	}

	/**
	 * Selects the item with the specified index. <em>Note:</em> the supplied
	 * item is compared with the item list using {@link Object#equals}.
	 */
	public void selectItem(Object item)
	{
		int selidx = -1;
		for (int ii = 0, ll = _items.size(); ii < ll; ii++)
		{
			ItemListItem mitem = _items.get(ii);
			if (mitem.item.equals(item))
			{
				selidx = ii;
				break;
			}
		}
		selectItem(selidx);
	}

	/**
	 * Returns the number of items in this combo box.
	 */
	public int getItemCount()
	{
		return _items.size();
	}

	// documentation inherited
	@Override
	protected String getDefaultStyleClass()
	{
		return "combobox";
	}

	// TODO: make getPreferredSize() use the widest label
	protected void selectItem(ItemListItem item, long when, int modifiers)
	{
		switch (_select_mode)
		{
		case SINGLE_SELECT:
			deSelectAllItem();
			item.toggleSelected();
			break;
		case CONTIGIOUS_SELECT:
			break;
		case MULTI_SELECT:
			if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0)
			{
				// Just toggle the item
				item.toggleSelected();
			}
			else if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0)
			{
				// deSelectAllItem();
				if (_last_clicked_item != null)
				{
					int from = Math.min(item.index, _last_clicked_item.index);
					int to = Math.max(item.index, _last_clicked_item.index);
					for (int i = from; i <= to; i++)
					{
						ItemListItem it = _items.get(i);
						if (!it.selected)
							it.toggleSelected();
					}
				}
				else
				{
					item.toggleSelected();
				}
			}
			else
			{
				deSelectAllItem();
				item.toggleSelected();
			}
			break;
		}
		if (item.selected)
		{
			if (_last_clicked_item != null)
				_last_clicked_item.invalidate();
			_last_clicked_item = item;
			_last_clicked_item.invalidate();
		}
		emitEvent(new ActionEvent(this, when, modifiers, "selectionChanged"));
	}

	private void deSelectAllItem()
	{
		for (ItemListItem i : _items)
		{
			if (i.selected)
				i.toggleSelected();
		}
	}

	public void setSelectionMode(SelectMode mode)
	{
		_select_mode = mode;
		// TODO: we might have to deselect everything.
	}
	protected class ItemListItem extends BMenuItem
	{
		public boolean selected;
		public Object item;
		int index;

		public ItemListItem(Object item, int index)
		{
			super(null, null, "select");
			if (item instanceof BIcon)
			{
				setIcon((BIcon) item);
			}
			else if (item instanceof BLabel)
			{
				BLabel l = (BLabel) item;
				setFit(l.getFit());
				setIcon(l.getIcon());
				setText(l.getText());
			}
			else
			{
				setText(item.toString());
			}
			this.item = item;
			this.selected = false;
			this.index = index;
		}

		public void toggleSelected()
		{
			selected = !selected;
			invalidate();
		}

		@Override
		public void validate()
		{
			if (!isValid())
			{
				if (this != _last_clicked_item)
				{
					setBackground(BComponent.DEFAULT, selected ? selected_bg : normal_bg);
					setBackground(BComponent.HOVER, selected ? selected_bg : hover_bg);
				}
				else
				{
					// TODO: this should rather have dashed borders...
					setBackground(BComponent.DEFAULT, last_selected_bg);
					setBackground(BComponent.HOVER, last_selected_bg);
				}
			}
			super.validate();
		}

		/**
		 * Called when the menu item is "clicked" which may due to the mouse
		 * being pressed and released while over the item or due to keyboard
		 * manipulation while the item has focus.
		 */
		@Override
		protected void fireAction(long when, int modifiers)
		{
			selectItem(this, when, modifiers);
		}
	}
	protected ArrayList<ItemListItem> _items = new ArrayList<ItemListItem>();
	ItemListItem _last_clicked_item = null;
	BBackground last_selected_bg = new TintedBackground(new ColorRGBA(0.7f, 0.7f, 1, 0.3f));
	BBackground selected_bg = new TintedBackground(new ColorRGBA(1, 1, 1, 0.2f));
	BBackground normal_bg = new TintedBackground(new ColorRGBA(1, 1, 1, 0));
	BBackground hover_bg = new TintedBackground(new ColorRGBA(1, 1, 1, 0.05f));
}
