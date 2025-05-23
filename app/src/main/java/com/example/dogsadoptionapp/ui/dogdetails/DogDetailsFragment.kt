package com.example.dogsadoptionapp.ui.dogdetails

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.databinding.FragmentDogDetailsBinding
import androidx.core.net.toUri
import com.example.dogsadoptionapp.ui.dogslist.DogsListViewModel

@Suppress("DEPRECATION")
class DogDetailsFragment : Fragment() {

    private var _binding: FragmentDogDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DogsListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDogDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[DogsListViewModel::class.java]

        val dogId = arguments?.getInt("dogId") ?: -1

        if (dogId != -1) {
            viewModel.getDogById(dogId).observe(viewLifecycleOwner) { dog ->
                dog?.let {
                    binding.dogDetailName.text = it.name
                    binding.dogDetailBreed.text = it.breed
                    binding.dogDetailAge.text = getString(R.string.years_old_format, it.age)
                    Glide.with(this).load(it.imageUri.toUri()).into(binding.dogDetailImage)
                    binding.toggleFavorite.isChecked = it.isFavorite

                    binding.btnAdoptDog.setOnClickListener {
                        val bundle = Bundle().apply {
                            putInt("dogId", dog.id)
                        }
                        NavHostFragment.findNavController(requireParentFragment())
                            .navigate(R.id.adoptionFormFragment, bundle)
                    }

                    binding.toggleFavorite.setOnCheckedChangeListener { _, isChecked ->
                        val updatedDog = dog.copy(isFavorite = isChecked)
                        viewModel.updateDog(updatedDog)
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), R.string.dog_not_found, Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        menu.findItem(R.id.action_delete).isVisible = false
        menu.findItem(R.id.action_return).isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_return -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}