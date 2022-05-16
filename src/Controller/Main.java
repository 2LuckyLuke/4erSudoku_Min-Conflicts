package Controller;

import Model.Position;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws IOException {
        List<Position> playingField = new ArrayList<>();
        Random random = new Random();
        File logFile = new File("Sudoku_min-conflicts.txt");
        if(logFile.createNewFile()){
            System.out.println("File created");
        }else {
            System.out.println("File already exists");
        }

        FileWriter logWriter = new FileWriter("Sudoku_min-conflicts.txt");

        //filling the playingField (in a disgusting, but simple way ^^)
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(i == 0 && j == 0){
                    playingField.add(new Position(i, j, 4, false));
                }else if (i == 2 && j == 1){
                    playingField.add(new Position(i, j, 3, false));
                } else if (i == 2 && j == 3) {
                    playingField.add(new Position(i, j, 1, false));
                } else if (i == 3 && j == 3) {
                    playingField.add(new Position(i, j, 3, false));
                }else {
                    playingField.add(new Position(i, j, 0, true));
                }
            }
        }
        System.out.println("Filling the playingField:\n" + fieldToString(playingField) + "\n");

        //assigning random numbers for the field
        for(Position p : playingField){
            if(p.isChangeable()){
                p.setValue(random.nextInt(4)+1);
            }
        }
        System.out.println("Assigning random numbers to the empty fields:\n" + fieldToString(playingField) + "\n");

        int count = 0;
        int randomInt;
        int lastChanged = 19;
        while (!isSolved(playingField)){
            randomInt = random.nextInt(16);
            if (playingField.get(randomInt).isChangeable() &&
                    calcConflicts(playingField, playingField.get(randomInt)) != 0 &&
                    randomInt != lastChanged){
                System.out.println("Changing random field with conflicts (" + playingField.get(randomInt).toString() + "):\n" + fieldToString(playingField) + "\n");
                playingField.get(randomInt).setValue(calcMinConflicts(playingField, playingField.get(randomInt)));
                lastChanged = randomInt;
                count++;
            }
        }
        System.out.println("Sudoku solved, final solution:\n" + fieldToString(playingField) + "\nTook " + count + " changes.");
        logWriter.write("Sudoku solved, final solution:\n" + fieldToString(playingField) + "\nTook " + count + " changes.");
        logWriter.close();

    }

    /**
     * Formats the playingField into a 4x4 Matrix.
     *
     * @param playingField the playingField; a List of 16 Positions
     * @return the string
     */
    public static String fieldToString(List<Position> playingField){
        String result;
        result ="[" + playingField.get(0).getValue() + "|" + playingField.get(4).getValue() + "|" + playingField.get(8).getValue() + "|" + playingField.get(12).getValue() + "]" + "\n"+
                "[" + playingField.get(1).getValue() + "|" + playingField.get(5).getValue() + "|" + playingField.get(9).getValue() + "|" + playingField.get(13).getValue() + "]" + "\n"+
                "[" + playingField.get(2).getValue() + "|" + playingField.get(6).getValue() + "|" + playingField.get(10).getValue() + "|" + playingField.get(14).getValue() + "]" + "\n"+
                "[" + playingField.get(3).getValue() + "|" + playingField.get(7).getValue() + "|" + playingField.get(11).getValue() + "|" + playingField.get(15).getValue() + "]";
        return result; //this is very bad, no doubt. But it works.
    }

    /**
     * Checks if the playingField/Sudoku is solved.
     *
     * @param playingField the playingField; a List of 16 Positions
     * @return true if the Sudoku is solved.
     */
    public static boolean isSolved(List<Position> playingField){
        for(Position p : playingField){
            if (calcConflicts(playingField, p) > 0){
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates the amount of Conflicts for a specific Position in playingField.
     *
     * @param playingField the playingField; a List of 16 Positions
     * @param toCheck      the position, to calculate the conflicts for
     * @return the amount of conflicts for that position as int
     */
    public static int calcConflicts(List<Position> playingField, Position toCheck){
        int conflicts = 0;

        //conflict if same number is in the same row/column
        for(Position p : playingField){
            if ((p.getX() == toCheck.getX()) ^ (p.getY() == toCheck.getY())){
                if(p.getValue() == toCheck.getValue()) conflicts++;
            }
        }

        //conflict if same number is in the same quadrant
        for(Position p : getPositionsFromQuadrant(getQuadrant(toCheck), playingField)){
            if (!(p.getX() == toCheck.getX() && p.getY() == toCheck.getY())){
                if(p.getValue() == toCheck.getValue()) conflicts++;
            }
        }

        //System.out.println("Conflicts @[" + toCheck.getX() + "|" + toCheck.getY() + "] for value " + toCheck.getValue() + ": " + conflicts);
        return conflicts;
    }

    /**
     * Calculates the Value with the least amount of Conflicts for a specific Position in playingField.
     *
     * @param playingField the playingField; a List of 16 Positions
     * @param toCheck      the position, to calculate the value with the least conflicts for
     * @return the value with the least conflicts as int
     */
    public static int calcMinConflicts(List<Position> playingField, Position toCheck){
        int value = 0;
        int minConflicts = 9;
        int tempConflicts;
        for(int i = 1; i < 5; i++){
            if(toCheck.getValue() != i){
                tempConflicts = calcConflicts(playingField, new Position(toCheck.getX(), toCheck.getY(), i, true));
                if (tempConflicts < minConflicts){
                    minConflicts = tempConflicts;
                    value = i;
                }
            }
        }
        return value;
    }

    /**
     * Get the quadrant of the playingField a Position is in.
     * The Quadrants are as follows:
     *  [2|1]
     *  [3|4]
     *
     * @param position the position, to get the quadrant of
     * @return the quadrant as int
     */
    public static int getQuadrant(Position position){
        if(position.getX() >= 2 && position.getY() < 2){
            return 1;
        }
        if (position.getX() < 2 && position.getY() < 2) {
            return 2;
        }
        if (position.getX() < 2 && position.getY() >= 2) {
            return 3;
        }
        if (position.getX() >= 2 && position.getY() >= 2){
            return 4;
        }
        return 999;
    }

    /**
     * Get all positions from a specified quadrant.
     *
     * @param quadrant     the quadrant, to get all Positions from
     * @param playingField the playingField with all 16 Positions
     * @return a List<Positions> with all Positions form the specified quadrant
     */
    public static List<Position> getPositionsFromQuadrant(int quadrant, List<Position> playingField){
        List<Position> quadrantList = new ArrayList<>();
        switch (quadrant) {
            case 1 -> {
                quadrantList.add(playingField.get(8));
                quadrantList.add(playingField.get(12));
                quadrantList.add(playingField.get(9));
                quadrantList.add(playingField.get(13));
            }
            case 2 -> {
                quadrantList.add(playingField.get(0));
                quadrantList.add(playingField.get(4));
                quadrantList.add(playingField.get(1));
                quadrantList.add(playingField.get(5));
            }
            case 3 -> {
                quadrantList.add(playingField.get(2));
                quadrantList.add(playingField.get(6));
                quadrantList.add(playingField.get(3));
                quadrantList.add(playingField.get(7));
            }
            case 4 -> {
                quadrantList.add(playingField.get(10));
                quadrantList.add(playingField.get(14));
                quadrantList.add(playingField.get(11));
                quadrantList.add(playingField.get(15));
            }
        }
        return quadrantList;
    }

}
