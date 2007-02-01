package com.jmex.bui;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;
import com.jmex.bui.BFileChooser.BFileList.BFileListItem;
import com.jmex.bui.BSelectList.SelectMode;
import com.jmex.bui.background.TintedBackground;
import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.ActionListener;
import com.jmex.bui.event.BEvent;
import com.jmex.bui.event.MouseEvent;
import com.jmex.bui.icon.BIcon;
import com.jmex.bui.icon.ImageIcon;
import com.jmex.bui.layout.TableLayout;
import com.jmex.bui.util.Dimension;

/**
 * This class displays a file-chooser.
 * 
 * It is based loosely on the "interface" of the JFileChooser, but uses the Java
 * 1.5 enums in stead of constants.
 * 
 * @author emanuel
 * 
 */
public class BFileChooser extends BPopupWindow
{
	private FileFilter filefilter;
	public enum FilterMode
	{
		FILES_ONLY,
		DIRECTORIES_ONLY,
		FILES_AND_DIRECTORIES,
	}
	FilterMode filtermode;
	File _selected_file = null;
	private BTextField selected_file_label;
	private BSelectList drivelist;
	BFileList filelist;
	private BButton cancelbutton;
	private BButton okbutton;
	FileSystemView filesystemview = FileSystemView.getFileSystemView();
	private boolean _hide_hidden_files;
	ActionListener _button_listener = new ActionListener()
	{
		public void actionPerformed(ActionEvent event)
		{
			emitEvent(new ActionEvent(this, event.getWhen(), event.getModifiers(), event.getAction()));
			dismiss();
		}
	};
	/**
	 * A special select-list that displays files/directories.
	 * 
	 * @author emanuel
	 * 
	 */
	class BFileList extends BSelectList
	{
		class BFileListItem extends BLabel
		{
			File file;

			public BFileListItem(File file)
			{
				super((_directory == null || _directory.getParentFile() == null || !_directory.getParentFile().equals(file)) ? file.getName() : "[..]", null, (filesystemview.getSystemIcon(file) != null ? new ImageIcon(filesystemview.getSystemIcon(file)) : null), false);
				this.file = file;
				System.out.println("Constructor[2]: " + useTextWrap());
			}
		}
		File _directory;

		@Override
		public void addItem(Object i)
		{
			File f = (File) i;
			super.addItem(new BFileListItem(f));
		}

		public File getDirectory()
		{
			return _directory;
		}

		public void setDirectory(File p)
		{
			_directory = p;
			Vector<File> fs = new Vector<File>();
			if (p.getParent() != null && !p.getParentFile().equals(p))
				fs.add(p.getParentFile());
			if (p.listFiles() != null)
			{
				for (File f : p.listFiles())
				{
					if (accept(f))
						fs.add(f);
				}
			}
			Object[] fso = fs.toArray();
			Arrays.sort(fso);
			setItems(fso);
		}
	}

	public BFileChooser(BWindow parent)
	{
		super(parent, new TableLayout(1, 5, 5));
		TableLayout lay = (TableLayout) getLayoutManager();
		lay.setHorizontalAlignment(TableLayout.STRETCH);
		// Add the top part (drives/filelist)
		{
			BContainer toppart;
			TableLayout tmplay;
			add(toppart = new BContainer(tmplay = new TableLayout(2)));
			tmplay.setHorizontalAlignment(TableLayout.STRETCH);
			tmplay.setVerticalAlignment(TableLayout.TOP);
			toppart.add(drivelist = new BFileList());
			drivelist.setPreferredSize(new Dimension(125, 200));
			for (File f : filesystemview.getRoots())
			{
				drivelist.addItem(f);
			}
			drivelist.addListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					System.out.println("TODO: we must react on the drive-list clicks: " + event.getAction());
				}
			});
			toppart.add(filelist = new BFileList());
			filelist.setPreferredSize(new Dimension(325, 200));
			filelist.setBackground(BComponent.DEFAULT, TintedBackground.red);
			filelist.addListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					if (event.getAction().equals("selectionChanged"))
					{
						BFileListItem i = (BFileListItem) filelist.getSelectedItem();
						setSelectedFile(i.file);
					}
				}
			});
		}
		// Add middle-part (the selected file)
		add(selected_file_label = new BTextField("..."));
		selected_file_label.setStyleClass("textfield");
		// Add the bottom part (ok and cancel buttons)
		{
			BContainer bottompart;
			TableLayout tmplay;
			add(bottompart = new BContainer(tmplay = new TableLayout(2)));
			// tmplay.set
			bottompart.add(okbutton = new BButton("OK"));
			okbutton.setPreferredSize(new Dimension(100, 30));
			okbutton.setAction("ok");
			okbutton.addListener(_button_listener);
			bottompart.add(cancelbutton = new BButton("Cancel"));
			cancelbutton.setPreferredSize(new Dimension(100, 30));
			cancelbutton.setAction("cancel");
			cancelbutton.addListener(_button_listener);
		}
		// And set our size
		setPreferredSize(new Dimension(450, 300));
	}

	/**
	 * Returns true if the file should be displayed.
	 * 
	 * @param file
	 * @return
	 */
	public boolean accept(File file)
	{
		if (file.isFile() && filtermode == FilterMode.DIRECTORIES_ONLY)
			return false;
		if (file.isDirectory() && filtermode == FilterMode.FILES_ONLY)
			return false;
		if (_hide_hidden_files && file.isHidden())
			return false;
		if (filefilter != null)
			return filefilter.accept(file);
		return true;
	}

	public void setFileFilter(FileFilter filter)
	{
		this.filefilter = filter;
	}

	public void setFileSelectionMode(FilterMode filtermode)
	{
		this.filtermode = filtermode;
	}

	public File getSelectedFile()
	{
		return _selected_file;
	}

	public void setSelectedFile(File file)
	{
		if (file != null)
		{
			File p = file.isDirectory() ? file : file.getParentFile();
			System.out.println("file: "+file);
			System.out.println("p:    "+p);
			if (p != null && !p.equals(filelist.getDirectory()))
			{
				filelist.setDirectory(p);
			}
			else
			{
				filelist.setDirectory(new File("."));
			}
		}
		_selected_file = file;
		selected_file_label.setText(file != null ? file.getAbsolutePath() : "");
	}

	public void popup(File directory)
	{
		if (directory == null)
		{
			directory = filesystemview.getDefaultDirectory();
		}
		setSelectedFile(directory);
		popup(0, 0, false);
		center();
	}

	// documentation inherited
	@Override
	public boolean dispatchEvent(BEvent event)
	{
		if (event instanceof MouseEvent)
		{
			MouseEvent mev = (MouseEvent) event;
			// if the mouse clicked outside of our window bounds, dismiss
			// ourselves
			if (mev.getType() == MouseEvent.MOUSE_PRESSED && getHitComponent(mev.getX(), mev.getY()) == null)
			{
				dismiss();
				return true;
			}
		}
		return super.dispatchEvent(event);
	}

	public void setFileHidingEnabled(boolean b)
	{
		_hide_hidden_files = b;
	}
}
