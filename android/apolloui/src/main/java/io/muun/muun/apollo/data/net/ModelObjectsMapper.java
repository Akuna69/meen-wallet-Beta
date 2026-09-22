package io.meen.apollo.data.net;

import io.meen.apollo.data.serialization.dates.ApolloZonedDateTime;
import io.meen.apollo.domain.errors.p2p.InvalidPhoneNumberError;
import io.meen.apollo.domain.model.BitcoinAmount;
import io.meen.apollo.domain.model.ChallengeKeyUpdateMigration;
import io.meen.apollo.domain.model.Contact;
import io.meen.apollo.domain.model.CreateFirstSessionOk;
import io.meen.apollo.domain.model.CreateSessionOk;
import io.meen.apollo.domain.model.CreateSessionRcOk;
import io.meen.apollo.domain.model.EmergencyKitExport;
import io.meen.apollo.domain.model.ExchangeRateWindow;
import io.meen.apollo.domain.model.FeeWindow;
import io.meen.apollo.domain.model.ForwardingPolicy;
import io.meen.apollo.domain.model.FulfillmentPushedResult;
import io.meen.apollo.domain.model.IncomingSwap;
import io.meen.apollo.domain.model.IncomingSwapHtlc;
import io.meen.apollo.domain.model.MeenFeature;
import io.meen.apollo.domain.model.NextTransactionSize;
import io.meen.apollo.domain.model.NotificationReport;
import io.meen.apollo.domain.model.OperationCreated;
import io.meen.apollo.domain.model.OperationWithMetadata;
import io.meen.apollo.domain.model.PendingChallengeUpdate;
import io.meen.apollo.domain.model.PublicKeySet;
import io.meen.apollo.domain.model.PublicProfile;
import io.meen.apollo.domain.model.RealTimeData;
import io.meen.apollo.domain.model.RealTimeFees;
import io.meen.apollo.domain.model.SubmarineSwap;
import io.meen.apollo.domain.model.TransactionPushed;
import io.meen.apollo.domain.model.feebump.FeeBumpFunctions;
import io.meen.apollo.domain.model.tx.PartiallySignedTransaction;
import io.meen.apollo.domain.model.user.EmergencyKit;
import io.meen.apollo.domain.model.user.User;
import io.meen.apollo.domain.model.user.UserPhoneNumber;
import io.meen.apollo.domain.model.user.UserPreferences;
import io.meen.apollo.domain.model.user.UserProfile;
import io.meen.common.Optional;
import io.meen.common.Rules;
import io.meen.common.api.BitcoinAmountJson;
import io.meen.common.api.ChallengeKeyUpdateMigrationJson;
import io.meen.common.api.CommonModelObjectsMapper;
import io.meen.common.api.CreateFirstSessionOkJson;
import io.meen.common.api.CreateSessionOkJson;
import io.meen.common.api.CreateSessionRcOkJson;
import io.meen.common.api.ExportEmergencyKitJson;
import io.meen.common.api.FeeBumpFunctionsJson;
import io.meen.common.api.FeeWindowJson;
import io.meen.common.api.ForwardingPolicyJson;
import io.meen.common.api.FulfillmentPushedJson;
import io.meen.common.api.IncomingSwapHtlcJson;
import io.meen.common.api.IncomingSwapJson;
import io.meen.common.api.MeenFeatureJson;
import io.meen.common.api.NextTransactionSizeJson;
import io.meen.common.api.OperationCreatedJson;
import io.meen.common.api.OperationJson;
import io.meen.common.api.PartiallySignedTransactionJson;
import io.meen.common.api.PendingChallengeUpdateJson;
import io.meen.common.api.PhoneNumberJson;
import io.meen.common.api.PublicKeySetJson;
import io.meen.common.api.PublicProfileJson;
import io.meen.common.api.RealTimeFeesJson;
import io.meen.common.api.SizeForAmountJson;
import io.meen.common.api.SubmarineSwapJson;
import io.meen.common.api.TransactionPushedJson;
import io.meen.common.api.UserJson;
import io.meen.common.api.beam.notification.NotificationReportJson;
import io.meen.common.crypto.hd.MeenAddress;
import io.meen.common.crypto.hd.PublicKeyTriple;
import io.meen.common.dates.MeenZonedDateTime;
import io.meen.common.exception.MissingCaseError;
import io.meen.common.model.SizeForAmount;
import io.meen.common.model.UtxoStatus;
import io.meen.common.utils.CollectionUtils;
import io.meen.common.utils.Encodings;
import io.meen.common.utils.Pair;
import io.meen.common.utils.Preconditions;

