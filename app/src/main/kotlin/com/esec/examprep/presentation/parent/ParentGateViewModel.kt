package com.esec.examprep.presentation.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.repository.ParentAccessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MAX_ATTEMPTS = 5
private const val LOCKOUT_MILLIS = 60_000L

data class ParentGateState(
    val mode: Mode = Mode.LOADING,
    val pin: String = "",
    val confirmPin: String = "",
    val errorRes: String? = null,
    val attemptsRemaining: Int = MAX_ATTEMPTS,
    val lockoutUntil: Long = 0L,
    val unlocked: Boolean = false,
) {
    enum class Mode { LOADING, SETUP, ENTER }
    val isLockedOut: Boolean get() = System.currentTimeMillis() < lockoutUntil
}

@HiltViewModel
class ParentGateViewModel @Inject constructor(
    private val parentAccess: ParentAccessRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ParentGateState())
    val state: StateFlow<ParentGateState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val mode = if (parentAccess.hasPin()) ParentGateState.Mode.ENTER else ParentGateState.Mode.SETUP
            _state.update { it.copy(mode = mode) }
        }
    }

    fun onPinChange(value: String) {
        if (value.length > 6 || !value.all { it.isDigit() }) return
        _state.update { it.copy(pin = value, errorRes = null) }
    }

    fun onConfirmPinChange(value: String) {
        if (value.length > 6 || !value.all { it.isDigit() }) return
        _state.update { it.copy(confirmPin = value, errorRes = null) }
    }

    fun submit() {
        viewModelScope.launch {
            val s = _state.value
            when (s.mode) {
                ParentGateState.Mode.SETUP -> handleSetup(s)
                ParentGateState.Mode.ENTER -> handleEnter(s)
                ParentGateState.Mode.LOADING -> Unit
            }
        }
    }

    private suspend fun handleSetup(s: ParentGateState) {
        if (s.pin.length < 4) {
            _state.update { it.copy(errorRes = "PIN must be at least 4 digits.") }
            return
        }
        if (s.pin != s.confirmPin) {
            _state.update { it.copy(errorRes = "PINs don't match.") }
            return
        }
        parentAccess.setPin(s.pin)
        _state.update { it.copy(unlocked = true, errorRes = null) }
    }

    private suspend fun handleEnter(s: ParentGateState) {
        if (s.isLockedOut) return
        if (s.pin.length < 4) {
            _state.update { it.copy(errorRes = "Enter your PIN.") }
            return
        }
        val ok = parentAccess.verifyPin(s.pin)
        if (ok) {
            _state.update { it.copy(unlocked = true, errorRes = null, attemptsRemaining = MAX_ATTEMPTS) }
        } else {
            val remaining = (s.attemptsRemaining - 1).coerceAtLeast(0)
            val lockUntil = if (remaining == 0) System.currentTimeMillis() + LOCKOUT_MILLIS else 0L
            _state.update {
                it.copy(
                    pin = "",
                    errorRes = if (remaining == 0) "Too many attempts. Try again in 1 minute." else "Incorrect PIN.",
                    attemptsRemaining = if (remaining == 0) MAX_ATTEMPTS else remaining,
                    lockoutUntil = lockUntil,
                )
            }
        }
    }
}
