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

package com.jmex.bui.layout;

import java.util.HashMap;

import com.jmex.bui.BComponent;
import com.jmex.bui.BContainer;
import com.jmex.bui.util.Dimension;

/**
 * Group layout managers lay out widgets in horizontal or vertical groups.
 */
public abstract class GroupLayout extends BLayoutManager
{
    /**
     * The group layout managers supports two constraints: fixedness
     * and weight. A fixed component will not be stretched along the major
     * axis of the group. Those components that are stretched will have
     * the extra space divided among them according to their weight
     * (specifically receiving the ratio of their weight to the total
     * weight of all of the free components in the container).
     *
     * <p/> If a constraints object is constructed with fixedness set to
     * true and with a weight, the weight will be ignored.
     */
    public static class Constraints
    {
	/** Whether or not this component is fixed. */
	public boolean fixed = false;

	/**
	 * The weight of this component relative to the other components
	 * in the container.
	 */
	public int weight = 1;

	/**
	 * Constructs a new constraints object with the specified
	 * fixedness and weight.
	 */
	public Constraints (boolean fixed)
	{
	    this.fixed = fixed;
	}

	/**
	 * Constructs a new constraints object with the specified
	 * fixedness and weight.
	 */
	public Constraints (int weight)
	{
	    this.weight = weight;
	}
    }

    /** A class used to make our policy constants type-safe. */
    public static class Policy
    {
        int code;

        public Policy (int code)
        {
            this.code = code;
        }
    }

    /** A class used to make our policy constants type-safe. */
    public static class Justification
    {
        int code;

        public Justification (int code)
        {
            this.code = code;
        }
    }

    /**
     * A constraints object that indicates that the component should be
     * fixed and have the default weight of one. This is so commonly used
     * that we create and make this object available here.
     */
    public final static Constraints FIXED = new Constraints(true);

    /**
     * Do not adjust the widgets on this axis.
     */
    public final static Policy NONE = new Policy(0);

    /**
     * Stretch all the widgets to their maximum possible size on this
     * axis.
     */
    public final static Policy STRETCH = new Policy(1);

    /**
     * Stretch all the widgets to be equal to the size of the largest
     * widget on this axis.
     */
    public final static Policy EQUALIZE = new Policy(2);

    /**
     * Only valid for off-axis policy, this leaves widgets alone unless
     * they are larger in the off-axis direction than their container, in
     * which case it constrains them to fit on the off-axis.
     */
    public final static Policy CONSTRAIN = new Policy(3);

    /** A justification constant. */
    public final static Justification CENTER = new Justification(0);

    /** A justification constant. */
    public final static Justification LEFT = new Justification(1);

    /** A justification constant. */
    public final static Justification RIGHT = new Justification(2);

    /** A justification constant. */
    public final static Justification TOP = new Justification(3);

    /** A justification constant. */
    public final static Justification BOTTOM = new Justification(4);

    public void setPolicy (Policy policy)
    {
	_policy = policy;
    }

    public Policy getPolicy ()
    {
	return _policy;
    }

    public void setOffAxisPolicy (Policy offpolicy)
    {
	_offpolicy = offpolicy;
    }

    public Policy getOffAxisPolicy ()
    {
	return _offpolicy;
    }

    public void setGap (int gap)
    {
	_gap = gap;
    }

    public int getGap ()
    {
	return _gap;
    }

    public void setJustification (Justification justification)
    {
	_justification = justification;
    }

    public Justification getJustification ()
    {
	return _justification;
    }

    public void setOffAxisJustification (Justification justification)
    {
	_offjust = justification;
    }

    public Justification getOffAxisJustification ()
    {
	return _offjust;
    }

    // documentation inherited from interface
    @Override
	public void addLayoutComponent (BComponent comp, Object constraints)
    {
	if (constraints != null) {
	    if (constraints instanceof Constraints) {
		if (_constraints == null) {
		    _constraints = new HashMap<BComponent, Object>();
		}
		_constraints.put(comp, constraints);

	    } else {
		throw new RuntimeException("GroupLayout constraints " +
					   "object must be of type " +
					   "GroupLayout.Constraints");
	    }
	}
    }

    // documentation inherited from interface
    @Override
	public void removeLayoutComponent (BComponent comp)
    {
	if (_constraints != null) {
	    _constraints.remove(comp);
	}
    }

    protected boolean isFixed (BComponent child)
    {
	if (_constraints == null) {
	    return false;
	}

	Constraints c = (Constraints)_constraints.get(child);
	if (c != null) {
	    return c.fixed;
	}

	return false;
    }

