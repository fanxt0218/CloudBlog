package com.cloudblog.common.exception;

public class CloudBlogException extends RuntimeException {

    private String errMessage;

    private CommonError errType;

    public CloudBlogException() {}

    public CloudBlogException(String message) {
        super(message);
    }

    public CloudBlogException(String message, CommonError commonError) {
        super(message);
        this.errMessage = commonError.getErrMessage();
        this.errType = commonError;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public String getSuperErrMessage() {
        return super.getMessage();
    }

    public CommonError getErrType() {
        return errType;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public void setErrType(CommonError errType) {
        this.errType = errType;
    }

    public static void cast(String message) {
        throw new CloudBlogException(message);
    }

    public static void cast(CommonError commonError) {
        throw new CloudBlogException(commonError.getErrMessage(), commonError);
    }

    public static void cast(String message, CommonError commonError) {
        throw new CloudBlogException(message, commonError);
    }
}
