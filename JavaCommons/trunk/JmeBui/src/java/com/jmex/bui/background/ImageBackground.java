//
// $Id$
//
// BUI - a user interface library for the JME 3D engine
// Copyright (C) 2005-2006, Michael Bayne, All Rights Reserved
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

package com.jmex.bui.background;

import com.jme.renderer.Renderer;
import com.jmex.bui.BImage;

/**
 * Supports image backgrounds in a variety of ways. Specifically:
 *
 * <ul>
 * <li> Centering the image either horizontally, vertically or both.
 * <li> Scaling the image either horizontally, vertically or both.
 * <li> Tiling the image either horizontally, vertically or both.
 * <li> Framing the image in a fancy way: the background image is divided into
 * nine sections (three across and three down), the corners are rendered
 * unscaled, the central edges are scaled in one direction and the center
 * section is scaled in both directions.
 *
 * <pre>
 * +----------+----------------+----------+
 * | unscaled |  <- scaled ->  | unscaled |
 * +----------+----------------+----------+
 * |    ^     |       ^        |    ^     |
 * |  scaled  |  <- scaled ->  |  scaled  |
 * |    v     |       v        |    v     |
 * +----------+----------------+----------+
 * | unscaled |  <- scaled ->  | unscaled |
 * +----------+----------------+----------+
 * </pre>
 * </ul>
 */
public class ImageBackground extends BBackground
{
    public static final int CENTER_XY = 0;
    public static final int CENTER_X = 1;
    public static final int CENTER_Y = 2;

    public static final int SCALE_XY = 3;
    public static final int SCALE_X = 4;
    public static final int SCALE_Y = 5;

    public static final int TILE_XY = 6;
    public static final int TILE_X = 7;
    public static final int TILE_Y = 8;

    public static final int FRAME_XY = 9;
    public static final int FRAME_X = 10;
    public static final int FRAME_Y = 11;
	private boolean is_added;

    public ImageBackground (int mode, BImage image)
    {
        _mode = mode;
        _image = image;
    }

    // documentation inherited
    @Override
	public int getMinimumWidth ()
    {
        return 1;
    }

    /**
     * Returns the minimum height allowed by this background.
     */
    @Override
	public int getMinimumHeight ()
    {
        return 1;
    }

    // documentation inherited
    @Override
	public void render (Renderer renderer, int x, int y, int width, int height,
        float alpha)
    {
        super.render(renderer, x, y, width, height, alpha);

        switch (_mode/3) {
        case CENTER:
            renderCentered(renderer, x, y, width, height, alpha);
            break;

        case SCALE:
            renderScaled(renderer, x, y, width, height, alpha);
            break;

        case TILE:
            renderTiled(renderer, x, y, width, height, alpha);
            break;

        case FRAME:
            renderFramed(renderer, x, y, width, height, alpha);
            break;
        }
    }

    // documentation inherited
    @Override
	public void wasAdded ()
    {
        super.wasAdded();
        _image.reference();
        is_added = true;
    }

    // documentation inherited
    @Override
	public void wasRemoved ()
    {
        super.wasRemoved();
        _image.release();
        is_added = false;
    }

    public void setImage(BImage image)
    {
    	if(_image != null && is_added)
    	{
    		_image.release();
    	}
    	_image = image;
    	if(is_added)
    	{
    		_image.reference();
    	}
    }
    
    public void setOffset(int offsetx, int offsety)
    {
    	_offsetx = offsetx;
    	_offsety = offsety;
    	
        // Fit offset into interval [0;iheight/width)
    	int iheight = _image.getHeight(), iwidth = _image.getWidth();
    	while(_offsety < 0)
    		_offsety += iheight;
    	while(_offsetx < 0)
    		_offsetx += iwidth;
    }
    
    public int getOffsetX()
    {
    	return _offsetx;
    }
    
    public int getOffsetY()
    {
    	return _offsety;
    }
    
    
    protected void renderCentered (
        Renderer renderer, int x, int y, int width, int height, float alpha)
    {
    	if(_image == null)
    	{
    		System.err.println("THE IMAGEBACKGROUND:"+this+" has a null image, WHAT THE CRAP !!!");
    		return;
    	}
        if(_image.getWidth() <= width && _image.getHeight() <= height)
        {
	        if (_mode == CENTER_X || _mode == CENTER_XY) {
	            x += (width-_image.getWidth())/2;
	        }
	        if (_mode == CENTER_Y || _mode == CENTER_XY) {
	            y += (height-_image.getHeight())/2;
	        }
	        x += _offsetx;
	        y += _offsety;
	        
            // No clipping
        	_image.render(renderer, x, y, alpha);
        }
        else
        {
        	// With clipping
        	int sx = (_image.getWidth()-width)/2 + _offsetx;
        	int sy = (_image.getHeight()-height)/2 + _offsety;
        	_image.render(renderer, sx, sy, width, height, x, y, width, height, alpha);
        }
    }

