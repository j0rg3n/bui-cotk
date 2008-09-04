package com.jmex.bui;

import com.jme.renderer.ColorRGBA;

public class BAtlasImage extends BImage
{
	/**
     * Create this image as a sub-image of the given image.
     * @param orig
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public BAtlasImage(BImage orig, int x, int y, int width, int height)
    {
    	//super("BImage("+width+"+"+x+"x"+height+"+"+y, width, height);
    	super(width, height);
    	_toffset_x = x;
    	_toffset_y = y;
    	_width = width;
    	_height = height;
    	_twidth = orig._twidth;
    	_theight = orig._theight;
    	setTransparent(orig.isTransparent());
    	_orig_image = orig;
    	_tstate = orig._tstate;
    	//_tstate = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
    	//_tstate.setTexture(orig._tstate.getTexture());
    	setRenderState(_tstate);
        updateRenderState();
    	//setImage(orig.)
        // make sure we have a unique default color object
        getDefaultColor().set(ColorRGBA.white);
    }

    // Used to keep the original alive.
    private BImage _orig_image;
}
