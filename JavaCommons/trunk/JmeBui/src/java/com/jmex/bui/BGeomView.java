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

import org.lwjgl.opengl.GL11;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.system.DisplaySystem;

/**
 * Displays 3D geometry (a {@link Spatial}) inside a normal user interface.
 */
public class BGeomView extends BComponent
{
	/**
	 * Creates a view with no configured geometry. Geometry can be set later
	 * with {@link #setGeometry}.
	 */
	public BGeomView()
	{
		this(null);
	}

	/**
	 * Creates a view with the specified {@link Spatial} to be rendered.
	 */
	public BGeomView(Spatial geom)
	{
		_geom = geom;
		DisplaySystem display = DisplaySystem.getDisplaySystem();
		_swidth = display.getWidth();
		_sheight = display.getHeight();
		setStyleClass("");
	}

	/**
	 * Returns the camera used when rendering our geometry.
	 */
	public Camera getCamera()
	{
		if (_camera == null)
		{
			_camera = createCamera(DisplaySystem.getDisplaySystem());
		}
		return _camera;
	}

	/**
	 * Configures the spatial to be rendered by this view.
	 */
	public void setGeometry(Spatial geom)
	{
		_geom = geom;
	}

	public Spatial getGeometry()
	{
		return _geom;
	}

	/**
	 * Called every frame (while we're added to the view hierarchy) by the
	 * {@link BRootNode}.
	 */
	public void update(float frameTime)
	{
		if (_geom != null)
		{
			_geom.updateGeometricState(frameTime, true);
		}
	}

	// documentation inherited
	@Override
	protected void wasAdded()
	{
		super.wasAdded();
		_root = getWindow().getRootNode();
		_root.registerGeomView(this);
	}

	// documentation inherited
	@Override
	protected void wasRemoved()
	{
		super.wasRemoved();
		_root.unregisterGeomView(this);
		_root = null;
	}

	// documentation inherited
	@Override
	protected void renderComponent(Renderer renderer)
	{
		super.renderComponent(renderer);
		if (_geom == null)
		{
			return;
		}
		applyDefaultStates();
		Camera cam = renderer.getCamera();
		boolean use_ortho = _geom.getRenderQueueMode() == Renderer.QUEUE_ORTHO;
		try
		{
			if (!use_ortho)
			{
				renderer.unsetOrtho();
				// create our camera if necessary
				if (_camera == null)
				{
					_camera = createCamera(DisplaySystem.getDisplaySystem());
				}
				// set up our camera viewport if it has changed
				//if (_cwidth != _width || _cheight != _height)
				{
					_cwidth = _width;
					_cheight = _height;
					int ax = getAbsoluteX(), ay = getAbsoluteY();
					float left = ax / _swidth, right = left + _cwidth / _swidth;
					float bottom = ay / _sheight;
					float top = bottom + _cheight / _sheight;
					_camera.setViewPort(left, right, bottom, top);
					_camera.setFrustumPerspective(45.0f, _width / (float) _height, 1, 1000);
				}
				// now set up the custom camera and render our geometry
				renderer.setCamera(_camera);
				_camera.update();
			}
			renderer.draw(_geom);
		}
		finally
		{
			// restore the camera
			if (!use_ortho)
			{
				renderer.setCamera(cam);
				cam.update();
				cam.apply();
				renderer.setOrtho();
				// we need to restore the GL translation as that got wiped out when
				// we left and re-entered ortho mode
				GL11.glTranslatef(getAbsoluteX(), getAbsoluteY(), 0);
			}
		}
	}

	/**
	 * Called to create and configure the camera that we'll use when rendering
	 * our geometry.
	 */
	protected Camera createCamera(DisplaySystem ds)
	{
		// create a standard camera and frustum
		Camera camera = ds.getRenderer().createCamera((int) _swidth, (int) _sheight);
		camera.setParallelProjection(false);
		// put and point it somewhere sensible by default
		Vector3f loc = new Vector3f(0.0f, 0.0f, 25.0f);
		Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
		Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
		Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
		camera.setFrame(loc, left, up, dir);
		return camera;
	}
	protected BRootNode _root;
	protected Camera _camera;
	protected Spatial _geom;
	protected float _swidth, _sheight, _cwidth, _cheight;
}
