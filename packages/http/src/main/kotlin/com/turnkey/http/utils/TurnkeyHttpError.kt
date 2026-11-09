package com.turnkey.http.utils

import com.turnkey.types.V1Activity
import com.turnkey.types.V1MfaStatus

sealed class TurnkeyHttpError (message: String, cause: Throwable? = null): Exception(
    if (cause != null) "$message - error: ${cause.message}" else message,
    cause
){
    data class MissingAuthProxyConfigId (override val cause: Throwable? = null): TurnkeyHttpError("Missing authProxyConfigId, please initialize the TurnkeyClient with the proper auth proxy params", cause)
    data class StamperNotInitialized (override val cause: Throwable? = null): TurnkeyHttpError("No stampers found, please initialized a stamper and pass it into the client.", cause)
    data class EmptyResponseBody(val url: String, override val cause: Throwable? = null): TurnkeyHttpError("Empty response body from $url", cause)
    data class OperationFailed(override val message: String, override val cause: Throwable): TurnkeyHttpError(message, cause)

    /**
     * A submit call returned an activity whose status is not `ACTIVITY_STATUS_COMPLETED` after the
     * client finished polling. The full [activity] is attached so callers can branch on
     * `activity.status` (for example `ACTIVITY_STATUS_PENDING` vs `ACTIVITY_STATUS_REJECTED`),
     * inspect `activity.failure`, and resume by polling `getActivity` with `activity.id` instead of
     * re-submitting.
     */
    data class ActivityNotCompleted(val activity: V1Activity, val path: String, override val cause: Throwable? = null): TurnkeyHttpError(
        "Activity ${activity.id} from $path has status ${activity.status}, expected ACTIVITY_STATUS_COMPLETED",
        cause
    )

    /**
     * A submit call returned an activity with status `ACTIVITY_STATUS_AUTHENTICATORS_NEEDED`: an MFA
     * policy requires more authentication before the activity can complete. The client stops polling
     * when it sees this status, because waiting does not change it.
     *
     * [mfaStatuses] lists each MFA policy that applies, the methods it requires and the methods
     * already satisfied, as returned by `getMfaStatus`. It is empty if that call failed. To continue,
     * approve the activity (`approveActivity` with `activity.fingerprint`) using a credential of a
     * required type, then poll `getActivity` with `activity.id`.
     */
    data class MfaRequired(val activity: V1Activity, val mfaStatuses: List<V1MfaStatus>, override val cause: Throwable? = null): TurnkeyHttpError(
        "Activity ${activity.id} needs more authentication (ACTIVITY_STATUS_AUTHENTICATORS_NEEDED) before it can complete",
        cause
    )

    companion object {
        fun wrap (s: String, t: Throwable): TurnkeyHttpError {
            return when (t) {
                is TurnkeyHttpError -> t
                else -> OperationFailed(s, t)
            }
        }
    }
}