import org.bitcoinj.core.NetworkParameters;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;


@Singleton
public class ModelObjectsMapper extends CommonModelObjectsMapper {

    /**
     * Constructor.
     */
    @Inject
    public ModelObjectsMapper(NetworkParameters networkParameters) {
        super(networkParameters);
    }

    /**
     * Create a CreateFirstSession.
     */
    public CreateFirstSessionOk mapCreateFirstSessionOk(CreateFirstSessionOkJson json) {
        return new CreateFirstSessionOk(
                mapUser(json.user),
                Objects.requireNonNull(mapPublicKey(json.cosigningPublicKey)),
                Objects.requireNonNull(mapPublicKey(json.swapServerPublicKey)),
                json.playIntegrityNonce
        );
    }

    private UserPreferences mapUserPreferences(final io.meen.common.model.UserPreferences prefs) {
        return UserPreferences.fromJson(prefs);
    }

    /**
     * Create a nullable date time.
     */
    @Nullable
    private ZonedDateTime mapZonedDateTime(@Nullable MeenZonedDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        return mapNonNullableZonedDateTime(dateTime);
    }

    /**
     * Create a date time.
     */
    @NotNull
    private ZonedDateTime mapNonNullableZonedDateTime(@NotNull MeenZonedDateTime dateTime) {
        return ((ApolloZonedDateTime) dateTime).dateTime;
    }

    /**
     * Create a user.
     */
    @NotNull
    public User mapUser(@NotNull UserJson apiUser) {

        final Optional<UserProfile> maybeProfile = Optional.ofNullable(apiUser.publicProfile)
                .map(this::mapUserProfile);

        final Optional<UserPhoneNumber> maybePhoneNumber = Optional.ofNullable(apiUser.phoneNumber)
                .map(this::mapUserPhoneNumber);

        EmergencyKit emergencyKit = null;
        if (apiUser.emergencyKitLastExportedAt != null) {
            final ExportEmergencyKitJson ek = apiUser.emergencyKit;
            Preconditions.checkNotNull(apiUser.emergencyKit);

            final Optional<EmergencyKitExport.Method> maybeMethod = Optional.ofNullable(ek.method)
                    .map(this::mapExportMethod);

            emergencyKit = new EmergencyKit(
                    Preconditions.checkNotNull(mapZonedDateTime(ek.lastExportedAt)),
                    Preconditions.checkNotNull(ek.version),
                    maybeMethod.isPresent() ? maybeMethod.get() : null
            );
        }

        return User.fromHouston(
                apiUser.id,
                Optional.ofNullable(apiUser.email),
                apiUser.isEmailVerified,
                maybePhoneNumber,
                maybeProfile,
                apiUser.primaryCurrency,
                apiUser.hasRecoveryCodeChallengeKey,
                apiUser.hasPasswordChallengeKey,
                apiUser.hasP2PEnabled,
                apiUser.hasExportedKeys,
                Optional.ofNullable(emergencyKit),
                Optional.ofNullable(apiUser.createdAt).map(this::mapZonedDateTime),
                new TreeSet<>(apiUser.exportedKitVersions)
        );
    }

    /**
     * Create a UserProfile.
     */
    public UserProfile mapUserProfile(@NotNull PublicProfileJson profile) {
        return new UserProfile(profile.firstName, profile.lastName, profile.profilePictureUrl);
    }

