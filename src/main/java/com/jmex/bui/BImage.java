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

package com.jmex.bui;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GLContext;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.image.Image.Format;
import com.jme.image.Texture.MagnificationFilter;
import com.jme.image.Texture.MinificationFilter;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.BlendState.DestinationFunction;
import com.jme.scene.state.BlendState.SourceFunction;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

/**
 * Contains a texture, its dimensions and a texture state.
 */
public class BImage extends Quad
{
	
	private static final long serialVersionUID = 1L;
	
    /** An interface for pooling OpenGL textures. */
    public interface TexturePool
    {
        /**
         * Acquires textures from the pool for the state.
         */
        public void acquireTextures (TextureState tstate);

        /**
         * Releases the state's textures back into the pool.
         */
        public void releaseTextures (TextureState tstate);
    }

    /**
     * A blend state that blends the source plus one minus destination.
     */
    public static BlendState blendState;

    /**
     * Configures the supplied spatial with transparency in the standard user interface sense which is that transparent
     * pixels show through to the background but non-transparent pixels are not blended with what is behind them.
     */
    public static void makeTransparent (Spatial target)
    {
        target.setRenderState(blendState);
    }

    /**
     * Sets the texture pool from which to acquire and release OpenGL texture objects. Applications can provide a pool
     * in order to avoid the rapid creation and destruction of OpenGL textures.  The default pool always creates new
     * textures and deletes released ones.
     */
    public static void setTexturePool (TexturePool pool)
    {
        _texturePool = pool;
    }

    /**
     * Returns a reference to the configured texture pool.
     */
    public static TexturePool getTexturePool ()
    {
        return _texturePool;
    }

    /**
     * Creates an image from the supplied source URL.
     */
    public BImage (URL image)
        throws IOException
    {
        this(ImageIO.read(image), true);
    }

    /**
     * Creates an image from the supplied source AWT image.
     */
    public BImage (java.awt.Image image)
    {
        this(image, true);
    }

    /**
     * Creates an image from the supplied source AWT image.
     */
    public BImage (java.awt.Image image, boolean flip)
    {
        this(image.getWidth(null), image.getHeight(null));

        // expand the texture data to a power of two if necessary
        int twidth = _width, theight = _height;
        if (!_supportsNonPowerOfTwo) {
            twidth = nextPOT(twidth);
            theight = nextPOT(theight);
        }

        // render the image into a raster of the proper format
        boolean hasAlpha = TextureManager.hasAlpha(image);
        int bpp = (hasAlpha ? 4 : 3);
		ByteBuffer scratch = ByteBuffer.allocateDirect(
                bpp * twidth * theight).order(ByteOrder.nativeOrder());
		/*try
		{
			BufferedImage bimage = (BufferedImage) image;
			putImageToByteBuffer(bimage, twidth, theight, scratch, flip);
		}
		catch (Exception e)
		{
        	logger.info("Non-bufferd/Unsuported-type image, doing things the old fasion way");
	   */   int type = hasAlpha ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR;
	        BufferedImage tex = new BufferedImage(twidth, theight, type);
	        AffineTransform tx = null;
	        if (flip) {
	            tx = AffineTransform.getScaleInstance(1, -1);
	            tx.translate(0, -_height);
	        }
	        Graphics2D gfx = (Graphics2D) tex.getGraphics();
	        gfx.drawImage(image, tx, null);
	        gfx.dispose();
	        // grab the image memory and stuff it into a direct byte buffer
	        scratch.clear();
	        scratch.put((byte[])tex.getRaster().getDataElements(0, 0, twidth, theight, null));
	        scratch.flip();
		//}
        Image textureImage = new Image();
        textureImage.setFormat(hasAlpha ? Format.RGBA8 : Format.RGBA8);
        textureImage.setWidth(twidth);
        textureImage.setHeight(theight);
        textureImage.setData(scratch);

        setImage(textureImage);

        // make sure we have a unique default color object
        getDefaultColor().set(ColorRGBA.white);
    }
    
    /**
     * Construct a BImage from a JME-image.
     * @param image
     */
    public BImage(Image image) {
    	this(image.getWidth(), image.getHeight());
    	setImage(image);
    }
    
