package com.nmarsollier.fitfat.ui.measuresList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.nmarsollier.fitfat.databinding.MainHomeFragmentBinding
import com.nmarsollier.fitfat.ui.editMeasure.NewMeasureActivity
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MeasuresListFragment : Fragment() {
    private val binding by lazy {
        MainHomeFragmentBinding.inflate(layoutInflater)
    }

    private val viewModel by lazy {
        getViewModel<MeasuresListViewModel>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
                DividerItemDecoration.VERTICAL
            )
        )

        binding.fab.setOnClickListener {
            val context = context ?: return@setOnClickListener
            NewMeasureActivity.startActivity(context)
        }

        binding.recyclerView.adapter = MeasureAdapter(this, viewModel)
    }
}
