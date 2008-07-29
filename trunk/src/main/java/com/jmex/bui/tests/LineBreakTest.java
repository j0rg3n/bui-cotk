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

package com.jmex.bui.tests;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jmex.bui.*;
import com.jmex.bui.background.TintedBackground;
import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.ActionListener;
import com.jmex.bui.layout.TableLayout;

public class LineBreakTest extends BaseTest implements BConstants, ActionListener
{
    private BWindow window;
    BSimpleScrollPane scrollingpane;
    private int width = 975;
    private int height = 720;
    private String text;
    private BLabel nl; 
    private BLabel sl; 
    
    protected void createWindows(BRootNode root, BStyleSheet style) 
    {
        TableLayout lay;
        window = new BDecoratedWindow(style, null);
        window.setLayoutManager(lay = new TableLayout(1));
        window.setBackground(BComponent.DEFAULT, TintedBackground.green);
        lay.setHorizontalAlignment(TableLayout.CENTER);
        lay.setVerticalAlignment(TableLayout.CENTER);

        text = "The newline (line feed) character (\\u000A):\n";
        text += "The carriage-return character (\\u000D):\r";
        for (int i = 0; i < 500; i++)
		{
            text += "@=#FF1111(This is red.) Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        	//text += i+" ";
		}
        
        BButton button = new BButton("Change size");
        button.addListener(this);
        window.add(button);

        nl = new BLabel(text);
		nl.setStyleClass("normaltext");
		window.add(nl);
		
		sl = new BLabel(text);
		sl.setStyleClass("smalltext");
		window.add(sl);
        
        root.addWindow(window);
        window.setSize(width, height);
        window.setLocation(25, 25);
    }

    public static void main(String[] args) 
    {
        Logger.global.setLevel(Level.OFF);
        LineBreakTest app = new LineBreakTest();
        app.setDialogBehaviour(NEVER_SHOW_PROPS_DIALOG);
        app.start();
    }

    public void actionPerformed(ActionEvent event)
	{
		nl.setText("");
		nl.setText(text);
		sl.setText("");
		sl.setText(text);
	}
}