    protected void renderScaled (
        Renderer renderer, int x, int y, int width, int height, float alpha)
    {
        switch (_mode) {
        case SCALE_X:
            y = (height-_image.getHeight())/2;
            height = _image.getHeight();
            break;
        case SCALE_Y:
            x = (width-_image.getWidth())/2;
            width = _image.getWidth();
            break;
        }
        //width -= _offsetx/2;
        //height -= _offsety/2;
        //x -= _offsetx;
        //y -= _offsety;
        //width = width;
        //height -= (_offsety - height);
        if(_offsety != 0)
        {
        	y -= (height - _image.getHeight() - _offsety);
        }
        if(_offsetx != 0)
        {
        	x -= (width - _image.getWidth() - _offsetx);
        }
        //height -= (height - _image.getHeight() - _offsety);
        _image.render(renderer, x, y, width, height, alpha);
    }

    protected void renderTiled (
        Renderer renderer, int x, int y, int width, int height, float alpha)
    {
        int iwidth = _image.getWidth(), iheight = _image.getHeight();
        
        if (_mode == TILE_X) {
            renderRow(renderer, x, y, width, Math.min(height, iheight), alpha);

        } else if (_mode == TILE_Y) {
        	_offsety = _offsety % iheight;
            int up = (height - _offsety) / iheight;
            iwidth = Math.min(width, iwidth);
            // Render the first part
            if(_offsety != 0)
            {
                _image.render(renderer, 0, iheight-_offsety, iwidth, Math.min(height, _offsety),
                        x, y, iwidth, Math.min(height, _offsety), alpha);
            }
            
            for (int yy = 0; yy < up; yy++) {
                _image.render(renderer, 0, 0, iwidth, iheight,
                              x, y + _offsety + yy*iheight, iwidth, iheight, alpha);
            }
            int remain = (height - _offsety) % iheight;
            if (remain > 0) {
                _image.render(renderer, 0, 0, iwidth, remain,
                              x, y + _offsety + up*iheight, iwidth, remain, alpha);
            }

        } else if (_mode == TILE_XY) {
        	// TODO: make offset-y apply here as well.
            int up = height / iheight;
            for (int yy = 0; yy < up; yy++) {
                renderRow(renderer, x, y + yy*iheight, width, iheight, alpha);
            }
            int remain = height % iheight;
            if (remain > 0) {
                renderRow(renderer, x, y + up*iheight, width, remain, alpha);
            }
        }
    }

    protected void renderRow (
        Renderer renderer, int x, int y, int width, int iheight, float alpha)
    {
        int iwidth = _image.getWidth();
        int across = (width - _offsetx) / iwidth;

        // Render the first part
        if(_offsetx != 0)
        {
            _image.render(renderer, iwidth - _offsetx, 0, Math.min(width, _offsetx), iheight,
                    x, y, Math.min(width, _offsetx), iheight, alpha);
        }
        
        for (int xx = 0; xx < across; xx++) {
            _image.render(renderer, 0, 0, iwidth, iheight,
                          x + _offsetx + xx*iwidth, y, iwidth, iheight, alpha);
        }
        int remain = (width - _offsetx) % iwidth;
        if (remain > 0) {
            _image.render(renderer, 0, 0, remain, iheight,
                          x + _offsetx + across*iwidth, y, remain, iheight, alpha);
        }
    }

    protected void renderFramed (
        Renderer renderer, int x, int y, int width, int height, float alpha)
    {
        // render each of our image sections appropriately
        int twidth = _image.getWidth(), theight = _image.getHeight();
        int wthird = twidth/3, hthird = theight/3;
        int wmiddle = twidth - 2*wthird, hmiddle = theight - 2*hthird;

        // draw the corners
        _image.render(renderer, 0, 0, wthird, hthird, x, y, alpha);
        _image.render(renderer, twidth-wthird, 0, wthird, hthird,
                      x+width-wthird, y, alpha);
        _image.render(renderer, 0, theight-hthird, wthird, hthird,
                      x, y+height-hthird, alpha);
        _image.render(renderer, twidth-wthird, theight-hthird, wthird, hthird,
                      x+width-wthird, y+height-hthird, alpha);

        // draw the "gaps"
        int ghmiddle = width-2*wthird, gvmiddle = height-2*hthird;
        _image.render(renderer, wthird, 0, wmiddle, hthird, x+wthird, y,
                      ghmiddle, hthird, alpha);
        _image.render(renderer, wthird, theight-hthird, wmiddle, hthird,
                      x+wthird, y+height-hthird, ghmiddle, hthird, alpha);

        _image.render(renderer, 0, hthird, wthird, hmiddle, x, y+hthird,
                      wthird, gvmiddle, alpha);
        _image.render(renderer, twidth-wthird, hthird, wthird, hmiddle,
                      x+width-wthird, y+hthird, wthird, gvmiddle, alpha);

        // draw the center
        _image.render(renderer, wthird, hthird, twidth-2*wthird,
                      theight-2*hthird, x+wthird, y+hthird, width-2*wthird,
                      height-2*hthird, alpha);
    }

    protected int _mode;
    protected BImage _image;
    protected int _offsetx = 0, _offsety = 0;

    protected static final int CENTER = 0;
    protected static final int SCALE = 1;
    protected static final int TILE = 2;
    protected static final int FRAME = 3;
}
