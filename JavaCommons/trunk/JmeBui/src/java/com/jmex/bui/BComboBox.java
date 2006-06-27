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

package com.jmex.bui;

import java.util.ArrayList;

import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.ActionListener;
import com.jmex.bui.event.BEvent;
import com.jmex.bui.event.MouseEvent;
import com.jmex.bui.icon.BIcon;

/**
 * Displays a selected value and allows that value to be changed by selecting
 * from a popup menu.
 */
public class BComboBox extends BLabel
{
    /**
     * Creates an empty combo box.
     */
    public BComboBox ()
    {
        super("");
    }

    /**
     * Creates a combo box with the supplied set of items. The result of {@link
     * Object#toString} for each item will be displayed in the list.
     */
    public BComboBox (Object[] items)
    {
        super("");
        setItems(items);
    }

    /**
     * Appends an item to our list of items. The result of {@link
     * Object#toString} for the item will be displayed in the list.
     */
    public void addItem (Object item)
    {
        addItem(_items.size(), item);
    }

    /**
     * Inserts an item into our list of items at the specified position
     * (zero being before all other items and so forth).  The result of
     * {@link Object#toString} for the item will be displayed in the list.
     */
    public void addItem (int index, Object item)
    {
        _items.add(index, new ComboMenuItem(item));
        clearCachedMenu();
    }

    /**
     * Replaces any existing items in this combo box with the supplied items.
     */
    public void setItems (Object[] items)
    {
        clearCachedMenu();
        _items.clear();
        _selidx = -1;

        for (int ii = 0; ii < items.length; ii++) {
            addItem(items[ii]);
        }
    }

    /**
     * Returns the index of the selected item or -1 if no item is selected.
     */
    public int getSelectedIndex ()
    {
        return _selidx;
    }

    /**
     * Returns the selected item or null if no item is selected.
     */
    public Object getSelectedItem ()
    {
        return _selidx == -1 ? null : (_items.get(_selidx)).item;
    }

    /**
     * Selects the item with the specified index.
     */
    public void selectItem (int index)
    {
        selectItem(index, 0L, 0);
    }

    /**
     * Selects the item with the specified index. <em>Note:</em> the supplied
     * item is compared with the item list using {@link Object#equals}.
     */
    public void selectItem (Object item)
    {
        int selidx = -1;
        for (int ii = 0, ll = _items.size(); ii < ll; ii++) {
            ComboMenuItem mitem = _items.get(ii);
            if (mitem.item.equals(item)) {
                selidx = ii;
                break;
            }
        }
        selectItem(selidx);
    }

    /**
     * Returns the number of items in this combo box.
     */
    public int getItemCount ()
    {
        return _items.size();
    }

    // documentation inherited
    @Override
	public boolean dispatchEvent (BEvent event)
    {
        if (event instanceof MouseEvent) {
            MouseEvent mev = (MouseEvent)event;
            switch (mev.getType()) {
            case MouseEvent.MOUSE_PRESSED:
                if (_menu == null) {
                    _menu = new BPopupMenu(getWindow());
                    _menu.addListener(_listener);
                    for (int ii = 0; ii < _items.size(); ii++) {
                        _menu.addMenuItem(_items.get(ii));
                    }
                }
                _menu.popup(getAbsoluteX(), getAbsoluteY(), false);
                break;

            case MouseEvent.MOUSE_RELEASED:
                break;

            default:
                return super.dispatchEvent(event);
            }

            return true;
        }

        return super.dispatchEvent(event);
    }

    // documentation inherited
    @Override
	protected String getDefaultStyleClass ()
    {
        return "combobox";
    }

    // TODO: make getPreferredSize() use the widest label

    protected void selectItem (int index, long when, int modifiers)
    {
        if (_selidx == index) {
            return;
        }

        _selidx = index;
        Object item = getSelectedItem();
        if (item instanceof BIcon) {
            setIcon((BIcon)item);
        } else {
            setText(item == null ? "" : item.toString());
        }
        emitEvent(new ActionEvent(this, when, modifiers, "selectionChanged"));
    }

    protected void clearCachedMenu ()
    {
        if (_menu != null) {
            _menu.removeAll();
            _menu = null;
        }
    }

    protected class ComboMenuItem extends BMenuItem
    {
        public Object item;

        public ComboMenuItem (Object item)
        {
            super(null, null, "select");
            if (item instanceof BIcon) {
                setIcon((BIcon)item);
            } else {
                setText(item.toString());
            }
            this.item = item;
        }
    }

    protected ActionListener _listener = new ActionListener() {
        public void actionPerformed (ActionEvent event) {
            selectItem(_items.indexOf(event.getSource()),
                       event.getWhen(), event.getModifiers());
        }
    };

    protected int _selidx = -1;
    protected ArrayList<ComboMenuItem> _items = new ArrayList<ComboMenuItem>();
    protected BPopupMenu _menu;
}
