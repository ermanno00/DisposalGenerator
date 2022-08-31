package disposalGenerator.disposal;

public interface DisposalGeneratorCallback {
    void onMongoConnectionStatusChange(boolean connected);
    void onArtemisConnectionStatusChange(boolean connected);
    void onError(String error);
    void onMessage(String message);



}
