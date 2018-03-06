package MotionProfile;

import java.io.*;
import java.util.*;
import java.lang.*;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

import org.json.simple.*;
import org.json.simple.parser.*;

public class Generate {
    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
        if(args.length !=1)
                throw new IllegalArgumentException("Path to the json file should be the only argument");

        //Parse json file
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(args[0]));
        JSONObject jsonObj = (JSONObject) obj;

        //Parse motion profile config file
        Waypoint[] waypoints = parseWaypoints((JSONArray)jsonObj.get("waypoints"));
        Trajectory.Config trajectoryConfig = parseTrajectoryConfig((JSONObject)jsonObj.get("config"));
        double wheelbaseWidth = ((Number)jsonObj.get("wheelbase-width")).doubleValue();

        //Generate motion profile
        Trajectory trajectory = Pathfinder.generate(waypoints, trajectoryConfig);
        TankModifier modifier = new TankModifier(trajectory).modify(wheelbaseWidth);
        
        //Generate motion profile for left and right sides of the robot
        Trajectory left = modifier.getLeftTrajectory();
        Trajectory right = modifier.getRightTrajectory();

        
        //Write .csv files for analyzing with R/Excel
        writeCsv(jsonObj, "output-center-csv", trajectory);
        writeCsv(jsonObj, "output-left-csv", left);
        writeCsv(jsonObj, "output-right-csv", right);

        //Write .bin files to be sent to and deserialized by the RoboRio
        writeBin(jsonObj, "output-center-bin", trajectory);
        writeBin(jsonObj, "output-left-bin", left);
        writeBin(jsonObj, "output-right-bin", right);
    }

    private static Waypoint[] parseWaypoints(JSONArray jsonWaypoints) {
        List<Waypoint> waypoints = new ArrayList<Waypoint>();
        Iterator<JSONObject> waypointIterator = jsonWaypoints.iterator();
        while (waypointIterator.hasNext()) {
                JSONObject waypoint = waypointIterator.next();
                double x = ((Number)waypoint.get("x")).doubleValue();
                double y = ((Number)waypoint.get("y")).doubleValue();
                double angle = ((Number)waypoint.get("angle")).doubleValue();
                waypoints.add(new Waypoint(x, y, Pathfinder.d2r(angle)));
        }

        Waypoint[] waypointArray = new Waypoint[waypoints.size()];
        waypoints.toArray(waypointArray);
        return waypointArray;
    }

    private static Trajectory.Config parseTrajectoryConfig(JSONObject jsonConfig) {

        Trajectory.FitMethod fitMethod = Trajectory.FitMethod.valueOf((String)jsonConfig.get("fit-method"));
        int numSamples = Math.toIntExact((long)jsonConfig.get("numSamples"));
        double dt = ((Number)jsonConfig.get("dt")).doubleValue();
        double maxVelocity = ((Number)jsonConfig.get("max-velocity")).doubleValue();
        double maxAcceleration = ((Number)jsonConfig.get("max-acceleration")).doubleValue();
        double maxJerk = ((Number)jsonConfig.get("max-jerk")).doubleValue();

        return new Trajectory.Config(
                Trajectory.FitMethod.HERMITE_CUBIC,
                numSamples,
                dt,
                maxVelocity,
                maxAcceleration,
                maxJerk);
    }

    private static void writeCsv(JSONObject jsonObj, String config, Trajectory trajectory) {
        String csv = (String)jsonObj.get(config);
        if(csv != null)
                Pathfinder.writeToCSV(new File(csv), trajectory);
    }

    private static void writeBin(JSONObject jsonObj, String config, Trajectory trajectory) {
        String bin = (String)jsonObj.get(config);
        if(bin != null)
                Pathfinder.writeToFile(new File(bin), trajectory);
    }
}
