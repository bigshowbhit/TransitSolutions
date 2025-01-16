// Copyright 2017 Sebastian Kuerten
//
// This file is part of OpenMetroMaps.
//
// OpenMetroMaps is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// OpenMetroMaps is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with OpenMetroMaps. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.xml.domabstraction.desktopimpl;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.topobyte.xml.domabstraction.iface.IElement;
import de.topobyte.xml.domabstraction.iface.INodeList;

class DNodeList implements INodeList
{

	private NodeList list;

	public DNodeList(NodeList list)
	{
		this.list = list;
	}

	@Override
	public int getLength()
	{
		return list.getLength();
	}

	@Override
	public IElement element(int i)
	{
		return new DElement((Element) list.item(i));
	}

}
