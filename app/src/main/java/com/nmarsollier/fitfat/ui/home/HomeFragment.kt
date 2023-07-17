package com.nmarsollier.fitfat.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.nmarsollier.fitfat.BuildConfig
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.databinding.MainHomeFragmentBinding
import com.nmarsollier.fitfat.ui.measures.NewMeasureActivity
import com.nmarsollier.fitfat.utils.openDbInspector

class HomeFragment : Fragment() {
    val binding: MainHomeFragmentBinding by lazy {
        MainHomeFragmentBinding.inflate(layoutInflater)
    }

    val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return binding.root
    }

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

        binding.recyclerView.adapter = MeasureAdapter(this)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadSettings(requireContext())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (BuildConfig.DEBUG) {
            inflater.inflate(R.menu.debug_menu, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_db_inspector -> {
                openDbInspector()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}
