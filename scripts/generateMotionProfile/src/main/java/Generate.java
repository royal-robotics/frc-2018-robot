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
        File file = new File(args[0]);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(file));
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

        
        String fileName = file.getName();
        int pos = fileName.lastIndexOf(".");
        if (pos > 0 && pos < (fileName.length() - 1)) { // If '.' is not the first or last character.
            fileName = fileName.substring(0, pos);
        }

        JSONObject outputs = (JSONObject)jsonObj.get("outputs");
        JSONObject csvOutputs = (JSONObject)outputs.get("csv");
        JSONObject binOutputs = (JSONObject)outputs.get("bin");
        
        File genDirectory = new File("motionProfiles/generated");
        if(!genDirectory.exists())
            genDirectory.mkdir();

        //Write .csv files for analyzing with R/Excel
        writeCsv(csvOutputs, fileName, "center", trajectory);
        writeCsv(csvOutputs, fileName, "left", left);
        writeCsv(csvOutputs, fileName, "right", right);

        //Write .bin files to be sent to and deserialized by the RoboRio
        writeBin(binOutputs, fileName, "center", trajectory);
        writeBin(binOutputs, fileName, "left", left);
        writeBin(binOutputs, fileName, "right", right);
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

    private static void writeCsv(JSONObject outputs, String profileName, String profileType, Trajectory trajectory) {
        String basePath = "motionProfiles/generated/";
        String fileName = basePath + profileName + "-" + profileType + ".csv";

        if(outputs.get(profileType) != null && (boolean)outputs.get(profileType))
            Pathfinder.writeToCSV(new File(fileName), trajectory);
    }

    private static void writeBin(JSONObject outputs, String profileName, String profileType, Trajectory trajectory) {
        String basePath = "motionProfiles/generated/";
        String fileName = basePath + profileName + "-" + profileType + ".bin";

        if(outputs.get(profileType) != null && (boolean)outputs.get(profileType))
            Pathfinder.writeToFile(new File(fileName), trajectory);
    }
}
