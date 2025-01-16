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

package org.openmetromaps.gtfs;

public class DraftStation
{

	private String id;
	private String name;
	private String source;
	private double lon;
	private double lat;

	public DraftStation(String id, String name, String source, double lon, double lat)
	{
		this.id = id;
		this.name = name;
		this.source = source;
		this.lon = lon;
		this.lat = lat;
	}

	public String getId() {
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getSource()
	{
		return source;
	}

	public double getLon()
	{
		return lon;
	}

	public double getLat()
	{
		return lat;
	}

}