    /**
     * Create a UserPhoneNumber.
     */
    public UserPhoneNumber mapUserPhoneNumber(@NotNull PhoneNumberJson phoneNumber) {
        try {
            return new UserPhoneNumber(phoneNumber.number, phoneNumber.isVerified);
        } catch (IllegalArgumentException e) {
            throw new InvalidPhoneNumberError(e);
        }
    }

    /**
     * Create an EmergencyKit export method.
     */
    public EmergencyKitExport.Method mapExportMethod(@NotNull ExportEmergencyKitJson.Method meth) {
        switch (meth) {
            case MANUAL:
                return EmergencyKitExport.Method.MANUAL;

            case DRIVE:
                return EmergencyKitExport.Method.DRIVE;

            case ICLOUD:
                return EmergencyKitExport.Method.ICLOUD;

            default:
                throw new MissingCaseError(meth);
        }
    }

    /**
     * Create a contact.
     */
    @NotNull
    public Contact mapContact(@NotNull io.meen.common.api.Contact contact) {

        return new Contact(
                null,
                contact.publicProfile.userId,
                mapPublicProfile(contact.publicProfile),
                contact.maxAddressVersion,
                mapPublicKey(contact.publicKey),
                mapPublicKey(contact.cosigningPublicKey),
                contact.lastDerivationIndex
        );
    }

    /**
     * Create a public profile.
     */
    @NotNull
    private PublicProfile mapPublicProfile(@NotNull PublicProfileJson publicProfile) {

        return new PublicProfile(
                null,
                publicProfile.userId,
                publicProfile.firstName,
                publicProfile.lastName,
                publicProfile.profilePictureUrl
        );
    }

    /**
     * Create an exchange rate window.
     */
    @NotNull
    private ExchangeRateWindow mapExchangeRateWindow(
            @NotNull io.meen.common.api.ExchangeRateWindow window
    ) {

        return new ExchangeRateWindow(
                window.id,
                mapZonedDateTime(window.fetchDate),
                window.rates
        );
    }

    /**
     * Create a bitcoin amount.
     */
    @NotNull
    private BitcoinAmount mapBitcoinAmount(@NotNull BitcoinAmountJson bitcoinAmount) {

        return new BitcoinAmount(
                bitcoinAmount.inSatoshis,
                bitcoinAmount.inInputCurrency,
                bitcoinAmount.inPrimaryCurrency
        );
    }

    /**
     * Create an operation.
     */
    @NotNull
    public OperationWithMetadata mapOperation(@NotNull OperationJson operation) {

        Preconditions.checkNotNull(operation.id);

        return new OperationWithMetadata(
                operation.id,
                operation.direction,
                operation.isExternal,
                operation.senderProfile != null ? mapPublicProfile(operation.senderProfile) : null,
                operation.senderIsExternal,
                operation.receiverProfile != null
                        ? mapPublicProfile(operation.receiverProfile) : null,
                operation.receiverIsExternal,
                operation.receiverAddress,
                operation.receiverAddressDerivationPath,
                null,
                mapBitcoinAmount(operation.amount),
                mapBitcoinAmount(operation.fee),
                operation.transaction != null ? operation.transaction.confirmations : 0L,
                operation.transaction != null ? operation.transaction.hash : null,
                operation.description,
                operation.status,
                Objects.requireNonNull(mapZonedDateTime(operation.creationDate)),
                operation.exchangeRatesWindowId,
                operation.swap != null ? mapSubmarineSwap(operation.swap) : null,
                operation.receiverMetadata,
                operation.senderMetadata,
                operation.incomingSwap != null ? mapIncomingSwap(operation.incomingSwap) : null,
                // If transaction null, no biggie, isRbf will be defined & updated by Houston later
                operation.transaction != null ? operation.transaction.isReplaceableByFee : false

        );
    }

