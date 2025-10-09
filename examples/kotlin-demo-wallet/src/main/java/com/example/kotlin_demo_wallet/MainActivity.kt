package com.example.kotlin_demo_wallet

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.kotlin_demo_wallet.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputEditText
import com.turnkey.core.TurnkeyCore
import com.turnkey.http.TCreateWalletBody
import com.turnkey.http.TGetWalletAccountsBody
import com.turnkey.http.TGetWhoamiBody
import com.turnkey.http.TSignRawPayloadBody
import com.turnkey.http.TSignRawPayloadsBody
import com.turnkey.http.TSignRawPayloadsRequest
import com.turnkey.http.model.ProxyV1InitOtpRequest
import com.turnkey.http.model.V1AddressFormat
import com.turnkey.http.model.V1Curve
import com.turnkey.http.model.V1GetWalletAccountRequest
import com.turnkey.http.model.V1GetWalletAccountsRequest
import com.turnkey.http.model.V1HashFunction
import com.turnkey.http.model.V1PathFormat
import com.turnkey.http.model.V1PayloadEncoding
import com.turnkey.http.model.V1WalletAccountParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var otpId: String
    private var email: String = ""
    private var otpCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSessionKey.text = TurnkeyCore.ctx.selectedSessionKey.value
        binding.tvAuthState.text = TurnkeyCore.ctx.authState.value.toString()

        // Wait until TurnkeyCore is initialized
        lifecycleScope.launch {
            TurnkeyCore.ready.await()

            val ctx = TurnkeyCore.ctx

            // Immediately show current values (optional)
            binding.tvAuthState.text = ctx.authState.value.toString()
            binding.tvSessionKey.text = ctx.selectedSessionKey.value.orEmpty()

            // Collect updates while Activity is STARTED
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    ctx.authState.collect { state ->
                        binding.tvAuthState.text = state.toString()
                    }
                }
                launch {
                    ctx.selectedSessionKey.collect { key ->
                        binding.tvSessionKey.text = key.orEmpty()
                    }
                }
            }
        }

        // Inputs
        binding.otpCodeTextInput.doOnTextChanged { text, _, _, _ ->
            otpCode = text?.toString().orEmpty()
        }
        binding.emailTextInput.doOnTextChanged { text, _, _, _ ->
            email = text?.toString().orEmpty()
        }

        // Start OTP
        binding.loginButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    println(TurnkeyCore.ctx.client.value)
                    val response = TurnkeyCore.ctx.client.value?.proxyInitOtp(
                        ProxyV1InitOtpRequest(
                            otpType = "OTP_TYPE_EMAIL",
                            contact = email
                        )
                    )
                    otpId = response!!.otpId
                    Log.d("MainActivity", "initOtp ok: $otpId")
                } catch (t: Throwable) {
                    Log.e("MainActivity", "initOtp failed", t)
                }
            }
        }

        binding.verifyOtpButton.setOnClickListener {
            lifecycleScope.launch {
                binding.verifyOtpButton.isEnabled = false
                try {
                    withContext(Dispatchers.IO) {
                        TurnkeyHelpers.completeOtp(
                            contact = email,
                            otpType = "OTP_TYPE_EMAIL",
                            otpCode = otpCode,
                            otpId = otpId
                        )
                    }
                } catch (e: Throwable) {
                    Log.e("MainActivity", "verifyOtp failed", e)
                    binding.tvAuthState.text = "error"
                } finally {
                    binding.verifyOtpButton.isEnabled = true
                }
            }
        }

        binding.signMessageButton.setOnClickListener {
            lifecycleScope.launch {
                binding.signMessageButton.isEnabled = false
                try {
                    withContext(Dispatchers.IO) {
                        val accounts: List<V1WalletAccountParams> = listOf(
                            V1WalletAccountParams(
                                curve = V1Curve.CURVE_SECP256K1,
                                pathFormat = V1PathFormat.PATH_FORMAT_BIP32,
                                path = "m/44'/60'/0'/0/0",
                                addressFormat = V1AddressFormat.ADDRESS_FORMAT_ETHEREUM
                            )
                        )

                        val walletRes = TurnkeyCore.ctx.client.value!!.createWallet(TCreateWalletBody(
                            organizationId = TurnkeyCore.ctx.user.value!!.organizationId,
                            walletName = "Wallet 11",
                            accounts = accounts
                        ))

                        val walletAccount = TurnkeyCore.ctx.client.value!!.getWalletAccount(
                            V1GetWalletAccountRequest(
                                organizationId = TurnkeyCore.ctx.user.value!!.organizationId,
                                walletId = walletRes.activity.result.createWalletResult!!.walletId,
                                address = walletRes.activity.result.createWalletResult!!.addresses[0],
                            ))

                        val payloadHex = "Hello"
                            .toByteArray(Charsets.UTF_8)
                            .joinToString("") { "%02x".format(it) }
                        val res = TurnkeyCore.ctx.client.value!!.signRawPayload(TSignRawPayloadBody(
                            organizationId = TurnkeyCore.ctx.user.value!!.organizationId,
                            signWith = walletAccount.account.address,
                            payload = payloadHex,
                            encoding = V1PayloadEncoding.PAYLOAD_ENCODING_HEXADECIMAL,
                            hashFunction = V1HashFunction.HASH_FUNCTION_KECCAK256
                        ))
                        println(res)
                    }
                } catch (e: Throwable) {
                    Log.e(TAG, "Sign message failed", e)
                } finally {
                    binding.signMessageButton.isEnabled = true
                }
            }
        }
    }
}
