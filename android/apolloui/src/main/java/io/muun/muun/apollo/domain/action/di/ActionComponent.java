package io.meen.apollo.domain.action.di;

import io.meen.apollo.domain.FeatureOverrideStore;
import io.meen.apollo.domain.LoggingContextManager;
import io.meen.apollo.domain.action.ContactActions;
import io.meen.apollo.domain.action.CurrencyActions;
import io.meen.apollo.domain.action.NotificationActions;
import io.meen.apollo.domain.action.NotificationPoller;
import io.meen.apollo.domain.action.OperationActions;
import io.meen.apollo.domain.action.SigninActions;
import io.meen.apollo.domain.action.UserActions;
import io.meen.apollo.domain.action.base.AsyncActionStore;
import io.meen.apollo.domain.action.challenge_keys.password_change.FinishPasswordChangeAction;
import io.meen.apollo.domain.action.challenge_keys.password_change.StartPasswordChangeAction;
import io.meen.apollo.domain.action.challenge_keys.password_setup.SetUpPasswordAction;
import io.meen.apollo.domain.action.challenge_keys.password_setup.StartEmailSetupAction;
import io.meen.apollo.domain.action.challenge_keys.recovery_code_setup.StartRecoveryCodeSetupAction;
import io.meen.apollo.domain.action.ek.AddEmergencyKitMetadataAction;
import io.meen.apollo.domain.action.ek.GenerateEmergencyKitPDF;
import io.meen.apollo.domain.action.ek.RenderEmergencyKitAction;
import io.meen.apollo.domain.action.ek.ReportEmergencyKitExportedAction;
import io.meen.apollo.domain.action.ek.UploadToDriveAction;
import io.meen.apollo.domain.action.ek.VerifyEmergencyKitAction;
import io.meen.apollo.domain.action.fcm.ForceFetchFcmAction;
import io.meen.apollo.domain.action.fcm.GetFcmTokenAction;
import io.meen.apollo.domain.action.incoming_swap.GenerateInvoiceAction;
import io.meen.apollo.domain.action.incoming_swap.RegisterInvoicesAction;
import io.meen.apollo.domain.action.integrity.IntegrityAction;
import io.meen.apollo.domain.action.operation.ResolveLnInvoiceAction;
import io.meen.apollo.domain.action.operation.ResolveOperationUriAction;
import io.meen.apollo.domain.action.operation.SubmitPaymentAction;
import io.meen.apollo.domain.action.permission.UpdateContactsPermissionStateAction;
import io.meen.apollo.domain.action.realtime.FetchRealTimeDataAction;
import io.meen.apollo.domain.action.realtime.PreloadFeeDataAction;
import io.meen.apollo.domain.action.session.CreateLoginSessionAction;
import io.meen.apollo.domain.action.session.LogInAction;
import io.meen.apollo.domain.action.session.SyncApplicationDataAction;
import io.meen.apollo.domain.action.session.UseMeenLinkAction;
import io.meen.apollo.domain.action.session.rc_only.LogInWithRcAction;
import io.meen.apollo.domain.action.user.DeleteWalletAction;
import io.meen.apollo.domain.action.user.EmailLinkAction;
import io.meen.apollo.domain.action.user.UpdateProfilePictureAction;
import io.meen.apollo.domain.debug.DebugExecutable;

import dagger.Component;

/**
 * Dagger Component. {@link Component}.
 * Add here:
 * - members-injection methods (e.g for classes which lifecycles are 3rd-party controlled, like
 * Android's). Example: void inject(GcmMessageListenerService service).
 * - provision methods, to expose injected or provided dependencies to other (dependent) components.
 * Example: UpdateProfilePictureAction updateProfilePictureAction();
 */
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public interface ActionComponent {

    // Exposed to dependent components

    // Action bags:

    SigninActions signinActions();

    LoggingContextManager loggingContextManager();

    ContactActions contactActions();

    OperationActions operationActions();

    UserActions userActions();

    CurrencyActions currencyActions();

    NotificationActions notificationActions();

    AsyncActionStore asyncActionStore();

    IntegrityAction integrityAction();

    // Own-class actions:

    NotificationPoller notificationPoller();

    UpdateProfilePictureAction updateProfilePictureAction();

    FetchRealTimeDataAction fetchRealTimeDataAction();

    PreloadFeeDataAction fetchRealTimeFeesAction();

    ResolveOperationUriAction resolveOperationUriAction();

    ResolveLnInvoiceAction resolveLnInvoiceAction();

    SubmitPaymentAction submitOutgoingPaymentAction();

    LogInAction logInAction();

    SyncApplicationDataAction syncApplicationDataAction();

    GetFcmTokenAction getFcmTokenAction();

    ForceFetchFcmAction forceFetchFcmTokenAction();

    LogInWithRcAction logInWithRcAction();

    StartEmailSetupAction startEmailSetupAction();

    SetUpPasswordAction setupPasswordAction();

    StartPasswordChangeAction startPasswordChangeAction();

    FinishPasswordChangeAction finishPasswordChangeAction();

    CreateLoginSessionAction createLoginSessionAction();

    ReportEmergencyKitExportedAction reportKeysExportedAction();

    RenderEmergencyKitAction renderEmergencyKitAction();

    GenerateEmergencyKitPDF generateEmergencyKitPdf();

    VerifyEmergencyKitAction verifyEmergencyKitAction();

    UseMeenLinkAction useMeenLinkAction();

    EmailLinkAction emailLinkAction();

    UploadToDriveAction uploadEmergencyKitAction();

    RegisterInvoicesAction registerInvoicesAction();

    GenerateInvoiceAction generateInvoiceAction();

    AddEmergencyKitMetadataAction addEmergencyKitMetadata();

    StartRecoveryCodeSetupAction startRecoveryCodeSetupAction();

    UpdateContactsPermissionStateAction updateContactsPermissionStateAction();

    DeleteWalletAction deleteWalletAction();

    DebugExecutable debugExecutable();

    FeatureOverrideStore featureOverrideStore();
}