    /**
     * Create an operation swap.
     */
    @NotNull
    public SubmarineSwap mapSubmarineSwap(SubmarineSwapJson swap) {
        return SubmarineSwap.Companion.fromJson(swap);
    }

    /**
     * Create an IncomingSwap.
     */
    @NotNull
    public IncomingSwap mapIncomingSwap(@NotNull final IncomingSwapJson swap) {

        return new IncomingSwap(
                null,
                swap.uuid,
                Encodings.hexToBytes(swap.paymentHashHex),
                swap.htlc != null ? mapIncomingSwapHtlc(swap.htlc) : null,
                swap.sphinxPacketHex != null ? Encodings.hexToBytes(swap.sphinxPacketHex) : null,
                swap.collectInSats,
                swap.paymentAmountInSats,
                swap.preimageHex != null ? Encodings.hexToBytes(swap.preimageHex) : null
        );
    }

    private IncomingSwapHtlc mapIncomingSwapHtlc(final IncomingSwapHtlcJson htlc) {
        return new IncomingSwapHtlc(
                null,
                htlc.uuid,
                htlc.expirationHeight,
                htlc.fulfillmentFeeSubsidyInSats,
                htlc.lentInSats,
                Encodings.hexToBytes(htlc.swapServerPublicKeyHex),
                htlc.fulfillmentTxHex != null
                        ? Encodings.hexToBytes(htlc.fulfillmentTxHex) : null,
                htlc.address,
                htlc.outputAmountInSatoshis,
                Encodings.hexToBytes(htlc.htlcTxHex)
        );
    }

    /**
     * Create a partially signed transaction.
     */
    @NotNull
    public OperationCreated mapOperationCreated(@NotNull OperationCreatedJson operationCreated) {
        final OperationJson apiOperation = operationCreated.operation;

        Preconditions.checkNotNull(operationCreated.operation);
        Preconditions.checkNotNull(operationCreated.nextTransactionSize);

        final OperationWithMetadata operation = mapOperation(apiOperation);

        return new OperationCreated(
                operation,
                PartiallySignedTransaction.fromJson(
                        operationCreated.partiallySignedTransaction,
                        networkParameters
                ),
                mapNextTransactionSize(operationCreated.nextTransactionSize),
                MeenAddress.fromJson(operationCreated.changeAddress),
                mapAlternativeTransactions(operationCreated.alternativeTransactions)
        );
    }

    private List<PartiallySignedTransaction> mapAlternativeTransactions(
            @Nullable final List<PartiallySignedTransactionJson> txs
    ) {
        if (txs == null) {
            return List.of();
        }

        final var result = new ArrayList<PartiallySignedTransaction>();
        for (final var tx : txs) {
            result.add(PartiallySignedTransaction.fromJson(tx, networkParameters));
        }

        return result;
    }

    /**
     * Create a TransactionPushed object.
     */
    @NotNull
    public TransactionPushed mapTransactionPushed(@NotNull TransactionPushedJson txPushed) {
        Preconditions.checkNotNull(txPushed.nextTransactionSize);

        return new TransactionPushed(
                txPushed.hex,
                mapNextTransactionSize(txPushed.nextTransactionSize),
                mapOperation(txPushed.updatedOperation),
                mapFeeBumpFunctions(txPushed.feeBumpFunctions)
        );
    }

    /**
     * Create a FeeBumpFunctions object.
     */
    @NotNull
    public FeeBumpFunctions mapFeeBumpFunctions(
            @NotNull FeeBumpFunctionsJson feeBumpFunctionsJson
    ) {
        return new FeeBumpFunctions(
                feeBumpFunctionsJson.uuid,
                feeBumpFunctionsJson.functions
        );
    }

    /**
     * Map push fulfillment result data.
     */
    public FulfillmentPushedResult mapFulfillmentPushed(final FulfillmentPushedJson json) {

        return new FulfillmentPushedResult(
                mapNextTransactionSize(json.nextTransactionSize),
                mapFeeBumpFunctions(json.feeBumpFunctions)
        );
    }

