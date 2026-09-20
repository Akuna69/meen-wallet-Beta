package io.meen.apollo.data.db.contact;

import io.meen.apollo.data.db.base.HoustonIdDao;
import io.meen.apollo.domain.model.Contact;
import io.meen.apollo.domain.model.PublicProfile;
import io.meen.common.crypto.hd.PublicKey;

import androidx.annotation.NonNull;
import rx.Completable;
import rx.Observable;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ContactDao extends HoustonIdDao<Contact> {

    /**
     * Constructor.
     */
    @Inject
    public ContactDao() {
        super("contacts");
    }

    @Override
    public Completable deleteAll() {
        return Completable.fromAction(() -> delightDb.getContactQueries().deleteAll());
    }

    @Override
    protected void storeUnsafe(@NonNull final Contact element) {
        final PublicKey userPublicKey = element.publicKey;
        final PublicKey meenPublicKey = element.cosigningPublicKey;

        delightDb.getContactQueries().insertContact(
                element.getId(),
                element.getHid(),
                userPublicKey.serializeBase58(),
                userPublicKey.getAbsoluteDerivationPath(),
                element.lastDerivationIndex,
                element.maxAddressVersion,
                meenPublicKey != null ? meenPublicKey.serializeBase58() : null,
                meenPublicKey != null ? meenPublicKey.getAbsoluteDerivationPath() : null
        );
    }

    /**
     * Update the last derivation index to the maximum between the current index and the one given.
     */
    public void updateLastDerivationIndex(long contactHid, long lastDerivationIndex) {
        delightDb.getContactQueries().updateLastDerivationIndex(lastDerivationIndex, contactHid);
    }

    /**
     * Fetches all contacts from the db.
     */
    public Observable<List<Contact>> fetchAll() {
        return fetchList(delightDb.getContactQueries().selectAll(this::fromAllFields));
    }

    /**
     * Fetches a single contact by its Houston id.
     */
    public Observable<Contact> fetchByHid(long contactHid) {
        return fetchOneOrFail(
                delightDb.getContactQueries().selectByHid(contactHid, this::fromAllFields)
        ).doOnError(error -> enhanceError(error, String.valueOf(contactHid)));
    }

    private Contact fromAllFields(
            long id,
            long hid,
            @Nonnull String serializedPublicKey,
            @Nonnull String publicKeyPath,
            long lastDerivationIndex,
            long maxAddressVersion,
            @Nullable String serializedCosigningPublicKey,
            @Nullable String cosigningPublicKeyPath,
            long publicProfileId,
            long publicProfileHid,
            @Nonnull String firstName,
            @Nonnull String lastName,
            @Nullable String profilePictureUrl
    ) {
        final PublicKey cosigningPublicKey;
        if (serializedCosigningPublicKey != null) {
            cosigningPublicKey = PublicKey.deserializeFromBase58(
                    cosigningPublicKeyPath,
                    serializedCosigningPublicKey
            );
        } else {
            cosigningPublicKey = null;
        }

        return new Contact(
                id,
                hid,
                new PublicProfile(
                        publicProfileId,
                        publicProfileHid,
                        firstName,
                        lastName,
                        profilePictureUrl
                ),
                (int) maxAddressVersion,
                PublicKey.deserializeFromBase58(publicKeyPath, serializedPublicKey),
                cosigningPublicKey,
                lastDerivationIndex
        );
    }
}
