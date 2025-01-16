// Copyright 2018 Sebastian Kuerten
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

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.topobyte.xml.domabstraction.iface.IElement;
import de.topobyte.xml.domabstraction.iface.INodeList;

class DNodeList2 implements INodeList
{

	private List<Node> list;

	public DNodeList2(List<Node> list)
	{
		this.list = list;
	}

	@Override
	public int getLength()
	{
		return list.size();
	}

	@Override
	public IElement element(int i)
	{
		return new DElement((Element) list.get(i));
	}

}
