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

package org.openmetromaps.maps.editor.actions.edit;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.openmetromaps.maps.editor.MapEditor;
import org.openmetromaps.maps.editor.actions.MapEditorAction;
import org.openmetromaps.maps.graph.LineConnectionResult;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkUtil;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.graph.NodeConnectionResult;
import org.openmetromaps.maps.graph.NodesInBetweenResult;
import org.openmetromaps.maps.model.Line;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.lightgeom.lina.Point;
import de.topobyte.swing.util.EmptyIcon;

public class DistributeEvenlyAction extends MapEditorAction
{

	final static Logger logger = LoggerFactory
			.getLogger(DistributeEvenlyAction.class);

	private static final long serialVersionUID = 1L;

	public DistributeEvenlyAction(MapEditor mapEditor)
	{
		super(mapEditor, "Distribute Evenly",
				"Distribute the stations between two selected ones evenly");
		setIcon(new EmptyIcon(24));
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		List<Node> nodes = mapEditor.getMapViewStatus().getSelectedNodes();

		if (nodes.size() != 2) {
			JOptionPane.showMessageDialog(mapEditor.getFrame(),
					"Please select exactly two stations.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		Iterator<Node> iterator = nodes.iterator();
		Node node1 = iterator.next();
		Node node2 = iterator.next();
		logger.debug(String.format("Trying to connect: '%s' and '%s'",
				node1.station.getName(), node2.station.getName()));

		NodeConnectionResult connection = LineNetworkUtil.findConnection(node1,
				node2);

		if (!connection.isConnected()) {
			JOptionPane.showMessageDialog(mapEditor.getFrame(),
					"Please select two stations that are connected with a line.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		Line line = connection.getCommonLines().iterator().next();

		logger.debug("Common line: " + line.getName());

		LineConnectionResult lineConnection = LineNetworkUtil
				.findConnection(line, node1, node2);

		if (!lineConnection.isValid()) {
			JOptionPane.showMessageDialog(mapEditor.getFrame(),
					"Unable to determine connection between stations.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		int idxNode1 = lineConnection.getIdxNode1();
		int idxNode2 = lineConnection.getIdxNode2();

		LineNetwork lineNetwork = mapEditor.getMap().getLineNetwork();
		NodesInBetweenResult nodesBetween = LineNetworkUtil
				.getNodesBetween(lineNetwork, line, idxNode1, idxNode2);

		List<Node> between = nodesBetween.getNodes();
		int num = between.size();

		Point c1 = nodesBetween.getStart().location;
		Point c2 = nodesBetween.getEnd().location;

		double diffX = c2.getX() - c1.getX();
		double diffY = c2.getY() - c1.getY();

		double dx = diffX / (num + 1);
		double dy = diffY / (num + 1);

		for (int i = 0; i < num; i++) {
			Node node = between.get(i);
			double x = c1.x + dx * (i + 1);
			double y = c1.y + dy * (i + 1);
			node.location = new Point(x, y);
		}

		LineNetworkUtil.updateEdges(nodesBetween.getStart());
		LineNetworkUtil.updateEdges(nodesBetween.getEnd());
		for (Node node : between) {
			LineNetworkUtil.updateEdges(node);
		}

		mapEditor.getMap().repaint();
	}

}