    /**
     * This will re-upload the image data (note how-ever that "flip" is currently not supported).
     * @param image
     * @param flip
     */
/*    public void reUploadImage(BufferedImage image, boolean flip)
    {
    	// We can only do this with equal size.
        assert (image.getWidth(null) == getWidth() 
        		&& image.getHeight(null) == getHeight());

        // expand the texture data to a power of two if necessary
        int twidth = _width, theight = _height;
        if (!_supportsNonPowerOfTwo) {
            twidth = nextPOT(twidth);
            theight = nextPOT(theight);
        }

        // grab the image memory and stuff it into a direct byte buffer
        ByteBuffer scratch = getTextureState().getTexture().getImage().getData();
        putImageToByteBuffer(image, twidth, theight, scratch, flip);

        getTextureState().setNeedsRefresh(true);
        getTextureState().load(0);
        updateRenderState();
    }

	private void putImageToByteBuffer(BufferedImage image, int twidth, int theight, ByteBuffer scratch, boolean flip)
	{
        int bpp = image.getColorModel().hasAlpha() ? 4 : 3;
        byte data[];
		scratch.clear();
        if(image.getRaster().getDataBuffer() instanceof DataBufferByte)
        {
        	data = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
        	switch(image.getType())
        	{
        	case BufferedImage.TYPE_4BYTE_ABGR:
	        	for(int i = 0; i < data.length; i += bpp)
	        	{
	        		byte b0 = data[i+0];
	        		byte b1 = data[i+1];
	        		byte b2 = data[i+2];
	        		byte b3 = data[i+3];
	        		data[i+0] = b3;
	        		data[i+1] = b2; // green (v)
	        		data[i+2] = b1; // blue
	        		data[i+3] = b0; // alpha (v)
	        	}
	        	break;
        	case BufferedImage.TYPE_3BYTE_BGR:
	        	for(int i = 0; i < data.length; i += bpp)
	        	{
	        		byte b0 = data[i+0];
	        		byte b1 = data[i+1];
	        		byte b2 = data[i+2];
	        		data[i+0] = b2; // red
	        		data[i+1] = b1; // green
	        		data[i+2] = b0; // blue
	        	}
	        	break;
        	default:
        		logger.warning("Could not convert image of type ["+image.getType()+"], lets just hope the data is usable");
        	}
        }
        else
        {
        	logger.warning("The databuffer was not a DataBufferByte, fall back to normal element getting");
        	data = (byte[])image.getRaster().getDataElements(0, 0, getWidth(), getHeight(), null);
        }
        if(_width == twidth && _height == theight && !flip)
        {
        	// Easy !
        	scratch.put(data);
        }
        else
        {
        	int row_size = _width * bpp;
        	int h = image.getHeight();
        	int pad_size = (twidth - _width)*bpp;
        	for(int y = 0; y < _height; y++)
        	{
        		int row_y = (flip ? (h-y-1) : y);
        		scratch.put(data, row_y*row_size, row_size);
        		// Just fill crap in for the rest
        		if(pad_size > 0)
        		{
        			scratch.put(data, 0, pad_size);
        		}
        	}
        }
        scratch.flip();
	}
*/

    /**
     * Creates an image of the specified size, using the supplied JME image data. The image should
     * be a power of two size if OpenGL requires it.
     *
     * @param width the width of the renderable image.
     * @param height the height of the renderable image.
     * @param image the image data.
     */
    public BImage (int width, int height, Image image)
    {
        this(width, height);
        setImage(image);
    }

    /**
     * Returns the width of this image.
     */
    public int getImageWidth ()
    {
        return _width;
    }

    /**
     * Returns the height of this image.
     */
    public int getImageHeight ()
    {
        return _height;
    }

    /**
     * Configures this image to use transparency or not (true by default).
     */
    public void setTransparent (boolean transparent)
    {
        if (transparent) {
            setRenderState(blendState);
        } else {
            clearRenderState(RenderState.RS_BLEND);
        }
        updateRenderState();
    }
    
    public boolean isTransparent ()
    {
    	return getRenderState(RenderState.RS_BLEND) != null;
    }

    /**
     * Configures the image data to be used by this image.
     */
    public void setImage (Image image)
    {
        // free our old texture as appropriate
        if (_referents > 0) {
            releaseTexture();
        }

        Texture texture = new Texture2D();
        texture.setImage(image);

        _twidth = image.getWidth();
        _theight = image.getHeight();
    	_toffset_x = 0;
    	_toffset_y = _theight - _height;

    	// Warn if we end here with unsupported NPOT
    	if(!_supportsNonPowerOfTwo && (nextPOT(_twidth) != _twidth || nextPOT(_theight) != _theight))
    	{
    		Log.log.log(Level.WARNING, "NPOT image set as texture ("+_twidth+"x"+_theight+", this will look bad", new Throwable());
    	}

        texture.setMagnificationFilter(MagnificationFilter.Bilinear);
        texture.setMinificationFilter(MinificationFilter.BilinearNearestMipMap);
        _tstate.setTexture(texture);
        _tstate.setEnabled(true);
        _tstate.setCorrectionType(TextureState.CorrectionType.Affine);
        setRenderState(_tstate);
        updateRenderState();

        // preload the new texture
        if (_referents > 0) {
            acquireTexture();
        }
    }

