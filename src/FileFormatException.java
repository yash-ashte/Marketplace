/**
 *
 *FileFormatException
 *
 *Thrown when a file is not properly formatted and can't be read
 *
 *@author Yash Ashtekar
 *@version 11/05/23
 */
public class FileFormatException extends Exception {

    public FileFormatException() {
        super();
    }

    public FileFormatException(String message) {
        super(message);
    }

}