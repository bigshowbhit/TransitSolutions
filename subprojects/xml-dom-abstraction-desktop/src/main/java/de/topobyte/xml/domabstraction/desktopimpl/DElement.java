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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.topobyte.xml.domabstraction.iface.IElement;
import de.topobyte.xml.domabstraction.iface.INodeList;

class DElement implements IElement
{

	private Element element;

	public DElement(Element element)
	{
		this.element = element;
	}

	@Override
	public INodeList getElementsByTagName(String name)
	{
		NodeList list = element.getElementsByTagName(name);
		return new DNodeList(list);
	}

	@Override
	public INodeList getChildElementsByTagName(String name)
	{
		NodeList children = element.getChildNodes();
		List<Node> filtered = new ArrayList<>();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeName().equals(name)) {
				filtered.add(child);
			}
		}
		return new DNodeList2(filtered);
	}

	@Override
	public boolean hasAttribute(String name)
	{
		return element.hasAttribute(name);
	}

	@Override
	public String getAttribute(String name)
	{
		return element.getAttribute(name);
	}

}
