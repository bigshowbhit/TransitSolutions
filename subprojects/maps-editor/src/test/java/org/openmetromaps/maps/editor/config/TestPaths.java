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

package org.openmetromaps.maps.editor.config;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;

public class TestPaths
{

	static Path PATH_PERMANENT;

	static Path PATH_VOLATILE;

	static {
		try {
			PATH_PERMANENT = Path.of(Objects.requireNonNull(TestPaths.class.getClassLoader().getResource("config.xml")).toURI());
			PATH_VOLATILE = Path.of(Objects.requireNonNull(TestPaths.class.getClassLoader().getResource("volatile-config.xml")).toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

}
