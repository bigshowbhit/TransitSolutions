package org.openmetromaps.maps;

import org.openmetromaps.maps.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ReplacementServices {
    private static final Logger log = LoggerFactory.getLogger(ReplacementServices.class);

    public static void closeStation(ModelData model, Station station, List<Line> lines) {

        for(Line line: lines)
        {
            if(line.getStops().size()<=2)
            {
                System.out.println("Line " + line.getName() + " has fewer than 2 stops. Operation not executed.");
                return;
            }
        }

        for(Line line: lines)
        {
            //1st step: Iterate through a line and get all the stops on that line
            List<Stop> stopsOnLine = line.getStops();
            Stop stopToRemove = null;
            //2nd step: Find the stop on the line to be removed!
            for(Stop stop: stopsOnLine)
            {
                if(stop.getStation().equals(station))
                {
                    stopToRemove = stop;
                    break;
                }
            }
            if(stopToRemove!=null) {
                //3rd step:Update the previous stop and the next stop variable so as to link them up.
                int index = stopsOnLine.indexOf(stopToRemove);
                Stop prevStop = (index > 0) ? stopsOnLine.get(index - 1) : null;
                Stop nextStop = (index < stopsOnLine.size() - 1) ? stopsOnLine.get(index + 1) : null;

                //5th step: Remove from the list of stops on that line
                stopsOnLine.remove(stopToRemove);

                //6th step: if the station is the first stop or the last stop
                if (index == 0 && nextStop != null) {
                    System.out.println("New first stop on line: " + line.getName() + ": " + nextStop.getStation().getName());
                } else if (index == stopsOnLine.size() && prevStop != null) {
                    System.out.println("New last stop on line: " + line.getName() + ": " + prevStop.getStation().getName());
                }
            }
        }
        //removing the station from the ModelData as no line stops at that station or stop anymore.
        boolean stationInUse = model.lines.stream()
                .anyMatch(line -> line.getStops().stream().anyMatch(stop->stop.getStation().equals(station)));

        if(!stationInUse)
        {
            model.stations.remove(station);
            System.out.println("Station " + station.getName() + " has been removed from the model.");
        }
    }

    public static void createReplacementService(ModelData model, List<Station> stations, List<Line> lines) {

        if (stations.isEmpty()) {
            log.warn("Station list is empty, cannot proceed with creating replacement service.");
            return;
        }

        for(Station station: stations)
        {
            log.info(station.getName());
        }

        for(Line line: lines)
        {
            if(line.getStops().getFirst().getStation().equals(stations.getFirst())&& line.getStops().getLast().getStation().equals(stations.getLast()))
            {
                return;
            }

            List<Station> stationsInLine = line.getStops().stream()
                    .map(Stop::getStation)
                    .collect(Collectors.toList());

            List<Station> reversedStationsInLine = new ArrayList<>(stationsInLine);
            Collections.reverse(reversedStationsInLine);

            log.info("HERE: " + Collections.indexOfSubList(stationsInLine, stations));
            log.info("HERE: " + Collections.indexOfSubList(reversedStationsInLine, stations));
            if(Collections.indexOfSubList(stationsInLine, stations) == -1 && Collections.indexOfSubList(reversedStationsInLine, stations) == -1)
            {
                return;
            }
        }

        String replacementLineName;
        int replacementLineCounter = model.lines.stream()
                .filter(line -> line.getName().matches("P-\\d+")) // Matches lines with format "P-<number>"
                .map(line -> Integer.parseInt(line.getName().split("-")[1])) // Extract the numerical part
                .max(Integer::compareTo) // Find the max value
                .orElse(0); // Default to 0 if no matches

        //1st step is to check whether the lines list has only 1 line or more than 1 line in order to set up the name
        if(lines.size()==1)
        {
            replacementLineName = "P" + lines.get(0).getName(); //access the first element of the list
        }else{
            replacementLineName = "P-" + (replacementLineCounter + 1);
        }

        //2nd step: create the new line object.
        Line replacementLine = new Line(maxLineId(model)+1,replacementLineName,"#009EE3", false, new ArrayList<>());

        //3rd step: Add it to the model now and later on add the stops
        model.lines.add(replacementLine);

        //4th step: Add the user selected station to the replacementLine.
        for(Station station: stations)
        {
            Stop newStop = new Stop(station, replacementLine);
            replacementLine.getStops().add(newStop);
        }

        //5th step: Applying constraints: -selected stations are terminal,
        Station primarySectionBoundaryStation = stations.get(0);
        Station secondarySectionBoundaryStation = stations.get(stations.size()-1);
        log.info("Primary station: " + primarySectionBoundaryStation.getName());
        log.info("Secondary station: " +secondarySectionBoundaryStation.getName());
        List<Line> linesToRemovePrimaryStationFrom = getLinesToRemoveFrom(model, primarySectionBoundaryStation, lines);
        List<Line> linesToRemoveSecondaryStationFrom = getLinesToRemoveFrom(model, secondarySectionBoundaryStation, lines);

        for(Station station: stations)
        {
            if(!(station == primarySectionBoundaryStation || station == secondarySectionBoundaryStation))
            {
                closeStation(model, station, lines);
            }
        }


        log.info("Before close station.");
        closeStation(model, primarySectionBoundaryStation, linesToRemovePrimaryStationFrom);
        closeStation(model, secondarySectionBoundaryStation, linesToRemoveSecondaryStationFrom);


        //splitting the line into 2
        List<Line> linesToSplit = new ArrayList<>();
        for(Line line: lines) {
            if (line.getStops().isEmpty()) {
                continue;
            }
            Station start = line.getStops().get(0).getStation();
            Station end = line.getStops().get(line.getStops().size() - 1).getStation();

            if (!start.equals(primarySectionBoundaryStation) && !end.equals(secondarySectionBoundaryStation)) {
                linesToSplit.add(line);
            }
        }

            for(Line line: linesToSplit) {
                Line part1 = new Line(maxLineId(model)+1, line.getName() + "-1", line.getColor(), false, new ArrayList<>());
                Line part2 = new Line(maxLineId(model)+2, line.getName() + "-2", line.getColor(), false, new ArrayList<>());

                for (Stop stop : line.getStops()) {
                    part1.getStops().add(new Stop(stop.getStation(), part1));
                    if (stop.getStation().equals(primarySectionBoundaryStation))
                        break;
                }

                boolean addToPart2 = false;
                for (Stop stop : line.getStops()) {
                    if (stop.getStation().equals(secondarySectionBoundaryStation))
                        addToPart2 = true;
                    if (addToPart2)
                        part2.getStops().add(new Stop(stop.getStation(), part2));
                }

                model.lines.add(part1);
                model.lines.add(part2);
                model.lines.remove(line);
            }

        }

    public static List<Line> getLinesToRemoveFrom(ModelData model, Station sectionTerminalStation, List<Line> lines)
    {
        List<Line> result = new ArrayList<>();

        for(Line line: lines)
        {
            log.info(line.getStops().get(0).getStation().getName());
            log.info(line.getStops().get(line.getStops().size()-1).getStation().getName());
            if(sectionTerminalStation == line.getStops().get(0).getStation() ||
                    sectionTerminalStation == line.getStops().get(line.getStops().size()-1).getStation())
            {
                log.info(line.getName());
                result.add(line);
            }
        }
        return result;
    }

    public static void createAlternativeService(ModelData model, Station stationA, Station stationB) {
    }

    public static int maxLineId(ModelData model)
    {
        List<Line> lines = model.lines;
        int maxId = 0;

        for(Line line: lines)
        {
            if(line.getId()>maxId)
            {
                maxId = line.getId();
            }
        }

        return maxId;
    }
}
