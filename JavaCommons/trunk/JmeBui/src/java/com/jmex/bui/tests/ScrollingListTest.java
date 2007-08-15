//
// $Id$
//
// BUI - a user interface library for the JME 3D engine
// Copyright (C) 2006, Pär Winzell, All Rights Reserved
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

import java.net.URL;
import java.util.logging.Level;

import com.jme.util.LoggingSystem;

import com.jmex.bui.*;
import com.jmex.bui.layout.GroupLayout;
import com.jmex.bui.layout.TableLayout;
import com.jmex.bui.background.TintedBackground;
import com.jmex.bui.event.ComponentListener;
import com.jmex.bui.event.MouseEvent;
import com.jmex.bui.event.MouseListener;
import com.jmex.bui.icon.ImageIcon;

public class ScrollingListTest extends BaseTest
    implements BConstants
{
	BImage image = null;
	class MySuperThing extends BContainer
	{

		public MySuperThing(String str)
		{
			setLayoutManager(new TableLayout(2));
			MouseListener ml = new MouseListener()
			{
				MySuperThing t = MySuperThing.this;
				public void mouseEntered(MouseEvent event)
				{
					t.setBackground(BComponent.DEFAULT, TintedBackground.blue);
					t.setBackground(BComponent.HOVER, TintedBackground.blue);
				}

				public void mouseExited(MouseEvent event)
				{
					t.setBackground(BComponent.DEFAULT, null);
					t.setBackground(BComponent.HOVER, null);
				}

				public void mousePressed(MouseEvent event)
				{
					t.setBackground(BComponent.DEFAULT, TintedBackground.red);
					t.setBackground(BComponent.HOVER, TintedBackground.red);
				}

				public void mouseReleased(MouseEvent event)
				{
					t.setBackground(BComponent.DEFAULT, TintedBackground.green);
					t.setBackground(BComponent.HOVER, TintedBackground.green);
				}
			};
			add(new BButton("Button:"+str));
			add(new BLabel(str, null, new ImageIcon(image)));
			add(new BLabel("some string"));
			add(new BLabel(null, null, new ImageIcon(image)));
			for(int i = 0; i < getComponentCount(); i++)
			{
				if(getComponent(i) instanceof BButton)
				{
					// Do nothing about buttons.
				}
				else
				{
					getComponent(i).addListener(ml);
				}
			}
			addListener(ml);
		}
		
		
	}
	
    protected void createWindows (BRootNode root, BStyleSheet style)
    {
        BWindow window = new BDecoratedWindow(style, null);
        window.setLayoutManager(GroupLayout.makeVStretch());

        
        try {
        	URL url = getClass().getClassLoader().getResource("rsrc/textures/flag.png");
        	System.out.println("url:"+url);
            image = new BImage(url);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e);
        }

        /*
        BScrollingList<String, BButton> list =
            new BScrollingList<String, BButton>() {
            public BButton createComponent(String str) {
                return new BButton(str);
            }
        };
        */
        BScrollingList<String, MySuperThing> list =
            new BScrollingList<String, MySuperThing>() {
            public MySuperThing createComponent(String str) {
                return new MySuperThing(str);
            }
        };


        window.add(list);

        root.addWindow(window);
        window.setSize(400, 400);
        window.setLocation(25, 25);

        for (int i = 0; i < 100; i ++) {
            list.addValue("Item #" + i, true);
        }
    }

    public static void main (String[] args)
    {
        LoggingSystem.getLogger().setLevel(Level.WARNING);
        ScrollingListTest test = new ScrollingListTest();
        test.start();
    }
}

