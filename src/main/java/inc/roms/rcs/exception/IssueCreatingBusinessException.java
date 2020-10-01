package inc.roms.rcs.exception;

public abstract class IssueCreatingBusinessException extends BusinessException implements ConvertibleToIssue {

    public IssueCreatingBusinessException(Throwable root) {
        super(root);
    }

    public IssueCreatingBusinessException(String message) {
        super(message);
    }
}
