/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.widget;

/**
 * A TitleProvider provides the title to display according to a view.
 */
public interface TitleProvider {

	/**
	 * Returns the title of the view at position
	 * @param position
	 * @return
	 */
	public String getTitle(int position);
	
}