    /**
     * Create an expected fee.
     */
    @NotNull
    private FeeWindow mapFeeWindow(@NotNull FeeWindowJson window) {

        // Sanitize targetedFees, just in case
        window.targetedFees.values().removeAll(Collections.singleton(null));

        return new FeeWindow(
                window.id,
                mapZonedDateTime(window.fetchDate),
                window.targetedFees,
                window.fastConfTarget,
                window.mediumConfTarget,
                window.slowConfTarget
        );
    }

    /**
     * Create a bag of real-time data provided by Houston.
     */
    @NotNull
    public RealTimeData mapRealTimeData(@NotNull io.meen.common.api.RealTimeData realTimeData) {
        return new RealTimeData(
                mapFeeWindow(realTimeData.feeWindow),
                mapExchangeRateWindow(realTimeData.exchangeRateWindow),
                realTimeData.currentBlockchainHeight,
                mapForwadingPolicies(realTimeData.forwardingPolicies),
                realTimeData.minFeeRateInWeightUnits,
                mapMeenFeatures(realTimeData.features)
        );
    }

    /**
     * Create a bag of real-time fees data provided by Houston.
     */
    @NotNull
    public RealTimeFees mapRealTimeFees(@NotNull RealTimeFeesJson realTimeFeesJson) {
        // Convert to domain model FeeWindow
        final FeeWindow feeWindow = new FeeWindow(
                1L, // It will be deleted later
                mapNonNullableZonedDateTime(realTimeFeesJson.computedAt),
                mapConfTargetToTargetFeeRateInSatPerVbyte(
                        realTimeFeesJson.targetFeeRates.confTargetToTargetFeeRateInSatPerVbyte
                ),
                realTimeFeesJson.targetFeeRates.fastConfTarget,
                realTimeFeesJson.targetFeeRates.mediumConfTarget,
                realTimeFeesJson.targetFeeRates.slowConfTarget
        );

        final FeeBumpFunctions feeBumpFunctions = new FeeBumpFunctions(
                realTimeFeesJson.feeBumpFunctions.uuid,
                realTimeFeesJson.feeBumpFunctions.functions
        );

        return new RealTimeFees(
                feeBumpFunctions,
                feeWindow,
                realTimeFeesJson.minMempoolFeeRateInSatPerVbyte,
                realTimeFeesJson.minFeeRateIncrementToReplaceByFeeInSatPerVbyte,
                mapNonNullableZonedDateTime(realTimeFeesJson.computedAt)
        );
    }

    private static SortedMap<Integer, Double> mapConfTargetToTargetFeeRateInSatPerVbyte(
            SortedMap<Integer, Double> confTargetToTargetFeeRateInSatPerVbyte
    ) {
        final SortedMap<Integer, Double> targetedFeeRates = new TreeMap<>();

        for (final var entry : confTargetToTargetFeeRateInSatPerVbyte.entrySet()) {

            final var target = entry.getKey();
            final var feeRateInSatPerVbyte = entry.getValue();
            targetedFeeRates.put(
                    target,
                    Rules.toSatsPerWeight(feeRateInSatPerVbyte)
            );
        }
        return targetedFeeRates;
    }

    private List<ForwardingPolicy> mapForwadingPolicies(
            final List<ForwardingPolicyJson> forwardingPolicies
    ) {

        final List<ForwardingPolicy> result = new ArrayList<>();
        for (final ForwardingPolicyJson json : forwardingPolicies) {
            result.add(new ForwardingPolicy(
                    Encodings.hexToBytes(json.identityKeyHex),
                    json.feeBaseMsat,
                    json.feeProportionalMillionths,
                    json.cltvExpiryDelta
            ));
        }

        return result;
    }

    private List<MeenFeature> mapMeenFeatures(List<MeenFeatureJson> features) {
        final List<MeenFeature> mappedFeatures =
                CollectionUtils.mapList(features, MeenFeature.Companion::fromJson);
        mappedFeatures.removeAll(Collections.singletonList(MeenFeature.UNSUPPORTED_FEATURE));
        return mappedFeatures;
    }