    protected int getWeight (BComponent child)
    {
	if (_constraints == null) {
	    return 1;
	}

	Constraints c = (Constraints)_constraints.get(child);
	if (c != null) {
	    return c.weight;
	}

	return 1;
    }

    /**
     * Computes dimensions of the children widgets that are useful for the
     * group layout managers.
     */
    protected DimenInfo computeDimens (BContainer parent, int whint, int hhint)
    {
	int count = parent.getComponentCount();
	DimenInfo info = new DimenInfo();
	info.dimens = new Dimension[count];

	for (int i = 0; i < count; i++) {
	    BComponent child = parent.getComponent(i);
// 	    if (!child.isVisible()) {
// 		continue;
// 	    }

            // our layout manager passes only one of the hints depending on
            // whether it is horizontal (height) or vertical (width), so we can
            // pass that hint directly along to the child
	    Dimension csize = child.getPreferredSize(whint, hhint);
	    info.count++;
	    info.totwid += csize.width;
	    info.tothei += csize.height;

	    if (csize.width > info.maxwid) {
		info.maxwid = csize.width;
	    }
	    if (csize.height > info.maxhei) {
		info.maxhei = csize.height;
	    }

	    if (isFixed(child)) {
		info.fixwid += csize.width;
		info.fixhei += csize.height;
		info.numfix++;

	    } else {
		info.totweight += getWeight(child);

                if (csize.width > info.maxfreewid) {
                    info.maxfreewid = csize.width;
                }
                if (csize.height > info.maxfreehei) {
                    info.maxfreehei = csize.height;
                }
	    }

	    info.dimens[i] = csize;
	}

	return info;
    }

    /**
     * Convenience method for creating a horizontal group layout manager.
     */
    public static GroupLayout makeHoriz (
        Policy policy, Justification justification, Policy offpolicy)
    {
        HGroupLayout lay = new HGroupLayout();
        lay.setPolicy(policy);
        lay.setJustification(justification);
        lay.setOffAxisPolicy(offpolicy);
        return lay;
    }

    /**
     * Convenience method for creating a vertical group layout manager.
     */
    public static GroupLayout makeVert (
        Policy policy, Justification justification, Policy offpolicy)
    {
        VGroupLayout lay = new VGroupLayout();
        lay.setPolicy(policy);
        lay.setJustification(justification);
        lay.setOffAxisPolicy(offpolicy);
        return lay;
    }

    /**
     * Convenience method for creating a horizontal group layout manager.
     */
    public static GroupLayout makeHoriz (Justification justification)
    {
        HGroupLayout lay = new HGroupLayout();
        lay.setJustification(justification);
        return lay;
    }

    /**
     * Convenience method for creating a vertical group layout manager.
     */
    public static GroupLayout makeVert (Justification justification)
    {
        VGroupLayout lay = new VGroupLayout();
        lay.setJustification(justification);
        return lay;
    }

    /**
     * Convenience method for creating a horizontal group layout manager
     * that stretches in both directions.
     */
    public static GroupLayout makeHStretch ()
    {
        HGroupLayout lay = new HGroupLayout();
        lay.setPolicy(GroupLayout.STRETCH);
        lay.setOffAxisPolicy(GroupLayout.STRETCH);
        return lay;
    }

    /**
     * Convenience method for creating a vertical group layout manager
     * that stretches in both directions.
     */
    public static GroupLayout makeVStretch ()
    {
        VGroupLayout lay = new VGroupLayout();
        lay.setPolicy(GroupLayout.STRETCH);
        lay.setOffAxisPolicy(GroupLayout.STRETCH);
        return lay;
    }

    /**
     * Makes a container configured with a horizontal group layout manager.
     */
    public static BContainer makeHBox (Justification justification)
    {
        HGroupLayout lay = new HGroupLayout();
        lay.setJustification(justification);
        return new BContainer(lay);
    }

    /**
     * Creates a container configured with a vertical group layout manager.
     */
    public static BContainer makeVBox (Justification justification)
    {
        VGroupLayout lay = new VGroupLayout();
        lay.setJustification(justification);
        return new BContainer(lay);
    }

    protected Policy _policy = NONE;
    protected Policy _offpolicy = CONSTRAIN;
    protected int _gap = DEFAULT_GAP;
    protected Justification _justification = CENTER;
    protected Justification _offjust = CENTER;

    protected HashMap<BComponent, Object> _constraints;

    protected static final int DEFAULT_GAP = 5;
}
