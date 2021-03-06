package com.mtg.counters.modules.fr_create_item

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mtg.counters.Application
import com.mtg.counters.R
import com.mtg.counters.databinding.FragmentCreateItemBinding
import com.mtg.counters.extensions.isNetworkEnable


/**
 * A simple [Fragment] subclass.
 * Use the [CreateItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateItemFragment : Fragment(), CreateItemPresenter, View.OnClickListener {

    private lateinit var binding: FragmentCreateItemBinding
    private val iterator by lazy { CreateItemIterator(this) }
    private var name: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_item, container, false)
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.edtNameItem.doOnTextChanged { text, _, _, _ ->
            name = text.toString().trim()
        }
        binding.imgClose.setOnClickListener(this)
        binding.txtSaveItem.setOnClickListener(this)
        binding.txtSeeExamples.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        val exampleSelected = iterator.getExampleSelected()
        if (exampleSelected.isNotBlank()) binding.edtNameItem.setText(exampleSelected)
    }

    override fun onError(msg: String) {
        val alert = AlertDialog.Builder(activity)
        alert.setTitle(getString(R.string.wt_internet_problem))
        alert.setMessage(msg)
        alert.setPositiveButton(getString(R.string.t_btn_ok)) { _,_ ->
            binding.txtSaveItem.visibility = VISIBLE
            binding.pgbCreateItem.visibility = GONE
            binding.edtNameItem.isEnabled = true
        }
        alert.show()
    }

    override fun onCounterSave() {
        val alert = AlertDialog.Builder(activity)
        alert.setTitle(getString(R.string.wt_counter_created))
        alert.setMessage("$name has been added to the system")
        alert.setPositiveButton(getString(R.string.t_btn_ok)) { _,_ ->
            findNavController().popBackStack()
        }
        alert.setCancelable(false)
        alert.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.imgClose.id -> findNavController().popBackStack()
            binding.txtSeeExamples.id -> findNavController().navigate(R.id.present_examples)
            binding.txtSaveItem.id -> {
                if (name.isBlank()) {
                    val alert = AlertDialog.Builder(activity)
                    alert.setTitle(getString(R.string.wt_empty_name))
                    alert.setMessage(getString(R.string.ws_empty_name))
                    alert.setPositiveButton(getString(R.string.t_btn_ok), null)
                    alert.show()
                } else if (!Application.getContext().isNetworkEnable()) {
                    val alert = AlertDialog.Builder(activity)
                    alert.setTitle(getString(R.string.wt_cant_create_counter))
                    alert.setMessage(getString(R.string.ws_offline_device))
                    alert.setPositiveButton(getString(R.string.t_btn_ok), null)
                    alert.show()
                } else {
                    binding.txtSaveItem.visibility = GONE
                    binding.pgbCreateItem.visibility = VISIBLE
                    binding.edtNameItem.isEnabled = false
                    iterator.saveCounter(name)
                }
            }
        }
    }

    override fun onDetach() {
        iterator.deleteExampleSelected()
        super.onDetach()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment CreateItemFragment.
         */
        @JvmStatic
        fun newInstance() =
                CreateItemFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}

interface CreateItemPresenter {
    fun onError(msg: String)
    fun onCounterSave()
}