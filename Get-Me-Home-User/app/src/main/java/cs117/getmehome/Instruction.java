package cs117.getmehome;

/**
 * Created by ty on 3/12/2017.
 */

public class Instruction {
    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;
    private String instruction;

    public static class Coordinate{
        double lat;
        double lng;
    }

    public Instruction(double startLat, double startLng, double endLat, double endLng, String instruction){
        this.startLat = startLat;
        this.startLng = startLng;
        this.endLat = endLat;
        this.endLng = endLng;
        this.instruction = instruction;
    }

    public Coordinate getStart(){
        Coordinate start = new Coordinate();
        start.lat = startLat;
        start.lng = startLng;
        return start;
    }

    public Coordinate getEnd(){
        Coordinate end = new Coordinate();
        end.lat = endLat;
        end.lng = endLng;
        return end;
    }

    public String getInstruction(){
        return instruction;
    }
}
