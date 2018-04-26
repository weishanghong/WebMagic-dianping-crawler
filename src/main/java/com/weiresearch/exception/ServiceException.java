package com.weiresearch.exception;

public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    // file storage
    public static final String EMPTY_FILE = "Failed to load or store empty file";
    public static final String USE_RELATIVE_PATH = "Could not use relative path";
    public static final String STORE_ERROR = "Failed to store file";
    public static final String LOAD_STORED_FILE_ERROR = "Failed to load stored file";
    public static final String INITIALIZE_ERROR = "Could not initialize storage path";
    public static final String FILE_NOT_FOUND = "Could not find file";

    // api
    public static final String INVALID_PARAMETER = "Invalid parameter";
    public static final String INVALID_ACCOUNT = "Invalid account info";
    public static final String LOGIN_EXPIRED = "Login expired";
    public static final String NO_VALID_DATA_FOUND = "No valid data found";
    public static final String INTERNAL_ERROR = "Internal error";
    public static final String INVALID_TYPE_OR_PARENT = "Invalid type or parent";
    public static final String NO_PERMISSION = "No permission";

    // vote
    public static final String DUPLICATE_VOTE = "Duplicate vote";
    public static final String MAX_ALLOWED_VOTE_NUMBER = "Max allowed vote number";


    public ServiceException(String label) {
        super(label);
    }

    public ServiceException(String label, Throwable cause) {
        super(label, cause);
    }

    public ServiceException(String label, String message) {
        super(label + ": " + message);
    }

    public ServiceException(String label, String message, Throwable cause) {
        super(label + ": " + message, cause);
    }
}
