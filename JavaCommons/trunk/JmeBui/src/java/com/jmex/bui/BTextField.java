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

import org.lwjgl.opengl.GL11;

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.BEvent;
import com.jmex.bui.event.FocusEvent;
import com.jmex.bui.event.InputEvent;
import com.jmex.bui.event.KeyEvent;
import com.jmex.bui.event.MouseEvent;
import com.jmex.bui.event.TextEvent;
import com.jmex.bui.text.BKeyMap;
import com.jmex.bui.text.BText;
import com.jmex.bui.text.Document;
import com.jmex.bui.text.EditCommands;
import com.jmex.bui.util.Dimension;
import com.jmex.bui.util.Insets;

/**
 * Displays and allows for the editing of a single line of text.
 */
public class BTextField extends BTextComponent
    implements EditCommands, Document.Listener
{
    /**
     * Creates a blank text field.
     */
    public BTextField ()
    {
        this("");
    }

    /**
     * Creates a text field with the specified starting text.
     */
    public BTextField (String text)
    {
        setDocument(new Document());
        setText(text);
    }

    /**
     * Configures this text field with the specified text for display and
     * editing. The cursor will be adjusted if this text is shorter than
     * its previous position.
     */
    @Override
	public void setText (String text)
    {
        if (text == null) {
            text = "";
        }
        if (!_text.getText().equals(text)) {
            _text.setText(text);
        }
    }

    // documentation inherited
    @Override
	public String getText ()
    {
        return _text.getText();
    }

    /**
     * Configures this text field with a custom document.
     */
    public void setDocument (Document document)
    {
        _text = document;
        _text.addListener(this);
    }

    /**
     * Returns the underlying document used by this text field to maintain its
     * state. Changes to the document will be reflected in the text field
     * display.
     */
    public Document getDocument ()
    {
        return _text;
    }

    /**
     * Configures the preferred width of this text field (the preferred
     * height will be calculated from the font).
     */
    public void setPreferredWidth (int width)
    {
        _prefWidth = width;
    }

    // documentation inherited from interface Document.Listener
    public void textInserted (Document document, int offset, int length)
    {
        // if we're already part of the hierarchy, recreate our glyps
        if (isAdded()) {
            recreateGlyphs();
        }

        // let anyone who is around to hear know that a tree fell in the woods
        emitEvent(new TextEvent(this, -1L));
    }

    // documentation inherited from interface Document.Listener
    public void textRemoved (Document document, int offset, int length)
    {
        // confine the cursor to the new text
        if (_cursp > _text.getLength()) {
            setCursorPos(_text.getLength());
        }

        // if we're already part of the hierarchy, recreate our glyps
        if (isAdded()) {
            recreateGlyphs();
        }

        // let anyone who is around to hear know that a tree fell in the woods
        emitEvent(new TextEvent(this, -1L));
    }

    // documentation inherited
    @Override
	public boolean acceptsFocus ()
    {
        return isEnabled();
    }

    // documentation inherited
    @Override
	public boolean dispatchEvent (BEvent event)
    {
        if (event instanceof KeyEvent) {
            KeyEvent kev = (KeyEvent)event;
            if (kev.getType() == KeyEvent.KEY_PRESSED) {
                int modifiers = kev.getModifiers(), keyCode = kev.getKeyCode();
                switch (_keymap.lookupMapping(modifiers, keyCode)) {
                case BACKSPACE:
                    if (_cursp > 0 && _text.getLength() > 0) {
                        int pos = _cursp-1;
                        if (_text.remove(pos, 1)) { // might change _cursp
                            setCursorPos(pos);
                        }
                    }
                    break;

                case DELETE:
                    if (_cursp < _text.getLength()) {
                        _text.remove(_cursp, 1);
                    }
                    break;

                case CURSOR_LEFT:
                    setCursorPos(Math.max(0, _cursp-1));
                    break;

                case CURSOR_RIGHT:
                    setCursorPos(Math.min(_text.getLength(), _cursp+1));
                    break;

                case START_OF_LINE:
                    setCursorPos(0);
                    break;

                case END_OF_LINE:
                    setCursorPos(_text.getLength());
                    break;

                case ACTION:
                    emitEvent(new ActionEvent(
                                  this, kev.getWhen(), kev.getModifiers(), ""));
                    break;

                case RELEASE_FOCUS:
                    getWindow().requestFocus(null);
                    break;

                case CLEAR:
                    _text.setText("");
                    break;

                default:
                    // insert printable and shifted printable characters
                    char c = kev.getKeyChar();
                    if ((modifiers & ~InputEvent.SHIFT_DOWN_MASK) == 0 &&
                        !Character.isISOControl(c)) {
                        String text = String.valueOf(kev.getKeyChar());
                        if (_text.insert(_cursp, text)) {
                            setCursorPos(_cursp + 1);
                        }
                    } else {
                        return super.dispatchEvent(event);
                    }
                    break;
                }

                return true; // we've consumed these events
            }

        } else if (event instanceof MouseEvent) {
            MouseEvent mev = (MouseEvent)event;
            if (mev.getType() == MouseEvent.MOUSE_PRESSED &&
                // don't adjust the cursor if we have no text
                _text.getLength() > 0) {
                Insets insets = getInsets();
                int mx = mev.getX() - getAbsoluteX() - insets.left + _txoff,
                    my = mev.getY() - getAbsoluteY() - insets.bottom;
                setCursorPos(_glyphs.getHitPos(mx, my));
                return true;
            }

        } else if (event instanceof FocusEvent) {
            FocusEvent fev = (FocusEvent)event;
            switch (fev.getType()) {
            case FocusEvent.FOCUS_GAINED:
                gainedFocus();
                break;
            case FocusEvent.FOCUS_LOST:
                lostFocus();
                break;
            }
            return true;
        }

        return super.dispatchEvent(event);
    }

    // documentation inherited
    @Override
	protected String getDefaultStyleClass ()
    {
        return "textfield";
    }

    // documentation inherited
    @Override
	protected void configureStyle (BStyleSheet style)
    {
        super.configureStyle(style);

        // look up our keymap
        _keymap = style.getKeyMap(this, null);
    }

    // documentation inherited
    @Override
	protected void wasAdded ()
    {
        super.wasAdded();

        // create our underlying text texture
        recreateGlyphs();
    }

    // documentation inherited
    @Override
	protected void wasRemoved ()
    {
        super.wasRemoved();

        if (_glyphs != null) {
            _glyphs.wasRemoved();
            _glyphs = null;
        }
    }

    // documentation inherited
    @Override
	protected void layout ()
    {
        super.layout();

        // cope with becoming smaller or larger
        recreateGlyphs();
    }

    // documentation inherited
    @Override
	protected void stateDidChange ()
    {
        super.stateDidChange();
        recreateGlyphs();
    }

    // documentation inherited
    @Override
	protected void renderComponent (Renderer renderer)
    {
        super.renderComponent(renderer);

        Insets insets = getInsets();

        // render our text
        if (_glyphs != null) {
            // clip the text to our visible text region
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(getAbsoluteX() + insets.left,
                           getAbsoluteY() + insets.bottom,
                           _width - insets.getHorizontal(),
                           _height - insets.getVertical());
            try {
                _glyphs.render(renderer, insets.left - _txoff,
                               insets.bottom, _alpha);
            } finally {
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }
        }

        // render the cursor if we have focus
        if (_showCursor) {
            int cx = insets.left - _txoff + _cursx;
            BComponent.applyDefaultStates();
            ColorRGBA c = getColor();
            GL11.glColor4f(c.r, c.g, c.b, c.a);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex2f(cx, insets.bottom);
            int cheight = getTextFactory().getHeight();
            GL11.glVertex2f(cx, insets.bottom + cheight);
            GL11.glEnd();
        }
    }

    // documentation inherited
    @Override
	protected Dimension computePreferredSize (int whint, int hhint)
    {
        Dimension d = (_glyphs == null) ?
            new Dimension(0, getTextFactory().getHeight()) :
            new Dimension(_glyphs.getSize());
        if (_prefWidth != -1) {
            d.width = _prefWidth;
        }
        return d;
    }

    /**
     * Called when this text field has gained the focus.
     */
    protected void gainedFocus ()
    {
        _showCursor = true;
        setCursorPos(_cursp);
    }

    /**
     * Called when this text field has lost the focus.
     */
    protected void lostFocus ()
    {
        _showCursor = false;
    }

    /**
     * Recreates the entity that we use to render our text.
     */
    protected void recreateGlyphs ()
    {
        clearGlyphs();

        // if we have no text, clear out all our internal markers
        if (_text.getLength() == 0) {
            _txoff = _cursp = _cursx = 0;
            return;
        }

        // format our text and determine how much of it we can display
        int avail = getWidth() - getInsets().getHorizontal();
        _glyphs = getTextFactory().createText(
            getDisplayText(), getColor(), BConstants.NORMAL, null, true);
        if (isAdded()) {
            _glyphs.wasAdded();
        }
        setCursorPos(_cursp);
    }

    /**
     * Clears out our text textures and other related bits.
     */
    protected void clearGlyphs ()
    {
        if (_glyphs != null && isAdded()) {
            _glyphs.wasRemoved();
        }
        _glyphs = null;
    }        

    /**
     * This method allows a derived class (specifically {@link
     * BPasswordField}) to display something other than the actual
     * contents of the text field.
     */
    protected String getDisplayText ()
    {
        return _text.getText();
    }

    /**
     * Updates the cursor position, moving the visible representation as
     * well as the insertion and deletion point.
     */
    protected void setCursorPos (int cursorPos)
    {
        // note the new cursor character position
        _cursp = cursorPos;

        // compute the new cursor screen position
        if (_glyphs != null) {
            _cursx = _glyphs.getCursorPos(cursorPos);
        } else {
            _cursx = 0;
        }

        // scroll our text left or right as necessary
        if (_cursx < _txoff) {
            _txoff = _cursx;
        } else {
            int avail = getWidth() - getInsets().getHorizontal();
            if (_cursx > _txoff + avail) {
                _txoff = _cursx - avail;
            }
        }
    }

    protected Document _text;
    protected BText _glyphs;
    protected BKeyMap _keymap;

    protected int _prefWidth = -1;
    protected boolean _showCursor;
    protected int _cursp, _cursx, _txoff;
}