    /**
     * Configures our texture coordinates to the specified subimage. This does not normally need to
     * be called, but if one is stealthily using a BImage as a quad, then it does.
     */
    public void setTextureCoords (int sx, int sy, int swidth, int sheight)
    {
        float lx = sx / (float)_twidth;
        float ly = sy / (float)_theight;
        float ux = (sx+swidth) / (float)_twidth;
        float uy = (sy+sheight) / (float)_theight;

        FloatBuffer tcoords = getTextureCoords().get(0).coords;
        tcoords.clear();
        tcoords.put(lx).put(uy);
        tcoords.put(lx).put(ly);
        tcoords.put(ux).put(ly);
        tcoords.put(ux).put(uy);
        tcoords.flip();
    }

    /**
     * Renders this image at the specified coordinates.
     */
    public void render (Renderer renderer, int tx, int ty, float alpha)
    {
        render(renderer, tx, ty, _width, _height, alpha);
    }

    /**
     * Renders this image at the specified coordinates, scaled to the specified size.
     */
    public void render (Renderer renderer, int tx, int ty, int twidth, int theight, float alpha)
    {
        render(renderer, 0, 0, _width, _height, tx, ty, twidth, theight, alpha);
    }

    /**
     * Renders a region of this image at the specified coordinates.
     */
    public void render (Renderer renderer, int sx, int sy,
                        int swidth, int sheight, int tx, int ty, float alpha)
    {
        render(renderer, sx, sy, swidth, sheight, tx, ty, swidth, sheight, alpha);
    }

    /**
     * Renders a region of this image at the specified coordinates, scaled to the specified size.
     */
    public void render (Renderer renderer, int sx, int sy, int swidth, int sheight,
                        int tx, int ty, int twidth, int theight, float alpha)
    {
        if (_referents == 0) {
            Log.log.warning("Unreferenced image rendered " + this + "!");
            Thread.dumpStack();
            return;
        }
        
        sx += _toffset_x;
    	sy += _theight-(_toffset_y+_height);

        setTextureCoords(sx, sy, swidth, sheight);

        resize(twidth, theight);
        localTranslation.x = tx + twidth/2f;
        localTranslation.y = ty + theight/2f;
        updateGeometricState(0, true);

        getDefaultColor().a = alpha;
        draw(renderer);
    }

    /**
     * Notes that something is referencing this image and will subsequently call {@link #render} to
     * render the image. <em>This must be paired with a call to {@link #release}.</em>
     */
    public void reference ()
    {
        if (_referents++ == 0) {
            acquireTexture();
        }
    }

    /**
     * Unbinds our underlying texture from OpenGL, removing the data from graphics memory. This
     * should be done when the an image is no longer being displayed. The image will automatically
     * rebind next time it is rendered.
     */
    public void release ()
    {
        if (_referents == 0) {
            Log.log.warning("Unreferenced image released " + this + "!");
            Thread.dumpStack();

        } else if (--_referents == 0) {
            releaseTexture();
        }
    }
    
    /**
     * Helper constructor.
     */
    public BImage (int width, int height)
    {
        super("name", width, height);
        _width = width;
        _height = height;
        _tstate = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        clearRenderState(RenderState.RS_MATERIAL);
        setTransparent(true);
    }

    protected void acquireTexture ()
    {
        if (_tstate.getNumberOfSetTextures() > 0) {
            _texturePool.acquireTextures(_tstate);
        }
    }

    protected void releaseTexture ()
    {
        if (_tstate.getNumberOfSetTextures() > 0) {
            _texturePool.releaseTextures(_tstate);
        }
    }
    
    /**
     * Return the texture state for changing things (advanced).
     * @return
     */
    public TextureState getTextureState()
    {
    	return _tstate;
    }

    /** Rounds the supplied value up to a power of two. */
    protected static int nextPOT (int value)
    {
        return (Integer.bitCount(value) > 1) ? (Integer.highestOneBit(value) << 1) : value;
    }

    private final static Logger logger = Logger.getLogger(BImage.class.getName());
    protected TextureState _tstate;
    protected int _width, _height;
    protected int _toffset_x, _toffset_y;
    protected int _twidth, _theight;
    protected int _referents;

    protected static boolean _supportsNonPowerOfTwo;

    protected static TexturePool _texturePool = new TexturePool() {
    	
        public void acquireTextures (TextureState tstate) {
            tstate.apply(); // preload
        }
        public void releaseTextures (TextureState tstate) {
           // tstate.deleteAll();
        }
    };

    static {
        blendState = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        blendState.setBlendEnabled(true);
        blendState.setSourceFunction(SourceFunction.SourceAlpha);
        blendState.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);
        blendState.setEnabled(true);
        _supportsNonPowerOfTwo = GLContext.getCapabilities().GL_ARB_texture_non_power_of_two;
    }

	public int getReferenceCount()
	{
		return _referents;
	}
}
