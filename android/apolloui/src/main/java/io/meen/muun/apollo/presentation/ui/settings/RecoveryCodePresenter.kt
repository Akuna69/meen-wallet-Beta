package io.meen.apollo.presentation.ui.settings

import android.os.Bundle
import io.meen.apollo.domain.action.base.ActionState
import io.meen.apollo.domain.action.challenge_keys.password_change.StartPasswordChangeAction
import io.meen.apollo.domain.errors.IncorrectRecoveryCodeError
import io.meen.apollo.domain.errors.InvalidChallengeSignatureError
import io.meen.apollo.domain.errors.rc.InvalidCharacterRecoveryCodeError
import io.meen.apollo.domain.libwallet.errors.InvalidRecoveryCodeFormatError
import io.meen.apollo.domain.model.ChangePasswordStep
import io.meen.apollo.domain.model.PendingChallengeUpdate
import io.meen.apollo.domain.model.RecoveryCode.Companion.validate
import io.meen.apollo.domain.model.RecoveryCode.RecoveryCodeAlphabetError
import io.meen.apollo.domain.model.RecoveryCode.RecoveryCodeLengthError
import io.meen.apollo.presentation.ui.base.di.PerFragment
import io.meen.apollo.presentation.ui.settings.edit_password.BaseEditPasswordPresenter
import io.meen.common.crypto.ChallengeType
import javax.inject.Inject

@PerFragment
class RecoveryCodePresenter @Inject constructor(
    private val startPasswordChange: StartPasswordChangeAction,
) : BaseEditPasswordPresenter<RecoveryCodeView>() {

    override fun setUp(arguments: Bundle) {
        super.setUp(arguments)
        setUpBeginPasswordChangeAction()
    }

    private fun setUpBeginPasswordChangeAction() {
        val observable = startPasswordChange
            .state
            .doOnNext { state: ActionState<PendingChallengeUpdate> ->
                when (state.kind) {
                    ActionState.Kind.VALUE -> {
                        parentPresenter.currentUuid = state.value.uuid
                        navigateToStep(ChangePasswordStep.WAIT_FOR_EMAIL)
                    }
                    ActionState.Kind.ERROR -> {
                        view.setLoading(false)
                        handleError(state.error)
                    }
                    else -> {
                        // Do nothing
                    }
                }
            }
        subscribeTo(observable)
    }

    override fun handleError(error: Throwable) {
        if (error is InvalidChallengeSignatureError) {
            view.setRecoveryCodeError(IncorrectRecoveryCodeError())

        } else {
            super.handleError(error)
        }
    }

    /**
     * Called when the user edits some part of the recovery code.
     */
    fun onRecoveryCodeEdited(recoveryCodeString: String) {
        view.setRecoveryCodeError(null)
        view.setConfirmEnabled(false)

        try {
            validate(recoveryCodeString)
            view.setConfirmEnabled(true)

        } catch (error: RecoveryCodeAlphabetError) {
            view.setRecoveryCodeError(InvalidCharacterRecoveryCodeError())

        } catch (error: InvalidRecoveryCodeFormatError) {
            view.setRecoveryCodeError(InvalidCharacterRecoveryCodeError())

        } catch (error: RecoveryCodeLengthError) {
            // Do nothing. Let the user finish typing.

        } catch (error: Exception) {
            handleError(error)
        }
    }

    /**
     * Start passwordItem change process, using the user's recovery code.
     */
    fun submitRecoveryCode(recoveryCode: String) {
        startPasswordChange.run(recoveryCode, ChallengeType.RECOVERY_CODE)
    }
}