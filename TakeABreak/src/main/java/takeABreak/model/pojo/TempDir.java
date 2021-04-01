package takeABreak.model.pojo;

import java.io.File;

public class TempDir {

    private static String tempLocation;

    private TempDir(){
    }
    public static String getLocation() {
        if(tempLocation == null) {
            String currentDir = System.getProperty("user.dir");
            String newDir = File.separator + "temp";
            String fullLocation = currentDir + newDir;
            File tempDir = new File(fullLocation);
            if(!tempDir.exists()){
                tempDir.mkdir();
            }
            tempLocation = fullLocation;
            return tempLocation;
        }

        return tempLocation;
    }
}
