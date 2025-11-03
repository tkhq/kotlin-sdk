package com.example.kotlin_demo_wallet.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_demo_wallet.R
import com.example.kotlin_demo_wallet.helpers.addressFormatToImageIcon
import com.example.kotlin_demo_wallet.helpers.addressFormatToReadable
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.turnkey.core.TurnkeyContext
import com.turnkey.types.V1AddressFormat
import com.turnkey.types.V1Curve
import com.turnkey.types.V1PathFormat
import com.turnkey.types.V1WalletAccount
import com.turnkey.types.V1WalletAccountParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private lateinit var tilWallet: TextInputLayout
    private lateinit var actWallets: AutoCompleteTextView // works, though MaterialAutoCompleteTextView is nicer
    private lateinit var rvAccounts: RecyclerView
    private lateinit var adapter: AccountsAdapter
    private lateinit var walletAdapter: ArrayAdapter<String>
    private lateinit var btnSignMessage: Button
    private lateinit var btnCreateWallet: Button
    private lateinit var btnImportWallet: Button
    private lateinit var btnExportWallet: Button
    private var selectedWalletIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tilWallet  = view.findViewById(R.id.tilWallet)
        actWallets = view.findViewById(R.id.actWallets)
        rvAccounts = view.findViewById(R.id.rvAccounts)
        btnSignMessage = view.findViewById(R.id.btnSignMessage)
        btnCreateWallet = view.findViewById(R.id.btnCreateWallet)
        btnExportWallet = view.findViewById(R.id.btnExportWallet)
        btnImportWallet = view.findViewById(R.id.btnImportWallet)

        // Accounts list
        adapter = AccountsAdapter()
        rvAccounts.layoutManager = LinearLayoutManager(requireContext())
        rvAccounts.adapter = adapter

        walletAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf())
        actWallets.setAdapter(walletAdapter)

        actWallets.setOnClickListener { actWallets.showDropDown() }
        actWallets.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) actWallets.showDropDown() }

        actWallets.setOnItemClickListener { _, _, position, _ ->
            selectedWalletIndex = position
            val wallets = TurnkeyContext.wallets.value.orEmpty()
            adapter.submit(wallets.getOrNull(position)?.accounts.orEmpty(), selectFirst = true)
        }

        btnSignMessage.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val selectedWalletAccount = adapter.getSelected() ?: return@launch
                val signature = TurnkeyContext.signMessage(
                    signWith = selectedWalletAccount.address,
                    addressFormat = selectedWalletAccount.addressFormat,
                    message = "Hello Turnkey!"
                )

                val display = "${signature.r}${signature.s}${signature.v}"

                launch(Dispatchers.Main) {
                    if (!isAdded) return@launch
                    SignatureBottomSheet
                        .newInstance(display)
                        .show(parentFragmentManager, "sign_result")
                }
            }
        }

        btnCreateWallet.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val accounts = listOf(
                    V1WalletAccountParams(
                        addressFormat = V1AddressFormat.ADDRESS_FORMAT_ETHEREUM,
                        curve = V1Curve.CURVE_SECP256K1,
                        path = "m/44'/60'/0'/0/0",
                        pathFormat = V1PathFormat.PATH_FORMAT_BIP32
                    ),
                    V1WalletAccountParams(
                        addressFormat = V1AddressFormat.ADDRESS_FORMAT_SOLANA,
                        curve = V1Curve.CURVE_ED25519,
                        path = "m/44'/501'/0'/0'",
                        pathFormat = V1PathFormat.PATH_FORMAT_BIP32
                    )
                )
                TurnkeyContext.createWallet(
                    walletName = "Wallet-${System.currentTimeMillis()}",
                    accounts = accounts,
                    mnemonicLength = 12
                )
            }
        }

        btnExportWallet.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val wallets = TurnkeyContext.wallets.value.orEmpty()
                    val selectedWallet = wallets.getOrNull(selectedWalletIndex) ?: return@launch
                    val res = TurnkeyContext.exportWallet(
                        walletId = selectedWallet.id
                    )

                    launch(Dispatchers.Main) {
                        if (!isAdded) return@launch
                        ExportBottomSheet
                            .newInstance(res.mnemonicPhrase)
                            .show(parentFragmentManager, "export_result")
                    }
                } catch (t: Throwable) {
                    Log.e("Dashboard", "Export Failed", t)
                }
            }
        }

        btnImportWallet.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                if (!isAdded) return@launch
                ImportBottomSheet.newInstance().show(parentFragmentManager, "import_result")
            }
        }

        // Collect wallets — update UI whenever data changes
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                TurnkeyContext.wallets.collect { walletsOrNull ->
                    val wallets = walletsOrNull.orEmpty()
                    val names = wallets.map { it.name }

                    // update dropdown contents
                    walletAdapter.clear()
                    walletAdapter.addAll(names)
                    walletAdapter.notifyDataSetChanged()

                    if (names.isEmpty()) {
                        actWallets.setText("", false)
                        adapter.submit(emptyList(), selectFirst = false)
                        selectedWalletIndex = 0
                        return@collect
                    }

                    // clamp selection if list size changed
                    if (selectedWalletIndex !in names.indices) selectedWalletIndex = 0

                    // show selected wallet title (no filtering)
                    actWallets.setText(names[selectedWalletIndex], false)

                    // show its accounts
                    adapter.submit(wallets[selectedWalletIndex].accounts, selectFirst = true)
                }
            }
        }
    }
}


private class AccountsAdapter : RecyclerView.Adapter<AccountsAdapter.VH>() {
    private val items = mutableListOf<V1WalletAccount>()
    private var selectedPos: Int = RecyclerView.NO_POSITION

    fun submit(newItems: List<V1WalletAccount>, selectFirst: Boolean = true) {
        items.clear()
        items.addAll(newItems)
        selectedPos = if (selectFirst && items.isNotEmpty()) 0 else RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

    fun getSelected(): V1WalletAccount? =
        items.getOrNull(selectedPos)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_account, parent, false)
        return VH(v) { clickedPos -> updateSelection(clickedPos) }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position], position == selectedPos)
    }

    override fun getItemCount() = items.size

    private fun updateSelection(newPos: Int) {
        if (newPos == RecyclerView.NO_POSITION || newPos == selectedPos) return
        val prev = selectedPos
        selectedPos = newPos
        if (prev != RecyclerView.NO_POSITION) notifyItemChanged(prev)
        notifyItemChanged(newPos)
    }

    class VH(
        itemView: View,
        private val onClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val name = itemView.findViewById<MaterialTextView>(R.id.tvAccountName)
        private val icon = itemView.findViewById<ImageView>(R.id.imgAccountLogo)
        private val addr = itemView.findViewById<MaterialTextView>(R.id.tvAccountAddress)
        private val radio = itemView.findViewById<MaterialRadioButton>(R.id.radio)

        init {
            // Toggle selection when tapping the row or the radio
            itemView.setOnClickListener { onClick(bindingAdapterPosition) }
            radio.setOnClickListener { onClick(bindingAdapterPosition) }
        }

        fun bind(item: V1WalletAccount, checked: Boolean) {
            name.text = addressFormatToReadable.getValue(item.addressFormat)
            icon.setImageResource(addressFormatToImageIcon.getValue(item.addressFormat))
            addr.text = item.address
            radio.isChecked = checked
        }
    }
}
