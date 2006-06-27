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

package com.jmex.bui.event;

import com.jme.input.KeyInput;

/**
 * Encapsulates the information associated with a keyboard event.
 */
public class KeyEvent extends InputEvent
{
	private static final long serialVersionUID = -8385589293663208383L;

	/** Indicates that an event represents a key pressing. */
    public static final int KEY_PRESSED = 0;

    /** Indicates that an event represents a key release. */
    public static final int KEY_RELEASED = 1;

    public KeyEvent (Object source, long when, int modifiers,
                     int type, char keyChar, int keyCode)
    {
        super(source, when, modifiers);
        _type = type;
        _keyChar = keyChar;
        _keyCode = keyCode;
    }

    /**
     * Indicates whether this was a {@link #KEY_PRESSED} or {@link
     * #KEY_RELEASED} event.
     */
    public int getType ()
    {
        return _type;
    }

    /**
     * Returns the character associated with the key. <em>Note:</em> this
     * is only valid for {@link #KEY_PRESSED} events, however {@link
     * #getKeyCode} works in all cases.
     */
    public char getKeyChar ()
    {
        return _keyChar;
    }

    /**
     * Returns the numeric identifier associated with the key.
     *
     * @see KeyInput
     */
    public int getKeyCode ()
    {
        return _keyCode;
    }

    // documentation inherited
    @Override
	public void dispatch (ComponentListener listener)
    {
        super.dispatch(listener);
        switch (_type) {
        case KEY_PRESSED:
            if (listener instanceof KeyListener) {
                ((KeyListener)listener).keyPressed(this);
            }
            break;

        case KEY_RELEASED:
            if (listener instanceof KeyListener) {
                ((KeyListener)listener).keyReleased(this);
            }
            break;
        }
    }
    
    @Override
	protected void toString (StringBuffer buf)
    {
        super.toString(buf);
        buf.append(", type=").append(_type);
        buf.append(", char=").append(_keyChar);
        buf.append(", code=").append(_keyCode);
    }

    protected int _type;
    protected char _keyChar;
    protected int _keyCode;
}
