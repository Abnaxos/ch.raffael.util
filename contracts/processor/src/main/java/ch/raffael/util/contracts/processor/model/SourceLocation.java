package ch.raffael.util.contracts.processor.model;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class SourceLocation {

    private String className = null;
    private String methodName = null;
    private String fileName = null;
    private int lineNumber = -1;

    private SourceLocation next = null;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

}
