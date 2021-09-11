package encryptdecrypt;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/*
 When starting the program, the necessary algorithm should be
  specified by an argument (-alg). The first algorithm should
  be named shift, the second one should be named unicode.
 If there is no -alg you should default it to shift
 If there is no -mode, the program should work in enc mode.
 If there is no -key, the program should consider that key = 0.
 If there is no -data, and there is no -in the program should
  assume that the data is an empty string.
 If there is no -out argument, the program must print data to
  the standard output.
 If there are both -data and -in arguments, your program should
  prefer -data over -in.
*/

public class Main {
    private String inputData;
    private String outputData;

    private String destinationPath = "";

    private ActionMode actionMode = ActionMode.ENC;
    private Algorithm algorithm = Algorithm.SHIFT;
    private int key;

    private void verifyArguments(String[] args){
        String data = "";
        String sourcePath = "";
        for (int i = 0;i < args.length; i += 2) {
            switch (args[i]) {
                case "-alg":
                    this.algorithm = Algorithm.valueOf(args[i + 1].toUpperCase());
                    break;
                case "-mode":
                    this.actionMode = ActionMode.valueOf(args[i + 1].toUpperCase());
                    break;

                case "-key":
                    this.key = Integer.parseInt(args[i + 1]);
                    break;

                case "-data":
                    data = args[i + 1];
                    break;

                case "-in":
                    sourcePath = args[i + 1];
                    break;

                case "-out":
                    this.destinationPath = args[i + 1];
                    break;
            }
        }

        // is source data from a file?
        if (data.isEmpty() && !sourcePath.isBlank()) {
            try {
                this.inputData = Files.readString(Path.of(sourcePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.inputData = data;
        }
    }

    private void applyRequest() {
        DataSecurityFactory dataSecurityFactory = new DataSecurityFactory();
        this.outputData = dataSecurityFactory.getRequestData(algorithm,
                                                actionMode, inputData, key);
    }

    private void writeOutput() {
        if (destinationPath.isEmpty()) {
            System.out.println(outputData);
        } else {
            try {
                Files.writeString(Path.of(destinationPath), outputData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Main mainApp = new Main();

        mainApp.verifyArguments(args);
        mainApp.applyRequest();
        mainApp.writeOutput();
    }
}

enum ActionMode { ENC, DEC;}
enum Algorithm {SHIFT, UNICODE}

class DataSecurityFactory {
    public String getRequestData(Algorithm algorithm, ActionMode actionMode,
                                 String data, int key) {

        DataSecurity dataSecurity;
        switch (algorithm) {
            case SHIFT:
                dataSecurity = new ShiftAlgorithm(data, key, actionMode);
                break;
            case UNICODE:
                dataSecurity = new UnicodeAlgorithm(data, key, actionMode);
                break;
            default:
                dataSecurity = null;
                break;
        }
        if (dataSecurity != null) {
            return dataSecurity.convert(data, key);
        } else {
            return "Impossible to handle the security request!";
        }
    }
}

abstract class DataSecurity {
    protected ActionMode actionMode;
    public abstract String convert(String data, int key);

    protected DataSecurity(ActionMode actionMode) {
        this.actionMode = actionMode;
    }
    protected ActionMode getActionMode() {
        return actionMode;
    }
}

class ShiftAlgorithm extends DataSecurity {
    ShiftAlgorithm(String data, int key, ActionMode actionMode) {
        super(actionMode);
    }

    public String convert(String data, int key) {
        int toCharIndex;
        if (getActionMode() == ActionMode.ENC) {
            toCharIndex = key % 26;
        } else {
            toCharIndex = 26 - (key % 26);
        }

        char[] chars = data.toCharArray();
        StringBuilder sb = new StringBuilder();

        for (char ch : chars) {
            sb.append(convertChar(ch, toCharIndex));
        }

        return sb.toString();
    }
    private char getCharZ(boolean isUpperCase) {
        return isUpperCase ? 'Z' : 'z';
    }

    private char getCharA(boolean upperCase) {
        return upperCase ? 'A' : 'a';
    }

    private char getAlteredChar (char ch, int key) {
        // Determine 'a' or 'A' position
        char charA = getCharA(Character.isUpperCase(ch));

        int oldCharPosition = ch - charA;
        int newCharPosition = (oldCharPosition + key) % 26;
        char newChar = (char) (charA + newCharPosition);

        return newChar;
    }

    private char convertChar(char ch, int key) {
        if (Character.isLetter(ch)) {
          return getAlteredChar(ch, key);
        }
        return ch;
    }
}

class  UnicodeAlgorithm extends  DataSecurity {
    UnicodeAlgorithm(String data, int key, ActionMode actionMode) {
        super(actionMode);
    }

    private char convertChar(char ch, int key) {
        return getActionMode() == ActionMode.ENC ? (char) (ch + key) : (char) (ch - key);
    }

    @Override
    public String convert(String data, int key) {
        char[] chars = data.toCharArray();
        StringBuilder sb = new StringBuilder();

        for (char ch : chars) {
            sb.append(convertChar(ch, key));
        }
        return sb.toString();
    }
}