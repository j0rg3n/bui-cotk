package com.jmex.bui;

import java.io.File;
import java.io.FileFilter;
import com.jmex.bui.BFileChooser.FilterMode;
import com.jmex.bui.background.TintedBackground;
import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.ActionListener;
import com.jmex.bui.event.BEvent;
import com.jmex.bui.event.ChangeEvent;
import com.jmex.bui.event.MouseEvent;
import com.jmex.bui.event.TextEvent;

/**
 * This class is much like the file-input field on a webpage. Except it does not
 * have the "Browse..." button, but is activated by pressing it (like a
 * combobox).
 * 
 * It uses the {@link BFileChooser} to let the user select a file/directory. To
 * modify what files can be selected/are shown, you must modify the filechooser
 * of this field. This can be accessed with the method {@linke #getFileChooser}.
 * 
 * @author emanuel
 * 
 */
public class BFileField extends BTextField
{
	BFileChooser _filechooser;
	File _selectedfile;
	private FileFilter filefilter;
	protected ActionListener _listener = new ActionListener()
	{
		public void actionPerformed(ActionEvent event)
		{
			System.out.println("Action: " + event.getAction());
			if (event.getAction().equals("ok"))
			{
				setSelectedFile(_filechooser.getSelectedFile());
			}
		}
	};
	private FilterMode filtermode = FilterMode.FILES_AND_DIRECTORIES;

	public BFileField(File file)
	{
		super(file == null ? "[Click to select file...]" : file.getAbsolutePath());
		_selectedfile = file;
		//setUseTextWrap(false);
		//setEnabled(false);
	}
	
	@Override
	protected void wasAdded()
	{
		super.wasAdded();
		
		// We are never enabled (as the user may not write in us)
		setEnabled(false);
	}
	

	public void setFilterMode(FilterMode mode)
	{
		this.filtermode = mode;
	}

	/**
	 * Get the filechooser when the BFileField is added to a window.
	 * 
	 * @return
	 */
	public BFileChooser getFileChooser()
	{
		return _filechooser;
	}

	// documentation inherited
	@Override
	public boolean dispatchEvent(BEvent event)
	{
		if (event instanceof MouseEvent)
		{
			MouseEvent mev = (MouseEvent) event;
			switch (mev.getType())
			{
			case MouseEvent.MOUSE_PRESSED:
				if (_filechooser == null)
				{
					_filechooser = new BFileChooser(getWindow());
					_filechooser.setFileSelectionMode(filtermode);
					if (filefilter != null)
						_filechooser.setFileFilter(filefilter);
					_filechooser.setFileHidingEnabled(true);
					_filechooser.setLayer(Integer.MAX_VALUE);
					_filechooser.setModal(true);
					_filechooser.addListener(_listener);
					_filechooser.setSelectedFile(_selectedfile);
				}
				_filechooser.popup(_selectedfile);
				return true;
			case MouseEvent.MOUSE_RELEASED:
				return true;
			}
		}
		return super.dispatchEvent(event);
	}

	// documentation inherited
	@Override
	protected String getDefaultStyleClass()
	{
		return "textfield"; // TODO: "filefield"
	}

	public void setSelectedFile(File file)
	{
		_selectedfile = file;
		if (_selectedfile != null)
		{
			setText(_selectedfile.getAbsolutePath());
		}
		else
		{
			setText("[Click to select file...]");
		}
		emitEvent(new TextEvent(this, 0));
	}

	public File getSelectedFile()
	{
		return _selectedfile;
	}

	public void setFileFilter(FileFilter filter)
	{
		this.filefilter = filter;
		if (_filechooser != null)
			_filechooser.setFileFilter(filter);
	}
}
