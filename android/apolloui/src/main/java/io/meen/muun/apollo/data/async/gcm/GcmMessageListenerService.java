package io.meen.apollo.data.async.gcm;

import io.meen.apollo.data.db.DaoManager;
import io.meen.apollo.data.external.DataComponentProvider;
import io.meen.apollo.data.net.HoustonClient;
import io.meen.apollo.data.net.ModelObjectsMapper;
import io.meen.apollo.data.os.execution.ExecutionTransformerFactory;
import io.meen.apollo.data.serialization.SerializationUtils;
import io.meen.apollo.domain.LoggingContextManager;
import io.meen.apollo.domain.action.NotificationActions;
import io.meen.apollo.domain.action.fcm.UpdateFcmTokenAction;
import io.meen.apollo.domain.errors.fcm.FcmMessageProcessingError;
import io.meen.apollo.domain.model.NotificationReport;
import io.meen.common.api.beam.notification.NotificationReportJson;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import timber.log.Timber;

import javax.inject.Inject;

public class GcmMessageListenerService extends FirebaseMessagingService {

    @Inject
    LoggingContextManager loggingContextManager;

    @Inject
    DaoManager daoManager; // not used directly by us, but needed in case we start the Application

    @Inject
    HoustonClient houstonClient;

    @Inject
    UpdateFcmTokenAction updateFcmTokenAction;

    @Inject
    ExecutionTransformerFactory executionTransformerFactory;

    @Inject
    ModelObjectsMapper mapper;

    @Inject
    NotificationActions notificationActions;

    @Override
    public void onCreate() {
        super.onCreate();

        final DataComponentProvider provider = (DataComponentProvider) getApplication();
        provider.getDataComponent().inject(this);

        Timber.d("Starting GcmMessageListenerService");

        loggingContextManager.setupCrashlytics();
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Timber.d("Received a new GCM token");

        updateFcmTokenAction.run(token);
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage wrapper for From and Data
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        final String message = remoteMessage.getData().get("message");

        if (message == null) {
            return;
        }

        try {
            processMessage(message);

        } catch (Throwable error) {
            Timber.e(new FcmMessageProcessingError(remoteMessage, error));
        }
    }

    private void processMessage(String message) {
        final NotificationReport report;

        try {
            report = mapper.mapNotificationReport(
                    SerializationUtils.deserializeJson(NotificationReportJson.class, message)
            );

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid NotificationReportJson: " + message, e);
        }

        notificationActions.onNotificationReport(report);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("Destroying GcmMessageListenerService");
    }
}