    /**
     * Create a NotificationReport.
     */
    @NotNull
    public NotificationReport mapNotificationReport(@NotNull NotificationReportJson reportJson) {
        return new NotificationReport(
                reportJson.previousId,
                reportJson.maximumId,
                reportJson.preview
        );
    }

    /**
     * Create a NextTransactionSize.
     */
    @NotNull
    public NextTransactionSize mapNextTransactionSize(@NotNull NextTransactionSizeJson json) {

        final ArrayList<SizeForAmount> progression = new ArrayList<>(json.sizeProgression.size());

        for (SizeForAmountJson sizeForAmount : json.sizeProgression) {
            progression.add(mapSizeForAmount(sizeForAmount));
        }

        return new NextTransactionSize(
                progression,
                json.validAtOperationHid,
                json.expectedDebtInSat
        );
    }

    /**
     * Create a SizeForAmount.
     */
    @NotNull
    private SizeForAmount mapSizeForAmount(@NotNull SizeForAmountJson sizeForAmount) {
        return new SizeForAmount(
                sizeForAmount.amountInSatoshis,
                sizeForAmount.sizeInBytes.intValue(),
                sizeForAmount.outpoint,
                UtxoStatus.fromJson(sizeForAmount.status),
                sizeForAmount.deltaInWeightUnits,
                sizeForAmount.derivationPath,
                sizeForAmount.addressVersion
        );
    }

    /**
     * Create a PublicKeySet.
     */
    @Nullable
    public PublicKeySet mapPublicKeySet(PublicKeySetJson publicKeySet) {
        return new PublicKeySet(
                new PublicKeyTriple(
                        mapPublicKey(publicKeySet.basePublicKey),
                        mapPublicKey(publicKeySet.baseCosigningPublicKey),
                        mapPublicKey(publicKeySet.baseSwapServerPublicKey)
                ),
                publicKeySet.externalPublicKeyIndices.maxUsedIndex,
                publicKeySet.externalPublicKeyIndices.maxWatchingIndex
        );
    }

    /**
     * Create a PendingChallengeUpdate.
     */
    public PendingChallengeUpdate mapPendingChallengeUpdate(PendingChallengeUpdateJson json) {
        return new PendingChallengeUpdate(json.uuid, json.type);
    }

    /**
     * Create a CreateSessionOk.
     */
    public CreateSessionOk mapCreateSessionOk(CreateSessionOkJson json) {
        return new CreateSessionOk(
                json.isExistingUser,
                json.canUseRecoveryCode,
                json.playIntegrityNonce
        );
    }

    /**
     * Create a CreateSessionRcOk.
     */
    public CreateSessionRcOk mapCreateSessionRcOk(CreateSessionRcOkJson json) {
        return new CreateSessionRcOk(
                json.keySet,
                json.hasEmailSetup,
                json.obfuscatedEmail,
                json.playIntegrityNonce
        );
    }

    /**
     * Create a ChallengeKeyUpdateMigration.
     */
    public ChallengeKeyUpdateMigration mapChalengeKeyUpdateMigration(
            final ChallengeKeyUpdateMigrationJson json
    ) {

        final byte[] passwordKeySalt = Encodings.hexToBytes(json.passwordKeySaltInHex);

        final byte[] recoveryCodeKeySalt = (json.recoveryCodeKeySaltInHex != null)
                ? Encodings.hexToBytes(json.recoveryCodeKeySaltInHex) : null;

        return new ChallengeKeyUpdateMigration(
                passwordKeySalt,
                recoveryCodeKeySalt,
                json.newEncrytpedMeenKey
        );
    }

    /**
     * Map a user and user prerences pair.
     */
    public Pair<User, UserPreferences> mapUserAndPreferences(final UserJson userJson) {
        return Pair.of(
                mapUser(userJson),
                mapUserPreferences(userJson.preferences)
        );
    }
}
